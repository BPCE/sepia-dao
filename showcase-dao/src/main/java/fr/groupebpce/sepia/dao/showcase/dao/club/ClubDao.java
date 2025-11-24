package fr.groupebpce.sepia.dao.showcase.dao.club;

import fr.groupebpce.sepia.criteria.sorting.Sorting;
import fr.groupebpce.sepia.dao.QueryContext;
import fr.groupebpce.sepia.dao.QueryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OneToMany;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class ClubDao extends QueryDao<Club, ClubCriteria> {

    public ClubDao() {
        super(Club.class);
    }

    @Override
    protected void filter(QueryContext<ClubCriteria> context) {

        ClubCriteria criteria = context.getCriteria();

        // name of the club 
        if (nonNull(criteria.getName())) {
            context.addStringCondition("club.name", "clubName", criteria.getName());
        }

        // country of the club (exact match)
        if (nonNull(criteria.getCountry())) {
            context.addCondition("club.country = :country")
                    .addParameter("country", criteria.getCountry());
        }

        // name of the stadium (like)
        if (isNotBlank(criteria.getStadiumName())) {
            context.addLikeCondition("club.stadium.name", "stadiumName", criteria.getStadiumName(), true);
        }

        // stadium capacity 
        if (nonNull(criteria.getStadiumCapacity())) {
            if (criteria.getStadiumCapacity().isBounded()) {
                context.addCondition("club.stadium.capacity between :stadiumMinCapacity and :stadiumMaxCapacity")
                        .addParameter("stadiumMinCapacity", criteria.getStadiumCapacity().getMin())
                        .addParameter("stadiumMaxCapacity", criteria.getStadiumCapacity().getMax());
            }
            else if (criteria.getStadiumCapacity().isMinorized()) {
                context.addCondition("club.stadium.capacity >= :stadiumMinCapacity")
                        .addParameter("stadiumMinCapacity", criteria.getStadiumCapacity().getMin());
            }
            else if (criteria.getStadiumCapacity().isMajorized()) {
                context.addCondition("club.stadium.capacity <= :stadiumMaxCapacity")
                        .addParameter("stadiumMaxCapacity", criteria.getStadiumCapacity().getMax());
            }
        }

        // contains a player whose country is exactly...
        if (nonNull(criteria.getPlayerCountry())) {
            context.addJoin("players", "club.players")
                    .addCondition("players.country = :playerCountry")
                    .addParameter("playerCountry", criteria.getPlayerCountry());
        }

        // contains a player whose name is like...
        if (isNotBlank(criteria.getPlayerName())) {
            context.addJoin("players", "club.players")
                    .addLikeCondition("players.name", "playerName", criteria.getPlayerName());
        }

    }

    protected void fetch(EntityGraph<Club> graph, ClubFetching fetching) {
        if (isTrue(fetching.getStadium())) {
            graph.addSubgraph("stadium")
                    .addSubgraph("address")
                    .addSubgraph("city")
                    ;
        }
    }

    @Override
    protected String sort(Sorting sorting) {
        if (StringUtils.equals(sorting.getProperty(), "stadiumCapacity")) {
            return getAliasName() + "stadium.capacity " + sorting.getDirection();
        }
        return super.sort(sorting);
    }
}
