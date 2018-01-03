package ua.softserve.academy.kv030.authservice.services.encryption;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.softserve.academy.kv030.authservice.exceptions.CipherException;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;

@Service
public class CipherServiceImpl implements CipherService {


    private Cipher cipher;
    private IvParameterSpec ivSpec;
    private Logger logger;
    private String algorithm;

    private SecretKey secretKey;

    @Autowired
    public CipherServiceImpl(@Value("${cipher.algorithm.name}")String algorithm, Cipher cipher, IvParameterSpec ivSpec, Logger logger) {
        this.algorithm = algorithm;
        this.cipher = cipher;
        this.ivSpec = ivSpec;
        this.logger = logger;
    }

    @Override
    public byte[] encrypt(byte[] fileBytes, String key) throws CipherException {
        if (key.length() != Constants.KEY_SIZE) {
            logger.error("Illegal key size");
            throw new CipherException("Illegal key size");
        } else {
            try {
                secretKey = new SecretKeySpec(key.getBytes(), algorithm);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                logger.info("Bytes encryption");
                System.out.println(secretKey.toString());
                return cipher.doFinal(fileBytes);
            } catch (InvalidAlgorithmParameterException |
                    InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                logger.error(e.getMessage(), e);
                throw new CipherException("ENCRYPTION: "+e.getMessage());
            }
        }

    }
    //TODO: extract key size into properties
    @Override
    public byte[] decrypt(byte[] fileBytes, String key) throws CipherException {
        if (key.length() != Constants.KEY_SIZE) {
            logger.error("Illegal key size");
            throw new CipherException("Illegal key size");
        } else {
            try {
                secretKey = new SecretKeySpec(key.getBytes(), algorithm);
                cipher.init(DECRYPT_MODE, secretKey, ivSpec);
                logger.info("Bytes decryption");
                System.out.println(secretKey.toString());
                return cipher.doFinal(fileBytes);
            } catch (InvalidAlgorithmParameterException |
                    InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                logger.error(e.getMessage(), e);
                throw new CipherException("DECRYPTION: "+ e.getMessage());
            }
        }
    }

    @Override
    public String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return encodedKey.substring(0, 16);
    }

}
