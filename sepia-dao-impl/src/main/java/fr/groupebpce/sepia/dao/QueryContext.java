package fr.groupebpce.sepia.dao;

import fr.groupebpce.sepia.criteria.GenericCriteria;
import fr.groupebpce.sepia.criteria.fetching.IFetching;
import fr.groupebpce.sepia.criteria.filter.StringFilter;
import fr.groupebpce.sepia.dao.domain.Join;
import fr.groupebpce.sepia.dao.domain.QueryParameter;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static jakarta.persistence.criteria.JoinType.INNER;
import static jakarta.persistence.criteria.JoinType.LEFT;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class QueryContext<C extends GenericCriteria<F>, F extends IFetching> {

    // ========== ATTRIBUTES ==========

    private C criteria;

    private final List<Join> joins;

    /**
     * Conditions to put in the where clause of the query
     */
    private final List<String> conditions;

    /**
     * Parameters to set into the query
     */
    private final Map<String, QueryParameter> params;

    // ========== CONSTRUCTOR ==========

    public QueryContext() {
        this.joins = new ArrayList<>();
        this.params = new HashMap<>();
        this.conditions = new ArrayList<>();
    }

    public QueryContext(C criteria) {
        this();
        this.criteria = criteria;
    }

    // ========== METHODS for Joins ==========

    /**
     * Adds one path to be added on the clause, with its alias
     */
    public QueryContext<C, F> addJoin(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get().type(INNER);
        } else {
            addJoin(INNER, alias, path);
        }
        return this;
    }

    public QueryContext<C, F> addJoinFetch(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get()
                    .type(INNER)
                    .mustFetch(true);
        } else {
            addJoin(INNER, alias, path, true);
        }
        return this;
    }

    public QueryContext<C, F> addLeftJoin(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isEmpty()) {
            addJoin(LEFT, alias, path);
        }
        return this;
    }

    public QueryContext<C, F> addLeftJoinFetch(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get()
                    .mustFetch(true);
        } else {
            addJoin(LEFT, alias, path, true);
        }
        return this;
    }

    private Optional<Join> getJoin(String alias) {
        return joins.stream().filter(j -> j.getAlias().equals(alias)).findFirst();
    }

    private void addJoin(JoinType type, String alias, String path) {
        addJoin(type, alias, path, false);
    }

    private void addJoin(JoinType type, String alias, String path, boolean mustFetch) {
        joins.add(new Join()
                .type(type)
                .alias(alias)
                .path(path)
                .mustFetch(mustFetch)
        );
    }

    protected void clearJoins() {
        getJoins().clear();
    }

    // ========== METHODS for Conditions ==========

    /**
     * Adds one condition to be added to the where part of the query
     *
     * @param condition : condition to be added (not beginning by an operator, directly after)
     */
    public QueryContext<C, F> addCondition(String condition) {
        getConditions().add(condition);
        return this;
    }

    /**
     * Adds one condition on a string based on the fact the search is exact or not (using StringSearch)
     *
     * @param path         : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias   : alias of the parameter in the query (ex: "stadiumName")
     * @param stringFilter : object containing the string to search and indicating if the search is exact or not
     */
    public QueryContext<C, F> addStringCondition(String path, String paramAlias, StringFilter stringFilter) {
        if (nonNull(stringFilter) && isNotEmpty(stringFilter.getValue())) {
            addCondition("{path} {operator} :{paramAlias}"
                    .replace("{path}", formatUpper(path, stringFilter.getIsIgnoreCase()))
                    .replace("{operator}", stringFilter.getOperator())
                    .replace("{paramAlias}", paramAlias)
            );
            if (isTrue(stringFilter.getIsExact())) {
                addParameter(paramAlias, stringFilter.getValueConsideringCase());
            } else {
                addParameter(paramAlias, formatLike(stringFilter.getValue(), stringFilter.getIsIgnoreCase()));
            }
        }
        return this;
    }

    /**
     * Adds one condition (that uses like operator) to be added to the where part of the query.
     * The like will :
     * - ignore special characters
     * - be case-insensitive
     *
     * @param path       : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias : alias of the parameter in the query (ex: "stadiumName")
     * @param paramValue : par of the parameter in the query (ex: "AreNa")
     */
    public QueryContext<C, F> addLikeCondition(String path, String paramAlias, String paramValue) {
        addLikeCondition(path, paramAlias, paramValue, false);
        return this;
    }
    /**
     * Adds one condition (that uses like operator) to be added to the where part of the query.
     * The like will :
     * - ignore special characters
     * - be case-insensitive
     *
     * @param path       : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias : alias of the parameter in the query (ex: "stadiumName")
     * @param paramValue : par of the parameter in the query (ex: "AreNa")
     */
    public QueryContext<C, F> addLikeCondition(String path, String paramAlias, String paramValue, boolean isIgnoreCase) {
        addCondition(formatUpper(path, isIgnoreCase) + " like :" + paramAlias);
        addParameter(paramAlias, formatLike(paramValue, isIgnoreCase));
        return this;
    }

    private String formatUpper(String path, boolean isIgnoreCase) {
        if (isTrue(isIgnoreCase)) {
            return "upper(" + path + ")";
        }
        return path;
    }

    public String formatLike(String value, boolean isIgnoreCase) {
        String formatted = value;
        // remove useless white spaces
        formatted = StringUtils.trim(formatted);
        if (isIgnoreCase) {
            // use uppercase method
            formatted = StringUtils.upperCase(formatted);
        }
        // escape underscore
//        formatted = formatted.replace("_", "/_");
        formatted = "%" + formatted + "%";
        return formatted;
    }

    // ========== METHODS for Params ==========

    /**
     * Adds one parameter to be added on the query, with its alias
     */
    public QueryContext<C, F> addParameter(String alias, Object value) {
        if (value instanceof Date) {
            params.put(alias, new QueryParameter(alias, value, TemporalType.DATE));
        } else {
            params.put(alias, new QueryParameter(alias, value, null));
        }
        return this;
    }

    public QueryContext<C, F> addParameter(String alias, Object value, TemporalType type) {
        params.put(alias, new QueryParameter(alias, value, type));
        return this;
    }

    // ========== GETTERS & SETTERS =====

    public C getCriteria() {
        return criteria;
    }

    protected void setCriteria(C criteria) {
        this.criteria = criteria;
    }

    protected List<String> getConditions() {
        return conditions;
    }

    protected Map<String, QueryParameter> getParams() {
        return params;
    }

    protected List<Join> getJoins() {
        return joins;
    }
}
