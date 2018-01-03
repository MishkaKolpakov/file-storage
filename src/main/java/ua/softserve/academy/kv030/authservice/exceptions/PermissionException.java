package ua.softserve.academy.kv030.authservice.exceptions;

/**
 * Created by Miha on 17.11.2017.
 */
public class PermissionException extends AuthServiceException{

    public PermissionException() {
        super();
    }

    public PermissionException(String message) {
        super(message);
    }
}
