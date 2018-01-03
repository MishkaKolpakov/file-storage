package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.softserve.academy.kv030.authservice.api.controller.helper.EmailValidator;
import ua.softserve.academy.kv030.authservice.api.model.UserCredentialsDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.api.model.UsersFilterDTO;
import ua.softserve.academy.kv030.authservice.dao.SortingOrderEnum;
import ua.softserve.academy.kv030.authservice.exceptions.DataValidationException;
import ua.softserve.academy.kv030.authservice.exceptions.PermissionException;
import ua.softserve.academy.kv030.authservice.services.AuthService;
import ua.softserve.academy.kv030.authservice.utils.JwtUtil;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserApiControllerImpl implements UserApi {

    private AuthService authService;
    private Logger logger;
    private EmailValidator emailValidator;
    private JwtUtil jwtUtil;

    @Autowired
    public UserApiControllerImpl(AuthService authService, Logger logger, EmailValidator emailValidator, JwtUtil jwtUtil) {
        this.authService = authService;
        this.logger = logger;
        this.emailValidator = emailValidator;
        this.jwtUtil = jwtUtil;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserCredentialsDTO user) {
        emailValidator.validateEmail(user.getEmail());
        UserDTO userDTO = authService.addUser(user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        httpHeaders.add(Constants.TOKEN_HEADER, jwtUtil.generateToken(userDTO.getEmail(), userDTO.getRole().toString()));

        return new ResponseEntity<>(userDTO, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long userId) {
        authService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isRoleUser(authentication)) {
            UserDTO userDTO = authService.findUserByEmail(authentication.getPrincipal().toString());
            if( !userId.equals(userDTO.getId())) {
                logger.error(String.format("User with role USER and ID %d not authorized to getById user with ID %d", userDTO.getId(), userId));
                throw new PermissionException("Not authorized to get by id the other user");
            }
        }
        UserDTO userDTO = authService.findUserById(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> updateUserById(@PathVariable("userId") Long userId, @Valid @RequestBody UserDTO user) {
        if (user.getEmail() != null) {
            emailValidator.validateEmail(user.getEmail());
        }
        UserDTO updatedUser = authService.updateUser(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> updateUserCredentials(@Min(1) @PathVariable("userId") Long userId, @Valid @RequestBody UserCredentialsDTO userCredentials) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isRoleUser(authentication)) {
            UserDTO userDTO = authService.findUserByEmail(authentication.getPrincipal().toString());
            if( !userId.equals(userDTO.getId())) {
                logger.error(String.format("User with role USER and ID %d tries to update user with ID %d", userDTO.getId(), userId));
                throw new PermissionException("Not authorized to update the other user");
            }
        }
        if (userCredentials.getEmail() != null) {
            emailValidator.validateEmail(userCredentials.getEmail());
        }
        UserDTO updatedUser = authService.updateUserCredentials(userId, userCredentials);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<UserDTO>> listUsersByOffsetAndPageLimit(@Min(0) @PathVariable("offset") Integer offset, @Min(5) @Max(50) @PathVariable("limit") Integer limit) {
        List<UserDTO> userDTOList = authService.listUsersPerPage(offset, limit, SortingOrderEnum.ASC);
        if (userDTOList.size() > 0) {
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userDTOList, HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> updateUserRole(@Min(1) @PathVariable("userId") Long userId, @Valid @RequestBody UserDTO user) {
        if (user.getRole() == null) {
            logger.warn("Role is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDTO updatedUser = authService.updateUser(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<UserDTO>> listUsersByFilter(@RequestParam(value = "filter", required = false) Optional<String> filter) {
        List<UserDTO> userDTOList;
        UsersFilterDTO filterDTO;
        if (filter.isPresent()) {
            String filterJSONString = filter.get();
            ObjectMapper mapper = new ObjectMapper();
            try {
                filterDTO = mapper.readValue(filterJSONString, UsersFilterDTO.class);
            } catch (IOException e) {
                logger.error(String.format("Can not read value from json string: %s.", filterJSONString));
                throw new DataValidationException(String.format("Can not read value from json string: %s.", filterJSONString));
            }
        } else {
            filterDTO = new UsersFilterDTO();
        }
        userDTOList = authService.listUsersByFilter(filterDTO);
        if (userDTOList.size() > 0) {
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userDTOList, HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<UserDTO> activateUser(@Min(1) @PathVariable("userId") Long userId) {
        UserDTO userDTO = authService.activateUser(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    private boolean isRoleUser(Authentication authentication) {
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        SimpleGrantedAuthority authority = authorities.get(0);
        return authority.getAuthority().equals("ROLE_USER");
    }
}
