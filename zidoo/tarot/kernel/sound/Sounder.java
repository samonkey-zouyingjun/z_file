package zidoo.tarot.kernel.sound;

import android.content.Context;
import android.media.SoundPool;
import java.io.IOException;
import java.util.List;

public class Sounder {
    public static int SOUND_ALL_STAR_TOUCH = 0;
    public static final int SOUND_LOOP_FOREVER = -1;
    public static final int SOUND_LOOP_ONCE = 0;
    public static int SOUND_PICKED_NUMBER = 0;
    public static int SOUND_STAR_TOUCH_01 = 0;
    public static int SOUND_STAR_TOUCH_02 = 0;
    public static int SOUND_STAR_TOUCH_03 = 0;
    private Context mContext = null;
    private int mMaxStreams = 4;
    private List<String> mSoundFiles = null;
    private SoundPool mSoundPool = null;
    private int mSoundPriority = 1;
    private int mSrcQuality = 0;

    public Sounder(Context context) {
        this.mContext = context;
        initialize();
    }

    private void initialize() {
        this.mSoundPool = new SoundPool(this.mMaxStreams, 3, this.mSrcQuality);
    }

    public int loadSound(String assetPath) {
        if (this.mSoundPool == null) {
            return 0;
        }
        try {
            return this.mSoundPool.load(this.mContext.getAssets().openFd(assetPath), this.mSoundPriority);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void unloadSound(int sound) {
        if (this.mSoundPool != null) {
            this.mSoundPool.unload(sound);
        }
    }

    public void play(int sound) {
        if (this.mSoundPool != null) {
            this.mSoundPool.play(sound, 1.0f, 1.0f, this.mSoundPriority, 0, 1.0f);
        }
    }

    public void play(int sound, int leftVolume, int rightVolume, int loopMode, int speed) {
        if (this.mSoundPool != null) {
            this.mSoundPool.play(sound, (float) leftVolume, (float) rightVolume, this.mSoundPriority, loopMode, (float) speed);
        }
    }

    public void destory() {
        if (this.mSoundPool != null) {
            this.mSoundPool.unload(SOUND_PICKED_NUMBER);
            this.mSoundPool.unload(SOUND_STAR_TOUCH_01);
            this.mSoundPool.unload(SOUND_STAR_TOUCH_02);
            this.mSoundPool.unload(SOUND_STAR_TOUCH_03);
            this.mSoundPool.unload(SOUND_ALL_STAR_TOUCH);
            this.mSoundPool.release();
        }
    }
}
