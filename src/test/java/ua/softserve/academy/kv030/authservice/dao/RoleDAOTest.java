package ua.softserve.academy.kv030.authservice.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleDAOTest {

    @Autowired
    private RoleDAO roleDAO;

    public Role createRole() {
        Role role = new Role();
        role.setRoleName("TEST_ROLE");

        return role;
    }

    private List<Role> templateInsert() {
        List<Role> roles = new ArrayList<>();

        Role role1 = createRole();
        roleDAO.insert(role1);
        roles.add(role1);

        Role role2 = createRole();
        role2.setRoleName("new role");
        roleDAO.insert(role2);
        roles.add(role2);

        return roles;
    }

    @Before
    public void templateDelete() {
        if (!roleDAO.findAll().isEmpty()) {
            for (Role role : roleDAO.findAll()) {
                roleDAO.delete(role);
            }
        }
    }

    @Test
    public void correctRoleFindAllTest() {
        List<Role> expected = templateInsert();

        List<Role> actual = roleDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getRoleName(), actual.get(0).getRoleName());
        assertEquals(expected.get(1).getRoleName(), actual.get(1).getRoleName());
        assertEquals(expected.get(0).getRoleId(), actual.get(0).getRoleId());
        assertEquals(expected.get(1).getRoleId(), actual.get(1).getRoleId());

        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllTest() {
        List<Role> actual = roleDAO.findAll();
        assertTrue(actual.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        roleDAO.insert(null);
    }

    @Test
    public void correctInsert() {
        Role expected = createRole();
        Role actual = roleDAO.insert(expected);

        List<Role> actualList = roleDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getRoleName(), actual.getRoleName());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getRoleId(), actual.getRoleId());
    }

    @Test
    public void correctFindById() {
        List<Role> roles = templateInsert();
        Role actual1 = roleDAO.findElementById(roles.get(0).getRoleId()).get();
        Role actual2 = roleDAO.findElementById(roles.get(1).getRoleId()).get();

        assertEquals(roles.get(0).getRoleId(), actual1.getRoleId());
        assertEquals(roles.get(1).getRoleId(), actual2.getRoleId());
    }


    @Test
    public void findByIdIfNotExists() {
        templateDelete();
        assertFalse(roleDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfNotExistsThrowException() {
        templateDelete();
        roleDAO.findElementById(1L).get();
    }

    @Test
    public void correctUpdate() {
        Role roleForUpdate = createRole();
        roleDAO.insert(roleForUpdate);

        String changedRole = "UPDATED";

        Role foundRole = roleDAO.findAll().get(0);
        foundRole.setRoleName(changedRole);

        roleDAO.update(foundRole);

        assertEquals(1, roleDAO.findAll().size());
        assertEquals(changedRole, foundRole.getRoleName());
        assertEquals(roleForUpdate.getRoleId(), foundRole.getRoleId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        roleDAO.update(null);
    }

    @Test
    public void correctDelete() {
        roleDAO.insert(createRole());
        assertEquals(1, roleDAO.findAll().size());

        roleDAO.delete(roleDAO.findAll().get(0));
        assertEquals(0, roleDAO.findAll().size());
    }

    @Test
    public void deleteIfNotExistsDelete() {
        assertEquals(0, roleDAO.findAll().size());
        for (Role role : roleDAO.findAll()) {
            roleDAO.delete(role);
        }
        assertEquals(0, roleDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        roleDAO.delete(null);
    }

    @Test
    public void correctFindElementByRoleName() throws Exception {

        List<Role> roles = templateInsert();
        Role actual1 = roleDAO.findElementByRoleName(roles.get(0).getRoleName()).get();
        Role actual2 = roleDAO.findElementByRoleName(roles.get(1).getRoleName()).get();

        assertEquals(roles.get(0).getRoleName(), actual1.getRoleName());
        assertEquals(roles.get(1).getRoleName(), actual2.getRoleName());
    }

    @Test
    public void findElementByRoleNameIfNotExists() throws Exception {
        assertFalse(roleDAO.findElementByRoleName("non_existing_role_name").isPresent());
    }
}
