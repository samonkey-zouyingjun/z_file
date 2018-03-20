package com.zidoo.custom.voice;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PlayVoiceTool {
    public static final int STYLES_FEMALE = 0;
    public static final int STYLES_MALE = 1;

    public static void initPlayVoice(Context context) {
        try {
            context.getContentResolver().getType(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playVoice(Context context, String voice) {
        if (voice != null && !voice.trim().equals("")) {
            Intent intent = new Intent("android.voice.play");
            intent.putExtra("voice", voice.trim());
            context.sendBroadcast(intent);
        }
    }

    public static void playVoiceUri(Context context, String voice) {
        if (voice != null && !voice.trim().equals("")) {
            try {
                context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1), "2", new String[]{voice.trim()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean setPlayVoiceStyle(Context context, int style) {
        try {
            context.getContentResolver().query(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1), null, new StringBuilder(String.valueOf(style)).toString(), new String[]{""}, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getPlayStyle(Context context) {
        int i = 0;
        try {
            i = context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1), "1", new String[]{""});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static boolean setPlayVoice(Context context, boolean isPlay) {
        try {
            System.out.println("bob   setPlayVoice  isPlay = " + isPlay);
            if (context.getContentResolver().update(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1), new ContentValues(), isPlay ? "1" : "0", new String[]{""}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPlayVoice(Context context) {
        try {
            if (context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://com.zidoo.voice.service.provide/"), 1), "0", new String[]{""}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
