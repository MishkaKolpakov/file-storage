package ua.softserve.academy.kv030.authservice.dao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.Role;

import java.util.Optional;

/**
 * Handles CRUD operations on Role entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class RoleDAOImpl extends CrudDAOImpl<Role> implements RoleDAO {

    @Autowired
    public Logger logger;

    public RoleDAOImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findElementByRoleName(String roleName) {
        return findOneByFieldEqual("roleName", roleName);
    }
}
