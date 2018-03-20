package com.zidoo.custom.animation;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ZidooScroller {
    public boolean isAnimation = false;
    private float mCurrentIndex;
    private float mCurrentPara;
    private Interpolator mInterpolator = new Interpolator() {
        public float getInterpolation(float input) {
            return input;
        }
    };
    private Scroller mScroller;
    private float mSpace;
    private float mTargetIndex;
    private float mTargetPara;

    public ZidooScroller(Context context) {
        this.mScroller = new Scroller(context, this.mInterpolator);
    }

    public ZidooScroller(Context context, Interpolator i) {
        this.mInterpolator = i;
        this.mScroller = new Scroller(context, this.mInterpolator);
    }

    public ZidooScroller(Context context, float index, float space) {
        this.mScroller = new Scroller(context, this.mInterpolator);
        init(index, space);
    }

    public ZidooScroller(Context context, float index, float space, Interpolator i) {
        this.mInterpolator = i;
        this.mScroller = new Scroller(context, this.mInterpolator);
        init(index, space);
    }

    public void init(float index, float space) {
        this.mCurrentIndex = index;
        this.mSpace = space;
        this.mCurrentPara = this.mCurrentIndex * this.mSpace;
        this.mScroller.setFinalX((int) this.mCurrentPara);
        this.mScroller.abortAnimation();
    }

    public void setCurrentIndex(float index) {
        this.mCurrentIndex = index;
        this.mCurrentPara = this.mCurrentIndex * this.mSpace;
        this.mScroller.setFinalX((int) this.mCurrentPara);
        this.mScroller.abortAnimation();
    }

    public void setCurrentPara(float para) {
        this.mCurrentPara = para;
        this.mScroller.setFinalX((int) this.mCurrentPara);
        this.mScroller.abortAnimation();
    }

    public float getCurrentPara() {
        this.mCurrentPara = (float) this.mScroller.getCurrX();
        return this.mCurrentPara;
    }

    public float getCurrentIndex() {
        this.mCurrentPara = (float) this.mScroller.getCurrX();
        this.mCurrentIndex = this.mCurrentPara / this.mSpace;
        return this.mCurrentIndex;
    }

    public void scrollToTargetIndex(float index, int duration) {
        this.mTargetIndex = index;
        this.mTargetPara = this.mTargetIndex * this.mSpace;
        int dx = (int) (this.mTargetPara - this.mCurrentPara);
        this.mScroller.forceFinished(true);
        this.mScroller.startScroll((int) this.mCurrentPara, 0, dx, 0, duration);
    }

    public boolean computeOffset() {
        return this.mScroller.computeScrollOffset();
    }

    public float getTargetIndex() {
        return this.mTargetIndex;
    }

    public float getCurrVelocity() {
        return this.mScroller.getCurrVelocity();
    }

    public void dragBy(int dd) {
        this.mCurrentPara = (float) (this.mScroller.getCurrX() + dd);
        this.mScroller.setFinalX((int) this.mCurrentPara);
        this.mScroller.abortAnimation();
    }

    public void flingTo(int velocity, int minX, int maxX, int minY, int maxY) {
        this.mScroller.setFinalX((int) this.mCurrentPara);
        this.mScroller.abortAnimation();
        this.mScroller.fling(this.mScroller.getCurrX(), 0, velocity, 0, minX, maxX, minY, maxY);
    }
}
