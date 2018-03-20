package com.zidoo.custom.animation;

public class ZidooAnimaitonInfo {
    public ZidooAlpha mAlpha = null;
    public ZidooPosition mPosition = null;
    public ZidooScale mScale = null;
    public int mTimeDuration = -1;

    public ZidooAnimaitonInfo(ZidooScale mScale, ZidooPosition mPosition, ZidooAlpha mAlpha, int mTimeDuration) {
        this.mScale = mScale;
        this.mPosition = mPosition;
        this.mAlpha = mAlpha;
        this.mTimeDuration = mTimeDuration;
    }

    public ZidooAnimaitonInfo(ZidooScale mScale, ZidooPosition mPosition, ZidooAlpha mAlpha) {
        this.mScale = mScale;
        this.mPosition = mPosition;
        this.mAlpha = mAlpha;
    }

    public ZidooAnimaitonInfo(ZidooScale mScale) {
        this.mScale = mScale;
    }

    public ZidooAnimaitonInfo(ZidooPosition mPosition) {
        this.mPosition = mPosition;
    }

    public ZidooAnimaitonInfo(ZidooAlpha mAlpha) {
        this.mAlpha = mAlpha;
    }
}
