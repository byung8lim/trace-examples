package com.lbh.openapi.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class CryptoUtil {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(CryptoUtil.class);

	private static final String ALGORITHM = "AES";
	private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static Key generateKey(String seed) {
        byte[] keyBytes = seed.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String encrypt(String plainText, String seed) throws NoSuchAlgorithmException
    		, NoSuchPaddingException
    		, InvalidKeyException
    		, BadPaddingException
    		, IllegalBlockSizeException {
        Key key = generateKey(seed);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        String encResStr = Base64.getEncoder().encodeToString(encryptedBytes);
        if(log.isDebugEnabled()) {
        	log.debug("seed:"+seed+",plainText:"+plainText);
        	log.debug("============>encResStr:"+encResStr);
        }
        return encResStr;
    }

    public static String decrypt(String encryptedText, String seed) throws NoSuchPaddingException
    		, BadPaddingException
    		, InvalidKeyException
    		, IllegalBlockSizeException
    		, NoSuchAlgorithmException {
        Key key = generateKey(seed);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decString = new String(decryptedBytes, StandardCharsets.UTF_8);
        if(log.isDebugEnabled()) {
        	log.debug("seed:"+seed+",encryptedText:"+encryptedText);
        	log.debug("============>decString:"+decString);
        }
        return decString;
    }
}
