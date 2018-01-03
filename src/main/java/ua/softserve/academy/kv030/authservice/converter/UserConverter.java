package ua.softserve.academy.kv030.authservice.converter;


import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting objects between Entity and DTO
 */
public final class UserConverter {

    private UserConverter() {
    }


    /**
     * Converts user entity object to corresponding Dto
     *
     * @param user User that needed to be converted to DTO
     * @return DTO object
     * @throws AuthServiceException if User entity is null, or if unknown role name is specified
     */
    public static UserDTO convertToDto(User user) {
        if(user == null) {
            throw new AuthServiceException("User entity to be converted to UserDTO is null.");
        }
        UserDTO userDto = new UserDTO();
        userDto.setEmail(user.getEmail());
        userDto.setFirstname(user.getFirstName());
        userDto.setLastname(user.getLastName());
        userDto.setId(user.getUserId());
        Role role = user.getRole();
        if(role != null && role.getRoleName() != null) {
            String roleName = role.getRoleName();
            switch (roleName) {
                case "ADMIN":
                    userDto.setRole(RoleDTO.ADMIN);
                    break;
                case "TECH_SUPP":
                    userDto.setRole(RoleDTO.TECH_SUPP);
                    break;
                case "USER":
                    userDto.setRole(RoleDTO.USER);
                    break;
                default:
                    throw new AuthServiceException(String.format("Not known role name: %s. Role name may be one of the following: %s).", roleName, Arrays.asList(RoleDTO.values())));
            }
        }
        return userDto;
    }

    /**
     * Converts user DTO object to corresponding Entity
     *
     * @param userDTO User data transfer object that needed to be converted to Entity
     * @return User object
     * @throws AuthServiceException if User DTO is null
     */
    public static User convertToEntity(UserDTO userDTO) {
        if(userDTO == null) {
            throw new AuthServiceException("UserDTO to be converted to User entity is null.");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        Role role = new Role();
        if (userDTO.getRole() != null) {
            role.setRoleName(userDTO.getRole().toString());
        }
        user.setRole(role);
        return user;
    }

    /**
     * Converts collection of user entity objects to list of DTO objects
     *
     * @param users Collection of user objects that will be converted to DTO
     * @return List of User DTOs
     */
    public static List<UserDTO> convertToDtoList(Collection<User> users) {
        return users.
                stream().
                map(UserConverter::convertToDto).
                collect(Collectors.toList());

    }

    /**
     * Converts collection of user DTO to list of entity objects
     *
     * @param userDTOS Collection of user DTO objects that will be converted to entity
     * @return List of User entity objects
     */
    public static List<User> convertToEntityList(Collection<UserDTO> userDTOS) {
        return userDTOS.
                stream().
                map(UserConverter::convertToEntity).
                collect(Collectors.toList());
    }


}
