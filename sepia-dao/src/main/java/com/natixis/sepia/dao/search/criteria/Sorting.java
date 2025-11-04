package com.natixis.sepia.dao.search.criteria;

import java.util.Objects;

public class Sorting {

    // ========== ATTRIBUTES ==========

    private String property;
    private SortingDirectionEnum direction = SortingDirectionEnum.ASC;

    // ========== CONSTRUCTOR ==========

    public Sorting() {
    }

    public Sorting(String property, SortingDirectionEnum direction) {
        this.property = property;
        this.direction = direction;
    }

    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sorting sorting = (Sorting) o;
        return Objects.equals(property, sorting.property) && direction == sorting.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, direction);
    }


    // ========== GETTERS & SETTERS ==========

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public SortingDirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(SortingDirectionEnum direction) {
        this.direction = direction;
    }

    // ========== FLUENT SETTERS ==========

    public Sorting property(String property) {
        this.property = property;
        return this;
    }

    public Sorting direction(SortingDirectionEnum direction) {
        this.direction = direction;
        return this;
    }

    public Sorting ascending() {
        this.direction = SortingDirectionEnum.ASC;
        return this;
    }

    public Sorting descending() {
        this.direction = SortingDirectionEnum.DESC;
        return this;
    }
}
