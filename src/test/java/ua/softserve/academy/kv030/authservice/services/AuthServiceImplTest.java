package ua.softserve.academy.kv030.authservice.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.converter.UserConverter;
import ua.softserve.academy.kv030.authservice.converter.UserCredentialsConverter;
import ua.softserve.academy.kv030.authservice.dao.*;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.*;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClient;
import ua.softserve.academy.kv030.authservice.services.mail.EmailContentFactory;
import ua.softserve.academy.kv030.authservice.services.mail.EmailContentHolder;
import ua.softserve.academy.kv030.authservice.services.mail.EmailServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    private static User user, user2;
    private static Role adminRole;
    private static UserDTO userDTO, updatedUserDTO, incorrectUserDTO;
    private static UserCredentialsDTO userCredentialsDTO, updatedUserCredentialsDTO, incorrectUserCredentialsDTO;
    private static AuthServiceImpl authService;
    private static List<User> users;
    private static UserDAO userDAO;
    private static RoleDAO roleDAO;
    private static PasswordDAO passwordDAO;
    private static ResourceDAO resourceDAO;
    private static JavaMailSender javaMailSender;
    private static EmailContentFactory emailContentFactory;
    private static EmailContentHolder emailContentHolder;
    private static HttpClient httpClient;
    private static PasswordEncoder passwordEncoder;
    private static List<Password> passwords;

    private static FileMetadataDTO metadataDTO;

    @Before
    public void init() {
        metadataDTO = mock(FileMetadataDTO.class);
        httpClient = mock(HttpClient.class);
        emailContentFactory = mock(EmailContentFactory.class);
        javaMailSender = mock(JavaMailSender.class);
        emailContentHolder = mock(EmailContentHolder.class);
        userDAO = mock(UserDAO.class);
        resourceDAO = mock(ResourceDAO.class);
        roleDAO = mock(RoleDAO.class);
        passwordDAO = mock(PasswordDAO.class);
        passwordEncoder = new BCryptPasswordEncoder();
        adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        Role role = new Role();
        role.setRoleName("ADMIN");
        Password password = new Password();
        password.setPassword("Password");
        password.setStatus(true);
        passwords = new ArrayList<>();
        passwords.add(password);
        updatedUserDTO = new UserDTO();
        updatedUserDTO.setFirstname("Updated First Name");
        updatedUserDTO.setLastname("Updated Last Name");
        updatedUserDTO.setEmail("Email");
        updatedUserDTO.setRole(RoleDTO.USER);
        updatedUserCredentialsDTO = new UserCredentialsDTO();
        updatedUserCredentialsDTO.setFirstname("Updated First Name");
        updatedUserCredentialsDTO.setLastname("Updated Last Name");
        updatedUserCredentialsDTO.setEmail("Email");
        updatedUserCredentialsDTO.setPassword("UpdatedPassword");
        updatedUserCredentialsDTO.setRole(RoleDTO.USER);
        user = new User();
        user.setUserId(1);
        user.setRole(role);
        user.setFirstName("Artem");
        user.setLastName("Lastname");
        user.setEmail("Email");
        user.setPasswords(passwords);
        user.setStatus(true);
        user2 = new User();
        user2.setRole(role);
        user2.setFirstName("Artem");
        user2.setLastName("Lastname");
        user2.setUserId(2);
        user2.setEmail("NotUnique");
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setRole(RoleDTO.ADMIN);
        userDTO.setFirstname("Artem");
        userDTO.setLastname("Lastname");
        userDTO.setEmail("Email");
        userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setEmail("Email");
        userCredentialsDTO.setPassword("Password");
        userCredentialsDTO.setFirstname("Artem");
        userCredentialsDTO.setLastname("Lastname");
        userCredentialsDTO.setRole(RoleDTO.ADMIN); // TODO : added
        incorrectUserCredentialsDTO = new UserCredentialsDTO();
        incorrectUserCredentialsDTO.setFirstname("Artem");
        incorrectUserCredentialsDTO.setLastname("Lastname");
        incorrectUserCredentialsDTO.setEmail("NotUnique");
        incorrectUserCredentialsDTO.setPassword("Password");
        incorrectUserCredentialsDTO.setRole(RoleDTO.ADMIN); // TODO : added
        incorrectUserDTO = new UserDTO();
        incorrectUserDTO.setEmail("NotUnique");
        incorrectUserDTO.setFirstname("Artem");
        incorrectUserDTO.setLastname("Lastname");
        incorrectUserDTO.setRole(RoleDTO.ADMIN); // TODO : added
        users = new ArrayList<>();
        users.add(user2);
        authService = new AuthServiceImpl(userDAO, resourceDAO, new EmailServiceImpl(javaMailSender, emailContentHolder), httpClient, passwordDAO, passwordEncoder, roleDAO);
    }

    @Test(expected = DuplicateEmailException.class)
    public void addUserWithExistingEmail() throws Exception {
        when(userDAO.findAll()).thenReturn(users);
        Role role = new Role();
        role.setRoleName(incorrectUserCredentialsDTO.getRole().toString());
        when(userDAO.containsEmail(incorrectUserCredentialsDTO.getEmail())).thenReturn(true);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.of(role));
        authService.addUser(incorrectUserCredentialsDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void addUserWithRoleNonExistingInDatabase() throws Exception {
        when(userDAO.findAll()).thenReturn(users);
        Role role = new Role();
        role.setRoleName(incorrectUserCredentialsDTO.getRole().toString());
        when(userDAO.containsEmail(incorrectUserCredentialsDTO.getEmail())).thenReturn(false);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.ofNullable(null));
        authService.addUser(incorrectUserCredentialsDTO);
    }

    @Test
    public void addUser() throws Exception {
        User newUser = UserCredentialsConverter.convertToEntity(userCredentialsDTO);
        newUser.setUserId(1);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.of(adminRole));
        when(userDAO.insert(any())).thenReturn(newUser);
        when(userDAO.findAll()).thenReturn(users);
        UserDTO newUserDTO = authService.addUser(userCredentialsDTO);
        assertEquals(newUserDTO.getId().longValue(), user.getUserId());
        assertEquals(newUserDTO.getEmail(), userCredentialsDTO.getEmail());
        assertEquals(newUserDTO.getFirstname(), userCredentialsDTO.getFirstname());
        assertEquals(newUserDTO.getLastname(), userCredentialsDTO.getLastname());
        assertEquals(newUserDTO.getRole(), userCredentialsDTO.getRole());
        verify(userDAO, times(1)).insert(any());
    }

    @Test
    public void activateUser() throws Exception {
        user.setStatus(false);
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        when(userDAO.update(any())).thenReturn(user);
        UserDTO userDTO = authService.activateUser(1L);
        assertTrue(user.isStatus());
        assertEquals(userDTO, UserConverter.convertToDto(user));
    }

    @Test
    public void login() throws Exception {
        Password passwordForLogin = new Password();
        String hashedPassword = passwordEncoder.encode(userCredentialsDTO.getPassword());
        passwordForLogin.setPassword(hashedPassword);
        Optional<User> userOptional = Optional.ofNullable(user);
        Optional<Password> password = Optional.ofNullable(passwordForLogin);
        when(userDAO.findUserByEmail(any())).thenReturn(userOptional);
        when(passwordDAO.findActivePasswordByUserId(anyLong())).thenReturn(password);
        UserDTO userDTO = authService.loginUser(userCredentialsDTO);
        assertEquals(UserConverter.convertToDto(user), userDTO);
    }

    @Test(expected = PermissionException.class)
    public void loginFailed() throws Exception {
        Optional<User> userOptional = Optional.ofNullable(user);
        Optional<Password> password = Optional.ofNullable(passwords.get(0));
        when(userDAO.findUserByEmail(any())).thenReturn(userOptional);
        when(passwordDAO.findActivePasswordByUserId(anyLong())).thenReturn(password);
        UserDTO userDTO = authService.loginUser(userCredentialsDTO);
        assertEquals(UserConverter.convertToDto(user), userDTO);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserWithIncorrectID() throws Exception {
        Optional<User> userOptional = Optional.empty();
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        boolean isDeleted = authService.deleteUser(0);
        //assertFalse(isDeleted);
    }

    @Test
    public void deleteUser() throws Exception {
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        when(userDAO.update(any())).thenReturn(user);
        assertTrue(user.isStatus());
        boolean isDeleted = authService.deleteUser(1);
        assertFalse(user.isStatus());
        assertTrue(isDeleted);
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserByIncorrectId() throws Exception {
        Optional<User> userOptional = Optional.empty();
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        UserDTO newUserDTO = authService.findUserById(0);
        verify(userDAO, times(1)).findElementById(0L);
        //assertNull(newUserDTO);
    }

    @Test
    public void findUserById() throws Exception {
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        UserDTO newUserDTO = authService.findUserById(1);
        assertEquals(newUserDTO.getId().longValue(), user.getUserId());
        assertEquals(newUserDTO.getEmail(), user.getEmail());
        assertEquals(newUserDTO.getFirstname(), user.getFirstName());
        assertEquals(newUserDTO.getLastname(), user.getLastName());
        assertEquals(newUserDTO.getRole().toString(), user.getRole().getRoleName());
        verify(userDAO, times(1)).findElementById(any());
    }

    @Test
    public void updateUser() throws Exception {
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        //when(userDAO.update(any())).thenReturn(user);
        when(userDAO.findAll()).thenReturn(users);
        Role updatedRole = new Role();
        updatedRole.setRoleName(updatedUserDTO.getRole().toString());
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.of(updatedRole));
        UserDTO newUserDTO = authService.updateUser(1, updatedUserDTO);
        assertEquals(newUserDTO.getId().longValue(), user.getUserId());
        assertEquals(newUserDTO.getEmail(), updatedUserDTO.getEmail());
        assertEquals(newUserDTO.getFirstname(), updatedUserDTO.getFirstname());
        assertEquals(newUserDTO.getLastname(), updatedUserDTO.getLastname());
        assertEquals(newUserDTO.getRole().toString(), updatedUserDTO.getRole().toString());
        verify(userDAO, times(1)).findElementById(any());
        verify(userDAO, times(1)).update(any());
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateUserWithRoleNonExistingInDatabase() throws Exception {
        when(userDAO.containsEmail(updatedUserDTO.getEmail())).thenReturn(true);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.ofNullable(null));
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        authService.updateUser(1, updatedUserDTO);
        verify(userDAO, times(1)).findElementById(any());
        verify(userDAO, times(1)).update(any());
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserWithIncorrectId() throws Exception {
        Optional<User> userOptional = Optional.empty();
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        UserDTO newUserDTO = authService.updateUser(0, updatedUserDTO);
        verify(userDAO, times(0)).update(any());
        //assertNull(newUserDTO);
    }

    @Test(expected = AuthServiceException.class)
    public void updateUserWithIncorrectInputData() throws Exception {
        UserDTO newUserDTO = authService.updateUser(0, null);
        verify(userDAO, times(0)).update(any());
        //assertNull(newUserDTO);
    }

    @Test
    public void updateUserInDB() throws Exception {
        User newUser = new User();
        newUser.setFirstName(updatedUserCredentialsDTO.getFirstname());
        newUser.setLastName(updatedUserCredentialsDTO.getLastname());
        newUser.setEmail(updatedUserCredentialsDTO.getEmail());
        Role updatedRole = new Role();
        updatedRole.setRoleName(updatedUserCredentialsDTO.getRole().toString());
        newUser.setRole(updatedRole);
        Optional<User> userOptional = Optional.ofNullable(user);
        int oldPasswordsListSize = user.getPasswords().size();
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.of(updatedRole));
        User updatedDBUser = authService.updateUserInDB(1, newUser, updatedUserCredentialsDTO.getPassword());
        assertEquals(1L, updatedDBUser.getUserId());
        assertEquals(updatedUserCredentialsDTO.getEmail(), updatedDBUser.getEmail());
        assertEquals(updatedUserCredentialsDTO.getFirstname(), updatedDBUser.getFirstName());
        assertEquals(updatedUserCredentialsDTO.getLastname(), updatedDBUser.getLastName());
        assertEquals(updatedUserCredentialsDTO.getRole().toString(), updatedDBUser.getRole().getRoleName());
        List<Password> activeDbPasswords = updatedDBUser.getPasswords().stream().filter(password -> password.isStatus()).collect(Collectors.toList());
        int newPasswordsListSize = updatedDBUser.getPasswords().size();
        assertTrue(oldPasswordsListSize == (newPasswordsListSize - 1));
        assertTrue(activeDbPasswords.size() == 1);
        assertTrue(passwordEncoder.matches(updatedUserCredentialsDTO.getPassword(), activeDbPasswords.get(0).getPassword()));
        assertEquals(updatedUserCredentialsDTO.getRole().toString(), updatedDBUser.getRole().getRoleName());
    }

    @Test
    public void updateUserCredentials() throws Exception {
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        Role updatedRole = new Role();
        updatedRole.setRoleName(updatedUserCredentialsDTO.getRole().toString());
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.of(updatedRole));
        UserDTO newUserDTO = authService.updateUserCredentials(1, updatedUserCredentialsDTO);
        assertEquals(newUserDTO.getId().longValue(), user.getUserId());
        assertEquals(newUserDTO.getEmail(), updatedUserCredentialsDTO.getEmail());
        assertEquals(newUserDTO.getFirstname(), updatedUserCredentialsDTO.getFirstname());
        assertEquals(newUserDTO.getLastname(), updatedUserCredentialsDTO.getLastname());
        assertEquals(newUserDTO.getRole().toString(), updatedUserCredentialsDTO.getRole().toString());
        verify(userDAO, times(1)).findElementById(any());
        verify(userDAO, times(1)).update(any());
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateUserCredentialsWithRoleNonExistingInDatabase() throws Exception {
        when(userDAO.containsEmail(updatedUserCredentialsDTO.getEmail())).thenReturn(true);
        when(roleDAO.findElementByRoleName(any())).thenReturn(Optional.ofNullable(null));
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        authService.updateUserCredentials(1, updatedUserCredentialsDTO);
        verify(userDAO, times(1)).findElementById(any());
        verify(userDAO, times(0)).update(any());
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserCredentialWithIncorrectId() throws Exception {
        Optional<User> userOptional = Optional.empty();
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        UserDTO newUserDTO = authService.updateUserCredentials(0, updatedUserCredentialsDTO);
        verify(userDAO, times(0)).update(any());
    }

    @Test(expected = AuthServiceException.class)
    public void updateUserCredentialsWithNullInputData() throws Exception {
        UserDTO newUserDTO = authService.updateUserCredentials(3, null);
        verify(userDAO, times(0)).update(any());
    }

    @Test
    public void listUsersPerPage() throws Exception {
        when(userDAO.findAllUsersByOffsetByPageSizeByIdSort(0, 5, SortingOrderEnum.ASC))
                .thenReturn(users);
        List<UserDTO> userDTOs = authService.listUsersPerPage(0, 5, SortingOrderEnum.ASC);
        assertEquals(users.size(), userDTOs.size());
        IntStream.rangeClosed(0, users.size() - 1).forEach(i -> {
            assertEquals(users.get(i).getEmail(), userDTOs.get(i).getEmail());
            assertEquals(users.get(i).getFirstName(), userDTOs.get(i).getFirstname());
            assertEquals(users.get(i).getLastName(), userDTOs.get(i).getLastname());
            assertEquals(users.get(i).getRole().getRoleName(), userDTOs.get(i).getRole().toString());
        });
    }

    @Ignore
    @Test
    public void uploadFile() throws Exception {
        byte[] bytes = new byte[10];
        MultipartFile file = new MockMultipartFile("file", bytes);
        Resource resource = new Resource();
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(1L)).thenReturn(userOptional);
        when(resourceDAO.insert(any())).thenReturn(resource);
        String isUploaded = authService.uploadFile(1, file, metadataDTO);
        assertEquals("uuid", isUploaded);
        verify(userDAO, times(1)).findElementById(1L);
        verify(resourceDAO, times(1)).insert(any());

    }

    @Test
    public void uploadFileWithIncorrectId() {
        byte[] bytes = new byte[10];
        MultipartFile file = new MockMultipartFile("file", bytes);
        Optional<User> userOptional = Optional.empty();
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        String isUploaded = authService.uploadFile(0, file, metadataDTO);
        assertEquals("", isUploaded);
        verify(resourceDAO, times(0)).insert(any());
        verify(userDAO, times(1)).findElementById(any());
    }

    @Ignore
    @Test
    public void uploadFileWithHttpException() throws IOException {
        byte[] bytes = new byte[10];
        MultipartFile file = new MockMultipartFile("file", bytes);
        Optional<User> userOptional = Optional.ofNullable(user);
        when(userDAO.findElementById(0L)).thenReturn(userOptional);
        when(httpClient.uploadFile(any(), "")).thenThrow(new IOException());
        String isUploaded = authService.uploadFile(0, file, metadataDTO);
        assertEquals("", isUploaded);
    }

}