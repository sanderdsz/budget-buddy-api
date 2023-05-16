package com.asana.budgetbuddy.util;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AESEncryptionTest implements WithAssertions {

    @Test
    void givenPassword_whenEncrypt_thenSuccess()
            throws
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException {
        // given
        String plainText = "loremipsum";
        String password = "1234567890";
        String salt = "1234567890";
        IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
        SecretKey key = AESEncryption.getKeyFromPassword(password, salt);
        // when
        String cipherText = AESEncryption.encryptPasswordBased(
                plainText,
                key,
                ivParameterSpec
        );
        String decryptedCipherText = AESEncryption.decryptPasswordBased(
                cipherText,
                key,
                ivParameterSpec
        );
        // then
        Assertions.assertEquals(plainText, decryptedCipherText);
    }
}
