package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.dao.QueryContext;
import fr.groupebpce.sepia.dao.QueryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import org.springframework.stereotype.Repository;

import static java.util.Objects.nonNull;

@Repository
public class PlayerDao extends QueryDao<Player, PlayerCriteria> {
    
    public PlayerDao() {
        super(Player.class);
    }

    @Override
    protected void filter(QueryContext<PlayerCriteria> context) {
        PlayerCriteria criteria = context.getCriteria();
        
        if (nonNull(criteria.getClub())) {
            context.addCondition("player.club = :club");
            context.addParameter("club", criteria.getClub());
        }
        
    }
}
