package ua.softserve.academy.kv030.authservice.services.mail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailContentHolderTest {
    @Autowired
    private EmailContentHolder emailContentHolder;

    private static User user;
    private static EmailType emailType;

    @BeforeClass
    public static void init(){
        user = new User();
        user.setFirstName("Misha");
        emailType = EmailType.SUCCESS_REGISTRATION;
    }

    @Test
    public void testGetSubjectByEmailType(){
        String expected = "Successful authorization in YourSuperCloud";
        String actual = emailContentHolder.getEmailSubjectByEmailType(emailType);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetContentByEmailType() throws AuthServiceException {
        String expected = "Misha";
        String actual = emailContentHolder.getContentByEmailType(emailType, user,null);
        assertTrue(actual.contains(expected));
    }

    @Test
    public void nullInputTestGetSubjectByEmailType(){
        String actual = emailContentHolder.getEmailSubjectByEmailType(null);

        assertNull(actual);
    }

    @Test(expected = NullPointerException.class)
    public void nullInputTestGetContentByEmailType() throws AuthServiceException {
        emailContentHolder.getContentByEmailType(null, user,null);
    }

    @Test(expected = AuthServiceException.class)
    public void exceptionTestGetContentByEmailType() throws AuthServiceException {
        EmailType emailType = EmailType.TEST_TYPE;
        emailContentHolder.getContentByEmailType(emailType, user,null);
    }


}
