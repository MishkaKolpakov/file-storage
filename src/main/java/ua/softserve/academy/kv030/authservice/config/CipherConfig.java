package ua.softserve.academy.kv030.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;

@Configuration
public class CipherConfig {

    @Value("${cipher.algorithm}")
    private String algorithm;

    private byte [] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @Bean
    public Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(algorithm);
    }

   @Bean
    public IvParameterSpec getIV(){
       return new IvParameterSpec(iv);
   }
}
