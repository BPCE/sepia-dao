package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.criteria.Criteria;
import fr.groupebpce.sepia.criteria.filter.StringFilter;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PlayerCriteria extends Criteria {
    
    Club club;
    StringFilter name;

    public PlayerCriteria nameLike(String name) {
        this.name = new StringFilter(name, false);
        return this;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerCriteria that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getClub(), that.getClub());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClub());
    }
}
