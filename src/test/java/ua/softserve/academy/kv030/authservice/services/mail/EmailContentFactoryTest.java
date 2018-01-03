package ua.softserve.academy.kv030.authservice.services.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailContentFactoryTest {

    @Autowired
    private EmailContentFactory contentBuilder;

    @Test
    public void successBuildPasswordExpiration() {

        String content = contentBuilder.buildForPasswordExpiration("Jimmy", Date.valueOf(LocalDate.now()));
        assertTrue(content.contains("Jimmy"));
        assertTrue(content.contains(Date.valueOf(LocalDate.now()).toString()));
    }

    @Test
    public void successBuildForRegistration() {
        String content = contentBuilder.buildForSuccessRegistration("Ivan");
        assertTrue(content.contains("Ivan"));
    }


    @Test(expected = DateTimeException.class)
    public void dateValidationTest() throws DateTimeException {

        Date date = Date.valueOf("2015-10-10");
        boolean expired = checkIfAlreadyExpired(date);
        assertTrue(expired);
        contentBuilder.buildForPasswordExpiration("Jimmy", date);
    }

    public boolean checkIfAlreadyExpired(Date date) {
        return date.before(Date.valueOf(LocalDate.now()));
    }

    @Test
    public void successBuildUpdatedUser() {
        String content = contentBuilder.buildForUpdatedUser("Ivan");
        assertTrue(content.contains("Ivan"));
    }

    @Test
    public void successBuildDeletedFile() {
        String content = contentBuilder.buildForDeletedFile("Ivan");
        assertTrue(content.contains("Ivan"));
    }

    @Test
    public void successBuildDisabledAccount() {
        String content = contentBuilder.buildForDisabledAccount("Ivan");
        assertTrue(content.contains("Ivan"));
    }



}