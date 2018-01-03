package ua.softserve.academy.kv030.authservice.converter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class UserCredentialsConverterTest {

    private static User user, user1;
    private static UserCredentialsDTO userCredentialsDTO, userCredentialsDTO1;
    private static Password password, password1;
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeClass
    public static void init() {
        user = new User();
        user1 = new User();
        userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO1 = new UserCredentialsDTO();
        password = new Password();
        password1 = new Password();
        password.setPassword("password");
        password1.setPassword("password1");
        List<Password> passwords = new ArrayList<>();
        passwords.add(password);
        List<Password> passwords1 = new ArrayList<>();
        passwords.add(password1);
        Role adminRole = new Role();
        adminRole.setRoleName(RoleDTO.ADMIN.toString());

        user.setUserId(1L);
        user.setEmail("Email");
        user.setFirstName("Artem");
        user.setLastName("Lastname");
        user.setPasswords(passwords);
        user.setRole(adminRole);
        user1.setUserId(2L);
        user1.setEmail("Email1");
        user1.setFirstName("Artem1");
        user1.setLastName("Lastname1");
        user1.setPasswords(passwords1);
        user1.setRole(adminRole);
        userCredentialsDTO.setId(1L);
        userCredentialsDTO.setFirstname("Artem");
        userCredentialsDTO.setLastname("Lastname");
        userCredentialsDTO.setEmail("Email");
        userCredentialsDTO.setPassword("password");
        userCredentialsDTO.setRole(RoleDTO.ADMIN);
        userCredentialsDTO1.setId(1L);
        userCredentialsDTO1.setFirstname("Artem1");
        userCredentialsDTO1.setLastname("Lastname1");
        userCredentialsDTO1.setEmail("Email1");
        userCredentialsDTO1.setPassword("password1");
        userCredentialsDTO1.setRole(RoleDTO.ADMIN);

    }

    @Test
    public void convertToDto() throws Exception {
        UserCredentialsDTO newUserCredentialsDTO = UserCredentialsConverter.convertToDto(user);
        assertEquals(1, newUserCredentialsDTO.getId().intValue());
        assertEquals("Email", newUserCredentialsDTO.getEmail());
        assertEquals("Artem", newUserCredentialsDTO.getFirstname());
        assertEquals("Lastname", newUserCredentialsDTO.getLastname());
        assertEquals("", newUserCredentialsDTO.getPassword());
    }

    @Test(expected = AuthServiceException.class)
    public void convertToDtoWhenEntityNull() throws Exception {
        User user = null;
        UserCredentialsConverter.convertToDto(user);
    }

    @Test
    public void convertToEntity() throws Exception {
        User newUser = UserCredentialsConverter.convertToEntity(userCredentialsDTO);
        assertEquals("Email", newUser.getEmail());
        assertEquals("Artem", newUser.getFirstName());
        assertEquals("Lastname", newUser.getLastName());
        assertTrue(passwordEncoder.matches("password", newUser.getPasswords().get(0).getPassword()));
    }

    @Test(expected = AuthServiceException.class)
    public void convertToEntityWhenDtoNull() throws Exception {
        UserCredentialsDTO userCredentialsDTO = null;
        UserCredentialsConverter.convertToEntity(userCredentialsDTO);
    }

    @Test
    public void convertToDtoList() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        List<UserCredentialsDTO> userCredentialsDTOS = UserCredentialsConverter.convertToDtoList(users);
        assertEquals(2, userCredentialsDTOS.size());
        assertEquals(1, userCredentialsDTOS.get(0).getId().intValue());
        assertEquals(2, userCredentialsDTOS.get(1).getId().intValue());
        assertEquals("Artem", userCredentialsDTOS.get(0).getFirstname());
        assertEquals("Artem1", userCredentialsDTOS.get(1).getFirstname());
        assertEquals("Lastname", userCredentialsDTOS.get(0).getLastname());
        assertEquals("Lastname1", userCredentialsDTOS.get(1).getLastname());
        assertEquals("", userCredentialsDTOS.get(0).getPassword());
        assertEquals("", userCredentialsDTOS.get(1).getPassword());
    }

    @Test
    public void convertToEntityList() throws Exception {
        List<UserCredentialsDTO> userCredentialsDTOS = new ArrayList<>();
        userCredentialsDTOS.add(userCredentialsDTO);
        userCredentialsDTOS.add(userCredentialsDTO1);
        List<User> users = UserCredentialsConverter.convertToEntityList(userCredentialsDTOS);
        assertEquals(2, users.size());
        assertEquals("Artem", users.get(0).getFirstName());
        assertEquals("Artem1", users.get(1).getFirstName());
        assertEquals("Lastname", users.get(0).getLastName());
        assertEquals("Lastname1", users.get(1).getLastName());
        assertTrue(passwordEncoder.matches("password", users.get(0).getPasswords().get(0).getPassword()));
        assertTrue(passwordEncoder.matches("password1", users.get(1).getPasswords().get(0).getPassword()));

    }

}