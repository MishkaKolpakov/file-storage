package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.PermissionTypeDTO;
import ua.softserve.academy.kv030.authservice.api.model.ShareDataDTO;
import ua.softserve.academy.kv030.authservice.converter.UserConverter;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.services.AuthService;
import ua.softserve.academy.kv030.authservice.services.ResourceService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileApiControllerImplTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authServiceMock;
    @MockBean
    private ResourceService resourceService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private FileApiControllerImpl fileApiController;

    private MockMultipartFile multipartFile;
    private Long userId;
    private FileMetadataDTO fileMetadataDTO;
    private User user1, user2;
    private Set<User> users;
    private ShareDataDTO shareDataDTO;

    @Before
    public void setUp() {
        List<Long> usersId;
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
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
    public void uploadFile() throws Exception {

        fileMetadataDTO.setFileUUID("1");
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fileName", "test.txt");
        multiValueMap.add("fileSize", String.valueOf(multipartFile.getSize()));
        multiValueMap.add("mime", String.valueOf(multipartFile.getContentType()));


        given(fileApiController.uploadFile(fileMetadataDTO, multipartFile, Optional.empty()))
                .willReturn(new ResponseEntity<>(fileMetadataDTO, new HttpHeaders(), HttpStatus.CREATED));

        RequestBuilder requestBuilder = fileUpload("/user/files/uploadFile")
                .file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA)
                .params(multiValueMap)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

    @Test
    @WithMockUser
    public void FailedToUploadFile() throws Exception {
        fileMetadataDTO.setFileUUID("1");
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fileName", "test.txt");
        multiValueMap.add("expirationTime", OffsetDateTime.now().toString());
        multiValueMap.add("fileSize", String.valueOf(multipartFile.getSize()));
        multiValueMap.add("mime", String.valueOf(multipartFile.getContentType()));

        given(fileApiController.uploadFile(fileMetadataDTO, multipartFile, Optional.empty()))
                .willReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        RequestBuilder requestBuilder = fileUpload(String.format("/user/%s/files/uploadFile", userId))
                .file(multipartFile)
                .params(multiValueMap)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser
    public void fileAddMetadataSuccess() throws Exception {
        given(fileApiController.addFileMetadata(fileMetadataDTO))
                .willReturn(new ResponseEntity<>(HttpStatus.OK));

        String jsonPostContent = mapper.writeValueAsString(fileMetadataDTO);

        RequestBuilder requestBuilder = (post("/user/files/addFileMetadata"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void fileAddMetadataError() throws Exception {
        given(fileApiController.addFileMetadata(fileMetadataDTO))
                .willReturn(null);

        String jsonPostContent = mapper.writeValueAsString(fileMetadataDTO);

        RequestBuilder requestBuilder = (post("/user/files/addFileMetadata"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());


    }

    /*@Test
    @WithMockUser
    public void shareFile_ReturnHttpSuccessStatusAndNoContent_IfSucceededToShareFile() throws Exception {
        given(authServiceMock.findUserById(1)).willReturn(UserConverter.convertToDto(user1));
        given(authServiceMock.findUserById(2)).willReturn(UserConverter.convertToDto(user2));
        given(resourceService.shareExistingResource(fileMetadataDTO, users))
                .willReturn(true);

        String jsonPostContent = mapper.writeValueAsString(shareDataDTO);

        RequestBuilder requestBuilder = put("/user/files/share")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }*/

    /*@Test
    @WithMockUser
    public void shareFile_ReturnHttpClientErrorStatusAndNoContent_IfFailedToShareFile() throws Exception {
        given(authServiceMock.findUserById(1)).willReturn(UserConverter.convertToDto(user1));
        given(authServiceMock.findUserById(2)).willReturn(UserConverter.convertToDto(user2));
        given(resourceService.shareExistingResource(fileMetadataDTO, users))
                .willReturn(true);

        String jsonPostContent = mapper.writeValueAsString(null);

        RequestBuilder requestBuilder = put("/user/files/share")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }*/

}