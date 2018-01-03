package ua.softserve.academy.kv030.authservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.softserve.academy.kv030.authservice.api.model.*;
import ua.softserve.academy.kv030.authservice.converter.UserConverter;
import ua.softserve.academy.kv030.authservice.converter.UserCredentialsConverter;
import ua.softserve.academy.kv030.authservice.converter.UsersFilterConverter;
import ua.softserve.academy.kv030.authservice.dao.*;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.Role;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.*;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClient;
import ua.softserve.academy.kv030.authservice.services.mail.EmailService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailServiceImpl;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * this class is implementation of AuthService interface
 *
 * @see AuthService
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private UserDAO userDAO;
    private ResourceDAO resourceDAO;
    private EmailService emailService;
    private HttpClient httpClient;
    private PasswordDAO passwordDAO;
    private PasswordEncoder passwordEncoder;
    private RoleDAO roleDAO;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    public AuthServiceImpl(UserDAO userDAO, ResourceDAO resourceDAO, EmailServiceImpl emailService, HttpClient httpClient,
                           PasswordDAO passwordDAO, PasswordEncoder passwordEncoder, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.resourceDAO = resourceDAO;
        this.emailService = emailService;
        this.httpClient = httpClient;
        this.passwordDAO = passwordDAO;
        this.passwordEncoder = passwordEncoder;
        this.roleDAO = roleDAO;
    }

    /**
     * Log in user
     *
     * @param login user credentials to login
     * @return DTO representation of user object if operation was successful;
     * @throws PermissionException if failed to validate supplied credentials
     */
    @Override
    public UserDTO loginUser(UserCredentialsDTO login) {
        Optional<User> userOptional = userDAO.findUserByEmail(login.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Password> activePasswordOptional = passwordDAO.findActivePasswordByUserId(user.getUserId());
            if (activePasswordOptional.isPresent()
                    && passwordEncoder.matches(login.getPassword(), activePasswordOptional.get().getPassword())) {
                return UserConverter.convertToDto(user);
            }
        }
        throw new PermissionException("Please enter valid credentials!");
    }

    @Override
    public boolean logoutUser() {
        return false;
    }

    /**
     * This method adds user to database
     *
     * @param userCredentialsDTO DTO object that needs to be converted to user object and be saved to database
     * @return DTO representation of user object that was added to database
     * @throws DuplicateEmailException
     * @throws EntityNotFoundException
     */
    @Override
    public UserDTO addUser(UserCredentialsDTO userCredentialsDTO) throws DuplicateEmailException {
        User user = UserCredentialsConverter.convertToEntity(userCredentialsDTO); // if DTO null converter throws exception

        if (user.getFirstName() == null || user.getLastName() == null) {
            log.error(String.format("First name '%s' and/or Lastname '%s' is null, but must be filled in.", user.getFirstName(), user.getLastName()));
            throw new AuthServiceException(String.format("First name '%s' and/or Lastname '%s' is null, but must be filled in.", user.getFirstName(), user.getLastName()));
        }

        if (userDAO.containsEmail(userCredentialsDTO.getEmail())) {
            log.error(String.format("Email [%s] already used by other user.", userCredentialsDTO.getEmail()));
            throw new DuplicateEmailException(String.format("Email [%s] already used by other user.", userCredentialsDTO.getEmail()));
        }

        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            if (roleName == null) {
                roleName = RoleDTO.USER.toString(); // by default add new user under 'USER' role
            }
            Optional<Role> roleOptional = roleDAO.findElementByRoleName(roleName);
            if (!roleOptional.isPresent()) {
                log.error(String.format("No role name [%s] found in database.", user.getRole().getRoleName()));
                throw new EntityNotFoundException(String.format("No role name [%s] found in database.", user.getRole().getRoleName()));
            }
            user.setRole(roleOptional.get());
        }
        user.getPasswords().get(0).setStatus(true);
        user.getPasswords().get(0).setExpirationTime(new Timestamp(System.currentTimeMillis() + Constants.PASSWORD_VALIDITY_DAYS * 24 * 60 * 60 * 1000));
        user.setStatus(true);

        log.info("Inserting user");
        User newUser = userDAO.insert(user);
        log.info(String.format("New user added: [%s]", newUser));
        emailService.sendMail(user, EmailType.SUCCESS_REGISTRATION);
        return UserConverter.convertToDto(newUser);
    }

    private User getUserFromDB(long id) {
        Optional<User> userOptional = userDAO.findElementById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        } else {
            log.error(String.format("No user found in database with ID %d.", id));
            throw new UserNotFoundException(id);
        }
    }

    /**
     * This method deletes user from database
     *
     * @param id id of user that needs to be deleted from database
     * @return <code>true</code> if operation was successful;
     * <code>false</code> otherwise
     * @throws UserNotFoundException if no user in database with specified ID
     */
    @Override
    public boolean deleteUser(long id) {
        User user = getUserFromDB(id);
        if (!user.isStatus()) {
            log.error(String.format("User with ID %d is not active, so can not be deleted.", id));
            throw new AuthServiceException(String.format("User with ID %d is already deleted.", id));
        }
        user.setStatus(false);
        // disable password(s) by changing status from true to false
        user.getPasswords().stream().filter(password -> password.isStatus()).forEach(password -> password.setStatus(false));
        if (userDAO.update(user) != null) {
            emailService.sendMail(user, EmailType.DISABLED_ACCOUNT);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method activates user by changing its status from false to to true
     *
     * @param id id of user that needs to be activated
     * @return DTO representation of user object that was activated
     * @throws UserNotFoundException if no user in database with specified ID
     */
    @Override
    public UserDTO activateUser(long id) {
        User databaseUser = getUserFromDB(id);
        if (databaseUser.isStatus()) {
            log.error(String.format("User with ID %d is already active, so can not be activated again.", id));
            throw new AuthServiceException(String.format("User with ID %d is already active, so can not be activated again.", id));
        }
        databaseUser.setStatus(true);
        userDAO.update(databaseUser);
        return UserConverter.convertToDto(databaseUser);
    }

    /**
     * This method gets user by Id
     *
     * @param id id of user that need to be found
     * @return DTO representation of user object
     * @throws UserNotFoundException if no user in database with specified ID
     */
    @Override
    public UserDTO findUserById(long id) {
        return UserConverter.convertToDto(getUserFromDB(id));
    }

    /**
     * This method gets user by email
     *
     * @param email of user that need to be found
     * @return DTO representation of user object
     * @throws UserNotFoundException if no user in database with specified ID
     */
    @Override
    public UserDTO findUserByEmail(String email) {
        Optional<User> userOptional = userDAO.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserConverter.convertToDto(user);
        }
        log.error(String.format("No user found in database with email %s.", email));
        throw new EntityNotFoundException(String.format("No user found in database with email %s.", email));
    }

    /**
     * This method updates user, including password
     *
     * @param userCredentialsDTO DTO object with new values,that need to be updated
     * @param id                 id of user that need to be updated
     * @return DTO representation of user object that was updated
     * @throws DataValidationException if try to update email
     * @throws UserNotFoundException   if no user in database with specified id
     * @throws AuthServiceException    if user DTO is null
     */
    @Override
    public UserDTO updateUserCredentials(long id, UserCredentialsDTO userCredentialsDTO) {
        User newUser = UserCredentialsConverter.convertToEntity(userCredentialsDTO); // if DTO null converter throws exception
        User databaseUser = updateUserInDB(id, newUser, userCredentialsDTO.getPassword());
        emailService.sendMail(databaseUser, EmailType.USER_UPDATE);
        return UserConverter.convertToDto(databaseUser);
    }

    /**
     * This method updates user, excluding password
     *
     * @param userDTO DTO object with new values,that need to be updated
     * @param id      id of user that need to be updated
     * @return DTO representation of user object that was updated
     * @throws DataValidationException if try to update email
     * @throws UserNotFoundException   if no user in database with specified id
     * @throws AuthServiceException    if user DTO is null
     */
    @Override
    public UserDTO updateUser(long id, UserDTO userDTO) {
        User newUser = UserConverter.convertToEntity(userDTO); // if DTO null converter throws exception
        User databaseUser = updateUserInDB(id, newUser, null);
        emailService.sendMail(databaseUser, EmailType.USER_UPDATE);
        return UserConverter.convertToDto(databaseUser);
    }

    protected User updateUserInDB(long id, User newUser, @Nullable String rawPassword) {
        User databaseUser = getUserFromDB(id);
        log.info(String.format("Updating user with ID %d. \nOld user [%s].", id, databaseUser));
        if (newUser.getEmail() != null) databaseUser.setEmail(newUser.getEmail());
        if (newUser.getFirstName() != null) databaseUser.setFirstName(newUser.getFirstName());
        if (newUser.getLastName() != null) databaseUser.setLastName(newUser.getLastName());
        if (newUser.isStatus() != null) databaseUser.setStatus(newUser.isStatus());
        if (newUser.getEmail() != null) databaseUser.setEmail(newUser.getEmail());

        if (newUser.getRole() != null && newUser.getRole().getRoleName() != null) {
            Optional<Role> roleOptional = roleDAO.findElementByRoleName(newUser.getRole().getRoleName());
            if (!roleOptional.isPresent()) {
                log.error(String.format("No role name [%s] found in database.", newUser.getRole().getRoleName()));
                throw new EntityNotFoundException(String.format("No role name [%s] found in database.", newUser.getRole().getRoleName()));
            }
            databaseUser.setRole(roleOptional.get());
        }

        // password update
        if (rawPassword != null && !rawPassword.isEmpty()) {
            List<Password> activePasswords = databaseUser.getPasswords().stream()
                    .filter(Password::isStatus)
                    .filter(password -> !passwordEncoder.matches(rawPassword, password.getPassword()))
                    .collect(Collectors.toList());
            if (!activePasswords.isEmpty()) {
                if (databaseUser.getPasswords().stream()
                        .filter(password -> !password.isStatus())
                        .filter(password -> passwordEncoder.matches(rawPassword, password.getPassword()))
                        .findAny().isPresent()) {
                    throw new PasswordAlreadyUsedException(databaseUser.getUserId());
                }
                activePasswords.forEach(password -> password.setStatus(false));
                Password newPassword = new Password();
                newPassword.setStatus(true);
                newPassword.setPassword(passwordEncoder.encode(rawPassword));
                newPassword.setExpirationTime(new Timestamp(System.currentTimeMillis() + Constants.PASSWORD_VALIDITY_DAYS * 24 * 60 * 60 * 1000));
                newPassword.setUser(databaseUser);
                databaseUser.getPasswords().add(newPassword);
            }
        }

        userDAO.update(databaseUser);
        log.info(String.format("Updated user [%s].", databaseUser));
        return databaseUser;
    }

    /**
     * This method lists users per page
     *
     * @param offset   a first user entity to be retrieved from the database, starts from 0
     * @param pageSize max number of results to retrieve
     * @param order    an ascending or descending order to sort results
     * @return a list of users per page
     */
    @Override
    public List<UserDTO> listUsersPerPage(int offset, int pageSize, SortingOrderEnum order) {
        List<User> users = userDAO.findAllUsersByOffsetByPageSizeByIdSort(offset, pageSize, order);
        return UserConverter.convertToDtoList(users);
    }

    /**
     * This method lists users filtered by criteria
     *
     * @param filterDTO a filter DTO object of search criteria
     * @return a list of users per page filtered by criteria
     */
    @Override
    public List<UserDTO> listUsersByFilter(UsersFilterDTO filterDTO) {
        UsersFilterCriteria criteria = UsersFilterConverter.convert(filterDTO);

        List<User> users = userDAO.findAllUsersByCriteria(criteria);
        return UserConverter.convertToDtoList(users);
    }

    /**
     * This method saves file to database
     *
     * @param file               file that needs to be saved to database
     * @param id                 id of user that owns file
     * @param additionalMetadata additional information about file
     * @return UUID of uploaded file
     */
    @Override
    public String uploadFile(long id, MultipartFile file, FileMetadataDTO additionalMetadata) {
        if (file != null) {
            Optional<User> userOptional = userDAO.findElementById(id);
            if (userOptional.isPresent()) {
                User owner = userOptional.get();
                additionalMetadata.setOwnerId(id);
                Resource resource = new Resource();
                resource.setOwner(owner);

                additionalMetadata = resourceService.addResourceMetadata(additionalMetadata);

                log.info("Inserting resource");
                try {
                    httpClient.uploadFile(file, additionalMetadata.getFileUUID());
                } catch (IOException e) {
                    return "";
                }
                if (resourceDAO.insert(resource) != null) {
                    return "uuid";
                }
            }
        }
        return "";
    }
}
