package zidoo.tarot.kernel;

import android.opengl.GLES11;

public class Texture {
    public static final int INVALID_TEXTURE_ID = 0;
    public static final String TAG = "Texture";
    private int mHeight = 0;
    private boolean mIsPreload = false;
    private int mResId = 0;
    private int mTextureID = 0;
    private int mWidth = 0;

    public int getTextureID() {
        return this.mTextureID;
    }

    public void setTextureID(int texture) {
        this.mTextureID = texture;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public float getWidth() {
        return (float) this.mWidth;
    }

    public float getHeight() {
        return (float) this.mHeight;
    }

    public float getRatio() {
        return ((float) this.mWidth) / ((float) this.mHeight);
    }

    public void recycle() {
        GLES11.glDeleteTextures(1, new int[]{this.mTextureID}, 0);
    }

    public boolean equals(Texture texture) {
        if (texture == null || texture.getTextureID() != this.mTextureID) {
            return false;
        }
        return true;
    }

    public void setPreloadFlag(boolean isPreload) {
        this.mIsPreload = isPreload;
    }

    public boolean isPreloading() {
        return this.mIsPreload;
    }

    public boolean isValid() {
        return (this.mTextureID == 0 || this.mIsPreload) ? false : true;
    }

    public Texture(int resid) {
        this.mResId = resid;
    }

    public int getResourceId() {
        return this.mResId;
    }

    public void setResouceId(int resid) {
        this.mResId = resid;
    }
}
