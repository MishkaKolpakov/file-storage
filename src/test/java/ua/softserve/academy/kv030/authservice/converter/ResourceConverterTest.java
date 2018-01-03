package ua.softserve.academy.kv030.authservice.converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.PermissionTypeDTO;
import ua.softserve.academy.kv030.authservice.entity.Permission;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;
import ua.softserve.academy.kv030.authservice.entity.User;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class ResourceConverterTest {
    private static Resource resource;
    private static FileMetadataDTO resourceDTO;
    private static SecretKey key;
    private static Permission permission;
    private static User user;
    private static String timeString = "2017-11-19T15:25:09.651+02:00";
    private static OffsetDateTime offsetDateTime ;
    private static Timestamp timestamp ;
    @Before
    public void setUp() {
        resource = new Resource();
        resourceDTO = new FileMetadataDTO();
        key = new SecretKey();
        user = new User();
        permission = new Permission();
        key.setStatus(true);
        //create similar OffsetDateTime and Timestamp values
        offsetDateTime = OffsetDateTime.parse(timeString);
        timestamp = Timestamp.valueOf(offsetDateTime.toLocalDateTime());
        //set init values into resourceDTO
        resourceDTO.setOwnerId((long) 1);
        resourceDTO.setResourceId((long) 1);
        resourceDTO.setKey("mysupersecretekey");
        resourceDTO.setFileUUID("fileuuid");
        resourceDTO.setMime("text");
        resourceDTO.setFileName("tempfile.jpg");
        resourceDTO.setPermission(PermissionTypeDTO.ALL_USERS);
        resourceDTO.setExpirationTime(offsetDateTime);
        resourceDTO.setFileSize(256L);
        //set init values into key
        key.setKeyId((long)1);
        key.setStatus(true);
        key.setKey("mysupersecretkey");
        key.setExpirationDate(timestamp);

        //set init values into resource
        resource.setResourceId((long)1);
        resource.setMimeType("text");
        resource.setLinkToFile("fileuuid");
        resource.setFileName("tempfile.jpg");
        resource.setSize((long)256);
        //set init values into permission
        permission.setPermission("ALL_USERS");
        permission.setPermissionId(1);

        //set init values into user
        user.setUserId(1);
        user.setFirstName("user");
        user.setLastName("lastName");
        //bind entities in resource
        resource.setPermission(permission);
        resource.setOwner(user);
        resource.setSecretKey(key);
        //TODO delete
//        System.out.println(timestamp.toString());
//        System.out.println(offsetDateTime);
    }

    @After
    public void tearDown() {
        resourceDTO = null;
        resource = null;
        key = null;
        user = null;
        permission = null;
    }

    @Test
    public void convertToDto() {
        FileMetadataDTO newDTO = ResourceConverter.convertToDto(resource);
        assertEquals(resource.getResourceId(),(long)newDTO.getResourceId());
        assertTrue(newDTO.getExpirationTime().toString().contains(offsetDateTime.toLocalDateTime().toString()));
        assertEquals(resource.getFileName(),newDTO.getFileName());
        assertEquals(resource.getLinkToFile(),newDTO.getFileUUID());
        assertEquals(resource.getSecretKey().getKey(),newDTO.getKey());
        assertEquals(resource.getMimeType(),newDTO.getMime());
        assertEquals(resource.getOwner().getUserId(),(long)newDTO.getOwnerId());
        assertEquals(resource.getPermission().getPermission(),newDTO.getPermission().toString());
        assertEquals(resource.getSize(),(long)newDTO.getFileSize());
    }

    @Test
    public void convertToEntity() {
        Resource newResource = ResourceConverter.convertToEntity(resourceDTO);
        assertEquals((Long)newResource.getResourceId(),resourceDTO.getResourceId());
        assertEquals(newResource.getLinkToFile(),resourceDTO.getFileUUID());
        assertEquals(newResource.getSecretKey().getKey(),resourceDTO.getKey());
        assertEquals(newResource.getSecretKey().getExpirationDate(),timestamp);
        assertEquals((Long)newResource.getOwner().getUserId(),resourceDTO.getOwnerId());
        assertEquals(newResource.getFileName(),resourceDTO.getFileName());
        assertEquals(newResource.getMimeType(),resourceDTO.getMime());
        assertEquals(newResource.getSize(),(long)resourceDTO.getFileSize());
        assertEquals(newResource.getPermission().getPermission(),resourceDTO.getPermission().toString());
    }
    @Test
    public void convertToEntityWithoutUUIDAndPermission() {
        resourceDTO.setPermission(null);
        resourceDTO.setFileUUID(null);
        Resource newResource = ResourceConverter.convertToEntity(resourceDTO);
        assertEquals((Long)newResource.getResourceId(),resourceDTO.getResourceId());

        assertEquals(newResource.getSecretKey().getKey(),resourceDTO.getKey());
        assertEquals(newResource.getSecretKey().getExpirationDate(),timestamp);
        assertEquals((Long)newResource.getOwner().getUserId(),resourceDTO.getOwnerId());
        assertEquals(newResource.getFileName(),resourceDTO.getFileName());
        assertEquals(newResource.getMimeType(),resourceDTO.getMime());
        assertEquals(newResource.getSize(),(long)resourceDTO.getFileSize());
        assertNull(newResource.getPermission());
        assertNull(newResource.getLinkToFile());
    }
    @Test
    public void convertToEntityWithoutKey() {
        resourceDTO.setKey(null);
        Resource newResource = ResourceConverter.convertToEntity(resourceDTO);
        assertNull(newResource.getSecretKey().getKey());
        assertNotNull(newResource.getSecretKey());
        assertEquals(timestamp, newResource.getSecretKey().getExpirationDate());
    }

    @Test
    public void convertToEntityWithoutResourceId() {
        resourceDTO.setResourceId(null);
        Resource newResource = ResourceConverter.convertToEntity(resourceDTO);
        assertEquals(0,newResource.getResourceId());
    }
    @Test
    public void convertToEntityWithNullValues() {
        Resource newResource = ResourceConverter.convertToEntity(new FileMetadataDTO());
        assertEquals(0,newResource.getResourceId());
        assertNull(newResource.getPermission());
        assertEquals(0,newResource.getSize());
        assertNull(newResource.getMimeType());
        assertNull(newResource.getFileName());
        assertNull(newResource.getLinkToFile());
        assertNotNull(newResource.getSecretKey());
        assertNotNull(newResource.getOwner());
    }
    @Test
    public void convertToEntityDtoWithoutNullValues() {
        Resource nullResource = new Resource();
        nullResource.setResourceId(0);
        nullResource.setSize(0);
        FileMetadataDTO newDTO = ResourceConverter.convertToDto(nullResource);
        assertNull(newDTO.getResourceId());
        assertNull(newDTO.getFileSize());
        assertNull(newDTO.getOwnerId());
        assertNull(newDTO.getFileUUID());
        assertNull(newDTO.getFileSize());
        assertNull(newDTO.getPermission());
        assertNull(newDTO.getMime());
        assertNull(newDTO.getKey());
        assertNull(newDTO.getFileName());
        assertNull(newDTO.getExpirationTime());
    }

}
