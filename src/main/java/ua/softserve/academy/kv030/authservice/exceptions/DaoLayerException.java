package ua.softserve.academy.kv030.authservice.exceptions;

public class DaoLayerException extends AuthServiceException{

    public DaoLayerException() {
    }

    public DaoLayerException(String message) {
        super(message);
    }
}
