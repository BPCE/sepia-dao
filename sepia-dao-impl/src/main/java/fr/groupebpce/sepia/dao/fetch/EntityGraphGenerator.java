package fr.groupebpce.sepia.dao.fetch;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.*;

import static java.util.Objects.nonNull;

public class EntityGraphGenerator<E> {

    private final EntityManager em;
    private final Class<E> entityClass;
    private final NodeCollector<E> nodeCollector;
    
    @Getter
    private EntityGraph<E> mainEntityGraph;
    @Getter
    private final List<EntityGraph<E>> collectionEntityGraphs = new ArrayList<>();

    public EntityGraphGenerator(EntityManager em, Class<E> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
        this.nodeCollector = new NodeCollector<>(em, entityClass);
    }

    public void generate(Fetching fetching) {
        if (nonNull(fetching) && nonNull(fetching.getPathsToFetch())) {
            nodeCollector.collect(fetching);
            generateMainEntityGraph();
            generateCollectionEntityGraphs();
        }
    }

    private void generateMainEntityGraph() {
        this.mainEntityGraph = em.createEntityGraph(entityClass);
        nodeCollector.getRoots().forEach(rootNode -> {
            if (rootNode.isNotCollection()) {
                Subgraph<E> subgraph = mainEntityGraph.addSubgraph(rootNode.getProperty());
                addDescendantToGraph(subgraph, rootNode, false);
            }
        });
    }

    private void addDescendantToGraph(Subgraph<E> subgraph, FetchNode node, boolean fetchMustIncludeCollections) {
        node.getChildren().forEach(child -> {
            if (fetchMustIncludeCollections || child.isNotCollection()) {
                Subgraph<E> subSubgraph = subgraph.addSubgraph(child.getProperty());
                addDescendantToGraph(subSubgraph, child, fetchMustIncludeCollections);
            }
        });
    }

    private void generateCollectionEntityGraphs() {

        nodeCollector.getNodesWithCollection().forEach(node -> {
            EntityGraph<E> graph = em.createEntityGraph(entityClass);
            Subgraph<E> subgraph = addNodeWithItsAncestorsToGraph(node, graph);
            addDescendantToGraph(subgraph, node, true);
            collectionEntityGraphs.add(graph);
        });
    }

    private Subgraph<E> addNodeWithItsAncestorsToGraph(FetchNode node, EntityGraph<E> graph) {
        List<FetchNode> nodes = new ArrayList<>(getAllAncestorsStartingFromRoot(node));
        nodes.add(node);

        Subgraph<E> subgraph = graph.addSubgraph(nodes.getFirst().getProperty());
        nodes.removeFirst();
        for (FetchNode parent : nodes) {
            subgraph = subgraph.addSubgraph(parent.getProperty());
        }
        return subgraph;
    }

    private List<FetchNode> getAllAncestorsStartingFromRoot(FetchNode node) {
        List<FetchNode> parents = new ArrayList<>();
        FetchNode parent = node.getParent();
        while (parent != null) {
            parents.addFirst(parent);
            parent = parent.getParent();
        }
        return parents;
    }

}
