package com.zidoo.permissions.des;

import android.content.Context;
import com.zidoo.custom.cpu.CpuIdTool;

public class DESTool {
    public static native String getCodePay();

    private static native String getDesKey();

    public static native String getLookPayResult();

    public static native String getProductList();

    public static void initLib() {
        System.loadLibrary("zidoodes_jni");
    }

    public static String encrypt(String content) {
        try {
            return encrypt(content, getKey());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String content) {
        try {
            return decrypt(content, getKey());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String content, String key) {
        try {
            return new DES(key).encrypt(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String content, String key) {
        try {
            return new DES(key).decrypt(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(byte[] result, String key) {
        try {
            return new DES(key).decrypt(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getKey() {
        return getDesKey();
    }

    public static String getId(Context context) {
        return CpuIdTool.getCpuId(context);
    }
}
