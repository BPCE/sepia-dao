package com.natixis.sepia.dao.search;

import com.natixis.sepia.dao.search.criteria.GenericCriteria;
import com.natixis.sepia.dao.search.criteria.IFetching;
import com.natixis.sepia.dao.search.domain.StringSearch;
import jakarta.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.*;

public class SearchContext<C extends GenericCriteria<F>, F extends IFetching> {

    // ========== ATTRIBUTES ==========

    private C criteria;

    /**
     * Joins to include in the query if there is some filtering on sublists
     */
    private Map<String, String> joins;
    /**
     * This list is made to fix the order of the joins, regarding "join dependencies"
     */
    private Set<String> joinsAlias;

    /**
     * Left joins to include in the query if there is some filtering on sublists
     */
    private Map<String, String> leftJoins;
    /**
     * This list is made to fix the order of the joins, regarding "join dependencies"
     */
    private Set<String> leftJoinsAlias;

    /**
     * Joins fetch to include in the query (when EntityGraph are not possible; ex: graphs don't work on @Embedded fields)
     */
    private Map<String, String> joinsToFetch;
    /**
     * This list is made to fix the order of the joins, regarding "join dependencies"
     */
    private Set<String> joinsToFetchAlias;

    /**
     * Conditions to put in the where clause of the query
     */
    private List<String> conditions;

    /**
     * Parameters to set into the query
     */
    private Map<String, QueryParameter> params;

    // ========== CONSTRUCTOR ==========

    public SearchContext() {
        this.joins = new HashMap<>();
        this.joinsAlias = new HashSet<>();
        this.leftJoins = new HashMap<>();
        this.leftJoinsAlias = new HashSet<>();
        this.joinsToFetch = new HashMap<>();
        this.joinsToFetchAlias = new HashSet<>();
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
    public void addJoin(String alias, String path) {
        this.getJoins().put(alias, path);
        this.getJoinsAlias().add(alias);
    }

    /**
     * Adds one path to be added as left join on the clause, with its alias
     */
    public void addLeftJoin(String alias, String path) {
        this.getLeftJoins().put(alias, path);
        this.getLeftJoinsAlias().add(alias);
    }

    /**
     * Adds one path to be fetched on the clause, with its alias
     */
    public void addJoinFetch(String alias, String path) {
        this.getJoinsToFetch().put(alias, path);
        this.getJoinsToFetchAlias().add(alias);
    }

    protected void clearJoins() {
        this.getJoinsToFetch().clear();
        this.getJoinsToFetchAlias().clear();
        this.getJoins().clear();
        this.getJoinsAlias().clear();
        this.getLeftJoins().clear();
        this.getLeftJoinsAlias().clear();
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

    protected Map<String, String> getJoins() {
        return joins;
    }

    protected Map<String, String> getJoinsToFetch() {
        return joinsToFetch;
    }

    protected Set<String> getJoinsToFetchAlias() {
        return joinsToFetchAlias;
    }

    protected Map<String, String> getLeftJoins() {
        return leftJoins;
    }

    protected Set<String> getJoinsAlias() {
        return joinsAlias;
    }

    protected Set<String> getLeftJoinsAlias() {
        return leftJoinsAlias;
    }
}
