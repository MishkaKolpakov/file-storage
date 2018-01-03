package ua.softserve.academy.kv030.authservice.dao;

import ua.softserve.academy.kv030.authservice.entity.Role;

import java.util.Optional;

/**
 * Handles CRUD operations on Role entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
public interface RoleDAO extends CrudDAO<Role> {

    /**
     * Finds a single role with specified role name.
     *
     * @param roleName a name of a role. The name must be unique.
     * @return optional of a role from database.
     * */
    Optional<Role> findElementByRoleName(String roleName);
}
