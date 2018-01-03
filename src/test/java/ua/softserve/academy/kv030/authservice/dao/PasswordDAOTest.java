package ua.softserve.academy.kv030.authservice.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordDAOTest {

    @Autowired
    private PasswordDAO passwordDAO;

    public Password createPassword() {
        Password password = new Password();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        long time = 0;
        try {
            date = dateFormat.parse("23/09/2017");
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Timestamp timestamp = new Timestamp(time);

        password.setPassword("secretpassword");
        password.setStatus(true);
        password.setExpirationTime(timestamp);

        return password;
    }

    private List<Password> templateInsert() {
        List<Password> passwords = new ArrayList<>();

        Password password1 = createPassword();
        passwordDAO.insert(password1);
        passwords.add(password1);

        Password password2 = createPassword();
        passwordDAO.insert(password2);
        passwords.add(password2);

        return passwords;
    }

    @Before
    public void templateDelete() {
        if (!passwordDAO.findAll().isEmpty()) {
            for (Password password : passwordDAO.findAll()) {
                passwordDAO.delete(password);
            }
        }
    }

    @Test
    public void correctPasswordFindAllTest() {
        List<Password> expected = templateInsert();

        List<Password> actual = passwordDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getPassword(), actual.get(0).getPassword());
        assertEquals(expected.get(1).getPassword(), actual.get(1).getPassword());
        assertEquals(expected.get(0).getPasswordId(), actual.get(0).getPasswordId());
        assertEquals(expected.get(1).getPasswordId(), actual.get(1).getPasswordId());
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllTest() {
        List<Password> actual = passwordDAO.findAll();
        assertTrue(actual.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        passwordDAO.insert(null);
    }

    @Test
    public void correctInsert() {
        Password expected = createPassword();
        Password actual = passwordDAO.insert(expected);

        List<Password> actualList = passwordDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getPasswordId(), actual.getPasswordId());
    }

    @Test
    public void correctFindById() {
        List<Password> passwords = templateInsert();
        Password actual1 = passwordDAO.findElementById(passwords.get(0).getPasswordId()).get();
        Password actual2 = passwordDAO.findElementById(passwords.get(1).getPasswordId()).get();

        assertEquals(passwords.get(0).getPasswordId(), actual1.getPasswordId());
        assertEquals(passwords.get(1).getPasswordId(), actual2.getPasswordId());
    }

    @Test
    public void findByIdIfNotExists() {
        templateDelete();
        assertFalse(passwordDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfNotExistsThrowException() {
        templateDelete();
        passwordDAO.findElementById(1L).get();
    }

    @Test
    public void correctUpdate() throws ParseException {
        Password passwordForUpdate = createPassword();
        passwordDAO.insert(passwordForUpdate);

        String changedPassword = "UPDATED";

        Password foundPassword = passwordDAO.findAll().get(0);
        foundPassword.setPassword(changedPassword);
        foundPassword.setStatus(false);

        passwordDAO.update(foundPassword);

        assertEquals(1, passwordDAO.findAll().size());
        assertEquals(changedPassword, foundPassword.getPassword());
        assertEquals(false, foundPassword.isStatus());
        assertEquals(passwordForUpdate.getPasswordId(), foundPassword.getPasswordId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        passwordDAO.update(null);
    }

    @Test
    public void correctDelete() {
        passwordDAO.insert(createPassword());
        assertEquals(1, passwordDAO.findAll().size());

        passwordDAO.delete(passwordDAO.findAll().get(0));
        assertEquals(0, passwordDAO.findAll().size());
    }

    @Test
    public void deleteIfNotExistsDelete() {
        assertEquals(0, passwordDAO.findAll().size());
        for (Password password : passwordDAO.findAll()) {
            passwordDAO.delete(password);
        }
        assertEquals(0, passwordDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        passwordDAO.delete(null);
    }

    @Test
    public void correctFindAllElementsByStatus() throws Exception {
        List<Password> expected = templateInsert();
        boolean expectedStatus = expected.get(0).isStatus();

        List<Password> actual  = passwordDAO.findAllElementsByStatus(expectedStatus);
        int expectedSize = 2;

        assertEquals(expectedStatus, actual.get(0).isStatus());
        assertEquals(expectedStatus, actual.get(1).isStatus());
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findAllElementsByStatusIfNotExists() throws Exception {
        templateInsert();
        List<Password> actual = passwordDAO.findAllElementsByStatus(false);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void correctFindActivePasswordByUserId() throws Exception {

        templateInsert();

        Password password = createPassword();
        User user = new User();
        Role role = new Role();

        role.setRoleName("Role");
        user.setFirstName("LastName");
        user.setLastName("FirstName");
        user.setEmail("email@email.com");
        user.setStatus(true);
        user.setRole(role);

        password.setUser(user);

        password = passwordDAO.insert(password);

        System.out.println(password.getUser().getUserId());
        Password actual = passwordDAO.findActivePasswordByUserId(password.getUser().getUserId()).get();

        assertTrue(actual.isStatus());
        assertEquals(password.getPasswordId(), actual.getPasswordId());
        assertEquals(password.getUser().getUserId(), actual.getUser().getUserId());
    }

    @Test
    public void findActivePasswordByUserIdIfNotExists() throws Exception {
        templateInsert();
        assertFalse(passwordDAO.findActivePasswordByUserId(1111111111111L).isPresent());
    }

    @Test
    public void correctFindAllActivePasswordsExpiredWithinTimeRange() throws Exception {
        List<Password> expected = templateInsert();

        Timestamp timestampStart = new Timestamp(expected.get(0).getExpirationTime().getTime() - 24 * 60 * 60 * 1000);
        Timestamp timestampEnd = new Timestamp(expected.get(0).getExpirationTime().getTime() + 24 * 60 * 60 * 1000);
        List<Password> actual  = passwordDAO.findAllActivePasswordsExpiredWithinTimeRange(timestampStart, timestampEnd);
        int expectedSize = 2;

        assertTrue(actual.get(0).getExpirationTime().before(timestampEnd));
        assertTrue(actual.get(0).getExpirationTime().after(timestampStart));
        assertTrue(actual.get(1).getExpirationTime().before(timestampEnd));
        assertTrue(actual.get(1).getExpirationTime().after(timestampStart));

        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findAllActivePasswordsExpiredWithinTimeRangeIfNotExists() throws Exception {
        List<Password> expected = templateInsert();
        Timestamp timestampStart = new Timestamp(expected.get(0).getExpirationTime().getTime() - 2 * 24 * 60 * 60 * 1000);
        Timestamp timestampEnd = new Timestamp(expected.get(0).getExpirationTime().getTime() - 1 * 24 * 60 * 60 * 1000);

        List<Password> actual  = passwordDAO.findAllActivePasswordsExpiredWithinTimeRange(timestampStart, timestampEnd);
        assertTrue(actual.isEmpty());
    }

}