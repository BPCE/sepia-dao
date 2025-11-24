package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.criteria.GenericCriteria;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerCriteria extends GenericCriteria {
    
    Club club;

    public PlayerCriteria clubIs(Club club) {
        this.club = club;
        return this;
    }
    public PlayerCriteria clubIs(String code) {
        this.club = new Club().code(code);
        return this;
    }

    @Override
    public PlayerCriteria fetch(String... paths) {
        super.fetch(paths);
        return this;
    }
}
