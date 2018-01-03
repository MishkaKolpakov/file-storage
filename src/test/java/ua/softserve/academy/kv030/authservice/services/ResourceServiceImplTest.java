package ua.softserve.academy.kv030.authservice.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import twitter4j.TwitterFactory;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.PermissionTypeDTO;
import ua.softserve.academy.kv030.authservice.converter.ResourceConverter;
import ua.softserve.academy.kv030.authservice.dao.PermissionDAO;
import ua.softserve.academy.kv030.authservice.dao.ResourceDAO;
import ua.softserve.academy.kv030.authservice.dao.UserDAO;
import ua.softserve.academy.kv030.authservice.entity.*;
import ua.softserve.academy.kv030.authservice.services.encryption.CipherServiceImpl;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClientImpl;
import ua.softserve.academy.kv030.authservice.services.mail.EmailServiceImpl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceImplTest {

    @Mock
    HttpClientImpl httpClient;
    @Mock
    CipherServiceImpl cipherService;
    @Mock
    EmailServiceImpl emailService;
    @Mock
    UserDAO userDAO;
    @Mock
    ResourceDAO resourceDAO;
    @Mock
    PermissionDAO permissionDAO;
    @Mock
    TwitterFactory twitterFactory;
    Logger logger;
    String fileServiceUrl = "http://localhost:8080/";
    String authServiceUrl = "http://localhost:8585/";
    int cipherFileSize = 50;
    ResourceServiceImpl resourceService;
    private static Resource resource;
    private static FileMetadataDTO resourceDTO;
    private static SecretKey key;
    private static Permission permission;
    private static User userHaveAccessToFile;
    private static User userHaveNotAccessToFile;
    private static User owner;
    private static String futureTimeString = "2020-11-19T15:25:09.651+02:00";
    private static String expiredTimeString = "2016-11-19T15:25:09.651+02:00";
    private MockMultipartFile multipartFile;

    private OffsetDateTime getOffsetDateTime(String str) {
        return OffsetDateTime.parse(str);
    }

    private Timestamp getTimestamp(String str) {
        return Timestamp.valueOf(getOffsetDateTime(str).toLocalDateTime());
    }

    @Before
    public void setUp() {
        multipartFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        logger = LoggerFactory.getLogger(ResourceServiceImplTest.class);
        resourceService = new ResourceServiceImpl(fileServiceUrl, authServiceUrl,cipherFileSize, logger,
                httpClient, cipherService, emailService, resourceDAO, userDAO,  permissionDAO, twitterFactory);

        resource = new Resource();
        resourceDTO = new FileMetadataDTO();
        key = new SecretKey();
        owner = new User();
        Role role = new Role();
        role.setRoleName("USER");
        owner.setRole(role);
        userHaveAccessToFile = new User();
        userHaveNotAccessToFile = new User();
        permission = new Permission();
        key.setStatus(true);
        //set init values into resourceDTO
        resourceDTO.setOwnerId(1L);
        resourceDTO.setResourceId(1L);
        resourceDTO.setKey("mysupersecretekey");
        resourceDTO.setFileUUID("fileuuid");
        resourceDTO.setMime("text");
        resourceDTO.setFileName("tempfile.jpg");
        resourceDTO.setPermission(PermissionTypeDTO.ALL_USERS);
        resourceDTO.setExpirationTime(getOffsetDateTime(futureTimeString));
        resourceDTO.setFileSize(256L);
        //set init values into key
        key.setKeyId(1L);
        key.setStatus(true);
        key.setKey("mysupersecretkey");
        key.setExpirationDate(getTimestamp(futureTimeString));

        //set init values into resource
        resource.setResourceId(1L);
        resource.setMimeType("text");
        resource.setLinkToFile("fileuuid");
        resource.setFileName("tempfile.jpg");
        resource.setSize(256L);
        //set init values into permission
        permission.setPermission("ALL_USERS");
        permission.setPermissionId(0);

        //set init values into user
        owner.setUserId(1);
        owner.setFirstName("owner");
        owner.setLastName("lastName");
        //
        userHaveAccessToFile.setUserId(2);
        userHaveAccessToFile.setFirstName("userHaveAccessToFile");
        userHaveAccessToFile.setLastName("lastName");
        resource.setUsers(new HashSet<>(Arrays.asList(userHaveAccessToFile)));
        //
        userHaveNotAccessToFile.setUserId(3);
        userHaveNotAccessToFile.setFirstName("userHaveNotAccessToFile");
        userHaveNotAccessToFile.setLastName("lastName");
        //bind entities in resource
        resource.setPermission(permission);
        resource.setOwner(owner);
        resource.setSecretKey(key);

    }

    @After
    public void tearDown() {
        resourceDTO = null;
        resource = null;
        key = null;
        owner = null;
    }

    @Test
    public void getExpectedFileUrlWithSmallFile() {
        resource.setSize(cipherFileSize*1024*1024-100);
        String uuid = "ksjghf-ljdlaksjda";
        resource.setLinkToFile(uuid);
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        assertEquals(fileServiceUrl +"files/"+uuid, resourceService.getFileURL(resourceDTO.getFileUUID()));
    }

    @Test
    public void getFileNameByUUID() {
        resource.setSize(cipherFileSize*1024*1024-100);
        String uuid = "ksjghf-ljdlaksjda";
        resource.setLinkToFile(uuid);
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        assertEquals("tempfile.jpg", resourceService.getFileNameByUUID(resourceDTO.getFileUUID()));
    }

    @Test
    public void getFileByUUID() {
        resource.setSize(cipherFileSize*1024*1024-100);
        String uuid = "ksjghf-ljdlaksjda";
        resource.setLinkToFile(uuid);
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        assertEquals(resource, resourceService.getFileByUUID(resourceDTO.getFileUUID()));
    }

    @Test
    public void getExpectedFileUrlWithBigFile() {
        resource.setSize(cipherFileSize*1024*1024+100);
        String uuid = "ksjghf-ljdlaksjda";
        resource.setLinkToFile(uuid);
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        assertEquals(authServiceUrl+"files/"+uuid, resourceService.getFileURL(resourceDTO.getFileUUID()));
    }
    @Ignore
    @Test
    public void addResourceMetadata() throws IOException {
        resourceDTO.setResourceId(null);
        resourceDTO.setFileUUID(null);
        when(resourceDAO.insert(any())).thenReturn(resource);
        List<Resource> list = new ArrayList<>(3);
        for(int i=0;i<3;i++){
            Resource r = new Resource(); r.setLinkToFile("uuid"+i);
            list.add(r);
        }
        when(resourceDAO.findAll()).thenReturn(list);
        FileMetadataDTO resourceMetadata = resourceService.addResourceMetadata(resourceDTO);
        assertNotNull(resourceMetadata.getFileUUID());
        assertNotNull(resourceMetadata.getResourceId());
    }
    @Test
    public void addResourceMetadataWithoutKey() throws IOException {
        when(resourceDAO.update(any())).thenReturn(resource);
        resourceDTO.setKey(null);
        FileMetadataDTO resourceMetadata = resourceService.addResourceMetadata(resourceDTO);
        assertNull(resourceMetadata);
    }

    public void addResourceAndLoadToFileService() {

    }

    @Test
    public void deleteResource() {
        when(resourceDAO.findElementById(any())).thenReturn(Optional.of(resource));
        when(resourceDAO.update(any())).thenReturn(resource);
        when(httpClient.deleteFile(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        boolean isResourceDeleted = resourceService.deleteResource(resourceDTO.getOwnerId(), resourceDTO.getResourceId());
        assertTrue(isResourceDeleted);
        verify(httpClient, times(1)).deleteFile(any());
    }
    @Test
    public void deleteResourceWithIncorrectId() {
        when(resourceDAO.findElementById(any())).thenReturn(Optional.empty());
        when(resourceDAO.update(any())).thenReturn(resource);
        boolean isResourceDeleted = resourceService.deleteResource(resourceDTO.getOwnerId(), resourceDTO.getResourceId());
        assertFalse(isResourceDeleted);
    }

    /*@Test
    public void shareResource() {
        Resource shareResource = ResourceConverter.convertToEntity(resourceDTO);
        shareResource.setPermission(null);
        when(resourceDAO.findElementById(any())).thenReturn(Optional.of(shareResource));
        when(resourceDAO.update(any())).thenReturn(resource);
        when(permissionDAO.findOneByFieldEqual(any(),any())).thenReturn(Optional.of(permission));
        boolean isResourceShared = resourceService.shareExistingResource(resourceDTO,new HashSet<>(Arrays.asList(userHaveAccessToFile)));
        Resource resource = ResourceConverter.convertToEntity(resourceDTO);
        assertNotNull(resource.getPermission());
        assertEquals(resource.getPermission().getPermission(),resourceDTO.getPermission().toString());
        assertTrue(isResourceShared);
    }*/

    @Test
    public void isResourceExpiredWhenResourceIsExpired() {
        resource.getSecretKey().setExpirationDate(getTimestamp(expiredTimeString));
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        boolean isResourceExpired = resourceService.isResourceExpired(resourceDTO.getFileUUID());
        assertTrue(isResourceExpired);
    }
    @Test
    public void isResourceExpiredWhenResourceIsNotExpired() {
        resource.getSecretKey().setExpirationDate(getTimestamp(futureTimeString));
        when(resourceDAO.findElementByUUID(any())).thenReturn(Optional.of(resource));
        boolean isResourceExpired = resourceService.isResourceExpired(resourceDTO.getFileUUID());
        assertFalse(isResourceExpired);
    }
    @Test
    public void isResourceExpiredWithIncorrectResourceId() {
        Optional<Resource> resourceOptional = Optional.empty();
        when(resourceDAO.findElementByUUID(any())).thenReturn(resourceOptional);
        boolean isResourceExpired = resourceService.isResourceExpired(resourceDTO.getFileUUID());
        assertTrue(isResourceExpired);
    }

    @Test
    public void isUserOwnerWhenUserIsOwner() {
        Optional<Resource> resourceOptional = Optional.of(resource);
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        boolean isUserOwner = resourceService.isUserOwner(owner.getUserId(),resourceDTO);
        assertTrue(isUserOwner);
    }

    @Test
    public void isUserOwnerWhenUserIsNotOwner() {
        Optional<Resource> resourceOptional = Optional.of(resource);
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        boolean isUserOwner = resourceService.isUserOwner(userHaveAccessToFile.getUserId(),resourceDTO);
        assertFalse(isUserOwner);
    }

    @Test
    public void isUserOwnerWithNonexistentResourceId() {
        Optional<Resource> resourceOptional = Optional.empty();
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        boolean isUserOwner = resourceService.isUserOwner(75L, resourceDTO);
        assertFalse(isUserOwner);
    }

    @Test
    public void isUserCanReadFileWhenUserHaveAccessToFile() {
        Optional<Resource> resourceOptional = Optional.of(resource);
        Optional<User> userOptional = Optional.of(userHaveAccessToFile);
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        when(userDAO.findElementById(any())).thenReturn(userOptional);
        boolean isUserCanReadFile = resourceService.isUserCanReadFile(userHaveAccessToFile.getUserId(), resourceDTO);
        assertTrue(isUserCanReadFile);
    }

    @Test
    public void isUserCanReadFileWhenUserHaveNotAccessToFile() {
        Optional<Resource> resourceOptional = Optional.of(resource);
        Optional<User> userOptional = Optional.of(userHaveNotAccessToFile);
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        when(userDAO.findElementById(any())).thenReturn(userOptional);
        resourceDTO.setPermission(PermissionTypeDTO.LIST_OF_USERS);
        boolean isUserCanReadFile = resourceService.isUserCanReadFile(userHaveNotAccessToFile.getUserId(), resourceDTO);
        assertFalse(isUserCanReadFile);
    }

    @Test
    public void isUserCanReadFileWhenUserIsOwner() {
        Optional<User> userOptional = Optional.of(owner);
        Optional<Resource> resourceOptional = Optional.of(resource);
        when(resourceDAO.findElementById(any())).thenReturn(resourceOptional);
        when(userDAO.findElementById(any())).thenReturn(userOptional);
        resourceDTO.setPermission(PermissionTypeDTO.LIST_OF_USERS);
        boolean isUserCanReadFile = resourceService.isUserCanReadFile(owner.getUserId(), resourceDTO);
        assertTrue(isUserCanReadFile);
    }

    @Test
    public void isUserCanReadFileWhenAllUsersCanReadFile() {
        when(resourceDAO.findElementById(any())).thenReturn(Optional.of(resource));
        when(userDAO.findElementById(any())).thenReturn(Optional.of(owner));

        resourceDTO.setPermission(PermissionTypeDTO.ALL_USERS);
        boolean isUserCanReadFile = resourceService.isUserCanReadFile(owner.getUserId(), resourceDTO);
        assertTrue(isUserCanReadFile);

        when(userDAO.findElementById(any())).thenReturn(Optional.of(userHaveAccessToFile));
        isUserCanReadFile = resourceService.isUserCanReadFile(userHaveAccessToFile.getUserId(), resourceDTO);
        assertTrue(isUserCanReadFile);

        when(userDAO.findElementById(any())).thenReturn(Optional.of(userHaveNotAccessToFile));
        isUserCanReadFile = resourceService.isUserCanReadFile(userHaveNotAccessToFile.getUserId(), resourceDTO);
        assertTrue(isUserCanReadFile);

        when(userDAO.findElementById(any())).thenReturn(Optional.empty());
        isUserCanReadFile = resourceService.isUserCanReadFile( 5L, resourceDTO);
        assertFalse(isUserCanReadFile);
    }
}
