package com.umeng.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* compiled from: AesHelper */
public class b {
    private static byte[] a = "uLi4/f4+Pb39.T19".getBytes();
    private static byte[] b = "nmeug.f9/Om+L823".getBytes();

    public static String a(String str, String str2) throws Exception {
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        instance.init(1, new SecretKeySpec(a, "AES"), new IvParameterSpec(b));
        return c.d(instance.doFinal(str.getBytes(str2)));
    }

    public static String b(String str, String str2) throws Exception {
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        instance.init(2, new SecretKeySpec(a, "AES"), new IvParameterSpec(b));
        return new String(instance.doFinal(c.b(str)), str2);
    }
}
