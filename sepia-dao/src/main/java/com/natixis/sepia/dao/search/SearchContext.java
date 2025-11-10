package com.natixis.sepia.dao.search;

import com.natixis.sepia.dao.search.criteria.GenericCriteria;
import com.natixis.sepia.dao.search.criteria.IFetching;
import com.natixis.sepia.dao.search.domain.StringSearch;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.*;

import static jakarta.persistence.criteria.JoinType.INNER;
import static jakarta.persistence.criteria.JoinType.LEFT;

public class SearchContext<C extends GenericCriteria<F>, F extends IFetching> {

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

    public SearchContext() {
        this.joins = new ArrayList<>();
        this.params = new HashMap<>();
        this.conditions = new ArrayList<>();
    }

    public SearchContext(C criteria) {
        this();
        this.criteria = criteria;
    }

    // ========== METHODS for Joins ==========

    /**
     * Adds one path to be added on the clause, with its alias
     */
    public void join(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get().type(INNER);
        } else {
            addJoin(INNER, alias, path);
        }
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

    public void joinFetch(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get()
                    .type(INNER)
                    .mustFetch(true);
        } else {
            addJoin(INNER, alias, path, true);
        }
    }

    public void leftJoin(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isEmpty()) {
            addJoin(LEFT, alias, path);
        }
    }

    public void leftJoinFetch(String alias, String path) {
        Optional<Join> join = getJoin(alias);
        if (join.isPresent()) {
            join.get()
                    .mustFetch(true);
        } else {
            addJoin(LEFT, alias, path, true);
        }
    }

    protected void clearJoins() {
        this.getJoins().clear();
    }

    // ========== METHODS for Conditions ==========

    /**
     * Adds one condition to be added to the where part of the query
     *
     * @param condition : condition to be added (not beginning by an operator, directly after)
     */
    public void addCondition(String condition) {
        this.getConditions().add(condition);
    }

    /** Adds one condition on a string based on the fact the search is exact or not (using StringSearch)
     *
     * @param path       : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias : alias of the parameter in the query (ex: "stadiumName")
     * @param stringSearch : object containing the string to search and indicating if the search is exact or not
     */
    public void addStringCondition(String path, String paramAlias, StringSearch stringSearch) {
        if (stringSearch != null && !StringUtils.isEmpty(stringSearch.getValue())) {
            if (stringSearch.getIsExact()) {
                addCondition(path + " = :" + paramAlias);
                addParameter(paramAlias, stringSearch.getValue());
            } else {
                addLikeCondition(path, paramAlias, stringSearch.getValue());
            }
        }
    }

    /**
     * Adds one condition (that uses like operator) to be added to the where part of the query.
     * The like will :
     * - ignore special characters
     * - be case insensitive
     *
     * @param path       : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias : alias of the parameter in the query (ex: "stadiumName")
     * @param paramValue : value of the parameter in the query (ex: "olYmPi")
     * @param isCaseSensitive : indicates if the match should be case sensitive (e != E)
     * @param isAccentSensitive : indicates if the match should be accent sensitive (e != é)
     */
    public void addLikeCondition(String path, String paramAlias, String paramValue, boolean isCaseSensitive, boolean isAccentSensitive, boolean startsWith, boolean endsWith) {
        String expression = path;
        // if we must ignore the accents, we transforme the accented characters into non-accented characters
        if (!isAccentSensitive) {
            expression = "translate(" + expression + ", 'àâéèêìîòôùûÿ', 'aaeeeiioouuy')";
        }
        // if we must ignore case, we transform charcters in uppercases
        if (!isCaseSensitive) {
            expression = "upper(" + expression + ")";
        }
        addCondition(expression + " like :" + paramAlias + " escape '/' ");
        addParameter(paramAlias, like(paramValue, isCaseSensitive, isAccentSensitive, startsWith, endsWith));
    }

    /**
     * Adds one condition (that uses like operator) to be added to the where part of the query.
     * The like will be :
     * - case insensitive
     * - accent sensitive.
     *
     * @param path       : path of the entity to be tested (ex: "club.stadium.name")
     * @param paramAlias : alias of the parameter in the query (ex: "stadiumName")
     * @param paramValue : value of the parameter in the query (ex: "olYmPi")
     */
    public void addLikeCondition(String path, String paramAlias, String paramValue) {
        addLikeCondition(path, paramAlias, paramValue, false, true, true, true);
    }

    public String like(String value, boolean isCaseSensitive, boolean isAccentSensitive, boolean startsWith, boolean endsWith) {
        String formatted = value;
        // remove useless white spaces
        formatted = StringUtils.trim(formatted);
        if (!isCaseSensitive) {
            // use uppercase method
            formatted = StringUtils.upperCase(formatted);
        }
        if (!isAccentSensitive) {
            // accents removal
            formatted = Normalizer.normalize(formatted, Normalizer.Form.NFD).replaceAll("\\p{IsM}+", "");
        }
        // escape underscore
        formatted = formatted.replace("_", "/_");
        if (startsWith) {
            formatted = "%" + formatted;
        }
        if (endsWith) {
            formatted += "%";
        }
        return formatted;
    }

    // ========== METHODS for Params ==========

    /**
     * Adds one parameter to be added on the query, with its alias
     */
    public void addParameter(String alias, Object value) {
        if (value instanceof Date) {
            params.put(alias, new QueryParameter(alias, value, TemporalType.DATE) );
        } else {
            params.put(alias, new QueryParameter(alias, value, null));
        }
    }

    public void addParameter(String alias, Object value, TemporalType type) {
        params.put(alias, new QueryParameter(alias, value, type));
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
