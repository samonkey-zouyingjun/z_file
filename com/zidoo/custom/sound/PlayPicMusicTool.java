package com.zidoo.custom.sound;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class PlayPicMusicTool {
    public static String getPicPlayPath() {
        return Environment.getExternalStorageDirectory() + "/.picplaymusic/music.mp3";
    }

    public static boolean setIsPlayMusic(Context context, boolean isPlay) {
        try {
            ContentResolver mContentResolver = context.getContentResolver();
            Uri mUri = ContentUris.withAppendedId(Uri.parse("content://com.zidoo.old.media.provide/"), 1);
            String str = "isPlay";
            String[] strArr = new String[1];
            strArr[0] = isPlay ? "1" : "0";
            if (mContentResolver.delete(mUri, str, strArr) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPlayMusic(Context context) {
        try {
            if (context.getContentResolver().update(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.old.media.provide/"), 1), new ContentValues(), "isPlay", new String[]{""}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static int getCurrentPlayModel(Context context) {
        int i = 0;
        try {
            i = context.getContentResolver().update(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.old.media.provide/"), 1), new ContentValues(), "playMode", new String[]{""});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static boolean setCurrentPlayModel(Context context, int model) {
        try {
            if (context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.old.media.provide/"), 1), "playMode", new String[]{new StringBuilder(String.valueOf(model)).toString()}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
