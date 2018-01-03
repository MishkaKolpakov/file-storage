package ua.softserve.academy.kv030.authservice.exceptions;


public class DuplicateEmailException extends UserValidationException {

    public DuplicateEmailException() {
        super();
    }

    public DuplicateEmailException(String message) {
        super("This email exists already");
    }

}
