package ua.softserve.academy.kv030.authservice.api.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.softserve.academy.kv030.authservice.exceptions.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationExceptionHandler.class
 * Handles exceptions that can be thrown when application works
 *
 * @author Michael Yablon
 * @since 14.11.2017.
 */
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger;

    public ApplicationExceptionHandler() {
    }

    @Autowired
    public ApplicationExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    /**
     * A fall-back handler that will catch all other exceptions
     *
     * @param ex - all other exceptions
     * @return response with internal server error code
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                ex.getLocalizedMessage(), "Exception occurred");
        logger.warn(apiError.getMessage() + "\n");
        ex.printStackTrace();
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    /**
     * Handles MissingServletRequestPartException
     * This exception is thrown when when the part of a multipart request not found
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                ex.getLocalizedMessage(), "Required part of the request is missing");

        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }

    /**
     * Handles MissingServletRequestParameterException
     * This exception is thrown when request missing parameter
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        String error = ex.getParameterName() + " parameter is missing";

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);

        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }


    /**
     * Handles  MethodArgumentNotValidException
     * This exception is thrown when argument annotated with @Valid failed validation
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors())
            errors.add(error.getField() + ": " + error.getDefaultMessage());

        for (ObjectError error : ex.getBindingResult().getGlobalErrors())
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    /**
     * Handles ConstraintViolationException
     * This exception reports the result of constraint violations
     *
     * @return response with error review
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError =
                new ApiError(HttpStatus.CONFLICT, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    /**
     * Handles HttpRequestMethodNotSupportedException
     * This exception is thrown when user send a request with an unsupported Http method
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t).append(" "));

        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED,
                ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }

    /**
     * Handles HttpRequestMethodNotSupportedException
     * This exception is thrown when user send a request with an unsupported Http method
     *
     * @return response with error review
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(DaoLayerException.class)
    public ResponseEntity<ApiError> handleDaoLayerException(DaoLayerException ex) {

        ApiError apiException = new ApiError(HttpStatus.NO_CONTENT, ex.getLocalizedMessage(), "Error in Dao Layer");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ApiError> handleAuthServiceException(AuthServiceException ex) {

        ApiError apiException = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "Auth Service Exception");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(AuthServiceException ex) {

        ApiError apiException = new ApiError(HttpStatus.NO_CONTENT, ex.getLocalizedMessage(), "User not found");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiError> handlePermissionException(PermissionException ex) {

        ApiError apiException = new ApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), "You have no permission for current operation");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiException, httpHeaders, apiException.getStatus());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ApiError> handleUserValidationException(UserValidationException ex) {

        ApiError apiException = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), "User validation exception");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiError> handleFileNotFoundException(FileNotFoundException ex) {

        ApiError apiException = new ApiError(HttpStatus.NO_CONTENT, ex.getLocalizedMessage(), "File not found exception");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError apiException = new ApiError(HttpStatus.NO_CONTENT, ex.getLocalizedMessage(), "Resource not found exception");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }
}
