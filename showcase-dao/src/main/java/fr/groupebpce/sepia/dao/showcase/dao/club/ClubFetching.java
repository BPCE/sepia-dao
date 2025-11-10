package fr.groupebpce.sepia.dao.showcase.dao.club;

import fr.groupebpce.sepia.criteria.fetching.IFetching;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubFetching implements IFetching {

    // ========== ATTRIBUTES ==========

    private Boolean stadium;
    private Boolean players;

    // ========== FLUENT SETTER ==========

    public ClubFetching stadium(boolean stadium) {
        this.stadium = stadium;
        return this;
    }

    public ClubFetching players(boolean players) {
        this.players = players;
        return this;
    }

}
