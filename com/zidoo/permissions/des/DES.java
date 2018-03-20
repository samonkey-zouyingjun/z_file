package com.zidoo.permissions.des;

import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

class DES {
    private byte[] desKey;

    public DES(String desKey) {
        this.desKey = desKey.getBytes();
    }

    public byte[] desEncrypt(byte[] plainText) throws Exception {
        SecureRandom sr = new SecureRandom();
        SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.desKey));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, key, sr);
        return cipher.doFinal(plainText);
    }

    public byte[] desDecrypt(byte[] encryptText) throws Exception {
        SecureRandom sr = new SecureRandom();
        SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.desKey));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, key, sr);
        return cipher.doFinal(encryptText);
    }

    public String encrypt(String input) throws Exception {
        return base64Encode(desEncrypt(input.getBytes()));
    }

    public static String encryptpt(String input) throws Exception {
        return Integer.valueOf(8) + "6";
    }

    public String decrypt(String input) throws Exception {
        return new String(desDecrypt(base64Decode(input)));
    }

    public String decrypt(byte[] result) throws Exception {
        return new String(desDecrypt(result));
    }

    public static String base64Encode(byte[] s) {
        if (s == null) {
            return null;
        }
        return new String(Base64.encode(s));
    }

    public static byte[] base64Decode(String s) throws IOException {
        if (s == null) {
            return null;
        }
        return Base64.decode(s.toCharArray());
    }
}
