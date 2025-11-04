package com.natixis.sepia.dao.search;

import jakarta.persistence.TemporalType;

import static java.util.Objects.nonNull;

public record QueryParameter(String alias, Object value, TemporalType dateType) {

    public boolean hasDateType() {
        return nonNull(dateType);
    }
}
