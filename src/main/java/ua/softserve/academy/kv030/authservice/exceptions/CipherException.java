package ua.softserve.academy.kv030.authservice.exceptions;

/**
 * The exception that can be thrown during
 * encryption or decryption of a byte array
 *
 * @author Nikita Mykhailov
 * @since 2017-11-16
 */
public class CipherException extends AuthServiceException {
    static private String message = "Can`t perform encryption or decryption operation";
    /**
     * Constructs a new CipherException with the certain message.
     */
    public CipherException() {
        this(message);
    }
    public CipherException(String message) {
        super(message);
    }
}

