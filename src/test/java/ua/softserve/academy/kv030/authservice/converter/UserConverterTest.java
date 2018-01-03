package ua.softserve.academy.kv030.authservice.converter;

import org.junit.BeforeClass;
import org.junit.Test;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserConverterTest {

    private static User user, user1;
    private static UserDTO userDTO, userDTO1;
    private static Role role, role1;

    @BeforeClass
    public static void init() {
        role = new Role();
        role1 = new Role();
        user = new User();
        user1 = new User();
        userDTO = new UserDTO();
        userDTO1 = new UserDTO();
        role.setRoleName("ADMIN");
        role1.setRoleName("USER");
        user.setUserId(1L);
        user.setEmail("Email");
        user.setFirstName("Artem");
        user.setLastName("Pupkin");
        user.setRole(role);
        user1.setUserId(1L);
        user1.setEmail("Email1");
        user1.setFirstName("Artem1");
        user1.setLastName("Pupkin1");
        user1.setRole(role1);
        userDTO.setId(1L);
        userDTO.setFirstname("Artem");
        userDTO.setLastname("Pupkin");
        userDTO.setEmail("Email");
        userDTO.setRole(RoleDTO.ADMIN);
        userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setEmail("Email1");
        userDTO1.setFirstname("Artem1");
        userDTO1.setLastname("Pupkin1");
        userDTO1.setRole(RoleDTO.USER);
    }

    @Test
    public void convertToDto() throws Exception {
        UserDTO newUserDTO = UserConverter.convertToDto(user);
        assertEquals(1, newUserDTO.getId().intValue());
        assertEquals("Email", newUserDTO.getEmail());
        assertEquals("Artem", newUserDTO.getFirstname());
        assertEquals("Pupkin", newUserDTO.getLastname());
        assertEquals(RoleDTO.ADMIN, newUserDTO.getRole());
    }

    @Test(expected = AuthServiceException.class)
    public void convertToDtoWhenEntityNull() throws Exception {
        User user = null;
        UserConverter.convertToDto( user );
    }

    @Test
    public void convertToEntity() throws Exception {
        User newUser = UserConverter.convertToEntity(userDTO);
        assertEquals("Artem", newUser.getFirstName());
        assertEquals("Pupkin", newUser.getLastName());
        assertEquals("Email", newUser.getEmail());
        assertEquals("ADMIN", newUser.getRole().getRoleName());
        assertNull(newUser.getPasswords());
    }

    @Test(expected = AuthServiceException.class)
    public void convertToEntityWhenDtoNull() throws Exception {
        UserDTO userDTO = null;
        UserConverter.convertToEntity(userDTO);
    }

    @Test
    public void convertToDtoList() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        List<UserDTO> userDTOS = UserConverter.convertToDtoList(users);
        assertEquals(2, userDTOS.size());
        assertEquals("Artem", userDTOS.get(0).getFirstname());
        assertEquals("Artem1", userDTOS.get(1).getFirstname());
        assertEquals("Pupkin", userDTOS.get(0).getLastname());
        assertEquals("Pupkin1", userDTOS.get(1).getLastname());
        assertEquals(RoleDTO.ADMIN, userDTOS.get(0).getRole());
        assertEquals(RoleDTO.USER, userDTOS.get(1).getRole());
    }

    @Test
    public void convertToEntityList() throws Exception {
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(userDTO);
        userDTOS.add(userDTO1);
        List<User> users = UserConverter.convertToEntityList(userDTOS);
        assertEquals(2, users.size());
        assertEquals("Email", users.get(0).getEmail());
        assertEquals("Email1", users.get(1).getEmail());
        assertEquals("ADMIN", users.get(0).getRole().getRoleName());
        assertEquals("USER", users.get(1).getRole().getRoleName());

    }

}