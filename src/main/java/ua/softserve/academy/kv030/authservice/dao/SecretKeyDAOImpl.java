package ua.softserve.academy.kv030.authservice.dao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;
import ua.softserve.academy.kv030.authservice.exceptions.DaoLayerException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on SecretKey entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class SecretKeyDAOImpl extends CrudDAOImpl<SecretKey> implements SecretKeyDAO {

    @Autowired
    public Logger logger;

    public SecretKeyDAOImpl() {
        super(SecretKey.class);
    }

    @Override
    public Optional<SecretKey> findElementByKeyValue(String key) {
        return findOneByFieldEqual("key", key);
    }

    @Override
    public List<SecretKey> findAllElementsExpiredAtDate(Date expirationDate) {

        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<SecretKey> criteriaQuery = criteriaBuilder.createQuery(SecretKey.class);
        Root<SecretKey> elementRoot = criteriaQuery.from(SecretKey.class);
        criteriaQuery.select(elementRoot).where(criteriaBuilder.equal(elementRoot.get("expirationDate").as(Date.class), expirationDate));

        try {
            List<SecretKey> elements = manager.createQuery(criteriaQuery).getResultList();
            return (elements == null) ? Collections.emptyList() : elements;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoLayerException(e.getMessage());
        }

    }
}
