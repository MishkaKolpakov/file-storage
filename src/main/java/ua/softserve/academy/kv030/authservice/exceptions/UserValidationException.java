package ua.softserve.academy.kv030.authservice.exceptions;

/**
 * UserValidationException that should be thrown in case of User validation problems
 *
 * @author Michael Yablon
 * @since 17.11.2017.
 */
public class UserValidationException extends AuthServiceException {

    public UserValidationException() {
        super();
    }

    public UserValidationException(String message) {
        super(message);
    }
}
