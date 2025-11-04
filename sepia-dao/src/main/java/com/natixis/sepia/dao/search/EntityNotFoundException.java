package com.natixis.sepia.dao.search;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class EntityNotFoundException extends RuntimeException {

    private final Serializable id;
    private final String entityName;

    public EntityNotFoundException(Serializable id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this)
                .append("id", id)
                .append("entityName", entityName)
                .toString();
    }
}
