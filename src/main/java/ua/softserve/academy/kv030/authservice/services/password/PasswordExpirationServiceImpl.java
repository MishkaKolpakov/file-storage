package ua.softserve.academy.kv030.authservice.services.password;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softserve.academy.kv030.authservice.dao.PasswordDAO;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.services.mail.EmailService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;
import ua.softserve.academy.kv030.authservice.values.Constants;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by user on 08.12.17.
 */
@Service
@EnableScheduling
public class PasswordExpirationServiceImpl implements PasswordExpirationService {

    private Logger logger;
    private PasswordDAO passwordDAO;
    private EmailService emailService;

    @Autowired
    public PasswordExpirationServiceImpl(Logger logger, PasswordDAO passwordDAO, EmailService emailService) {
        this.logger = logger;
        this.passwordDAO = passwordDAO;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${cron.expression.password-notify}")
    @Transactional(readOnly = true)
    @Override
    public void notifyUserIfPasswordExpiresSoon() {
        logger.info(String.format("Running scheduled task thread [expiring password notification]: %s", Thread.currentThread().getName()));

        final long currentTimeMillis = System.currentTimeMillis();
        Timestamp startTime = new Timestamp(currentTimeMillis);
        Timestamp endTime = new Timestamp(currentTimeMillis + Constants.PASSWORD_EXPIRY_NOTICE_DAYS * 24 * 60 *60 * 1000);
        List<Password> passwords = passwordDAO.findAllActivePasswordsExpiredWithinTimeRange(startTime, endTime);
        logger.info(String.format("There are %d passwords that expire within %tF-%tF.", passwords.size(), startTime, endTime));
        passwords.forEach(password -> {
            User user = password.getUser();
            if( !user.isStatus()) {
                logger.error(String.format("Status of User with ID %s is expected to be true, but false instead.", user.getUserId()));
            } else {
                try{
                    logger.info(String.format("Sending email to user (ID %d) about expiring password (ID %d)", user.getUserId(), password.getPasswordId()));
                    emailService.sendMail(user, EmailType.PASSWORD_EXPIRATION);
                } catch (Exception e) {
                    logger.error(String.format("Exception occurred when sending email to notify user (ID %d) about expiring password (ID %d): %s",
                            user.getUserId(), password.getPasswordId(), e.getMessage()));
                }
            }
        });

        logger.info(String.format("Ending scheduled task thread [expiring password notification]: %s", Thread.currentThread().getName()));
    }

    @Scheduled(cron = "${cron.expression.password-clean}")
    @Transactional
    @Override
    public void disableExpiredPasswords() {
        logger.info(String.format("Running scheduled task thread [disable expired passwords]: %s", Thread.currentThread().getName()));

        List<Password> passwords = passwordDAO.findAllActivePasswordsThatExpired();
        logger.info(String.format("There are %d passwords already expired.", passwords.size()));
        passwords.forEach(password -> {
            User user = password.getUser();
            logger.info(String.format("Disabling expired password (ID %1$d, expired on %2$tF %2$tT) for user (ID %3$d)",
                    password.getPasswordId(), password.getExpirationTime(), user.getUserId()));
            try{
                password.setStatus(false);
                passwordDAO.update(password);
                emailService.sendMail(user, EmailType.PASSWORD_EXPIRATION);
            } catch (Exception e) {
                logger.error(String.format("Exception occurred when disabling expired password (ID %d) for user (ID %d): %s",
                        password.getPasswordId(), user.getUserId(), e.getMessage()));
            }

        });

        logger.info(String.format("Ending scheduled task thread [disable expired passwords]: %s", Thread.currentThread().getName()));
    }

}
