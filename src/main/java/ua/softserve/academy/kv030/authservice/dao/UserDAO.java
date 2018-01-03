package ua.softserve.academy.kv030.authservice.dao;

import ua.softserve.academy.kv030.authservice.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on User entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
public interface UserDAO extends CrudDAO<User> {

    /**
     * Finds all users with a status equals to the specified.
     *
     * @param status status of a user
     * @return a list of users from database
     * */
    List<User> findAllUsersByStatus(boolean status);

    /**
     * Finds a single user with specified email.
     *
     * @param email email of a user. Email must be unique.
     * @return optional of a user from database
     * */
    Optional<User> findUserByEmail(String email);

    /**
     * Checks whether a user with specified email exists in database.
     *
     * @param email email of a user
     * @return <tt>true</tt> if email exists, <tt>false</tt> otherwise
     * */
    Boolean containsEmail(String email);

    /**
     * Finds all users with a role equals to the specified.
     *
     * @param roleId ID of a role of the user
     * @return a list of users from database
     * */
    List<User> findAllUsersByRoleId(long roleId);

    /**
     * Finds a user by specified password ID.
     *
     * @param passwordId  ID of password of a user.
     * @return optional of a user from database
     * */
    Optional<User> findUserByPasswordId(long passwordId);

    /**
     * Finds all users per page defined by offset to retrieve results from and by page size, sorted by user ID.
     *
     * @param offset a first result entity to be retrieved from the database, starts from 0
     * @param pageSize max number of results to retrieve
     * @param order an ascending or descending order to sort results
     * @return a list of users from database
     * */
    List<User> findAllUsersByOffsetByPageSizeByIdSort(int offset, int pageSize, SortingOrderEnum order);

    /**
     * Finds all users by defined search criteria.
     *
     * @param criteria criteria to search users by
     * @return a list of users from database
     * */
    List<User> findAllUsersByCriteria(UsersFilterCriteria criteria);
}
