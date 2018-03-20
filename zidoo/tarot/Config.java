package zidoo.tarot;

import android.graphics.Typeface;

public class Config {
    private final DisplayConfig display = new DisplayConfig();
    private final EngineInfo engineInfo = new EngineInfo();
    private GLEnvirnment glEnvirnment;
    private Typeface typeface = Typeface.DEFAULT;

    public static class DebugConfig {
        public static boolean sDebug = true;
    }

    public static class DisplayConfig {
        public float sHeightRatio;
        public float sRatioX;
        public float sRatioY;
        public int sReferenceHeight = 1080;
        public int sReferenceWidth = 1920;
        public float sScaleX;
        public float sScaleY;
        public int sScreenHeight;
        public int sScreenWidth;
        public float sWidthRatio;

        public void init(int width, int height) {
            this.sRatioX = ((float) width) / ((float) this.sReferenceWidth);
            this.sRatioY = ((float) height) / ((float) this.sReferenceHeight);
            this.sScreenWidth = width;
            this.sScreenHeight = height;
            if (this.sRatioX > this.sRatioY) {
                this.sScaleX = (this.sRatioX * 5.343756E-4f) / this.sRatioY;
                this.sScaleY = 5.342583E-4f;
                this.sWidthRatio = (this.sRatioX * 0.0053333333f) / this.sRatioY;
                this.sHeightRatio = 0.0053333337f;
                return;
            }
            this.sScaleX = 5.343756E-4f;
            this.sScaleY = (this.sRatioX * 5.342583E-4f) / this.sRatioY;
            this.sWidthRatio = 0.0053333333f;
            this.sHeightRatio = (this.sRatioY * 0.0053333337f) / this.sRatioX;
        }

        public float adaptationWidthPixels(float pixels) {
            return (((float) this.sScreenWidth) * pixels) / 1920.0f;
        }

        public float adaptationHeightPixels(float pixels) {
            return (((float) this.sScreenHeight) * pixels) / 1080.0f;
        }

        public String print() {
            return "RatioX:\t" + this.sRatioX + "RatioY:" + this.sRatioY + "ScreenWidth:" + this.sScreenWidth + " ScreenHeight:" + this.sScreenHeight + " ScaleX:" + this.sScaleX + " ScaleY:" + this.sScaleY + " WidthRatio:" + this.sWidthRatio + " HeightRatio:" + this.sHeightRatio;
        }
    }

    public class InputConfig {
    }

    public class PhysicConfig {
    }

    public class SoundConfig {
    }

    public DisplayConfig getDisplay() {
        return this.display;
    }

    public EngineInfo getEngineInfo() {
        return this.engineInfo;
    }

    public void setGlEnvirnment(GLEnvirnment glEnvirnment) {
        this.glEnvirnment = glEnvirnment;
    }

    public GLEnvirnment getGlEnvirnment() {
        return this.glEnvirnment;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }
}
