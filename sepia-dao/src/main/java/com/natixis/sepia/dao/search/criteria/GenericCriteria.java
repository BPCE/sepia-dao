package com.natixis.sepia.dao.search.criteria;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class GenericCriteria<T extends IFetching> {

    // ========== ATTRIBUTES ==========
    private List<Sorting> ordering;
    private Paging paging;
    private T fetching;

    // ========== CONSTRUCTOR ==========

    protected GenericCriteria() {
    }

    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericCriteria<?> that = (GenericCriteria<?>) o;
        return Objects.equals(ordering, that.ordering) &&
                Objects.equals(paging, that.paging) &&
                Objects.equals(fetching, that.fetching);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordering, paging, fetching);
    }

    // ========== GETTER & SETTER ==========

    /**
     * setter for the {@link #ordering} attribute.
     * replaces the contents of the {@link #ordering} list by the Sorting param object
     * @deprecated since version 22.005. Use {@link #orderBy(List)} or {@link #orderBy(Sorting... sorting)} instead
     * @param sorting
     */
    @Deprecated
    public final void setSorting(Sorting sorting) {
        if (this.ordering == null) {
            this.ordering = new ArrayList<>();
        }
        if (!this.ordering.isEmpty()) {
            this.ordering.clear();
        }

        this.ordering.add(sorting);
    }

    /**
     * getter for the {@link #ordering} attribute
     * @deprecated since version 22.005. Use {@link #getOrdering()} instead
     * @return the ordering attribute
     */
    @Deprecated
    public final Sorting getSorting() {
        return CollectionUtils.isEmpty(this.ordering) ? null : this.ordering.get(0);
    }

    /**
     * setter of the {@link #ordering} attribute.
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
    public GenericCriteria<T> orderBy(Sorting... sorting) {
        if (this.ordering == null) {
            this.ordering = new ArrayList<>();
        }
        if (!this.ordering.isEmpty()) {
            this.ordering.clear();
        }

        Collections.addAll(this.ordering, sorting);
        return this;
    }

    public final List<Sorting> getOrdering() {
        return ordering;
    }

    public final T getFetching() {
        return fetching;
    }

    public final void setFetching(T fetching) {
        this.fetching = fetching;
    }

    public final Paging getPaging() {
        return paging;
    }

    public final void setPaging(Paging paging) {
        this.paging = paging;
    }
}
