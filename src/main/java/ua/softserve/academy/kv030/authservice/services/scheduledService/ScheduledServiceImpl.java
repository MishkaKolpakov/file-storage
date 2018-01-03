package ua.softserve.academy.kv030.authservice.services.scheduledService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import ua.softserve.academy.kv030.authservice.dao.ResourceDAO;
import ua.softserve.academy.kv030.authservice.dao.UserDAO;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.services.ResourceService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;

import java.util.List;

/**
 * this class is implementation of ScheduledService interface
 *
 * @see ScheduledService
 */

@Service
public class ScheduledServiceImpl implements ScheduledService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledServiceImpl.class);
    private EmailService emailService;
    private ResourceDAO resourceDAO;
    private UserDAO userDAO;
    private ResourceService resourceService;
    private TwitterFactory twitterFactory;

    @Autowired
    public ScheduledServiceImpl(EmailService emailService, ResourceDAO resourceDAO, ResourceService resourceService, UserDAO userDAO, TwitterFactory twitterFactory) {
        this.emailService = emailService;
        this.resourceDAO = resourceDAO;
        this.resourceService = resourceService;
        this.userDAO = userDAO;
        this.twitterFactory = twitterFactory;
    }

    /**
     * This method regularly deletes expired files from database
     */
    @Override
    @Scheduled(cron = "${cron.expression.clean-files}")
    public void cleanExpiredFiles() {
        List<Resource> resources = resourceDAO.findAll();
        for (Resource resource : resources) {
            if (resourceService.isResourceExpired(resource.getLinkToFile())) {
                log.info("Cleaning service working");
                emailService.sendMail(resource.getOwner(), null, EmailType.DELETED_FILE);
            }

        }
    }

    @Override
    @Scheduled(cron = "${cron.expression.post-twitter}")
    public void postRegisteredUsersInTwitter() {
        Twitter twitter = twitterFactory.getInstance();
        int usersAmount = userDAO.findAll().size();
        try {
            Status status = twitter.updateStatus(String.format("We have already %d registered users, thank you for your support!", usersAmount));
            log.info("Success to post registered users in twitter");
        }
        catch (TwitterException ex){
            log.info("Failed to post registered users");
        }
    }

    @Override
    @Scheduled(cron = "${cron.expression.post-twitter}")
    public void postSavedFilesInTwitter() {
        Twitter twitter = twitterFactory.getInstance();
        int resourceAmount = resourceDAO.findAll().size();
        try {
            Status status = twitter.updateStatus(String.format("We have already %d saved files, thank you for your support!", resourceAmount));
            log.info("Success to post saved files in twitter");
        }
        catch (TwitterException ex){
            log.info("Failed to post saved files");
        }
    }
}

