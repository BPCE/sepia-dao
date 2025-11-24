package fr.groupebpce.sepia.dao.fetch;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.*;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EntityGraphBuilder<E> {

    private final EntityManager em;
    private final Class<E> entityClass;
    private final Map<String, FetchNode> nodes = new HashMap<>();
    private final Set<FetchNode> rootNodes = new HashSet<>();
    private final Set<FetchNode> nodesWithCollection = new HashSet<>();

    public EntityGraphBuilder(EntityManager em, Class<E> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public EntityGraphList<E> build(Fetching fetching) {

        EntityGraphList<E> entityGraphList = new EntityGraphList<>();

        if (isNull(fetching) || isNull(fetching.getPathsToFetch())) {
            entityGraphList.setMainEntityGraph(em.createEntityGraph(entityClass));
            return entityGraphList;
        }

        Collections.sort(fetching.getPathsToFetch());
        for (String path : fetching.getPathsToFetch()) {
            addNodes(path);
        }
        entityGraphList.setMainEntityGraph(getMainEntityGraph());
        entityGraphList.setCollectionEntityGraphs(getCollectionEntityGraphs());
        return entityGraphList;
    }

    private void addNodes(String path) {

        if (isBlank(path)) throw new IllegalArgumentException("Path to fetch cannot be blank");

        String[] properties = path.split("\\.");
        String nodeId = "";
        FetchNode parent = null;
        for (String property : properties) {
            nodeId += isEmpty(nodeId) ? property : "." + property;
            nodes.putIfAbsent(nodeId, new FetchNode(nodeId)
                    .property(property)
                    .parent(parent)
            );
            FetchNode node = nodes.get(nodeId);
            defineClassTypeAndCollection(node);
            if (isNull(node.getParent())) {
                rootNodes.add(node);
            } else {
                node.getParent().getChildren().add(node);
            }
            parent = node;
        }

    }

    private void defineClassTypeAndCollection(FetchNode node) {
        Class<?> parentClass = isNull(node.getParent()) ? entityClass : node.getParent().getClassType();

        ManagedType<?> parentJpaType = null;
        if (parentClass.isAnnotationPresent(Entity.class)) {
            parentJpaType = em.getMetamodel().entity(parentClass);
        } else if (parentClass.isAnnotationPresent(Embeddable.class)) {
            parentJpaType = em.getMetamodel().embeddable(parentClass);
        } else {
            throw new RuntimeException("Cannot fetch node %s since it is not an Entity, nor an Embeddable".formatted(node.getId()));
        }
        defineClassTypeAndCollection(node, parentJpaType);
    }

    private EntityGraph<E> getMainEntityGraph() {

        EntityGraph<E> graph = em.createEntityGraph(entityClass);
        rootNodes.forEach(rootNode -> {
            if (!rootNode.isCollection()) {
                Subgraph<E> subgraph = graph.addSubgraph(rootNode.getProperty());
                addSubgraphs(subgraph, rootNode.getChildren(), false);
            }
        });
        return graph;
    }

    private void addSubgraphs(Subgraph<E> subgraph, List<FetchNode> children, boolean includingCollection) {
        children.forEach(node -> {
            if (includingCollection || !node.isCollection()) {
                Subgraph<E> subSubgraph = subgraph.addSubgraph(node.getProperty());
                addSubgraphs(subSubgraph, node.getChildren(), includingCollection);
            }
        });
    }

    private List<EntityGraph<E>> getCollectionEntityGraphs() {

        List<EntityGraph<E>> graphs = new ArrayList<>();
        nodesWithCollection.forEach(node -> {
            EntityGraph<E> graph = em.createEntityGraph(entityClass);
            // parents
            List<FetchNode> parents = getParents(node);
            Subgraph<E> subgraph = null;
            if (isNotEmpty(parents)) {
                subgraph = graph.addSubgraph(parents.getFirst().getProperty());
                parents.removeFirst();
                for (FetchNode parent : parents) {
                    subgraph = subgraph.addSubgraph(parent.getProperty());
                }
                subgraph = subgraph.addSubgraph(node.getProperty());
            }
            else {
                subgraph = graph.addSubgraph(node.getProperty());
            }
            addSubgraphs(subgraph, node.getChildren(), true);
            graphs.add(graph);
        });
        return graphs;
    }

    private List<FetchNode> getParents(FetchNode node) {
        List<FetchNode> parents = new ArrayList<>();
        FetchNode parent = node.getParent();
        while (parent != null) {
            parents.add(0, parent);
            parent = parent.getParent();
        }
        return parents;
    }


    private void defineClassTypeAndCollection(FetchNode node, ManagedType<?> managedType) {
        for (SingularAttribute<?, ?> attribute : managedType.getDeclaredSingularAttributes()) {
            if (attribute.getName().equals(node.getProperty())) {
                node.collection(false)
                        .classType(attribute.getType().getJavaType());
                return;
            }
        }

        for (PluralAttribute<?, ?, ?> attribute : managedType.getDeclaredPluralAttributes()) {
            if (attribute.getName().equals(node.getProperty())) {
                node.collection(true)
                        .classType(attribute.getBindableJavaType());
                nodesWithCollection.add(node);
                return;
            }
        }

        throw new RuntimeException("Cannot find property %s. Seems it's not managed/mapped in JPA".formatted(node.getId()));
    }

}
