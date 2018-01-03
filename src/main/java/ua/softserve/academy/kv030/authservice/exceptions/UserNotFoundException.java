package ua.softserve.academy.kv030.authservice.exceptions;

/**
 * Created by Miha on 14.11.2017.
 */
public class UserNotFoundException extends EntityNotFoundException {
    private static String message = "User not found ";

    public UserNotFoundException() {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        this(message,userId);
    }
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(String message,Long userId) {
        super(String.format("%s with id: %s",message,userId));
        entityId = userId+"";
    }
}
