package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import ua.softserve.academy.kv030.authservice.exceptions.PermissionException;
import ua.softserve.academy.kv030.authservice.services.AuthService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationApiControllerImplTest {

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

        userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setId(new Long(78));
        userCredentialsDTO.setFirstname("FirstName");
        userCredentialsDTO.setLastname("LastName");
        userCredentialsDTO.setEmail("email@email.com");
        userCredentialsDTO.setPassword("1111");
        userCredentialsDTO.setRole(RoleDTO.USER);

        userDTO = new UserDTO();
        userDTO.setId(new Long(78));
        userDTO.setFirstname("FirstName");
        userDTO.setLastname("LastName");
        userDTO.setEmail("email@email.com");
        userDTO.setRole(RoleDTO.USER);

    }

    @Test
    @WithMockUser
    public void logoutUser_ReturnHttpSuccessStatusAndNoContent_IfSucceededToLogoutUser() throws Exception {
        given(authServiceMock.logoutUser())
                .willReturn(true);

        RequestBuilder requestBuilder = get(String.format("/user/logout"));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    public void logoutUser_ReturnHttpClientErrorStatusAndNoContent_IfFailedToLogoutUser() throws Exception {
        given(authServiceMock.logoutUser())
                .willReturn(false);

        RequestBuilder requestBuilder = get(String.format("/user/logout"));
                //.header(Constants.TOKEN_HEADER, token);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(""));
    }

    @Test
    public void loginUser_ReturnHttpSuccessStatusAndUser_IfSucceededToLoginUser() throws Exception {
        given(authServiceMock.loginUser(userCredentialsDTO))
                .willReturn(userDTO);

        String jsonPostContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = post(String.format("/user/login"))
                .contentType(MediaType.APPLICATION_JSON).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(mapper.writeValueAsString(userDTO)));
    }

    @Test
    public void loginUser_ReturnHttpClientErrorStatusAndNoContent_IfFailedToLoginUser() throws Exception {
        given(authServiceMock.loginUser(userCredentialsDTO))
                .willThrow(new PermissionException());

        String jsonPostContent = mapper.writeValueAsString(userCredentialsDTO);

        RequestBuilder requestBuilder = post(String.format("/user/login"))
                .contentType(MediaType.APPLICATION_JSON).content(jsonPostContent)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}