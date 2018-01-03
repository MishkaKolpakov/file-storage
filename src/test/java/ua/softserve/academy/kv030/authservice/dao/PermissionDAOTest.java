package ua.softserve.academy.kv030.authservice.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionDAOTest {

    @Autowired
    private PermissionDAO permissionDAO;

    public Permission createPermission() {
        Permission permission = new Permission();
        permission.setPermission("TEST_PERMISSION");

        return permission;
    }

    private List<Permission> templateInsert() {
        List<Permission> permissions = new ArrayList<>();

        Permission permission1 = createPermission();
        permissionDAO.insert(permission1);
        permissions.add(permission1);

        Permission permission2 = createPermission();
        permission2.setPermission("new permission");
        permissionDAO.insert(permission2);
        permissions.add(permission2);

        return permissions;
    }

    @Before
    public void templateDelete() {
        if (!permissionDAO.findAll().isEmpty()) {
            for (Permission permission : permissionDAO.findAll()) {
                permissionDAO.delete(permission);
            }
        }
    }

    @Test
    public void correctFindAllTest() {
        List<Permission> expected = templateInsert();

        List<Permission> actual = permissionDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getPermission(), actual.get(0).getPermission());
        assertEquals(expected.get(1).getPermission(), actual.get(1).getPermission());
        assertEquals(expected.get(0).getPermissionId(), actual.get(0).getPermissionId());
        assertEquals(expected.get(1).getPermissionId(), actual.get(1).getPermissionId());

        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllsTest() {
        List<Permission> actual = permissionDAO.findAll();
        assertTrue(actual.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        permissionDAO.insert(null);
    }

    @Test
    public void correctInsert() {
        Permission expected = createPermission();
        Permission actual = permissionDAO.insert(expected);

        List<Permission> actualList = permissionDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getPermission(), actual.getPermission());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getPermissionId(), actual.getPermissionId());
    }

    @Test
    public void correctFindById() {
        List<Permission> roles = templateInsert();
        Permission actual1 = permissionDAO.findElementById(roles.get(0).getPermissionId()).get();
        Permission actual2 = permissionDAO.findElementById(roles.get(1).getPermissionId()).get();

        assertEquals(roles.get(0).getPermissionId(), actual1.getPermissionId());
        assertEquals(roles.get(1).getPermissionId(), actual2.getPermissionId());
    }


    @Test
    public void findByIdIfNotExists() {
        templateDelete();
        assertFalse(permissionDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfNotExistsThrowException() {
        templateDelete();
        permissionDAO.findElementById(1L).get();
    }

    @Test
    public void correctUpdate() {
        Permission permissionForUpdate = createPermission();
        permissionDAO.insert(permissionForUpdate);

        String changedRole = "UPDATED";

        Permission foundPermission = permissionDAO.findAll().get(0);
        foundPermission.setPermission(changedRole);

        permissionDAO.update(foundPermission);

        assertEquals(1, permissionDAO.findAll().size());
        assertEquals(changedRole, foundPermission.getPermission());
        assertEquals(permissionForUpdate.getPermissionId(), permissionForUpdate.getPermissionId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        permissionDAO.update(null);
    }

    @Test
    public void correctDelete() {
        permissionDAO.insert(createPermission());
        assertEquals(1, permissionDAO.findAll().size());

        permissionDAO.delete(permissionDAO.findAll().get(0));
        assertEquals(0, permissionDAO.findAll().size());
    }

    @Test
    public void deleteIfNotExistsDelete() {
        assertEquals(0, permissionDAO.findAll().size());
        for (Permission permission : permissionDAO.findAll()) {
            permissionDAO.delete(permission);
        }
        assertEquals(0, permissionDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        permissionDAO.delete(null);
    }
}
