package com.zidoo.custom.sound;

import android.content.Context;
import android.media.SoundPool;
import android.provider.Settings.System;
import android.support.v4.view.MotionEventCompat;
import com.zidoo.custom.init.ZidooJarPermissions;
import java.util.HashMap;

public class ZidooKeySoundTool {
    private static boolean ISSOUND = true;
    private static boolean ISSYSTEM = true;
    public static final int WARNINGCODE = -1000;
    private static Context mContext = null;
    private static HashMap<Integer, Integer> soundMap = new HashMap();
    private static SoundPool soundPool = null;

    public static void setSound(boolean isPlaySound) {
        ISSOUND = isPlaySound;
    }

    public static void setConnectSystem(boolean isConnectSystem) {
        ISSYSTEM = isConnectSystem;
    }

    private static boolean isTouchVoice() {
        try {
            if (System.getInt(mContext.getContentResolver(), "sound_effects_enabled", 0) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void initKeySound(Context context, int upSound, int downSound, int leftSound, int rightSound, int menuSound, int backSound, int centerSound, int warnSound) {
        mContext = context;
        soundPool = new SoundPool(10, 3, 100);
        soundMap.put(Integer.valueOf(1), Integer.valueOf(soundPool.load(context, upSound, 1)));
        soundMap.put(Integer.valueOf(2), Integer.valueOf(soundPool.load(context, downSound, 1)));
        soundMap.put(Integer.valueOf(3), Integer.valueOf(soundPool.load(context, leftSound, 1)));
        soundMap.put(Integer.valueOf(4), Integer.valueOf(soundPool.load(context, rightSound, 1)));
        soundMap.put(Integer.valueOf(5), Integer.valueOf(soundPool.load(context, menuSound, 1)));
        soundMap.put(Integer.valueOf(6), Integer.valueOf(soundPool.load(context, backSound, 1)));
        soundMap.put(Integer.valueOf(7), Integer.valueOf(soundPool.load(context, centerSound, 1)));
        soundMap.put(Integer.valueOf(8), Integer.valueOf(soundPool.load(context, warnSound, 1)));
        ZidooJarPermissions.checkZidooPermissions();
    }

    public static void playKeySound(int keyCode) {
        if (!ISSOUND || soundPool == null) {
            return;
        }
        if (ISSYSTEM && !isTouchVoice()) {
            return;
        }
        if (keyCode == -1000) {
            try {
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(8))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (keyCode) {
            case 4:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(6))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case 19:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(1))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case 20:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(2))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case MotionEventCompat.AXIS_WHEEL /*21*/:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(3))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case MotionEventCompat.AXIS_GAS /*22*/:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(4))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case MotionEventCompat.AXIS_BRAKE /*23*/:
            case 66:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(7))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            case 82:
                soundPool.play(((Integer) soundMap.get(Integer.valueOf(5))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            default:
                return;
        }
        e.printStackTrace();
    }
}
