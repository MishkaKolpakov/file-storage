package ua.softserve.academy.kv030.authservice.dao;

import ua.softserve.academy.kv030.authservice.entity.Password;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on Password entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
public interface PasswordDAO extends CrudDAO<Password> {

    /**
     * Finds all password entities with a status equals to the specified.
     *
     * @param status status of a password.
     * @return a list of password entities from database.
     * */
    List<Password> findAllElementsByStatus(boolean status);

    /**
     * Finds active (status filed = true) password for user with defined ID.
     *
     * @param userId ID of a user.
     * @return optional of a password from database.
     * */
    Optional<Password> findActivePasswordByUserId(long userId);

    /**
     * Finds all password entities with active status (status filed = true) and that expires between start time and end time.
     *
     * @param start time starting from which to check expiration of password.
     * @param end time till which to check expiration of password.
     * @return a list of password entities from database.
     * */
    List<Password> findAllActivePasswordsExpiredWithinTimeRange(Timestamp start, Timestamp end);

    /**
     * Finds all expired passwords with active status (status filed = true).
     *
     * @return a list of password entities from database.
     * */
    List<Password> findAllActivePasswordsThatExpired();
}