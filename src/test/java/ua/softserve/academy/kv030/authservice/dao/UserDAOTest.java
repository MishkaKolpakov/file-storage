package ua.softserve.academy.kv030.authservice.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ua.softserve.academy.kv030.authservice.entity.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDAOTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordDAO passwordDAO;

    private Timestamp createTimeStamp() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("23/09/2017");
        long time = date.getTime();
        return new Timestamp(time);
    }

    private User createUser() {
        User user = new User();
        Password password = new Password();
        Role role = new Role();

        Resource resource = new Resource();
        SecretKey secretKey = new SecretKey();
        Permission permission = new Permission();

        role.setRoleName("ROLE_TEST");

        password.setPassword("secret");
        password.setStatus(true);
        try {
            password.setExpirationTime(createTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Password> passwords = new ArrayList<>();
        passwords.add(password);
        password.setUser(user);

        user.setFirstName("Test User");
        user.setLastName("Pupkin");
        user.setEmail("testuser@email.com");
        user.setStatus(true);
        user.setRole(role);
        user.setPasswords(passwords);

        secretKey.setKey("somesecretkey");
        try {
            secretKey.setExpirationDate(createTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        secretKey.setStatus(true);

        permission.setPermission("all");

        resource.setLinkToFile("fileLink/fileId");
        resource.setPermission(permission);
        resource.setSecretKey(secretKey);
        resource.setOwner(user);

        return user;
    }

    private User createUserForUpdate() throws ParseException {
        User userForUpdate = new User();
        Password passwordForUpdate = new Password();
        List<Password> passwordsForUpdate = new ArrayList<>();
        passwordsForUpdate.add(passwordForUpdate);
        Role roleForUpdate = new Role();

        userForUpdate.setFirstName("UserFirstName for update");
        userForUpdate.setLastName("UserLastName for update");
        userForUpdate.setEmail("userForUpdate@email.com");
        userForUpdate.setStatus(true);
        userForUpdate.setRole(roleForUpdate);
        userForUpdate.setPasswords(passwordsForUpdate);

        passwordForUpdate.setPassword("password for update");
        passwordForUpdate.setStatus(true);
        passwordForUpdate.setExpirationTime(createTimeStamp());

        roleForUpdate.setRoleName("ROLE_FORUPDATE");
        return userForUpdate;
    }

    private List<User> templateInsert() {
        List<User> users = new ArrayList<>();
        User user1 = createUser();
        users.add(user1);
        userDAO.insert(user1);

        User user2 = createUser();
        user2.getRole().setRoleName("ROLE_2");
        user2.setFirstName("new userFirstName");
        user2.setLastName("new userLastName");
        user2.setEmail("testuser2@email.com");
        users.add(user2);
        userDAO.insert(user2);

        return users;
    }

    @Before
    public void templateDelete() {
        if (!userDAO.findAll().isEmpty()) {
            for (User user : userDAO.findAll()) {
                userDAO.delete(user);
            }
        }
    }

    @Test
    public void correctUserFindAllUsersTest() {
        List<User> expected = templateInsert();

        List<User> actual = userDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getFirstName(), actual.get(0).getFirstName());
        assertEquals(expected.get(1).getFirstName(), actual.get(1).getFirstName());
        assertEquals(expected.get(0).getLastName(), actual.get(0).getLastName());
        assertEquals(expected.get(1).getLastName(), actual.get(1).getLastName());
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllUsersTest() {
        List<User> actual = userDAO.findAll();
        assertTrue(actual.isEmpty());

    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        userDAO.insert(null);
    }

    @Test
    public void correctInsert() {

        User expected = createUser();
        User actual = userDAO.insert(expected);

        List<User> actualList = userDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getUserId(), actual.getUserId());
    }

    @Test
    public void correctFindById() {
        List<User> users = templateInsert();
        User actual1 = userDAO.findElementById(users.get(0).getUserId()).get();
        User actual2 = userDAO.findElementById(users.get(1).getUserId()).get();

        assertEquals(users.get(0).getUserId(), actual1.getUserId());
        assertEquals(users.get(1).getUserId(), actual2.getUserId());
    }

    @Test
    public void findByIdIfUserNotExists() {
        templateDelete();
        assertFalse(userDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfUserNotExistsThrowException() {
        templateDelete();
        userDAO.findElementById(1L).get();
    }

    @Test
    public void correctUpdate() throws ParseException {
        User userForUpdate = createUserForUpdate();
        userDAO.insert(userForUpdate);

        String changedName = "UPDATED";

        User foundUser = userDAO.findAll().get(0);
        foundUser.setFirstName(changedName);
        foundUser.setLastName(changedName);
        foundUser.setStatus(false);

        userDAO.update(foundUser);

        assertEquals(1, userDAO.findAll().size());
        assertEquals(changedName, foundUser.getFirstName());
        assertEquals(changedName, foundUser.getLastName());
        assertEquals(false, foundUser.isStatus());
        assertEquals(userForUpdate.getUserId(), foundUser.getUserId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        userDAO.update(null);
    }

    @Test
    public void correctDelete() {
        userDAO.insert(createUser());
        assertEquals(1, userDAO.findAll().size());

        userDAO.delete(userDAO.findAll().get(0));
        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    public void deleteIfUserNotExistsDelete() {
        assertEquals(0, userDAO.findAll().size());
        for (User user : userDAO.findAll()) {
            userDAO.delete(user);
        }
        assertEquals(0, userDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        userDAO.delete(null);
    }

    @Test
    public void correctFindAllUsersByStatus() throws Exception {
        List<User> expected = templateInsert();

        List<User> actual  = userDAO.findAllUsersByStatus(true);
        int expectedSize = 2;

        assertEquals(expected.get(0).isStatus(), actual.get(0).isStatus());
        assertEquals(expected.get(1).isStatus(), actual.get(1).isStatus());
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findAllUsersByStatusIfNotExists() throws Exception {
        List<User> actual = userDAO.findAllUsersByStatus(false);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void correctFindUserByEmail() throws Exception {
        List<User> expected = templateInsert();
        User actual1 = userDAO.findUserByEmail(expected.get(0).getEmail()).get();
        User actual2 = userDAO.findUserByEmail(expected.get(1).getEmail()).get();

        assertEquals(expected.get(0).getEmail(), actual1.getEmail());
        assertEquals(expected.get(1).getEmail(), actual2.getEmail());
    }

    @Test
    public void findUserByEmailIfNotExists() throws Exception {
        assertFalse(userDAO.findUserByEmail("non_existing_email@email.com").isPresent());
    }


    @Test
    public void correctFindAllUsersByRoleId() throws Exception {
        List<User> allUsers = templateInsert();
        long roleId = allUsers.get(0).getRole().getRoleId();

        List<User> actual  = userDAO.findAllUsersByRoleId(roleId);
        int expectedSize = 1;

        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findAllUsersByRoleIdIfNotExists() throws Exception {
        templateInsert();
        List<User> actual  = userDAO.findAllUsersByRoleId(30000000000000L);
        assertTrue(actual.isEmpty());
    }

    @Test
    @Transactional
    public void correctFindUserByPasswordId() throws Exception {
        User user = createUser();
        user.getRole().setRoleName("SOME_ROLE");
        user.setEmail("SOME_EMAIL");
        userDAO.insert(user);
        long expectedPasswordId = user.getPasswords().get(0).getPasswordId();
        User actual = userDAO.findUserByPasswordId(expectedPasswordId).get();
        assertEquals(expectedPasswordId, actual.getPasswords().get(0).getPasswordId());
    }

    @Test
    public void findUserByPasswordIdIfNotExists() throws Exception {
        templateInsert();
        assertFalse(userDAO.findUserByPasswordId(10000000000000000L).isPresent());
    }

    @Test
    public void correctFindAllUsersByOffsetByPageSizeByIdSort() throws Exception {
        IntStream.rangeClosed(1,50).forEach(i->{
            User user = createUser();
            user.setFirstName(i+"_firstname");
            user.setLastName(i+"_lastname");
            user.setEmail(i+"_email@email.com");
            user.getRole().setRoleName(i+ "_role");
            userDAO.insert(user);
        });

        List<User> actual_offset0_size5 = userDAO.findAllUsersByOffsetByPageSizeByIdSort(0, 5, SortingOrderEnum.ASC);
        assertTrue(actual_offset0_size5.size()==5);

        List<User> actual_offset0_size50 = userDAO.findAllUsersByOffsetByPageSizeByIdSort(0, 50, SortingOrderEnum.ASC);
        assertTrue(actual_offset0_size50.size()==50);

        List<User> actual_offset48_size5 = userDAO.findAllUsersByOffsetByPageSizeByIdSort(48, 5, SortingOrderEnum.DESC);
        assertTrue(actual_offset48_size5.size()==2);

        List<User> actual_offset52_size5 = userDAO.findAllUsersByOffsetByPageSizeByIdSort(52, 5, SortingOrderEnum.DESC);
        assertTrue(actual_offset52_size5.size()==0);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllUsersByOffsetByPageSizeIfOffsetOutOfRange() throws Exception {
        templateInsert();
        userDAO.findAllUsersByOffsetByPageSizeByIdSort(-201, 50, SortingOrderEnum.DESC);
    }

    @Test
    public void containsEmailIfExists() throws Exception {
        List<User> users = templateInsert();
        assertTrue(userDAO.containsEmail(users.get(0).getEmail()));
        assertTrue(userDAO.containsEmail(users.get(1).getEmail()));
    }

    @Test
    public void containsEmailIfNotExists() throws Exception {
        List<User> users = templateInsert();
        assertFalse(userDAO.containsEmail("non-existing-"+users.get(0).getEmail()));
        assertFalse(userDAO.containsEmail("non-existing-"+users.get(1).getEmail()));
    }

    @Test
    public void correctCount() throws Exception {
        List<User> users = templateInsert();
        long expected_size = users.size();
        long actual_size = userDAO.count();
        assertTrue(expected_size > 0);
        assertEquals(expected_size, actual_size);
    }

    @Test
    public void countIfEmpty() throws Exception {
        templateDelete();
        long actual_size = userDAO.count();
        assertEquals(0L, actual_size);
    }
}
