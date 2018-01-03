package ua.softserve.academy.kv030.authservice.services.mail;


import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.User;

public interface EmailService {

    boolean sendMail(User user, Resource resource, EmailType type);
    boolean sendMail(User user, EmailType type);
}
