package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.dao.QueryContext;
import fr.groupebpce.sepia.dao.QueryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class PlayerDao extends QueryDao<Player, PlayerCriteria> {
    
    public PlayerDao() {
        super(Player.class);
    }

    @Override
    protected void filter(QueryContext<PlayerCriteria> context) {

        EntityType<Club> entityType = entityManager.getMetamodel().entity(Club.class);
        Set<SingularAttribute<Club, ?>> singularAttributes = entityType.getDeclaredSingularAttributes();
        Set<PluralAttribute<Club, ?, ?>> declaredPluralAttributes = entityType.getDeclaredPluralAttributes();

        System.out.println(Club.class);
        for (SingularAttribute<Club, ?> attribute : singularAttributes) {
            System.out.println(attribute.getType().getJavaType());
        }
        
    }
}
