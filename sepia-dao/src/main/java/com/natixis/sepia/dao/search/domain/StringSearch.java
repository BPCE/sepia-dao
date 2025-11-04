package com.natixis.sepia.dao.search.domain;

import java.io.Serializable;
import java.util.Objects;

public class StringSearch implements Serializable {

    // ========== ATTRIBUTES ==========

    /**
     * The value that we search in database
     */
    private String value;

    /**
     * Indicates if it is an exact search (=) or not (approximative search... LIKE)
     */
    private boolean isExact;

    // ========== CONSTRUCTOR ==========

    public StringSearch() {
    }

    public StringSearch(String value, boolean isExact) {
        this.value = value;
        this.isExact = isExact;
    }

    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringSearch that = (StringSearch) o;
        return isExact == that.isExact && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isExact);
    }

    // ========== FLUENT SETTERS ==========

    @Deprecated
    /**
     * @deprecated : use exactMatches instead (name of this method is too close from equals according to Sonar)
     */
    public StringSearch equal(String value) {
        return exactMatches(value);
    }

    public StringSearch exactMatches(String value) {
        this.isExact = true;
        this.value = value;
        return this;
    }

    public StringSearch like(String value) {
        this.isExact = false;
        this.value = value;
        return this;
    }

    // ========== GETTERS & SETTERS ==========

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getIsExact() {
        return isExact;
    }

    public void setIsExact(boolean isExact) {
        this.isExact = isExact;
    }
}
