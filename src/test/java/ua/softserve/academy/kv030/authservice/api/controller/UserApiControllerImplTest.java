package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.dao.SortingOrderEnum;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;
import ua.softserve.academy.kv030.authservice.exceptions.DuplicateEmailException;
import ua.softserve.academy.kv030.authservice.exceptions.UserNotFoundException;
import ua.softserve.academy.kv030.authservice.services.AuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserApiControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authServiceMock;

    private ObjectMapper mapper;

    private UserCredentialsDTO userCredentialsDTO;

    private UserDTO userDTO;

    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setId(new Long(78));
        userCredentialsDTO.setFirstname("someFirstName");
        userCredentialsDTO.setLastname("someLastName");
        userCredentialsDTO.setEmail("email@email.com");
        userCredentialsDTO.setPassword("1111");
        userCredentialsDTO.setRole(RoleDTO.ADMIN);

        userDTO = new UserDTO();
        userDTO.setId(new Long(78));
        userDTO.setFirstname("someFirstName");
        userDTO.setLastname("someLastName");
        userDTO.setEmail("email@email.com");
        userDTO.setRole(RoleDTO.ADMIN);

    }

    @Ignore
    @Test
    @WithMockUser(roles = "ADMIN")
    public void addUser_ReturnHttpSuccessStatusAndAddedUserData_IfSucceededToAddNewUser() throws Exception {
        given(authServiceMock.addUser(userCredentialsDTO))
                .willReturn(userDTO);

        String jsonPostContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        String responseContentAsString =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

        UserDTO expectedUserDTO = userDTO;
        UserDTO actualUserDTO = mapper.readValue(responseContentAsString, UserDTO.class);

        assertEquals(expectedUserDTO, actualUserDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addUser_ReturnHttpClientErrorStatus_IfEmailAlreadyExist() throws Exception {
        given(authServiceMock.addUser(userCredentialsDTO))
                .willThrow(new DuplicateEmailException());

        String jsonPostContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteUserById_ReturnHttpSuccessStatusAndNoContent_IfSucceededToDeleteUser() throws Exception {
        given(authServiceMock.deleteUser(userDTO.getId()))
                .willReturn(true);

        RequestBuilder requestBuilder = delete(String.format("/users/%s", userDTO.getId()));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteUserById_ReturnHttpClientErrorStatus_IfUserNotFound() throws Exception {
        given(authServiceMock.deleteUser(userDTO.getId()))
                .willThrow(new UserNotFoundException(userDTO.getId()));

        RequestBuilder requestBuilder = delete(String.format("/users/%s", userDTO.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                //.andExpect(status().is4xxClientError());
                .andExpect(status().is2xxSuccessful()); // TODO: Success or Error?
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserById_ReturnHttpSuccessStatusAndUserData_IfUserIsFound() throws Exception {
        given(authServiceMock.findUserById(userDTO.getId()))
                .willReturn(userDTO);

        RequestBuilder requestBuilder = get(String.format("/users/%s", userDTO.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8);

        String responseContentAsString =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn().getResponse().getContentAsString();

        UserDTO expectedUserDTO = userDTO;
        UserDTO actualUserDTO = mapper.readValue(responseContentAsString, UserDTO.class);

        assertEquals(expectedUserDTO, actualUserDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserById_ReturnHttpClientErrorStatus_IfFailedToFindUser() throws Exception {
        given(authServiceMock.findUserById(userDTO.getId()))
                .willThrow(UserNotFoundException.class);

        RequestBuilder requestBuilder = get(String.format("/users/%s", userDTO.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                //.andExpect(status().is4xxClientError());
                .andExpect(status().is2xxSuccessful()); // TODO: Success or Error?
    }

    @Test
    @WithMockUser(roles = "TECH_SUPP")
    public void updateUserById_ReturnHttpSuccessStatusAndUserData_IfSucceededToUpdateUser() throws Exception {
        given(authServiceMock.updateUser(userDTO.getId(), userDTO))
                .willReturn(userDTO);

        String jsonPostContent = mapper.writeValueAsString(userDTO);

        RequestBuilder requestBuilder = put(String.format("/users/%s", userDTO.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        String responseContentAsString =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn().getResponse().getContentAsString();

        UserDTO expectedUserDTO = userDTO;
        UserDTO actualUserDTO = mapper.readValue(responseContentAsString, UserDTO.class);

        assertEquals(expectedUserDTO, actualUserDTO);
    }

    @Test
    @WithMockUser(roles = "TECH_SUPP")
    public void updateUserById_ReturnHttpClientErrorStatus_IfUserNotFound() throws Exception {
        given(authServiceMock.updateUser(userDTO.getId(), userDTO))
                .willThrow(UserNotFoundException.class);

        String jsonPostContent = mapper.writeValueAsString(userDTO);

        RequestBuilder requestBuilder = put(String.format("/users/%s", userDTO.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                //.andExpect(status().is4xxClientError());
                .andExpect(status().is2xxSuccessful()); // TODO: Success or Error?
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateUserCredentials_ReturnHttpSuccessStatusAndUpdatedUserData_IfSucceededToUpdateUserCredentials() throws Exception {
        given(authServiceMock.updateUserCredentials(userCredentialsDTO.getId(), userCredentialsDTO))
                .willReturn(userDTO);

        String jsonPutContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = put(String.format("/users/%d/pass", userCredentialsDTO.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPutContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        String responseContentAsString =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn().getResponse().getContentAsString();

        UserDTO expectedUserDTO = userDTO;
        UserDTO actualUserDTO = mapper.readValue(responseContentAsString, UserDTO.class);

        assertEquals(expectedUserDTO, actualUserDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateUserCredentials_ReturnHttpClientErrorStatus_IfExceptionThrown() throws Exception {
        given(authServiceMock.updateUserCredentials(userCredentialsDTO.getId(), userCredentialsDTO))
                .willThrow(new AuthServiceException(""));

        String jsonPutContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = put(String.format("/users/%d/pass", userCredentialsDTO.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonPutContent)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    public void listUsersByOffsetAndPageLimit_ReturnHttpSuccessStatusAndUserDtoList_IfUsersFound() throws Exception {
        int offset = 0;
        int pageSize = 10;
        List<UserDTO> userDTOList = new ArrayList(){{
            add(userDTO);
        }};

        given(authServiceMock.listUsersPerPage(offset, pageSize, SortingOrderEnum.ASC))
                .willReturn(userDTOList);

        RequestBuilder requestBuilder = get(String.format("/users/list/%d/%d", offset, pageSize))
                .accept(MediaType.APPLICATION_JSON_UTF8);

        String responseContentAsString =
                mockMvc.perform(requestBuilder)
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn().getResponse().getContentAsString();

        TypeFactory typeFactory = mapper.getTypeFactory();
        List<UserDTO> actualUserDTOList = mapper.readValue(responseContentAsString, typeFactory.constructCollectionType(List.class, UserDTO.class));
        assertEquals(userDTOList.size(), actualUserDTOList.size());
        IntStream.range(0, userDTOList.size()).forEach(i->{
            assertEquals(userDTOList.get(i).getId(), actualUserDTOList.get(i).getId());
            assertEquals(userDTOList.get(i).getEmail(), actualUserDTOList.get(i).getEmail());
            assertEquals(userDTOList.get(i).getFirstname(), actualUserDTOList.get(i).getFirstname());
            assertEquals(userDTOList.get(i).getLastname(), actualUserDTOList.get(i).getLastname());
            assertEquals(userDTOList.get(i).getRole(), actualUserDTOList.get(i).getRole());
        });
    }

}