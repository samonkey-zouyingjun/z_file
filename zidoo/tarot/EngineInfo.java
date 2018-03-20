package zidoo.tarot;

public class EngineInfo {
    public static int SCREEN_LANDSCAPE = 0;
    public static int SCREEN_PORTRAIT = 1;
    protected long mDeltaTime = 0;
    private float mFPS = 0.0f;
    protected int mFSAASamples = 0;
    private boolean mIsLandscape = true;
    protected long mLastTime = 0;

    public float getFPS() {
        return this.mFPS;
    }

    public long getDeltaTime() {
        return this.mDeltaTime;
    }

    public long getCurrentTime() {
        return this.mLastTime;
    }

    protected void setCurrentTime(long currentMillTime) {
        this.mDeltaTime = currentMillTime - this.mLastTime;
        this.mLastTime = currentMillTime;
        this.mFPS = 1000.0f / ((float) this.mDeltaTime);
    }

    public void setScreenOrientation(boolean isLandscape) {
        this.mIsLandscape = isLandscape;
    }

    public void setScreenOrientation(int orientation) {
        this.mIsLandscape = orientation != SCREEN_PORTRAIT;
    }

    public int getScreenOrientation() {
        return this.mIsLandscape ? SCREEN_LANDSCAPE : SCREEN_PORTRAIT;
    }

    public int getFSAASamples() {
        return this.mFSAASamples;
    }

    public void setFSAASamples(int samples) {
        this.mFSAASamples = samples;
    }
}
