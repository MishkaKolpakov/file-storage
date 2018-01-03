package ua.softserve.academy.kv030.authservice.services.encryption;

import ua.softserve.academy.kv030.authservice.exceptions.CipherException;

import java.security.NoSuchAlgorithmException;

/**
 * The CipherService interface
 * consists methods for encrypting and decrypting byte arrays
 *
 * @author  Nikita Mykhailov
 * @version 1.0
 * @since   2017-11-16
 */
public interface CipherService {

    /**
     * This method encrypts an array of bytes using AES –
     * Advanced Encryption Standard which is a symmetric encryption algorithm.
     *
     * @param  fileBytes the bytes to be encrypted
     * @param  key the string for generation a SecretKey
     * @return encrypted byte array
     * @throws CipherException
     */
    byte[] encrypt(byte[] fileBytes, String key) throws CipherException;

    /**
     * This method decrypts an array of encrypted bytes using AES
     *
     * @param  fileBytes the encrypted bytes to be decrypted
     * @param  key string for generation a SecretKey (should be the same as a key for encryption)
     * @return decrypted byte array
     * @throws CipherException
     */
    byte[] decrypt(byte[] fileBytes, String key) throws CipherException;

    /**
     * This method generates secret key for encryption
     *
     * @return key as a string value
     * @throws CipherException
     */
    String generateKey() throws NoSuchAlgorithmException;

}

