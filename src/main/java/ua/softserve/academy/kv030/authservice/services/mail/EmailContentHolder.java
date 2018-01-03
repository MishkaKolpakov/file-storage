package ua.softserve.academy.kv030.authservice.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EmailContentHolder {
    private static Map<EmailType, String> subjectMap;
    @Autowired
    private EmailContentFactory emailContentFactory;

    //initializing hash map with email subjects
    public void init() {
        subjectMap = new HashMap<>();
        subjectMap.put(EmailType.PASSWORD_EXPIRATION, "IMPORTANT: Password expiration");
        subjectMap.put(EmailType.SUCCESS_REGISTRATION, "Successful authorization in YourSuperCloud");
        subjectMap.put(EmailType.USER_UPDATE, "Your profile was updated");
        subjectMap.put(EmailType.DELETED_FILE, "Deleted file");
        subjectMap.put(EmailType.DISABLED_ACCOUNT, "Disabled account");
        subjectMap.put(EmailType.SHARED_FILE, "Shared file");
    }

    /**
     * Factory method which return subject of email according to email type
     *
     * @param emailType type of email
     * @return string subject from hash map
     */
    public String getEmailSubjectByEmailType(EmailType emailType) {
        return subjectMap.get(emailType);
    }

    /**
     * Factory method which return content according to email type
     *
     * @param emailType type of email
     * @param user      entity object of user that should get email
     * @return content from template html
     * @throws AuthServiceException if there is no such an email type
     */
    public String getContentByEmailType(EmailType emailType, User user, Resource resource) {
        String fullName = nameConcatenation(user);
        switch (emailType) {
            case PASSWORD_EXPIRATION:
                Optional<Password> passwordOptional = user.getPasswords().stream().filter(Password::isStatus).findAny();
                if (passwordOptional.isPresent()) {
                    Date expirationDate = new Date(passwordOptional.get().getExpirationTime().getTime());
                    return emailContentFactory.buildForPasswordExpiration(fullName, expirationDate);
                } else {
                    return emailContentFactory.buildForNonActivePassword(fullName);
                }

            case SUCCESS_REGISTRATION:
                return emailContentFactory.buildForSuccessRegistration(fullName);

            case USER_UPDATE:
                return emailContentFactory.buildForUpdatedUser(fullName);

            case DELETED_FILE:
                return emailContentFactory.buildForDeletedFile(fullName);

            case DISABLED_ACCOUNT:
                return emailContentFactory.buildForDisabledAccount(fullName);

            case SHARED_FILE:
                String fileUUID = resource.getLinkToFile();
                String fileOwner = nameConcatenation(resource.getOwner());
                String fileName = resource.getFileName();
                return emailContentFactory.buildForSharedFile(fullName, fileUUID, fileOwner, fileName);

            default:
                throw new AuthServiceException("No such email type");
        }
    }

    /**
     * Method which formatting string with full name of user
     *
     * @param user entity object of user that should get email
     * @return string value with full name of user
     */
    private String nameConcatenation(User user) {
        return new StringBuffer().append(user.getFirstName()).append(" ").append(user.getLastName()).toString();
    }
}
