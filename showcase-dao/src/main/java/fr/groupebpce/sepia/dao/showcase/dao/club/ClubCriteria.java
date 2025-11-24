package fr.groupebpce.sepia.dao.showcase.dao.club;

import fr.groupebpce.sepia.criteria.GenericCriteria;
import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.criteria.filter.Range;
import fr.groupebpce.sepia.criteria.filter.StringFilter;
import fr.groupebpce.sepia.criteria.paging.Paging;
import fr.groupebpce.sepia.criteria.sorting.Sorting;
import fr.groupebpce.sepia.dao.showcase.entity.Country;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import static java.util.Objects.isNull;

@Getter
@Setter
public class ClubCriteria extends GenericCriteria {

    /**
     * Club's name must be like
     */
    private StringFilter name;
    /**
     * Club's country must be exactly
     */
    private Country country;
    /**
     * The club's stadium name must be like
     */
    private String stadiumName;
    /**
     * The club's stadium capacity must be between
     */
    private Range<Integer> stadiumCapacity;
    /**
     * The club must have a player whose name is like
     */
    private String playerName;
    /**
     * The club must have a player whose country is exactly
     */
    private Country playerCountry;

    // ========== FLUENT SETTERS ==========

    public ClubCriteria nameEquals(String name) {
        this.name = StringFilter.exactMatches(name);
        return this;
    }

    public ClubCriteria nameLike(String name) {
        this.name = StringFilter.like(name).ignoreCase();
        return this;
    }

    public ClubCriteria countryIs(Country country) {
        this.country = country;
        return this;
    }

    public ClubCriteria stadiumNameLike(String stadiumName) {
        this.stadiumName = stadiumName;
        return this;
    }

    public ClubCriteria stadiumCapacityGreaterThan(Integer minCapacity) {
        if (isNull(this.stadiumCapacity)){
            this.stadiumCapacity = Range.minorized(minCapacity); 
        }
        else {
            this.stadiumCapacity.setMin(minCapacity);
        }
         
        return this;
    }

    public ClubCriteria stadiumCapacityLesserThan(Integer maxCapacity) {
        if (isNull(this.stadiumCapacity)){
            this.stadiumCapacity = Range.majorized(maxCapacity);
        }
        else {
            this.stadiumCapacity.setMax(maxCapacity);
        }
        return this;
    }

    public ClubCriteria stadiumCapacityIn(Integer minCapacity, Integer maxCapacity) {
        this.stadiumCapacity = Range.bounded(minCapacity, maxCapacity);
        return this;
    }

    public ClubCriteria hasPlayerWithName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public ClubCriteria hasPlayerOfNationality(Country playerCountry) {
        this.playerCountry = playerCountry;
        return this;
    }

    public ClubCriteria paging(Paging paging) {
        super.setPaging(paging);
        return this;
    }

    @Override
    public ClubCriteria fetching(Fetching fetching) {
        super.fetching(fetching);
        return this;
    }

    @Override
    public ClubCriteria fetch(String... paths) {
        super.fetch(paths);
        return this;
    }

    @Override
    public ClubCriteria orderBy(Sorting... sortings) {
        super.orderBy(sortings);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClubCriteria other = (ClubCriteria) o;
        return Objects.equals(this.getName(), other.name) &&
                Objects.equals(this.getCountry(), other.country) &&
                Objects.equals(this.getStadiumName(), other.stadiumName) &&
                Objects.equals(this.getStadiumCapacity(), other.stadiumCapacity) &&
                Objects.equals(this.getPlayerName(), other.playerName) &&
                Objects.equals(this.getPlayerCountry(), other.playerCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, country, stadiumName, stadiumCapacity,
                playerName, playerCountry);
    }
}
