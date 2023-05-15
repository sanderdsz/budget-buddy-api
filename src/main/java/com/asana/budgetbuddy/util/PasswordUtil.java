package com.asana.budgetbuddy.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component
public class PasswordUtil {

    @Value("salt")
    private String salt;

    @Value("secret")
    private String secret;

    public String generateEncryption(String password)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException {
        SecretKey key = AESEncryption.getKeyFromPassword(password, salt);
        IvParameterSpec iv = AESEncryption.generateIv();
        String encryptedPassword = AESEncryption.encryptPasswordBased(
                secret,
                key,
                iv
        );
        return encryptedPassword;
    }
}
