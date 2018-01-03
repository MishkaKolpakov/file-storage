package ua.softserve.academy.kv030.authservice.dao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.exceptions.DaoLayerException;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on Password entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class PasswordDAOImpl extends CrudDAOImpl<Password> implements PasswordDAO {

    @Autowired
    public Logger logger;

    public PasswordDAOImpl() {
        super(Password.class);
    }

    @Override
    public List<Password> findAllElementsByStatus(boolean status) {
        return findAllByFieldEqual( "status", status);
    }

    @Override
    public Optional<Password> findActivePasswordByUserId(long userId) {

        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Password> criteriaQuery = criteriaBuilder.createQuery(Password.class);
        Root<Password> elementRoot = criteriaQuery.from(Password.class);
        criteriaQuery.select(elementRoot)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(elementRoot.get("user"), userId),
                                criteriaBuilder.equal(elementRoot.get("status"), true)
                        )
                );

        try {
            Password element = manager.createQuery(criteriaQuery).getSingleResult();
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
    public List<Password> findAllActivePasswordsExpiredWithinTimeRange(Timestamp start, Timestamp end) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Password> criteriaQuery = criteriaBuilder.createQuery(Password.class);
        Root<Password> elementRoot = criteriaQuery.from(Password.class);
        criteriaQuery.select(elementRoot)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(elementRoot.get("status"), true),
                                criteriaBuilder.between(elementRoot.get("expirationTime"), start, end)
                        )
                );

        try {
            List<Password> elements = manager.createQuery(criteriaQuery).getResultList();
            return (elements == null) ? Collections.emptyList() : elements;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoLayerException(e.getMessage());
        }
    }

    @Override
    public List<Password> findAllActivePasswordsThatExpired() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Password> criteriaQuery = criteriaBuilder.createQuery(Password.class);
        Root<Password> elementRoot = criteriaQuery.from(Password.class);
        criteriaQuery.select(elementRoot)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(elementRoot.get("status"), true),
                                criteriaBuilder.lessThanOrEqualTo(elementRoot.get("expirationTime"), time)
                        )
                );

        try {
            List<Password> elements = manager.createQuery(criteriaQuery).getResultList();
            return (elements == null) ? Collections.emptyList() : elements;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoLayerException(e.getMessage());
        }
    }
}