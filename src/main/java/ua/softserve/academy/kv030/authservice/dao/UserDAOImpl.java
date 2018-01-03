package ua.softserve.academy.kv030.authservice.dao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.DaoLayerException;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Handles CRUD operations on User entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class UserDAOImpl extends CrudDAOImpl<User> implements UserDAO {

    @Autowired
    public Logger logger;

    public UserDAOImpl() {
        super(User.class);
    }

    @Override
    public List<User> findAllUsersByStatus(boolean status) {
        return findAllByFieldEqual("status", status);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return findOneByFieldEqual("email", email);
    }

    @Override
    public Boolean containsEmail(String email) {
        return containsFieldEqual("email", email);
    }

    @Override
    public List<User> findAllUsersByRoleId(long roleId) {
        return findAllByFieldEqual("role", roleId);
    }

    @Override
    public Optional<User> findUserByPasswordId(long passwordId) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> elementRoot = criteriaQuery.from(User.class);
        Join<User, Password> join = elementRoot.join("passwords");
        criteriaQuery.select(elementRoot).where(criteriaBuilder.equal(join.get("passwordId"), passwordId));

        try {
            User element = manager.createQuery(criteriaQuery).getSingleResult();
            return Optional.of(element);
        } catch (NoResultException e) {
            logger.debug(e.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoLayerException(ex.getMessage());
        }
    }

    @Override
    public List<User> findAllUsersByOffsetByPageSizeByIdSort(int offset, int pageSize, SortingOrderEnum order) {
        return findAllByOffsetByPageSizeSortedByField(offset, pageSize, "userId", order);
    }

    @Override
    public List<User> findAllUsersByCriteria(UsersFilterCriteria criteria) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> elementRoot = criteriaQuery.from(User.class);
        CriteriaQuery<User> select = criteriaQuery.select(elementRoot);

        List<Predicate> predicates = new ArrayList<>();

        if(criteria.getUserStatus() != null) {
            predicates.add(criteriaBuilder.equal(elementRoot.get("status"), criteria.getUserStatus()));
        }

        if(criteria.getRole() != null) {
            Join<User, Role> joinRole = elementRoot.join("role");
            predicates.add(criteriaBuilder.equal(joinRole.get("roleName"), criteria.getRole()));
        }
        if( !predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        if(criteria.getOrder() != null) {
            String fieldToSortBy = "userId";
            if(criteria.getFieldToSortBy() != null) {
                try {
                    User.class.getDeclaredField(criteria.getFieldToSortBy());
                    fieldToSortBy = criteria.getFieldToSortBy();
                } catch (NoSuchFieldException e) {
                    logger.warn(String.format("There is no '%s' field in User class. Sort will be done by 'userId' field of User class", criteria.getFieldToSortBy()));
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

        TypedQuery<User> typedQuery = manager.createQuery(select);
        if(criteria.getOffset() != null) typedQuery.setFirstResult(criteria.getOffset());
        if(criteria.getLimit() != null) typedQuery.setMaxResults(criteria.getLimit());

        try {
            List<User> users = typedQuery.getResultList();
            return (users == null) ? Collections.emptyList() : users;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoLayerException(ex.getMessage());
        }
    }

}
