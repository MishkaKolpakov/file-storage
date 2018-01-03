package ua.softserve.academy.kv030.authservice.dao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.Permission;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.DaoLayerException;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Handles CRUD operations on Resource entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class ResourceDAOImpl extends CrudDAOImpl<Resource> implements ResourceDAO {

    @Autowired
    public Logger logger;

    public ResourceDAOImpl() {
        super(Resource.class);
    }

    @Override
    public Optional<Resource> findElementByUUID(String uuid) {
        return findOneByFieldEqual("linkToFile", uuid);
    }

    @Override
    public Boolean containsUUID(String uuid) {
        return containsFieldEqual("linkToFile", uuid);
    }

    @Override
    public Optional<Resource> findElementBySecretKeyId(long keyId) {
        return findOneByFieldEqual("secretKey", keyId);
    }

    @Override
    public List<Resource> findAllElementsByOffsetByPageSizeByIdSort(int offset, int pageSize, SortingOrderEnum order) {
        return findAllByOffsetByPageSizeSortedByField(offset, pageSize, "resourceId", order);
    }

    @Override
    public List<Resource> findAllElementsByCriteria(ResourceFilterCriteria criteria) {
        logger.info(criteria.toString());
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Resource> criteriaQuery = criteriaBuilder.createQuery(Resource.class);
        Root<Resource> elementRoot = criteriaQuery.from(Resource.class);
        CriteriaQuery<Resource> select = criteriaQuery.select(elementRoot);

        List<Predicate> predicates = new ArrayList<>();

        if(criteria.getFileType() != null && criteria.getUserId() != null ) {
            switch (criteria.getFileType()) {
                case OWN:
                    predicates.add(criteriaBuilder.equal(elementRoot.get("owner"), criteria.getUserId()));
                    break;
                case SHARED:

                    Join<Resource, Permission> joinPermission = elementRoot.join("permission", JoinType.LEFT);
                    Join<Resource, User> joinUsers = elementRoot.join("users", JoinType.LEFT);

                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(joinPermission.get("permission"), "ALL_USERS"),
                                    criteriaBuilder.notEqual(elementRoot.get("owner"), criteria.getUserId())
                            ),
                            criteriaBuilder.equal(joinUsers.get("userId"), criteria.getUserId())
                    ));
                    break;
            }
        }

        if(criteria.getPermissionType() != null) {
            Join<Resource, Permission> joinPermission = elementRoot.join("permission", JoinType.LEFT);
            switch (criteria.getPermissionType()) {
                case NOT_SET:
                    predicates.add(criteriaBuilder.isNull(elementRoot.get("permission")));
                    break;
                case ALL_USERS:
                    predicates.add(criteriaBuilder.equal(joinPermission.get("permission"), "ALL_USERS"));
                    break;
                case LIST_OF_USERS:
                    predicates.add(criteriaBuilder.equal(joinPermission.get("permission"), "LIST_OF_USERS"));
                    break;
            }
        }

        if( !predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        if(criteria.getOrder() != null) {
            String fieldToSortBy = "resourceId";
            if(criteria.getFieldToSortBy() != null) {
                try {
                    Resource.class.getDeclaredField(criteria.getFieldToSortBy());
                    fieldToSortBy = criteria.getFieldToSortBy();
                } catch (NoSuchFieldException e) {
                    logger.warn(String.format("There is no '%s' field in Resource class. Sort will be done by 'resourceId' field of Resource class", criteria.getFieldToSortBy()));
                }
            }
            switch(criteria.getOrder()) {
                case ASC:
                    criteriaQuery.orderBy(criteriaBuilder.asc(elementRoot.get(fieldToSortBy)));
                    break;
                case DESC:
                    criteriaQuery.orderBy(criteriaBuilder.desc(elementRoot.get(fieldToSortBy)));
                    break;
            }
        }

        TypedQuery<Resource> typedQuery = manager.createQuery(select);
        if(criteria.getOffset() != null) typedQuery.setFirstResult(criteria.getOffset());
        if(criteria.getLimit() != null) typedQuery.setMaxResults(criteria.getLimit());

        try {
            List<Resource> resources = typedQuery.getResultList();
            return (resources == null) ? Collections.emptyList() : resources;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoLayerException(ex.getMessage());
        }
    }
}
