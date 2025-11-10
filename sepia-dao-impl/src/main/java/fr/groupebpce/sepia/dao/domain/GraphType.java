package fr.groupebpce.sepia.dao.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GraphType {

    LOAD_GRAPH("jakarta.persistence.loadgraph"),
    FETCH_GRAPH("jakarta.persistence.fetchgraph");

    private final String code;

}
