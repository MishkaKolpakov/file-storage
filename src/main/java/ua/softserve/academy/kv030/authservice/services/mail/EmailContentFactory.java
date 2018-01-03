package ua.softserve.academy.kv030.authservice.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;

@Service
public class EmailContentFactory {

    private TemplateEngine templateEngine;

    @Autowired
    public EmailContentFactory(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildForPasswordExpiration(String userName, Date expDate) {
        if (expDate.before(Date.valueOf(LocalDate.now()))) {
            throw new DateTimeException("The password is already expired");
        } else {
            Context context = createContextFromUserName(userName);
            context.setVariable("expDate", expDate);
            return templateEngine.process("expiringPassword", context);
        }
    }

    public String buildForNonActivePassword(String userName) {
        Context context = createContextFromUserName(userName);
        return templateEngine.process("non-activePassword", context);
    }

    public String buildForSuccessRegistration(String userName) {
        Context context = createContextFromUserName(userName);
        return templateEngine.process("successRegistration", context);
    }

    public String buildForDeletedFile(String userName) {
        Context context = createContextFromUserName(userName);
        return templateEngine.process("deletedFiles", context);
    }

    public String buildForDisabledAccount(String userName) {
        Context context = createContextFromUserName(userName);
        return templateEngine.process("disabledAccount", context);
    }

    public String buildForUpdatedUser(String userName) {
        Context context = createContextFromUserName(userName);
        return templateEngine.process("updatedUser", context);
    }

    public String buildForSharedFile(String userName, String fileUUID, String fileOwner, String fileName) {
        Context context = createContextFromUserName(userName);
        context.setVariable("fileUUID", fileUUID);
        context.setVariable("fileName", fileName);
        context.setVariable("fileOwner", fileOwner);
        return templateEngine.process("sharedFile", context);
    }

    private Context createContextFromUserName(String userName) {
        Context context = new Context();
        context.setVariable("userName", userName);
        return context;
    }


}
