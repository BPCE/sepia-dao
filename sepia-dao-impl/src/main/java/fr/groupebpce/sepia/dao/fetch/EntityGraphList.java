package fr.groupebpce.sepia.dao.fetch;

import jakarta.persistence.EntityGraph;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EntityGraphList<E> {
    
    EntityGraph<E> mainEntityGraph;
    List<EntityGraph<E>> collectionEntityGraphs = new ArrayList<>();
    
    
    
}
