package ua.softserve.academy.kv030.authservice.exceptions;

public class InvalidTokenException extends PermissionException {
    private static final String generalMessage = "Invalid authentication token";

    public InvalidTokenException() {
        super(generalMessage);
    }

    public InvalidTokenException(String message) {
        super(generalMessage + ": " + message);
    }
}
