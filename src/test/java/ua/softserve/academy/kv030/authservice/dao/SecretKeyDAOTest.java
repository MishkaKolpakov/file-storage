package ua.softserve.academy.kv030.authservice.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;

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
public class SecretKeyDAOTest {

    @Autowired
    private SecretKeyDAO secretKeyDAO;

    private SecretKey createSecretKey() {
        SecretKey secretKey = new SecretKey();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        long time = 0L;
        try {
            date = dateFormat.parse("23/09/2017");
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Timestamp timestamp = new Timestamp(time);

        secretKey.setKey("secretkeyvalue");
        secretKey.setStatus(true);
        secretKey.setExpirationDate(timestamp);

        return secretKey;
    }

    private List<SecretKey> templateInsert() {
        List<SecretKey> secretKeys = new ArrayList<>();

        SecretKey secretKey1 = createSecretKey();
        secretKeyDAO.insert(secretKey1);
        secretKeys.add(secretKey1);

        SecretKey secretKey2 = createSecretKey();
        secretKey2.setKey("new secret key");
        secretKeyDAO.insert(secretKey2);
        secretKeys.add(secretKey2);

        return secretKeys;
    }

    @Before
    public void templateDelete() {
        if (!secretKeyDAO.findAll().isEmpty()) {
            for (SecretKey secretKey : secretKeyDAO.findAll()) {
                secretKeyDAO.delete(secretKey);
            }
        }
    }

    @Test
    public void correctFindAllTest() {
        List<SecretKey> expected = templateInsert();

        List<SecretKey> actual = secretKeyDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getKey(), actual.get(0).getKey());
        assertEquals(expected.get(1).getKey(), actual.get(1).getKey());

        assertEquals(expected.get(0).getKeyId(), actual.get(0).getKeyId());
        assertEquals(expected.get(1).getKeyId(), actual.get(1).getKeyId());
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllsTest() {
        List<SecretKey> actual = secretKeyDAO.findAll();
        assertTrue(actual.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        secretKeyDAO.insert(null);
    }

    @Test
    public void correctInsert() {
        SecretKey expected = createSecretKey();
        SecretKey actual = secretKeyDAO.insert(expected);

        List<SecretKey> actualList = secretKeyDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getKey(), actual.getKey());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getKeyId(), actual.getKeyId());
    }

    @Test
    public void correctFindById() {
        List<SecretKey> secretKeys = templateInsert();
        SecretKey actual1 = secretKeyDAO.findElementById(secretKeys.get(0).getKeyId()).get();
        SecretKey actual2 = secretKeyDAO.findElementById(secretKeys.get(1).getKeyId()).get();

        assertEquals(secretKeys.get(0).getKeyId(), actual1.getKeyId());
        assertEquals(secretKeys.get(1).getKeyId(), actual2.getKeyId());
    }

    @Test
    public void findByIdIfNotExists() {
        templateDelete();
        assertFalse(secretKeyDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfNotExistsThrowException() {
        templateDelete();
        secretKeyDAO.findElementById(1L).get();
    }

    @Test
    public void correctUpdate() throws ParseException {
        SecretKey secretKeyForUpdate = createSecretKey();
        secretKeyDAO.insert(secretKeyForUpdate);

        String changedSecretKey = "UPDATED";

        SecretKey foundSecretKey = secretKeyDAO.findAll().get(0);
        foundSecretKey.setKey(changedSecretKey);
        foundSecretKey.setStatus(false);

        secretKeyDAO.update(secretKeyForUpdate);

        assertEquals(1, secretKeyDAO.findAll().size());
        assertEquals(changedSecretKey, foundSecretKey.getKey());
        assertEquals(false, foundSecretKey.isStatus());
        assertEquals(secretKeyForUpdate.getKeyId(), foundSecretKey.getKeyId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        secretKeyDAO.update(null);
    }

    @Test
    public void correctDelete() {
        secretKeyDAO.insert(createSecretKey());
        assertEquals(1, secretKeyDAO.findAll().size());

        secretKeyDAO.delete(secretKeyDAO.findAll().get(0));
        assertEquals(0, secretKeyDAO.findAll().size());
    }

    @Test
    public void deleteIfNotExistsDelete() {
        assertEquals(0, secretKeyDAO.findAll().size());
        for (SecretKey secretKey : secretKeyDAO.findAll()) {
            secretKeyDAO.delete(secretKey);
        }
        assertEquals(0, secretKeyDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        secretKeyDAO.delete(null);
    }

    @Test
    public void correctFindElementByKeyValue() throws Exception {

        List<SecretKey> secretKeys = templateInsert();
        SecretKey actual1 = secretKeyDAO.findElementByKeyValue(secretKeys.get(0).getKey()).get();
        SecretKey actual2 = secretKeyDAO.findElementByKeyValue(secretKeys.get(1).getKey()).get();

        assertEquals(secretKeys.get(0).getKeyId(), actual1.getKeyId());
        assertEquals(secretKeys.get(1).getKeyId(), actual2.getKeyId());
    }

    @Test
    public void findElementByKeyValueIfNotExists() throws Exception {
        assertFalse(secretKeyDAO.findElementByKeyValue("non_existing_key_value").isPresent());
    }

    @Test
    public void correctFindAllElementsExpiredAtDate() throws Exception {
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        IntStream.range(0,10).forEach(i->{
            SecretKey key = createSecretKey();
            key.setKey(i+"_key_value");
            key.setExpirationDate(expirationDate);
            secretKeyDAO.insert(key);
        });

        java.sql.Date date = new java.sql.Date(expirationDate.getTime());
        List<SecretKey> actual  = secretKeyDAO.findAllElementsExpiredAtDate(date);
        int expectedSize = 10;
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findAllElementsExpiredAtDateIfNotExists() throws Exception {
        List<SecretKey> expected = templateInsert();
        Timestamp timestamp = new Timestamp(expected.get(0).getExpirationDate().getTime() - 24 * 60 * 60 * 1000);
        java.sql.Date date = new java.sql.Date(timestamp.getTime());
        List<SecretKey> actual  = secretKeyDAO.findAllElementsExpiredAtDate(date);

        assertTrue(actual.isEmpty());
    }
}
