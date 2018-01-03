package ua.softserve.academy.kv030.authservice.dao;

import org.springframework.stereotype.Repository;
import ua.softserve.academy.kv030.authservice.entity.Permission;

/**
 * Handles CRUD operations on Permission entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
@Repository
public class PermissionDAOImpl extends CrudDAOImpl<Permission> implements PermissionDAO {
    public PermissionDAOImpl() {
        super(Permission.class);
    }
}
