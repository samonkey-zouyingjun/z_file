package com.zidoo.custom.animation;

public class ZidooScale {
    public float mFromScaleX;
    public float mFromScaleY;
    public float mToScaleX;
    public float mToScaleY;

    public ZidooScale(float fromScaleX, float fromScaleY, float toScaleX, float toScaleY) {
        this.mFromScaleX = fromScaleX;
        this.mFromScaleY = fromScaleY;
        this.mToScaleX = toScaleX;
        this.mToScaleY = toScaleY;
    }

    public ZidooScale(float fromScale, float toScale) {
        this.mFromScaleX = fromScale;
        this.mFromScaleY = fromScale;
        this.mToScaleX = toScale;
        this.mToScaleY = toScale;
    }
}
