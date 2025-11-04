package com.natixis.sepia.dao.search.domain;

import com.natixis.sepia.service.codifs.Codifiable;

public enum GraphType implements Codifiable {
    LOAD_GRAPH("jakarta.persistence.loadgraph"),
    FETCH_GRAPH("jakarta.persistence.fetchgraph");

    private final String code;

    GraphType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
