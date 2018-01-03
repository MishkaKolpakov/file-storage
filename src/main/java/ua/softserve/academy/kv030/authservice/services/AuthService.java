package ua.softserve.academy.kv030.authservice.services;


import org.springframework.web.multipart.MultipartFile;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.api.model.UsersFilterDTO;
import ua.softserve.academy.kv030.authservice.dao.SortingOrderEnum;

import java.util.List;
import java.util.Optional;

/**
 * This interface represents methods for such operations : add user to database,
 * delete user, update user, get user by Id, logout and upload file
 */
public interface AuthService {


    UserDTO loginUser(UserCredentialsDTO login);

    boolean logoutUser();

    UserDTO addUser(UserCredentialsDTO userCredentialsDTO);

    boolean deleteUser(long id);

    UserDTO activateUser(long id);

    UserDTO findUserById(long id);

    UserDTO findUserByEmail(String email);

    UserDTO updateUserCredentials(long id, UserCredentialsDTO userCredentialsDTO);

    UserDTO updateUser(long id, UserDTO userDTO);

    List<UserDTO> listUsersPerPage(int offset, int pageSize, SortingOrderEnum order);

    List<UserDTO> listUsersByFilter(UsersFilterDTO filterDTO);

    String uploadFile(long id, MultipartFile file, FileMetadataDTO additionalMetadata);

}
