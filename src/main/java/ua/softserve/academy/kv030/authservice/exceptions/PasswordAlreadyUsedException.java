package ua.softserve.academy.kv030.authservice.exceptions;

/**
 * Created by user on 28.11.17.
 */
public class PasswordAlreadyUsedException extends DataValidationException {

    public PasswordAlreadyUsedException(Long userId) {
        super(String.format("User with ID %d tried to set one of her/his old passwords. New password is required.", userId));
    }
}
