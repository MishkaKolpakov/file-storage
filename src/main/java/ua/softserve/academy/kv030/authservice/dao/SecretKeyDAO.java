package ua.softserve.academy.kv030.authservice.dao;

import ua.softserve.academy.kv030.authservice.entity.SecretKey;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on SecretKey entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
public interface SecretKeyDAO extends CrudDAO<SecretKey> {

    /**
     * Finds a single secret key entity with specified key value.
     *
     * @param key key value, must be unique.
     * @return optional of a secret key entity from database
     * */
    Optional<SecretKey> findElementByKeyValue(String key);

    /**
     * Finds all secret key entities that expired at specified date.
     *
     * @param expirationDate date by which keys will expire.
     * @return a list of secret key entities from database
     * */
    List<SecretKey> findAllElementsExpiredAtDate(Date expirationDate);
}
