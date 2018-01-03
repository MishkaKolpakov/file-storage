package ua.softserve.academy.kv030.authservice.api.controller.helper;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softserve.academy.kv030.authservice.exceptions.DataValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator {

    private Logger logger;
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    @Autowired
    public EmailValidator(Logger logger) {
        this.logger = logger;
    }

    public void validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if( !matcher.matches()) {
            logger.error(String.format("Not valid email: %s.", email));
            throw new DataValidationException(String.format("Not valid email: %s.", email));
        }
    }
}
