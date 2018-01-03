package ua.softserve.academy.kv030.authservice.services.mail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.Password;
import ua.softserve.academy.kv030.authservice.entity.User;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceImplTest {

    @Autowired
    private EmailService emailService;
    @Autowired
    private JavaMailSender mailSender;

    private static final String email = "nikmikhailov13@gmail.com";
    private static final String userName = "User";
    private static User user;

    @BeforeClass
    public static void init(){
        user = new User();
        user.setEmail(email);
        user.setFirstName(userName);
        Password p = new Password();
        p.setPassword("123");
        user.setPasswords(new ArrayList<>());

    }

    @Test(expected = MailParseException.class)
    public void mailPreparationTest() {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo("");
        };
        mailSender.send(messagePreparator);
    }

    @Test
    public void sendAuthSuccessTest() {

        boolean success = emailService.sendMail(user, EmailType.SUCCESS_REGISTRATION);
        assertTrue(success);
    }

    @Test
    public void sendPasswordExpTest() {

        boolean success = emailService.sendMail(user,  EmailType.PASSWORD_EXPIRATION);
        assertTrue(success);
    }


}