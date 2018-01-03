package ua.softserve.academy.kv030.authservice.services.encryption;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.exceptions.CipherException;
import ua.softserve.academy.kv030.authservice.services.ResourceServiceImplTest;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CipherServiceImplTest {

    private CipherService cipherService;

    private String algorithmName="AES";
    private byte[] iv;
    private byte[] fileBytes;
    private String key;

    private static String algorithm="AES/CTR/NoPadding";
    private static Cipher cipher;
    @BeforeClass
    public static void before(){
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
    @Before
    public void setUp() throws Exception {
        iv = new byte[16];
        Arrays.fill(iv,(byte)0);
        Logger logger = LoggerFactory.getLogger(ResourceServiceImplTest.class);
        cipherService = new CipherServiceImpl(algorithmName, cipher ,new IvParameterSpec(iv),logger);
        fileBytes = Files.readAllBytes(Paths.get("README.md"));
        key = "YourSuperKey1234";
    }

    @Test
    public void successfulEncryptDecryptTest() throws Exception {
        byte[] encryptedBytes = cipherService.encrypt(fileBytes, key);
        byte[] decryptedBytes = cipherService.decrypt(encryptedBytes, key);
        assertArrayEquals(fileBytes, decryptedBytes);
    }

    @Test
    public void unsuccessfulEncryptDecryptTest() throws Exception {
        byte[] encryptedBytes = cipherService.encrypt(fileBytes, key);
        byte[] decryptedBytes = cipherService.decrypt(encryptedBytes, "YourSuperKey1235");
        boolean equal = Arrays.equals(fileBytes, decryptedBytes);
        assertFalse(equal);
    }

    @Test(expected = CipherException.class)
    public void illegalSizeKeyTest() throws Exception {

        cipherService.encrypt(fileBytes, "YourSuperKey123");
    }


    @Test
    public void fileExistsTest() {
        assertNotNull(fileBytes);
    }

}