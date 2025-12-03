package fr.groupebpce.sepia.dao.domain;

import jakarta.persistence.criteria.JoinType;
import lombok.Getter;

import java.util.Objects;
import java.util.StringJoiner;

import static jakarta.persistence.criteria.JoinType.LEFT;

@Getter
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

    // ============== UTILS ==============

    public String getJoinClause() {
        return generateJoinClause(mustFetch);
    }

    public String getJoinClauseWithoutFetch() {
        return generateJoinClause(false);
    }

    private String generateJoinClause(boolean withFetch) {
        StringJoiner clause = new StringJoiner(" ", " ", " ");
        if (type == LEFT)
            clause.add("LEFT");
        clause.add("JOIN");
        if (withFetch)
            clause.add("FETCH");
        clause.add(path).add(alias);
        return clause.toString();
    }

    // ============ extends Object ============

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Join join)) return false;
        return Objects.equals(getAlias(), join.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAlias());
    }
}
