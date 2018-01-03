package ua.softserve.academy.kv030.authservice.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.User;

import java.time.DateTimeException;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private EmailContentHolder emailContentHolder;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, EmailContentHolder emailContentHolder) {
        this.mailSender = mailSender;
        this.emailContentHolder = emailContentHolder;
    }

    @Override
    public boolean sendMail(User user, Resource resource, EmailType type) throws DateTimeException {

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            String content = emailContentHolder.getContentByEmailType(type, user, resource);
            String subject = emailContentHolder.getEmailSubjectByEmailType(type);
            String email = user.getEmail();

            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
        };

        try {
            mailSender.send(messagePreparator);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendMail(User user, EmailType type) throws DateTimeException {
        return sendMail(user, null, type);
    }
}
