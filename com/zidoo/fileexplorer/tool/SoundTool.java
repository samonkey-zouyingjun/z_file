package com.zidoo.fileexplorer.tool;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.widget.ImageView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import java.util.HashMap;

public class SoundTool {
    public static final boolean ISZIDOO = true;
    static final String THEMEURI_egreat = "content://com.egreat.theme.provide/";
    static final String THEMEURI_zidoo = "content://com.zidoo.sound.provide/";
    private static ImageView bgView = null;
    @SuppressLint({"UseSparseArrays"})
    static HashMap<Integer, Integer> soundMap = new HashMap();
    static SoundPool soundPool = null;

    public static int initSound(Context context, ImageView bgView1) {
        bgView = bgView1;
        if (soundPool == null) {
            soundPool = new SoundPool(10, 3, 100);
            soundMap.put(Integer.valueOf(1), Integer.valueOf(soundPool.load(context, R.raw.up, 1)));
            soundMap.put(Integer.valueOf(2), Integer.valueOf(soundPool.load(context, R.raw.soud_theme0, 1)));
            soundMap.put(Integer.valueOf(3), Integer.valueOf(soundPool.load(context, R.raw.left, 1)));
            soundMap.put(Integer.valueOf(4), Integer.valueOf(soundPool.load(context, R.raw.right, 1)));
            soundMap.put(Integer.valueOf(5), Integer.valueOf(soundPool.load(context, R.raw.menu, 1)));
            soundMap.put(Integer.valueOf(6), Integer.valueOf(soundPool.load(context, R.raw.back, 1)));
            soundMap.put(Integer.valueOf(7), Integer.valueOf(soundPool.load(context, R.raw.dpad_center, 1)));
        }
        try {
            String resutl = context.getContentResolver().getType(ContentUris.withAppendedId(Uri.parse(THEMEURI_zidoo), 1));
            if (resutl != null) {
                String[] resu = resutl.split("]");
                if (resu[0].equals("1")) {
                    AppConstant.sIsSound = true;
                } else {
                    AppConstant.sIsSound = false;
                }
                return Integer.valueOf(resu[1]).intValue();
            }
            AppConstant.sIsSound = true;
            AppConstant.sIsSound = false;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void soundKey(int keyCode) {
        if (AppConstant.sIsSound && soundPool != null) {
            try {
                if (bgView != null) {
                    bgView.playSoundEffect(0);
                    return;
                }
                switch (keyCode) {
                    case 4:
                        soundPool.play(((Integer) soundMap.get(Integer.valueOf(6))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                        return;
                    case 19:
                        soundPool.play(((Integer) soundMap.get(Integer.valueOf(2))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                        return;
                    case 20:
                        soundPool.play(((Integer) soundMap.get(Integer.valueOf(2))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                        return;
                    case MotionEventCompat.AXIS_WHEEL /*21*/:
                        soundPool.play(((Integer) soundMap.get(Integer.valueOf(2))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
                        return;
                    case MotionEventCompat.AXIS_GAS /*22*/:
                        soundPool.play(((Integer) soundMap.get(Integer.valueOf(2))).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
