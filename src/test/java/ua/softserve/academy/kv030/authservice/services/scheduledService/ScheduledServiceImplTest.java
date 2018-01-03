package ua.softserve.academy.kv030.authservice.services.scheduledService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import twitter4j.TwitterFactory;
import ua.softserve.academy.kv030.authservice.dao.ResourceDAO;
import ua.softserve.academy.kv030.authservice.dao.UserDAO;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.services.ResourceService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailService;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledServiceImplTest {

    private static ScheduledServiceImpl scheduledService;
    private static List<Resource> resourceList;
    @Mock
    private static EmailService emailService;
    @Mock
    private static ResourceDAO resourceDAO;
    @Mock
    private static ResourceService resourceService;
    @Mock
    private static UserDAO userDAO;
    @Mock
    private static TwitterFactory twitterFactory;
    private static SecretKey secretKey;
    private static SecretKey secretKey1;


    @BeforeClass
    public static void init() {
        resourceList = new ArrayList<>();
        User user = new User();
        Resource resource = new Resource();
        Resource resource1 = new Resource();
        secretKey = new SecretKey();
        secretKey1 = new SecretKey();
        Date notTodayDate = new Date(1000);
        secretKey.setExpirationDate(new Timestamp(System.currentTimeMillis()));
        secretKey1.setExpirationDate(new Timestamp(notTodayDate.getTime()));
        resource.setOwner(user);
        resource.setSecretKey(secretKey);
        resource1.setSecretKey(secretKey1);
        resourceList.add(resource);
        resourceList.add(resource1);
        emailService = mock(EmailService.class);
        resourceDAO = mock(ResourceDAO.class);
        resourceService = mock(ResourceService.class);
        userDAO = mock(UserDAO.class);
        twitterFactory = mock(TwitterFactory.class);
    }

    @Before
    public void runBeforeTest() {
        scheduledService = new ScheduledServiceImpl(emailService, resourceDAO, resourceService, userDAO, twitterFactory);
    }

    @Test
    public void cleanExpiredFiles() throws Exception {
        when(resourceDAO.findAll()).thenReturn(resourceList);
        when(resourceService.isResourceExpired(anyString())).thenReturn(true);
        scheduledService.cleanExpiredFiles();
        verify(emailService, times(1)).sendMail(resourceList.get(0).getOwner(), null, EmailType.DELETED_FILE);
        verify(resourceDAO, times(1)).findAll();
        verify(resourceService, times(resourceList.size())).isResourceExpired(anyString());
    }

    @Test
    public void cleanExpiredFilesFailed() throws Exception {
        when(resourceDAO.findAll()).thenReturn(resourceList);
        when(resourceService.isResourceExpired(anyString())).thenReturn(false);
        scheduledService.cleanExpiredFiles();
        verify(emailService, times(0)).sendMail(resourceList.get(0).getOwner(), null, EmailType.DELETED_FILE);
        verify(resourceDAO, times(1)).findAll();
        verify(resourceService, times(resourceList.size())).isResourceExpired(anyString());
    }

}