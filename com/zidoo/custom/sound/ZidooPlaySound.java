package com.zidoo.custom.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class ZidooPlaySound {
    public static void playSound(Context context, int sound) {
        SoundPool soundPool = new SoundPool(10, 3, 100);
        soundPool.play(soundPool.load(context, sound, 1), 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public static void playSound(Context context, String assetsName) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(assetsName);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
