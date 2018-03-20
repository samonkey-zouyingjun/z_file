package com.zidoo.custom.animation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.zidoo.custom.init.ZidooJarPermissions;

public class ZidooTimeAnimation {
    private static final int START = 0;
    private long mAnimationFlag = -10000;
    private Handler mHandler = null;
    private ZidooScroller mHolderAlpha = null;
    private ZidooScroller mHolderPositonX = null;
    private ZidooScroller mHolderPositonY = null;
    private ZidooScroller mHolderScaleX = null;
    private ZidooScroller mHolderScaleY = null;
    private View mTargetView = null;
    private int mTimeDuration = 350;
    private ZidooTimeAnimationListener mZidooAnimationListener = null;

    public interface ZidooTimeAnimationListener {
        void animationOver();
    }

    public ZidooTimeAnimation(Context context) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mHolderScaleX = new ZidooScroller(context, 1.0f, 1000.0f);
        this.mHolderScaleY = new ZidooScroller(context, 1.0f, 1000.0f);
        this.mHolderPositonX = new ZidooScroller(context, 0.0f, 10.0f);
        this.mHolderPositonY = new ZidooScroller(context, 0.0f, 10.0f);
        this.mHolderAlpha = new ZidooScroller(context, 0.0f, 1000.0f);
        initHandler();
    }

    private void initHandler() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ZidooTimeAnimation.this.startAnimation();
                        return;
                    default:
                        return;
                }
            }
        };
    }

    private void reset() {
        this.mHolderScaleX.isAnimation = false;
        this.mHolderScaleY.isAnimation = false;
        this.mHolderPositonX.isAnimation = false;
        this.mHolderPositonY.isAnimation = false;
        this.mHolderAlpha.isAnimation = false;
    }

    public ZidooTimeAnimation setAnimationView(View targetView, int mTimeDuration) {
        stop();
        reset();
        this.mTargetView = targetView;
        this.mTimeDuration = mTimeDuration;
        return this;
    }

    public void startAnimation(View targetView, ZidooScale scale, ZidooPosition position, ZidooAlpha alpha, int mTimeDuration, ZidooTimeAnimationListener zidooAnimationListener) {
        setAnimationView(targetView, mTimeDuration);
        setScale(scale);
        setPosition(position);
        setAlpha(alpha);
        setAnimationListener(zidooAnimationListener);
        start();
    }

    public ZidooTimeAnimation setScale(ZidooScale scale) {
        if (this.mTargetView == null) {
            throw new RuntimeException("Animation view ----------------> null");
        }
        this.mHolderScaleX.isAnimation = true;
        this.mHolderScaleY.isAnimation = true;
        this.mTargetView.setScaleX(scale.mFromScaleX);
        this.mTargetView.setScaleY(scale.mFromScaleY);
        this.mHolderScaleX.setCurrentIndex(scale.mFromScaleX);
        this.mHolderScaleY.setCurrentIndex(scale.mFromScaleY);
        this.mHolderScaleX.scrollToTargetIndex(scale.mToScaleX, this.mTimeDuration);
        this.mHolderScaleY.scrollToTargetIndex(scale.mToScaleY, this.mTimeDuration);
        return this;
    }

    public ZidooTimeAnimation setPosition(ZidooPosition position) {
        if (this.mTargetView == null) {
            throw new RuntimeException("Animation view ----------------> null");
        }
        this.mHolderPositonX.isAnimation = true;
        this.mHolderPositonY.isAnimation = true;
        this.mTargetView.setTranslationX(position.mFromX);
        this.mTargetView.setTranslationY(position.mFromY);
        this.mHolderPositonX.setCurrentIndex(position.mFromX);
        this.mHolderPositonY.setCurrentIndex(position.mFromY);
        this.mHolderPositonX.scrollToTargetIndex(position.mToX, this.mTimeDuration);
        this.mHolderPositonY.scrollToTargetIndex(position.mToY, this.mTimeDuration);
        return this;
    }

    public ZidooTimeAnimation setAlpha(ZidooAlpha alpha) {
        if (this.mTargetView == null) {
            throw new RuntimeException("Animation view ----------------> null");
        }
        this.mHolderAlpha.isAnimation = true;
        this.mTargetView.setAlpha(alpha.mFromAlpha);
        this.mHolderAlpha.setCurrentIndex(alpha.mFromAlpha);
        this.mHolderAlpha.scrollToTargetIndex(alpha.mToAlpha, this.mTimeDuration);
        return this;
    }

    public ZidooTimeAnimation setZidooAnimaitonInfo(ZidooAnimaitonInfo zidooAnimaitonInfo) {
        if (this.mTargetView == null) {
            throw new RuntimeException("Animation view ----------------> null");
        }
        if (zidooAnimaitonInfo.mScale != null) {
            setScale(zidooAnimaitonInfo.mScale);
        }
        if (zidooAnimaitonInfo.mPosition != null) {
            setPosition(zidooAnimaitonInfo.mPosition);
        }
        if (zidooAnimaitonInfo.mAlpha != null) {
            setAlpha(zidooAnimaitonInfo.mAlpha);
        }
        if (zidooAnimaitonInfo.mTimeDuration != -1) {
            this.mTimeDuration = zidooAnimaitonInfo.mTimeDuration;
        }
        return this;
    }

    public ZidooTimeAnimation setAnimationListener(ZidooTimeAnimationListener zidooAnimationListener) {
        this.mZidooAnimationListener = zidooAnimationListener;
        return this;
    }

    public void stop() {
        this.mAnimationFlag++;
        this.mHandler.removeMessages(0);
    }

    public void start() {
        this.mAnimationFlag++;
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }

    private void startAnimation() {
        long flag = this.mAnimationFlag;
        boolean b1 = this.mHolderScaleX.computeOffset();
        boolean b2 = this.mHolderScaleY.computeOffset();
        boolean b3 = this.mHolderPositonX.computeOffset();
        boolean b4 = this.mHolderPositonY.computeOffset();
        boolean b5 = this.mHolderAlpha.computeOffset();
        if (this.mHolderScaleX.isAnimation) {
            this.mTargetView.setScaleX(this.mHolderScaleX.getCurrentIndex());
        } else {
            b1 = false;
        }
        if (this.mHolderScaleY.isAnimation) {
            this.mTargetView.setScaleY(this.mHolderScaleY.getCurrentIndex());
        } else {
            b2 = false;
        }
        if (this.mHolderPositonX.isAnimation) {
            this.mTargetView.setTranslationX(this.mHolderPositonX.getCurrentIndex());
        } else {
            b3 = false;
        }
        if (this.mHolderPositonY.isAnimation) {
            this.mTargetView.setTranslationY(this.mHolderPositonY.getCurrentIndex());
        } else {
            b4 = false;
        }
        if (this.mHolderAlpha.isAnimation) {
            float alpha = this.mHolderAlpha.getCurrentIndex();
            if (alpha < 0.0f) {
                alpha = 0.0f;
            }
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
            this.mTargetView.setAlpha(alpha);
        } else {
            b5 = false;
        }
        if (flag == this.mAnimationFlag) {
            if (b1 || b2 || b2 || b3 || b4 || b5) {
                this.mHandler.removeMessages(0);
                this.mHandler.sendEmptyMessage(0);
            } else if (this.mZidooAnimationListener != null) {
                this.mZidooAnimationListener.animationOver();
            }
        }
    }
}
