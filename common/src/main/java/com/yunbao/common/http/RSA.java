package com.yunbao.common.http;

//import java.util.Base64;
import android.util.Log;

import com.yunbao.common.pay.ali.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

import okhttp3.RequestBody;

public class RSA {
    private static int chunkEncrypt = 128-11;
    private static int chunkDecrypt = 128;

    private static RSAPublicKey pubKey;
    private static RSAPrivateKey priKey;

    public static RSAPublicKey publicKey(String keyString) throws Exception {
        if (pubKey == null) {
            byte[] keyBytes = Base64.decode(keyString);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            pubKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(keyBytes));
        }
        return pubKey;
    }

    public static RSAPrivateKey privateKey(String keyString) throws Exception {
        if (priKey == null) {
            byte[] keyBytes = Base64.decode(keyString);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            priKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        }
        return priKey;
    }

    public static String encrypt(String s, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);
        byte[] rBytes = doChuck(cipher, sBytes, chunkEncrypt);

        return Base64.encode(rBytes);
    }

    public static String decrypt(String s, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] sBytes = Base64.decode(s);
        byte[] rBytes = doChuck(cipher, sBytes, chunkDecrypt);

        return new String(rBytes);
    }

    private static byte[] doChuck(Cipher cipher, byte[] bytes, int chunkLength) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i += chunkLength) {
            byte[] chunkBytes = Arrays.copyOfRange(bytes, i, i+chunkLength);
            byte[] ciph = cipher.doFinal(chunkBytes);
            output.write(ciph);
        }
        return output.toByteArray();
    }
}
