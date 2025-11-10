package fr.groupebpce.sepia.dao;

import fr.groupebpce.sepia.criteria.GenericCriteria;
import fr.groupebpce.sepia.criteria.fetching.IFetching;
import fr.groupebpce.sepia.criteria.paging.Paging;
import fr.groupebpce.sepia.criteria.sorting.Sorting;
import fr.groupebpce.sepia.dao.domain.GraphType;
import fr.groupebpce.sepia.dao.domain.Join;
import fr.groupebpce.sepia.dao.domain.Operator;
import fr.groupebpce.sepia.dao.domain.QueryParameter;
import fr.groupebpce.sepia.exception.EntityNotFoundException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

import static fr.groupebpce.sepia.dao.domain.GraphType.LOAD_GRAPH;
import static fr.groupebpce.sepia.dao.domain.Operator.AND;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

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
public abstract class QueryDao<E, C extends GenericCriteria<F>, F extends IFetching> {

    // ========== ATTRIBUTES ==========

    /**
     * Default values for pagination
     */
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_MAX_PAGE_SIZE = 100;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Class of the Entity we are querying
     */
    private final Class<E> entityClass;

    // ========== CONSTRUCTOR ==========

    protected QueryDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    // ========== METHODS FOR Services ==========

    /**
     * @param criteria : functional criteria
     * @return all the records matching the criteria (including technical : paging, fetching and sorting).
     */
    public List<E> search(C criteria) {

        QueryContext<C, F> context = generateContext(criteria);

        // ========== FILTERING ==========
        filter(context);

        // ========== FETCHING ==========
        EntityGraph<E> graph = generateGraph(criteria.getFetching());

        // ========== QUERY ==========

        String jpql = new StringJoiner(" ")
                .add(select())
                .add(from())
                .add(join(context))
                .add(where(context))
                .add(orderBy(criteria))
                .toString();

        TypedQuery<E> query = generateQuery(criteria, jpql, graph);
        applyQueryParameters(query, context.getParams());

        // ========== PAGING ==========
        applyPaginationParameters(criteria.getPaging(), query);

        // ========== LOADING ==========
        List<E> resultList = query.getResultList();
        if (nonNull(criteria.getFetching()) && isNotEmpty(resultList)) {
            context.clearJoins();
            fetchSublists(resultList, criteria.getFetching());
        }

        return resultList;
    }

    /**
     * @param criteria : functional criteria
     * @return an Optional containing the first record that is found while matching the criteria (used in case we filter with functional criteria rather than the ID and it's supposed to return 0 or 1 result)
     */
    public Optional<E> searchOne(C criteria) {
        // saves initial state
        Paging paging = criteria.getPaging();
        // overrides
        criteria.setPaging(new Paging().pageNumber(1).pageSize(1));
        // searches
        Optional<E> singleResult = search(criteria).stream().findFirst();
        // reverts initial state
        criteria.setPaging(paging);
        return singleResult;
    }

    /**
     * @param criteria : functional criteria
     * @return the total number of records matching with the criteria
     */
    public Long count(C criteria) {

        QueryContext<C, F> context = generateContext(criteria);

        // ========== FILTERING ==========
        filter(context);

        // ========== QUERY ==========
        String jpql = new StringJoiner(" ")
                .add("select count(distinct").add(getAliasName()).add(")")
                .add(from())
                .add(joinWithoutFetch(context))
                .add(where(context))
                .toString();

        // ========== EXECUTING ==========
        Query query = entityManager.createQuery(jpql);
        applyQueryParameters(query, context.getParams());
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
        Map<String, Object> props = Map.of(getGraphType().getCode(), generateGraph(fetching));

        E entity = entityManager.find(getEntityClass(), id, props);
        if (isNull(entity)) {
            throw new EntityNotFoundException(id, getEntityName());
        }

        if (nonNull(fetching)) {
            fetchSublists(List.of(entity), fetching);
        }
        return entity;

    }


    // ========== To be implemented / overridden ==========

    protected abstract void filter(QueryContext<C, F> context);

    /**
     * Fills the EntityGraph that will fetch all mentioned attributes of the entity.
     * It can also include (maximum) one sublist.
     * If the child DAO wants to fetch several sublists, then it must implement fetchSublists();
     *
     * @param graph    : the graph that will be used by the query to fetch the data
     * @param fetching : contains data to be fetched
     */
    protected void fetch(EntityGraph<E> graph, F fetching) {
    }

    /**
     * This method is invoked by search() after the result list has been loaded from the database.
     * It is used to load several sublists of the entities contained in the result list.
     *
     * @param list     : result list from the query using the conditions and parameters
     * @param fetching : contains data to be fetched
     */
    protected void fetchSublists(List<E> list, F fetching) {
    }

    /**
     * This method allows to fetch a subgraph of all entities contained in the list provided in parameter.
     *
     * @param list  : result list from the 1st query executed, considering the conditions set by the child Dao
     * @param graph : EntityGraph that will fetch a sublist of all entities contained in the list.
     */
    protected void fetchSublist(List<E> list, EntityGraph<E> graph) {
        fetchSublist(list, graph, getEntityClass());
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
    protected void fetchSublist(List<E> list, QueryContext<C, F> context) {
        String jpql = new StringJoiner(" ")
                .add(select())
                .add(from())
                .add(join(context))
                .add("where").add(getAliasName()).add("in :list")
                .toString();
        entityManager.createQuery(jpql, getEntityClass())
                .setParameter("list", list)
                .getResultList();

        // clear context joins
        context.clearJoins();
    }

    private static <C extends GenericCriteria<F>, F extends IFetching> QueryContext<C, F> generateContext(C criteria) {
        QueryContext<C, F> context = new QueryContext<>();
        context.setCriteria(criteria);
        return context;
    }

    private EntityGraph<E> generateGraph(F fetching) {
        EntityGraph<E> graph = entityManager.createEntityGraph(getEntityClass());
        if (nonNull(fetching)) {
            fetch(graph, fetching);
        }
        return graph;
    }

    private TypedQuery<E> generateQuery(C criteria, String jpql, EntityGraph<E> graph) {
        TypedQuery<E> query = entityManager.createQuery(jpql, getEntityClass());
        if (nonNull(graph) && nonNull(criteria.getFetching())) {
            query.setHint(getGraphType().getCode(), graph);
        }
        return query;
    }

    /**
     * Fills the parameters managed/provided by another class into the query
     */
    private void applyQueryParameters(Query query, Map<String, QueryParameter> params) {
        for (QueryParameter param : params.values()) {
            if (param.hasDateType()) {
                query.setParameter(param.alias(), (Date) param.value(), param.dateType());
            } else {
                query.setParameter(param.alias(), param.value());
            }
        }
    }

    private void applyPaginationParameters(Paging paging, TypedQuery<E> query) {
        if (nonNull(paging)) {
            query.setFirstResult(paging.getFirstResult());
            query.setMaxResults(Math.min(paging.getPageSize(), getMaxPageSize()));
        }
    }

    // ========= JPQL Generator methods ==========


    /**
     * Generates the SELECT clause of the jpql query
     */
    private String select() {
        return new StringJoiner(" ")
                .add("SELECT")
                .add(getAliasName())
                .toString();
    }

    /**
     * Generates the FROM clause of the jpql query
     */
    private String from() {
        return new StringJoiner(" ")
                .add("FROM")
                .add(getEntityName())
                .add(getAliasName())
                .toString();
    }

    /**
     * Generates the JOIN clause of the jpql query
     */
    private String join(QueryContext<C, F> context) {
        return context.getJoins().stream()
                .map(Join::getJoinClause)
                .collect(joining());
    }

    /**
     * Generates the JOIN clause of the jpql query (ignor
     */
    private String joinWithoutFetch(QueryContext<C, F> context) {
        return context.getJoins().stream()
                .map(Join::getJoinClauseWithoutFetch)
                .collect(joining());
    }

    /**
     * Generates the WHERE clause of the jpql query
     */
    private String where(QueryContext<C, F> context) {
        StringJoiner jpql = new StringJoiner(" ")
                .add("WHERE").add(getOperator().trueConditionTrick());
        context.getConditions().forEach(condition -> jpql.add(getOperator().name()).add(condition));
        return jpql.toString();
    }

    /**
     * Generates the ORDER BY clause of the jpql query
     */
    private String orderBy(C criteria) {
        if (isNotEmpty(criteria.getOrdering())) {
            String orderBy = sort(criteria.getOrdering());
            if (isNotBlank(orderBy))
                return "ORDER BY " + orderBy;
        }
        return "";
    }

    /**
     * Provides a default sorting by a list of properties.
     *
     * @param sortings contains the properties to sort by
     * @return a string with the "order by" clause to be used in the database query
     */
    private String sort(List<Sorting> sortings) {
        if (nonNull(sortings)) {
            final String orderBy = sortings
                    .stream()
                    .map(this::sort)
                    .collect(joining(", "));
            if (isNotEmpty(orderBy)) {
                return orderBy;
            }
        }
        return "";
    }

    // ========== Overridable methods / settings ==========

    /**
     * Provides a default sorting that can be overridden by the Dao.
     *
     * @param sorting property to sort by
     * @return a part of  to be used in the order by clause of the database query
     */
    protected String sort(Sorting sorting) {
        if (isNotEmpty(sorting.getProperty())) {
            return "{alias}.{property} {direction}"
                    .replace("{alias}", getAliasName())
                    .replace("{property}", sorting.getProperty().trim())
                    .replace("{direction}", sorting.getDirection().toString());
        }
        return "";
    }

    /**
     * Get the type of graph used when loading relationships
     * If you want to specify the type of graph used, you should override this method
     *
     * @return - the Graph Type
     */
    protected GraphType getGraphType() {
        return LOAD_GRAPH;
    }

    /**
     * Default implementation uses AND operator.
     * It can be overridden by the child Dao.
     *
     * @return the operator to use (AND or OR)
     */
    protected Operator getOperator() {
        return AND;
    }

    protected int getPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getMaxPageSize() {
        return DEFAULT_MAX_PAGE_SIZE;
    }

    // ========== UTILS ==========

    protected String getEntityName() {
        return getEntityClass().getSimpleName();
    }

    protected Class<E> getEntityClass() {
        return this.entityClass;
    }

    protected String getAliasName() {
        return uncapitalize(getEntityName());
    }
}
