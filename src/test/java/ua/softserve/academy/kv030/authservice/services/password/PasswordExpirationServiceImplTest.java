package ua.softserve.academy.kv030.authservice.services.password;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import ua.softserve.academy.kv030.authservice.dao.PasswordDAO;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;
import ua.softserve.academy.kv030.authservice.services.mail.EmailService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasswordExpirationServiceImplTest {

    private PasswordExpirationService passwordExpirationService;

    private List<Password> passwordList;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordDAO passwordDAO;
    @Mock
    private Logger logger;

    @Before
    public void setUp() throws Exception {
        this.passwordList = new ArrayList<>();
        IntStream.range(0,3).forEach(i->{
            Password password = new Password();
            password.setPasswordId(10+i);
            password.setStatus(true);
            password.setExpirationTime(new Timestamp(System.currentTimeMillis()+i*60*1000));
            User user = new User();
            user.setUserId(10+i);
            user.setStatus(true);
            List<Password> userPasswords = new ArrayList<>();
            userPasswords.add(password);
            user.setPasswords(userPasswords);
            password.setUser(user);
            this.passwordList.add(password);
        });
        passwordExpirationService = new PasswordExpirationServiceImpl(logger, passwordDAO, emailService);
    }

    @Test
    public void notifyUserIfPasswordExpiresSoon_Correct() throws Exception {
        when(passwordDAO.findAllActivePasswordsExpiredWithinTimeRange(any(), any())).thenReturn(passwordList);

        passwordExpirationService.notifyUserIfPasswordExpiresSoon();

        passwordList.forEach(password -> {
            verify(emailService, times(1)).sendMail(password.getUser(), EmailType.PASSWORD_EXPIRATION);
        });
    }

    @Test
    public void notifyUserIfPasswordExpiresSoon_InCaseEmailServiceThrowsException_OtherTasksStillGo() throws Exception {
        when(passwordDAO.findAllActivePasswordsExpiredWithinTimeRange(any(Timestamp.class), any(Timestamp.class))).thenReturn(passwordList);
        when(emailService.sendMail(passwordList.get(0).getUser(), EmailType.PASSWORD_EXPIRATION)).thenThrow(new AuthServiceException(""));

        passwordExpirationService.notifyUserIfPasswordExpiresSoon();

        passwordList.forEach(password -> {
            verify(emailService, times(1)).sendMail(password.getUser(), EmailType.PASSWORD_EXPIRATION);
        });
    }


    @Test
    public void disableExpiredPasswords_Correct() throws Exception {
        when(passwordDAO.findAllActivePasswordsThatExpired()).thenReturn(passwordList);

        passwordExpirationService.disableExpiredPasswords();

        passwordList.forEach(password -> {
            verify(passwordDAO, times(1)).update(password);
            assertEquals(false, password.isStatus());
            verify(emailService,times(1)).sendMail(password.getUser(), EmailType.PASSWORD_EXPIRATION);
        });
    }

    @Test
    public void disableExpiredPasswords__InCaseEmailServiceThrowsException_OtherTasksStillGo() throws Exception {
        when(passwordDAO.findAllActivePasswordsThatExpired()).thenReturn(passwordList);
        when(emailService.sendMail(passwordList.get(0).getUser(), EmailType.PASSWORD_EXPIRATION)).thenThrow(new AuthServiceException(""));

        passwordExpirationService.disableExpiredPasswords();

        passwordList.forEach(password -> {
            verify(passwordDAO, times(1)).update(passwordList.get(0));
            verify(emailService, times(1)).sendMail(password.getUser(), EmailType.PASSWORD_EXPIRATION);
        });
    }

}