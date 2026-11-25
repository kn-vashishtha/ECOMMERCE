package org.bootcamp.ecommerce.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class SimpleEncryptorUtil {

    private static final String ALGO = "AES";
    @Value("${security.jwt.aes-key}")
    private String aesSecretKey;

    public String encrypt(String data) {
        try {
            SecretKeySpec secretKeySec = new SecretKeySpec(aesSecretKey.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: ", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec secretKeySec = new SecretKeySpec(aesSecretKey.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: ", e);
        }

    }
}
