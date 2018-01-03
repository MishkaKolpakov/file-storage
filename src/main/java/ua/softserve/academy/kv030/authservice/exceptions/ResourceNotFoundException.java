package ua.softserve.academy.kv030.authservice.exceptions;

public class ResourceNotFoundException extends EntityNotFoundException {
    private static String message = "Resource not found ";
    public ResourceNotFoundException() {
        super(message);
    }

    public ResourceNotFoundException(String resourceId) {
        this(message,resourceId);
    }

    public ResourceNotFoundException(long resourceId) {
        this(message,resourceId+"");
    }

    public ResourceNotFoundException(String message, Long resourceId) {
        this(message,resourceId+"");
    }

    public ResourceNotFoundException(String message, String resourceId) {
        super(String.format("%s with id: %s",message,resourceId));

    }

}

