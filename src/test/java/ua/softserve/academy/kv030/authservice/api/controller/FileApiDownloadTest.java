package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.PermissionTypeDTO;
import ua.softserve.academy.kv030.authservice.api.model.ShareDataDTO;
import ua.softserve.academy.kv030.authservice.converter.UserConverter;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.DaoLayerException;
import ua.softserve.academy.kv030.authservice.exceptions.ResourceNotFoundException;
import ua.softserve.academy.kv030.authservice.services.AuthService;
import ua.softserve.academy.kv030.authservice.services.ResourceService;
import ua.softserve.academy.kv030.authservice.services.ResourceServiceImplTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileApiDownloadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authServiceMock;
    @MockBean
    private ResourceService resourceService;
    @Autowired
    private ObjectMapper mapper;

    private MockMultipartFile multipartFile;
    private Long userId;
    private FileMetadataDTO fileMetadataDTO;
    private User user1, user2;
    private Set<User> users;
    ShareDataDTO shareDataDTO;
    private List<Long> usersId;

    @Before
    public void setUp() throws Exception {
        multipartFile = new MockMultipartFile(
                "file", "12345", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        userId = 7L;
        fileMetadataDTO = new FileMetadataDTO();
        fileMetadataDTO.setPermission(PermissionTypeDTO.LIST_OF_USERS);
        fileMetadataDTO.setResourceId(1L);
        fileMetadataDTO.setFileName("name");
        fileMetadataDTO.setFileSize(200L);
        fileMetadataDTO.setOwnerId(1L);
        fileMetadataDTO.setFileUUID("uuid");
        fileMetadataDTO.setMime("hello");
        fileMetadataDTO.setKey("key");
        fileMetadataDTO.setExpirationTime(OffsetDateTime.of(2017, 3, 3, 3, 3, 3, 3, ZoneOffset.UTC));
        user1 = new User();
        user2 = new User();
        user1.setUserId(1);
        user2.setUserId(2);
        users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        usersId = new ArrayList<>();
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());
        shareDataDTO = new ShareDataDTO();
        shareDataDTO.setFileMetadata(fileMetadataDTO);
        shareDataDTO.setPermittedUserIds(usersId);
    }

    @Test
    @WithMockUser
    public void getFileUrlSucceeded() throws Exception{
        String uuid = fileMetadataDTO.getFileUUID();
        String expectedUrl = String.format("http://localhost:8585/files/%s",uuid);
        given(resourceService.getFileURL(any())).willReturn(expectedUrl);

        RequestBuilder requestBuilder = get("/files/{uuid}/url/",uuid)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        MvcResult res =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();
        String responseUrl = res.getResponse().getContentAsString();
        assertEquals(expectedUrl,responseUrl);
    }
    @Test
    @WithMockUser
    public void getFileUrlFailed() throws Exception{
        String uuid = fileMetadataDTO.getFileUUID();
        given(resourceService.getFileURL(any())).willReturn(null);

        RequestBuilder requestBuilder = get("/files/{uuid}/url/",uuid)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(resourceService,times (1)).getFileURL(any());
    }
    @Test
    @WithMockUser
    @Ignore
    public void downloadFileFailed() throws Exception{
        String uuid = fileMetadataDTO.getFileUUID();
        given(resourceService.downloadFile(any(), any())).willReturn(null);

        RequestBuilder requestBuilder = get("/files/{uuid}/",uuid)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_OCTET_STREAM);
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(resourceService,times (1)).downloadFile(any(), any());
    }
    @Ignore
    @Test //Failed to invoke @ExceptionHandler method
    public void downloadFileFailedWithException() throws Exception{
        String uuid = fileMetadataDTO.getFileUUID();
        given(resourceService.downloadFile(any(), any())).willThrow(new DaoLayerException("akshjlanks"));

        RequestBuilder requestBuilder = get("/files/{uuid}/",uuid)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_OCTET_STREAM);
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(resourceService,times (1)).downloadFile(any(), any());
    }
    @Ignore
    @Test
    @WithMockUser
    public void downloadFileSucceeded() throws Exception{
        byte[] array = new byte[256];
        Arrays.fill(array,(byte)5);
        String uuid = fileMetadataDTO.getFileUUID();
        given(resourceService.downloadFile(any(), any())).willReturn(array);
        given(resourceService.getFileNameByUUID(any())).willReturn("file.txt");
        RequestBuilder requestBuilder = get("/files/{uuid}/",uuid)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_OCTET_STREAM);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("attachment; filename=file.txt",result.getResponse().getHeader("Content-Disposition"));
        byte responseArray[] = result.getResponse().getContentAsByteArray();
        assertTrue(Arrays.equals(array,responseArray));
        verify(resourceService,times (1)).downloadFile(any(), any());
    }
}