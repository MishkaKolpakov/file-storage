package ua.softserve.academy.kv030.authservice.api.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.*;
import ua.softserve.academy.kv030.authservice.api.model.*;
import ua.softserve.academy.kv030.authservice.services.AuthService;
import ua.softserve.academy.kv030.authservice.utils.JwtUtil;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.validation.Valid;

@RestController
public class AuthorizationApiControllerImpl implements AuthorizationApi {

    private AuthService authService;
    private Logger logger;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthorizationApiControllerImpl(AuthService authService, Logger logger, JwtUtil jwtUtil) {
        this.authService = authService;
        this.logger = logger;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody UserCredentialsDTO login) {
        UserDTO response = authService.loginUser(login);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        httpHeaders.add(Constants.TOKEN_HEADER, jwtUtil.generateToken(response.getEmail(), response.getRole().toString()));
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> logoutUser() {
        if(authService.logoutUser()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Void> validate(@Valid @RequestBody TokenValidationRequestDTO tokenRequest) {
        return (jwtUtil.isValid(tokenRequest.getToken()))
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}

