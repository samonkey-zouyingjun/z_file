package com.zidoo.custom.share;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ZidooSharedPrefsUtil {
    public static void putValue(Context context, String key, int value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, boolean value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putBoolean(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, String value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putString(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, long value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putLong(key, value);
        sp.commit();
    }

    public static int getValue(Context context, String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public static long getValue(Context context, String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue);
    }

    public static boolean getValue(Context context, String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    public static String getValue(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static void delValue(Context context, String key) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.remove(key);
        sp.commit();
    }
}
