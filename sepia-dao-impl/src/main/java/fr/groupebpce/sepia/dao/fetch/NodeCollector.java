package fr.groupebpce.sepia.dao.fetch;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.exception.FetchException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Getter;

import java.util.*;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class NodeCollector<E> {

    private final EntityManager em;
    private final Class<E> entityClass;

    private final Map<String, FetchNode> nodes = new HashMap<>();
    private final Set<FetchNode> roots = new HashSet<>();
    private final Set<FetchNode> nodesWithCollection = new HashSet<>();

    public NodeCollector(EntityManager em, Class<E> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }
    
    public void collect(Fetching fetching) {
        Collections.sort(fetching.getPathsToFetch());
        for (String path : fetching.getPathsToFetch()) {
            addNodes(path);
        }
    }

    private void addNodes(String path) {

        if (isBlank(path)) throw new IllegalArgumentException("Path to fetch cannot be blank");

        StringJoiner nodeId = new StringJoiner(".");
        FetchNode parent = null;
        for (String property : path.split("\\.")) {
            nodeId.add(property);
            nodes.putIfAbsent(nodeId.toString(), new FetchNode(nodeId.toString())
                    .property(property)
                    .parent(parent)
            );
            FetchNode node = nodes.get(nodeId.toString());
            defineClassTypeAndCollection(node);
            if (isNull(node.getParent())) {
                roots.add(node);
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
            throw new FetchException("Cannot fetch node %s since it is not an Entity, nor an Embeddable".formatted(node.getId()));
        }
        defineClassTypeAndCollection(node, parentJpaType);
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

        throw new FetchException("Cannot find property %s. Seems it's not managed/mapped in JPA".formatted(node.getId()));
    }
    
}
