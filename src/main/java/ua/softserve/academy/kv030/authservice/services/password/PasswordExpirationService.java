package ua.softserve.academy.kv030.authservice.services.password;

/**
 * Handles operations related to expired/expiring passwords.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-12-17
 */
public interface PasswordExpirationService {

    /**
     * Notifies user about expiring password.
     */
    void notifyUserIfPasswordExpiresSoon();

    /**
     * Disables expired passwords.
     */
    void disableExpiredPasswords();

}
