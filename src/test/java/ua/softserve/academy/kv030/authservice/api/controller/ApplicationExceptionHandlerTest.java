package ua.softserve.academy.kv030.authservice.api.controller;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import ua.softserve.academy.kv030.authservice.AuthServiceApplication;
import ua.softserve.academy.kv030.authservice.exceptions.*;
import ua.softserve.academy.kv030.authservice.exceptions.FileNotFoundException;
import ua.softserve.academy.kv030.authservice.services.ResourceServiceImplTest;

import javax.validation.ConstraintViolationException;

import java.io.*;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by Miha on 16.11.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ApplicationExceptionHandlerTest {

    private ResponseEntityExceptionHandler exceptionHandlerSupport;

    private DefaultHandlerExceptionResolver defaultExceptionResolver;

    private WebRequest request;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ExceptionHandlerExceptionResolver resolver;

    @Before
    public void setup() {
        this.servletRequest = new MockHttpServletRequest("GET", "/");
        this.servletResponse = new MockHttpServletResponse();
        this.request = new ServletWebRequest(this.servletRequest, this.servletResponse);
        this.exceptionHandlerSupport = new ApplicationExceptionHandler(LoggerFactory.getLogger(ResourceServiceImplTest.class));
        this.defaultExceptionResolver = new DefaultHandlerExceptionResolver();

        StaticWebApplicationContext cxt = new StaticWebApplicationContext();
        cxt.registerSingleton("exceptionHandler", ApplicationExceptionHandler.class);
        cxt.refresh();

        resolver = new ExceptionHandlerExceptionResolver();
        resolver.setApplicationContext(cxt);
        resolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        resolver.afterPropertiesSet();
    }

    @Value("${application.url}")
    private String URL_PREFIX;

    @Test
    @Ignore
    public void handleHttpRequestMethodNotSupported() throws Exception {

        final Response response = given().delete(URL_PREFIX + "user/logout");

        final ApiError error = response.as(ApiError.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("Supported methods are"));

    }

    @Test
    @Ignore
    public void handleMethodArgumentTypeMismatch() throws Exception {
        RestAssured.defaultParser = Parser.JSON;
        Response response = RestAssured.given().header("Accept", "application/json").get(URL_PREFIX + "users/a");

        ApiError error = response.as(ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("should be of type"));
    }


    @Test
    public void handleMissingServletRequestPart() throws Exception {
        Exception ex = new MissingServletRequestPartException("partName");
        testException(ex);
    }

    @Test
    public void handleMissingServletRequestParameter() throws Exception {
        Exception ex = new MissingServletRequestParameterException("parameterName"," title");
        testException(ex);
    }

    @Mock
    private
    MethodParameter methodParameter;
    @Mock
    private
    BindingResult bindingResult;


    @Ignore
    @Test
    public void handleMethodArgumentNotValid() throws Exception {
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }

    @Test
    public void handleConstraintViolation() throws Exception {
        ConstraintViolationException ex = new ConstraintViolationException(new HashSet<>());
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(409, this.servletResponse.getStatus());
    }

    @Test
    public void handleAuthServiceException() throws Exception {

        AuthServiceException ex = new AuthServiceException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }
    @Test
    public void handleDaoLayerException() throws Exception {

        DaoLayerException ex = new DaoLayerException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(204, this.servletResponse.getStatus());
    }

    @Test
    public void handleUserNotFoundException() throws Exception {

        UserNotFoundException ex = new UserNotFoundException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(204, this.servletResponse.getStatus());
    }

    @Test
    public void handleFileNotFoundException() throws Exception {

        FileNotFoundException exception = new FileNotFoundException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, exception);

        assertEquals(204, this.servletResponse.getStatus());
    }

    @Test
    public void handleResourceNotFoundException() throws Exception {

        ResourceNotFoundException exception = new ResourceNotFoundException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, exception);

        assertEquals(204, this.servletResponse.getStatus());
    }

    @Test
    public void handlePermissionException() throws Exception {
        PermissionException ex = new PermissionException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(403, this.servletResponse.getStatus());
    }

    @Test
    public void handleUserValidationException() throws Exception {
        UserValidationException ex = new UserValidationException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(401, this.servletResponse.getStatus());
    }

    @Test
    public void controllerAdvice() throws Exception {

        ServletRequestBindingException ex = new ServletRequestBindingException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }

    private ResponseEntity<Object> testException(Exception ex) {
        ResponseEntity<Object> responseEntity = this.exceptionHandlerSupport.handleException(ex, this.request);

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(responseEntity.getStatusCode())) {
            assertSame(ex, this.servletRequest.getAttribute("javax.servlet.error.exception"));
        }

        this.defaultExceptionResolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(this.servletResponse.getStatus(), responseEntity.getStatusCode().value());

        return responseEntity;
    }

}