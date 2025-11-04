package com.natixis.sepia.dao.search;

import com.natixis.sepia.dao.search.criteria.GenericCriteria;
import com.natixis.sepia.dao.search.criteria.IFetching;
import com.natixis.sepia.dao.search.criteria.Paging;
import com.natixis.sepia.dao.search.criteria.Sorting;
import com.natixis.sepia.dao.search.domain.GraphType;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * This Dao is used to provide generic search and count method.
 * <p>
 * -- About Paging and PageSize.
 * In the findByCriteria method, if pageSize in the criteria
 * - is not defined, then a default value is set -> DEFAULT_PAGE_SIZE
 * - is defined but with a too big value, then a maximum value will be set  -> MAX_PAGE_SIZE
 * These values are overridable by any class extending SearchDao (through their getters).
 * <p>
 * Generic Types !
 *
 * @param <E> : Entity class
 * @param <C> : Criteria type
 * @param <F> : Fetching type
 */
public abstract class SearchDao<E, C extends GenericCriteria<F>, F extends IFetching> {

    // ========== ATTRIBUTES ==========

    @PersistenceContext
    protected EntityManager entityManager;

    private static final String FROM = "from ";

    /**
     * Default values for pagination
     */
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_MAX_PAGE_SIZE = 100;

    /**
     * Class of the Entity we are querying
     */
    private final Class<E> entityClass;

    // ========== CONSTRUCTOR ==========

    protected SearchDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    // ========== METHODS FOR Services ==========

    /**
     * @param criteria : functional criteria
     * @return all the records matching the criteria (including technical : paging, fetching and sorting).
     */
    public List<E> search(C criteria) {

        SearchContext<C, F> context = new SearchContext<>();
        context.setCriteria(criteria);

        // ========== FILTERING ==========
        filter(context);

        // ========== FETCHING ==========

        EntityGraph<E> graph = entityManager.createEntityGraph(entityClass);
        if (criteria.getFetching() != null) {
            fetch(graph, criteria.getFetching());
        }

        // ========== SORTING ==========
        String orderBy = "";
        if (criteria.getOrdering() != null) {
            orderBy = sort(criteria.getOrdering());
        }

        // ========== QUERY ==========
        String jpql = "select " + distinct(context) + " " + getAliasName() + " "
                + FROM + getEntityName() + " " + getAliasName() + " "
                + join(context)
                + where(context)
                + orderBy;

        TypedQuery<E> query = entityManager.createQuery(jpql, entityClass);
        if (graph != null && criteria.getFetching() != null) {
            query.setHint(this.getGraphType().getCode(), graph);
        }
        fillParameters(query, context.getParams());

        // ========== PAGING ==========
        if (criteria.getPaging() != null) {
            query.setFirstResult(criteria.getPaging().getFirstResult());
            query.setMaxResults(Math.min(criteria.getPaging().getPageSize(), getMaxPageSize()));
        }

        // ========== LOADING ==========
        List<E> list = query.getResultList();
        if (criteria.getFetching() != null && !list.isEmpty()) {
            // clear context joins
            context.clearJoins();
            fetchSublists(list, criteria.getFetching());
        }

        return list;
    }

    /**
     *
     * @param criteria : functional criteria
     * @return an Optional containing the first record that is found while matching the criteria (used in case we filter with functional criteria rather than the ID and it's supposed to return 0 or 1 result)
     */
    public Optional<E> searchOne(C criteria) {
        // saves initial state
        Paging paging = criteria.getPaging();
        List<Sorting> ordering = criteria.getOrdering();
        // overrides
        criteria.setPaging(new Paging().pageNumber(1).pageSize(1));
        criteria.orderBy();
        // searches
        Optional<E> singleResult = search(criteria).stream().findFirst();
        // reverts initial state
        criteria.setPaging(paging);
        criteria.orderBy(ordering);
        return singleResult;
    }

    /**
     * @param criteria : functional criteria
     * @return the total number of records matching with the criteria
     */
    public Long count(C criteria) {

        SearchContext<C, F> context = new SearchContext<>();
        context.setCriteria(criteria);

        // ========== FILTERING ==========
        filter(context);

        // ========== QUERY ==========
        String jpql = "select count(distinct " + getCountSelectStatement() + ") "
                + FROM + getEntityName() + " " + getAliasName() + " "
                + join(context)
                + where(context);

        // ========== EXECUTING ==========
        Query query = entityManager.createQuery(jpql);
        fillParameters(query, context.getParams());
        return (Long) query.getSingleResult();
    }

    /**
     * @param criteria : functional criteria
     * @return true if there is at least one existing record that matches the criteria, else false
     */
    public boolean exists(C criteria) {// saves initial state
        Paging paging = criteria.getPaging();
        F fetching = criteria.getFetching();
        List<Sorting> ordering = criteria.getOrdering();
        // overrides
        criteria.setPaging(new Paging().pageNumber(1).pageSize(1));
        criteria.setFetching(null);
        criteria.orderBy();
        // executes
        boolean exists = !search(criteria).isEmpty();
        // reverts initial state
        criteria.setPaging(paging);
        criteria.setFetching(fetching);
        criteria.orderBy(ordering);
        return exists;
    }

    /**
     * @param criteria : functional criteria
     * @return true if there is no existing record that matches the criteria, else false
     */
    public boolean notExist(C criteria) {
        return !exists(criteria);
    }

    public E find(Serializable id) {
        E entity = entityManager.find(getEntityClass(), id);
        if (isNull(entity)) {
            throw new EntityNotFoundException(id, getEntityName());
        }
        return entity;
    }

    public E find(Serializable id, F fetching) {
        EntityGraph<E> graph = entityManager.createEntityGraph(getEntityClass());
        if (fetching != null) {
            fetch(graph, fetching);
        }
        Map<String, Object> props = Map.of(getGraphType().getCode(), graph);

        E entity = entityManager.find(getEntityClass(), id, props);
        if (isNull(entity)) {
           throw new EntityNotFoundException(id, getEntityName());
        }

        if (fetching != null) {
            fetchSublists(List.of(entity), fetching);
        }
        return entity;

    }


    // ========== To be implemented / overridden ==========

    protected abstract void filter(SearchContext<C, F> context);

    /**
     * Provides a default sorting that can be overriden by the Dao.
     *
     * @param sorting property to sort by
     * @return a to be used in the order by clause of the database query
     */
    protected String sort(Sorting sorting) {

        if (sorting.getProperty() != null) {
            return getAliasName() + "." + sorting.getProperty().trim() + " " + sorting.getDirection();
        }
        return "";
    }

    /**
     * Provides a default sorting by a list of properties.
     *
     * @param ordering properties to sort by
     * @return a string with the "order by" clause to be used in the database query
     */
    private String sort(List<Sorting> ordering) {
        if (ordering != null) {
            final String orderBy = ordering
                    .stream()
                    .map(this::sort)
                    .collect(Collectors.joining(", "));

            if (StringUtils.isNotEmpty(orderBy)) {
                return "order by " + orderBy;
            }
        }
        return "";
    }

    /**
     * Fills the EntityGraph that will fetch all mentioned attributes of the entity.
     * It can also include (maximum) one sublist.
     * If the child DAO wants to fetch several sublists, then it must implement fetchSublists();
     *
     * @param graph    : the graph that will be used by the query to fetch the data
     * @param fetching : contains data to be fetched
     */
    protected abstract void fetch(EntityGraph<E> graph, F fetching);

    /**
     * This method is invoked by search() after the result list has been loaded from the database.
     * It is used to load several sublists of the entities contained in the result list.
     *
     * @param list     : result list from the query using the conditions and parameters
     * @param fetching : contains data to be fetched
     */
    protected abstract void fetchSublists(List<E> list, F fetching);

    /**
     * This method allows to fetch a subgraph of all entities contained in the list provided in parameter.
     *
     * @param list  : result list from the 1st query executed, considering the conditions set by the child Dao
     * @param graph : EntityGraph that will fetch a sublist of all entities contained in the list.
     */
    protected void fetchSublist(List<E> list, EntityGraph<E> graph) {
        fetchSublist(list, graph, entityClass);
    }


    /**
     * This method allows to fetch a subgraph of all entities contained in the list provided in parameter.
     *
     * @param list  : result list from the 1st query executed, considering the conditions set by the child Dao
     * @param graph : EntityGraph that will fetch a sublist of all entities contained in the list.
     * @param clazz : the root class of the graph
     */
    protected <S> void fetchSublist(List<S> list, EntityGraph<S> graph, Class<S> clazz) {
        String jpql = "select e from " + clazz.getSimpleName() + " e where e in :list ";
        TypedQuery<S> query = entityManager.createQuery(jpql, clazz);
        if (graph != null) {
            query.setHint(getGraphType().getCode(), graph);
        }
        query.setParameter("list", list);
        query.getResultList();
    }

    /**
     * This method allows to fetch a set of lists using join fetch into all entities contained in the list provided in parameter.
     *
     * @param list    : result list from the 1st query executed, considering the conditions set by the child Dao
     * @param context : SearchContext containing the list of fetchs to be done.
     */
    protected void fetchSublist(List<E> list, SearchContext<C, F> context) {
        String jpql = "select " + distinct(context) + " " + getAliasName() + " "
                + FROM + getEntityName() + " " + getAliasName() + " "
                + join(context)
                + "where " + getAliasName() + " in :list ";
        TypedQuery<E> query = entityManager.createQuery(jpql, entityClass);
        query.setParameter("list", list);
        query.getResultList();

        // clear context joins
        context.clearJoins();
    }

    /**
     * Default implementation uses AND operator.
     * It can be overridden by the child Dao.
     *
     * @return the operator to use (AND or OR)
     */
    protected Operator getOperator() {
        return Operator.AND;
    }

    protected int getPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getMaxPageSize() {
        return DEFAULT_MAX_PAGE_SIZE;
    }

    // ========== UTILS ==========

    protected String getEntityName() {
        return this.entityClass.getSimpleName();
    }

    protected Class<E> getEntityClass() {
        return this.entityClass;
    }

    protected String getAliasName() {
        return StringUtils.uncapitalize(getEntityName());
    }

    protected String getCountSelectStatement() {
        return getAliasName();
    }

    /**
     * Fills the parameters managed/provided by another class into the query
     */
    private void fillParameters(Query query, Map<String, QueryParameter> params) {
        for (QueryParameter param : params.values()) {
            if (param.hasDateType()) {
                query.setParameter(param.alias(), (Date) param.value(), param.dateType());
            } else {
                query.setParameter(param.alias(), param.value());
            }
        }
    }

    // ========= JPQL Generator methods ==========

    /**
     * Generates the join clause of the jpql query
     */
    private String join(SearchContext<C, F> context) {
        StringBuilder jpql = new StringBuilder();
        for (String alias : context.getJoinsAlias()) {
            String path = context.getJoins().get(alias);
            jpql.append("join ").append(path).append(" ").append(alias).append(" ");
        }
        for (String alias : context.getLeftJoinsAlias()) {
            String path = context.getLeftJoins().get(alias);
            jpql.append("left join ").append(path).append(" ").append(alias).append(" ");
        }
        for (String alias : context.getJoinsToFetchAlias()) {
            String path = context.getJoinsToFetch().get(alias);
            jpql.append("left join fetch ").append(path).append(" ").append(alias).append(" ");
        }
        return jpql.toString();
    }

    /**
     * @return "distinct" if there are joins to make with other tables.
     */
    private String distinct(SearchContext<C, F> context) {
        if (context.getJoins().isEmpty()) {
            return "";
        } else {
            return " distinct ";
        }
    }

    /**
     * Generates the where clause of the jpql query
     */
    private String where(SearchContext<C, F> context) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("where ");
        jpql.append(getOperator() == Operator.AND ? "1=1 " : "1=0 ");
        if (context.getConditions() != null) {
            for (String condition : context.getConditions()) {
                jpql.append(operator()).append(" ").append(condition).append(" ");
            }
        }
        return jpql.toString();
    }

    /**
     * @return "and " if the operator is AND, else "or "
     */
    private String operator() {
        return getOperator() == Operator.AND ? " and " : " or ";
    }


    /**
     * Get the type of graph used when loading relationships
     * If you want to specify the type of graph used, you should override this method
     *
     * @return - the Graph Type
     */
    protected GraphType getGraphType() {
        return GraphType.LOAD_GRAPH;
    }

}
