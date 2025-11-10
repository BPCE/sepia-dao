package com.natixis.sepia.dao.search;

import jakarta.persistence.criteria.JoinType;

import java.util.Objects;
import java.util.StringJoiner;

import static jakarta.persistence.criteria.JoinType.LEFT;

public class Join {

    private JoinType type;
    private String alias;
    private String path;
    private boolean mustFetch;

    // ============== FLUENT SETTERS ==============

    public Join alias(String alias) {
        this.alias = alias;
        return this;
    }

    public Join path(String path) {
        this.path = path;
        return this;
    }

    public Join type(JoinType joinType) {
        this.type = joinType;
        return this;
    }

    public Join mustFetch(boolean mustFetch) {
        this.mustFetch = mustFetch;
        return this;
    }

    public String getJoinClause() {
        return generateJoinClause(mustFetch);
    }

    private String generateJoinClause(boolean withFetch) {
        StringJoiner clause = new StringJoiner(" ", " ", " ");
        if (type == LEFT)
            clause.add("left");
        clause.add("join");
        if (withFetch)
            clause.add("fetch");
        clause.add(path).add(alias);
        return clause.toString();
    }

    public String getJoinClauseWithoutFetch() {
        return generateJoinClause(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Join join)) return false;
        return Objects.equals(getAlias(), join.getAlias());
    }

    public String getAlias() {
        return alias;
    }

    // ============ GETTERS AND SETTERS ============

    @Override
    public int hashCode() {
        return Objects.hashCode(getAlias());
    }

    public JoinType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public boolean isMustFetch() {
        return mustFetch;
    }
}
