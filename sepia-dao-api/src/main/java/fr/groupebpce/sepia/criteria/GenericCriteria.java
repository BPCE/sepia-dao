package fr.groupebpce.sepia.criteria;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.criteria.paging.Paging;
import fr.groupebpce.sepia.criteria.sorting.Sorting;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class GenericCriteria {

    private List<Sorting> ordering;
    private Paging paging;
    private Fetching fetching;

    public GenericCriteria fetch(String... paths) {
        return fetching(Fetching.fetch(paths));
    }
    
    
    public GenericCriteria fetching(Fetching fetching) {
        this.fetching = fetching;
        return this;
    }

    /**
     * Setter of the {@link #ordering} attribute.
     * replaces the contents of the {@link #ordering} list by the Sorting param list
     * @param ordering
     */
    public final void orderBy(List<Sorting> ordering) {
        this.ordering = ordering;
    }

    /**
     * setter for the {@link #ordering} attribute.
     * replaces the contents of the {@link #ordering} list by the Sorting varargs param
     * @param sorting
     * @return this
     */
    public GenericCriteria orderBy(Sorting... sorting) {
        if (this.ordering == null) {
            this.ordering = new ArrayList<>();
        }
        if (!this.ordering.isEmpty()) {
            this.ordering.clear();
        }

        Collections.addAll(this.ordering, sorting);
        return this;
    }

    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericCriteria that = (GenericCriteria) o;
        return Objects.equals(ordering, that.ordering) &&
                Objects.equals(paging, that.paging) &&
                Objects.equals(fetching, that.fetching);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordering, paging, fetching);
    }
}
