package ua.softserve.academy.kv030.authservice.dao;

import org.junit.*;
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
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceDAOTest {

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private RoleDAO roleDAO;

    private Timestamp createTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        long time = 0L;
        try {
            date = dateFormat.parse("11/11/2012");
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Timestamp(time);
    }

    private User createOwnerInDB() {
        User owner = new User();
        Password password = new Password();
        List<Password> passwords = new ArrayList<>();
        passwords.add(password);
        password.setPassword("secret");
        password.setStatus(true);
        password.setExpirationTime(createTimeStamp());

        owner.setFirstName("Test User Vasya");
        owner.setLastName("Pupkin");
        owner.setEmail("testuser@email.com");
        owner.setStatus(true);
        owner.setRole(roleDAO.findAll().get(0));
        owner.setPasswords(passwords);
        userDAO.insert(owner);
        return owner;
    }

    @Before
    public void assureAtLeastOnePermissionInDB() {
        if(permissionDAO.findAll() == null || permissionDAO.findAll().isEmpty()) {
            Permission permission = new Permission();
            permission.setPermission("TEST_PERMISSION");
            permissionDAO.insert(permission);
        }
    }

    @Before
    public void assureAtLeastOneRoleInDB() {
        if(roleDAO.findAll() == null || roleDAO.findAll().isEmpty()) {
            Role role = new Role();
            role.setRoleName("TEST_ROLE");
            roleDAO.insert(role);
        }
    }

    private Resource createResource() {
        Resource resource = new Resource();
        SecretKey secretKey = new SecretKey();
        secretKey.setKey("secretkeyvalue");
        secretKey.setStatus(true);
        secretKey.setExpirationDate(createTimeStamp());

        resource.setLinkToFile("fileLink/fileId");
        resource.setPermission(permissionDAO.findAll().get(0));
        resource.setSecretKey(secretKey);
        resource.setFileName("fileName");
        resource.setMimeType("mime");
        resource.setSize(123);

        return resource;
    }

    private List<Resource> templateInsert() {

        User owner = createOwnerInDB();

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = createResource();
        resource1.setOwner(owner);
        resources.add(resource1);
        resourceDAO.insert(resource1);

        Resource resource2 = createResource();
        resource2.getSecretKey().setKey("key_2");
        resource2.setLinkToFile("new resource");
        resources.add(resource2);
        resource2.setOwner(owner);
        resourceDAO.insert(resource2);

        return resources;
    }

    @Before
    public void templateDelete() {
        if (!resourceDAO.findAll().isEmpty()) {
            for (Resource resource : resourceDAO.findAll()) {
                resourceDAO.delete(resource);
            }
        }
        if (!userDAO.findAll().isEmpty()) {
            for (User user : userDAO.findAll()) {
                userDAO.delete(user);
            }
        }
    }

    @Test
    @Transactional
    public void correctFindAllTest() {
        List<Resource> expected = templateInsert();

        List<Resource> actual = resourceDAO.findAll();
        int expectedSize = 2;

        assertEquals(expected.get(0).getLinkToFile(), actual.get(0).getLinkToFile());
        assertEquals(expected.get(1).getLinkToFile(), actual.get(1).getLinkToFile());

        assertEquals(expected.get(0).getResourceId(), actual.get(0).getResourceId());
        assertEquals(expected.get(1).getResourceId(), actual.get(1).getResourceId());

        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void emptyFindAllTest() {
        List<Resource> actual = resourceDAO.findAll();
        assertTrue(actual.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void nullInsertTest() {
        resourceDAO.insert(null);
    }

    @Test
    @Transactional
    public void correctInsert() {
        Resource expected = createResource();
        expected.setOwner(createOwnerInDB());
        Resource actual = resourceDAO.insert(expected);

        List<Resource> actualList = resourceDAO.findAll();
        int expectedSize = 1;

        assertEquals(expected.getLinkToFile(), actual.getLinkToFile());
        assertEquals(expectedSize, actualList.size());
        assertEquals(actualList.get(0).getResourceId(), actual.getResourceId());
    }

    @Test
    @Transactional
    public void correctFindById() {
        List<Resource> resources = templateInsert();
        Resource actual1 = resourceDAO.findElementById(resources.get(0).getResourceId()).get();
        Resource actual2 = resourceDAO.findElementById(resources.get(1).getResourceId()).get();

        assertEquals(resources.get(0).getResourceId(), actual1.getResourceId());
        assertEquals(resources.get(1).getResourceId(), actual2.getResourceId());
    }

    @Test
    public void findByIdIfNotExists() {
        templateDelete();
        assertFalse(resourceDAO.findElementById(1L).isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdIfNotExistsThrowException() {
        templateDelete();
        resourceDAO.findElementById(1L).get();
    }

    @Test
    @Transactional
    public void correctUpdate() throws ParseException {
        Resource secretKeyForUpdate = createResource();
        User owner = createOwnerInDB();
        secretKeyForUpdate.setOwner(owner);
        resourceDAO.insert(secretKeyForUpdate);

        String changedResource = "UPDATED";

        Resource foundSecretKey = resourceDAO.findAll().get(0);
        foundSecretKey.setLinkToFile(changedResource);

        resourceDAO.update(secretKeyForUpdate);

        assertEquals(1, resourceDAO.findAll().size());
        assertEquals(changedResource, foundSecretKey.getLinkToFile());
        assertEquals(secretKeyForUpdate.getResourceId(), foundSecretKey.getResourceId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void updateNull() {
        resourceDAO.update(null);
    }

    @Test
    @Transactional
    public void correctDelete() {
        Resource resource = createResource();
        resource.setOwner(createOwnerInDB());
        resourceDAO.insert(resource);
        assertEquals(1, resourceDAO.findAll().size());

        resourceDAO.delete(resourceDAO.findAll().get(0));
        assertEquals(0, resourceDAO.findAll().size());
    }

    @Test
    public void deleteIfNotExistsDelete() {
        assertEquals(0, resourceDAO.findAll().size());
        for (Resource resource : resourceDAO.findAll()) {
            resourceDAO.delete(resource);
        }
        assertEquals(0, resourceDAO.findAll().size());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void deleteNullArgument() {
        resourceDAO.delete(null);
    }


    @Test
    @Transactional
    public void correctFindElementByUUID() {
        List<Resource> resources = templateInsert();
        Resource actual1 = resourceDAO.findElementByUUID(resources.get(0).getLinkToFile()).get();
        Resource actual2 = resourceDAO.findElementByUUID(resources.get(1).getLinkToFile()).get();

        assertEquals(resources.get(0).getLinkToFile(), actual1.getLinkToFile());
        assertEquals(resources.get(0).getResourceId(), actual1.getResourceId());
        assertEquals(resources.get(1).getLinkToFile(), actual2.getLinkToFile());
        assertEquals(resources.get(1).getResourceId(), actual2.getResourceId());
    }

    @Test
    public void findElementByUUIDIfNotExists() {
        assertFalse(resourceDAO.findElementByUUID("non_existing_UUID").isPresent());
    }

    @Test
    @Transactional
    public void correctFindElementBySecretKey() {
        List<Resource> resources = templateInsert();
        Resource actual1 = resourceDAO.findElementBySecretKeyId(resources.get(0).getSecretKey().getKeyId()).get();
        Resource actual2 = resourceDAO.findElementBySecretKeyId(resources.get(1).getSecretKey().getKeyId()).get();

        assertEquals(resources.get(0).getSecretKey().getKeyId(), actual1.getSecretKey().getKeyId());
        assertEquals(resources.get(1).getSecretKey().getKeyId(), actual2.getSecretKey().getKeyId());
    }

    @Test
    @Transactional
    public void findElementBySecretKeyIfNotExists() {
        templateInsert();
        assertFalse(resourceDAO.findElementBySecretKeyId(1000000000000000000L).isPresent());
    }

    @Test
    @Transactional
    public void correctFindAllElementsByOffsetByPageSizeByIdSort() throws Exception {
        User owner = createOwnerInDB();
        IntStream.rangeClosed(1,50).forEach(i->{
            Resource resource = createResource();
            resource.setLinkToFile(i+"_uuid");
            resource.setOwner(owner);
            resource.getSecretKey().setKey(i+"_key");
            resourceDAO.insert(resource);
        });

        List<Resource> actual_offset0_size5 = resourceDAO.findAllElementsByOffsetByPageSizeByIdSort(0, 5, SortingOrderEnum.ASC);
        assertTrue(actual_offset0_size5.size()==5);
        assertTrue(actual_offset0_size5.get(0).getResourceId() < actual_offset0_size5.get(4).getResourceId());

        List<Resource> actual_offset0_size50 = resourceDAO.findAllElementsByOffsetByPageSizeByIdSort(0, 50, SortingOrderEnum.ASC);
        assertTrue(actual_offset0_size50.size()==50);
        assertTrue(actual_offset0_size50.get(0).getResourceId() < actual_offset0_size50.get(49).getResourceId());

        List<Resource> actual_offset48_size5 = resourceDAO.findAllElementsByOffsetByPageSizeByIdSort(48, 5, SortingOrderEnum.DESC);
        assertTrue(actual_offset48_size5.size()==2);
        assertTrue(actual_offset48_size5.get(0).getResourceId() > actual_offset48_size5.get(1).getResourceId());

        List<Resource> actual_offset52_size5 = resourceDAO.findAllElementsByOffsetByPageSizeByIdSort(52, 5, SortingOrderEnum.DESC);
        assertTrue(actual_offset52_size5.size()==0);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllElementsByOffsetByPageSizeByIdSortIfOffsetOutOfRange() throws Exception {
        templateInsert();
        resourceDAO.findAllElementsByOffsetByPageSizeByIdSort(-1, 50, SortingOrderEnum.ASC);
    }

    @Test
    @Transactional
    public void containsUUIDIfExists() throws Exception {
        List<Resource> resources = templateInsert();
        assertTrue(resourceDAO.containsUUID(resources.get(0).getLinkToFile()));
        assertTrue(resourceDAO.containsUUID(resources.get(1).getLinkToFile()));
    }

    @Test
    @Transactional
    public void containsUUIDIfNotExists() throws Exception {
        List<Resource> resources = templateInsert();
        assertFalse(resourceDAO.containsUUID("non_existing_"+resources.get(0).getLinkToFile()));
        assertFalse(resourceDAO.containsUUID("non_existing_"+resources.get(1).getLinkToFile()));
    }
}
