package com.natixis.sepia.dao.search.criteria;

import java.util.Objects;

public class Paging {

    // ========== ATTRIBUTES ==========

    private Integer pageSize;
    private Integer pageNumber;

    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paging paging = (Paging) o;
        return Objects.equals(getPageNumber(), paging.getPageNumber()) && Objects.equals(getPageSize(), paging.getPageSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNumber(), getPageSize());
    }


    // ========== FLUENT SETTERS ==========

    public Paging pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Paging pageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    // ========== GETTER & SETTER ==========

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getFirstResult() {
        if (Objects.isNull(pageNumber)) {
            throw new IllegalArgumentException("getFirstResult() invoked while pageNumber is NULL");
        }
        else if (Objects.isNull(pageSize)) {
            throw new IllegalArgumentException("getFirstResult() invoked while pageSize is NULL");
        }
        return (pageNumber - 1) * pageSize;
    }
}
