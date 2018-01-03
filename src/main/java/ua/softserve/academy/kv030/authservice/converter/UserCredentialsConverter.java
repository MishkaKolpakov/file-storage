package ua.softserve.academy.kv030.authservice.converter;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting objects between Entity and DTO
 */
public final class UserCredentialsConverter {

    private UserCredentialsConverter() {
    }

    /**
     * Converts user entity object to corresponding user with credentials DTO
     *
     * @param user User that needed to be converted to DTO
     * @return user with credentials DTO object
     * @throws AuthServiceException if User entity is null
     */
    public static UserCredentialsDTO convertToDto(User user) {
        if(user == null) {
            throw new AuthServiceException("User entity to be converted to UserCredentialsDTO is null.");
        }
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setId(user.getUserId());
        userCredentialsDTO.setFirstname(user.getFirstName());
        userCredentialsDTO.setLastname(user.getLastName());
        userCredentialsDTO.setEmail(user.getEmail());
        Role role = user.getRole();
        if(role!=null && role.getRoleName()!=null) {
            userCredentialsDTO.setRole(Enum.valueOf(RoleDTO.class, role.getRoleName()));
        }
        userCredentialsDTO.setPassword("");
        return userCredentialsDTO;
    }

    /**
     * Converts user with credentials DTO object to corresponding Entity
     *
     * @param userCredentialsDTO User with credentials data transfer object that needed to be converted to Entity
     * @return User object
     * @throws AuthServiceException if UserCredentials DTO is null
     */
    public static User convertToEntity(UserCredentialsDTO userCredentialsDTO) {
        if(userCredentialsDTO == null) {
            throw new AuthServiceException("UserCredentialsDTO to be converted to User entity is null.");
        }

        User user = new User();
        user.setEmail(userCredentialsDTO.getEmail());
        user.setFirstName(userCredentialsDTO.getFirstname());
        user.setLastName(userCredentialsDTO.getLastname());
        List<Password> passwords = new ArrayList<>();
        Password password = new Password();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userCredentialsDTO.getPassword());
        password.setPassword(hashedPassword);
        password.setUser(user);
        passwords.add(password);
        user.setPasswords(passwords);
        Role role = new Role();
        if(userCredentialsDTO.getRole() != null) {
            role.setRoleName(userCredentialsDTO.getRole().name());
        }
        user.setRole(role);
        return user;
    }

    /**
     * Converts collection of user entity objects to list of DTO objects
     *
     * @param users Collection of user objects that will be converted to DTO
     * @return List of user with credentials DTOs
     */
    public static List<UserCredentialsDTO> convertToDtoList(Collection<User> users) {
        return users.
                stream().
                map(UserCredentialsConverter::convertToDto).
                collect(Collectors.toList());

    }

    /**
     * Converts collection of user with credentials DTOs to list of entity objects
     *
     * @param userCredentialsDTOS Collection of user with credentials DTOs that will be converted to entity
     * @return List of User entity objects
     */
    public static List<User> convertToEntityList(Collection<UserCredentialsDTO> userCredentialsDTOS) {
        return userCredentialsDTOS.
                stream().
                map(UserCredentialsConverter::convertToEntity).
                collect(Collectors.toList());
    }
}
