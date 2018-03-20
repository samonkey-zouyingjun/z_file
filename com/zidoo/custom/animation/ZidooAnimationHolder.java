package com.zidoo.custom.animation;

public class ZidooAnimationHolder {
    private static final int BEFORE_STATIC = -1;
    public static final int DRAG = 4;
    public static final int DRAG_STOP = 5;
    public static final int DRAG_STOP_ATTRACT = 6;
    public static final float F = 0.3f;
    public static final float F2 = 0.8f;
    public static final int HARMONIC_ = 3;
    public static final int HARMONIC_MOTION = 2;
    public static final int RUNNING = 1;
    private static final int STATIC = 0;
    private float mAllDragOffset;
    private float mBete = F;
    private int mDragCount;
    private float mDragF;
    private float mDragOffset;
    private float mDragSpeed;
    private float mF = F;
    private boolean mLimitMaxEnable;
    private boolean mLimitMinEnable;
    private float mLimiteDragSpeed;
    private float mLimiteMaxSpeed;
    private float mLimiteMinSpeed;
    private float mLimiteSpace;
    private float mPointValue;
    private float mSpace;
    private float mStartValue;
    public int mState = 0;
    private float mTargetIndex;
    private float mTargetValue;

    public class Parameter {
        private float mF;
        private boolean mLimitMax;
        private boolean mLimitMin;
        private int mMethod;

        public Parameter(int method, float f, boolean limitMin, boolean limitMax) {
            this.mMethod = method;
            this.mF = f;
            this.mLimitMin = limitMin;
            this.mLimitMax = limitMax;
        }
    }

    public ZidooAnimationHolder(float index, float space) {
        this.mSpace = space;
        this.mStartValue = this.mSpace * index;
        this.mTargetValue = 0.0f;
        this.mLimiteSpace = 1.0f;
        this.mState = 0;
    }

    public float getCurrentIndex() {
        return this.mStartValue / this.mSpace;
    }

    public float getCurrentPara() {
        return this.mStartValue;
    }

    public float getTargetIndex() {
        return this.mTargetValue / this.mSpace;
    }

    public float getTargetPara() {
        return this.mTargetValue;
    }

    public float getDestinationIndex() {
        return this.mTargetIndex;
    }

    public void setCurrentIndex(float index) {
        this.mStartValue = this.mSpace * index;
        this.mTargetValue = this.mStartValue;
        this.mF = F;
        this.mState = 1;
    }

    public void setDestinationIndex(float index) {
        this.mTargetIndex = index;
        this.mTargetValue = this.mSpace * index;
        this.mF = F;
        this.mLimiteMaxSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteMinSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteSpace = (Math.abs(this.mTargetValue - this.mStartValue) * this.mF) * 0.1f;
        this.mState = 1;
    }

    public void setDestinationIndex(float index, Parameter parameter) {
        this.mTargetIndex = index;
        this.mTargetValue = this.mSpace * index;
        this.mF = F;
        this.mLimiteMaxSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteMinSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteSpace = (Math.abs(this.mTargetValue - this.mStartValue) * this.mF) * 0.1f;
        this.mState = 1;
    }

    public void setDestinationIndex(float index, int method, float f, boolean limitMin, boolean limitMax) {
        this.mTargetIndex = index;
        this.mTargetValue = this.mSpace * index;
        this.mF = f;
        this.mLimiteMaxSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteMinSpeed = ((this.mTargetValue - this.mStartValue) * this.mF) * 0.2f;
        this.mLimiteSpace = (Math.abs(this.mTargetValue - this.mStartValue) * this.mF) * 0.01f;
        this.mPointValue = this.mTargetValue + ((this.mTargetValue - this.mStartValue) * this.mBete);
        this.mLimitMinEnable = limitMin;
        this.mLimitMaxEnable = limitMax;
        this.mState = method;
    }

    public void setCurrentIndex(float index, int method) {
        this.mStartValue = this.mSpace * index;
        this.mTargetValue = this.mStartValue;
        this.mState = method;
    }

    public void drag(float offset) {
        this.mDragOffset = offset;
        this.mDragCount++;
        this.mAllDragOffset += this.mDragOffset;
        this.mStartValue += this.mDragOffset;
        this.mState = 4;
    }

    public void dragReset() {
        this.mDragOffset = 0.0f;
        this.mDragCount = 0;
        this.mAllDragOffset = 0.0f;
        this.mState = 4;
    }

    public void run(Object sourceObject) {
        switch (this.mState) {
            case -1:
                this.mState = 0;
                return;
            case 0:
                this.mState = 0;
                return;
            case 1:
                float speed = (this.mTargetValue - this.mStartValue) * this.mF;
                if (Math.abs(speed) > Math.abs(this.mLimiteMaxSpeed)) {
                    if (this.mLimitMaxEnable) {
                        this.mStartValue += this.mLimiteMaxSpeed;
                    } else {
                        this.mStartValue += speed;
                    }
                } else if (Math.abs(speed) >= Math.abs(this.mLimiteMinSpeed) || Math.abs(this.mTargetValue - this.mStartValue) <= Math.abs(this.mLimiteMinSpeed)) {
                    this.mStartValue += speed;
                } else if (this.mLimitMinEnable) {
                    this.mStartValue += this.mLimiteMinSpeed;
                } else {
                    this.mStartValue += speed;
                }
                if (Math.abs(this.mStartValue - this.mTargetValue) <= this.mLimiteSpace) {
                    this.mStartValue = this.mTargetValue;
                    this.mState = -1;
                    return;
                }
                return;
            case 2:
                this.mStartValue += (this.mPointValue - this.mStartValue) * this.mF;
                if (Math.abs(this.mStartValue - this.mPointValue) <= this.mLimiteSpace * 5.0f) {
                    this.mStartValue = this.mPointValue;
                    this.mPointValue = this.mTargetValue + ((this.mTargetValue - this.mStartValue) * this.mBete);
                    this.mF = this.mBete;
                }
                if (Math.abs(this.mPointValue - this.mTargetValue) <= this.mLimiteSpace) {
                    this.mStartValue = this.mTargetValue;
                    this.mState = -1;
                    return;
                }
                return;
            case 5:
                this.mStartValue += this.mDragSpeed;
                this.mDragSpeed = this.mDragF * this.mDragSpeed;
                if (Math.abs(this.mDragSpeed) < Math.abs(this.mLimiteDragSpeed)) {
                    this.mState = 6;
                    return;
                }
                return;
            default:
                return;
        }
    }

    public boolean isStatic() {
        return this.mState == 0;
    }

    public boolean isRunning() {
        return this.mState != 0;
    }

    public int getState() {
        return this.mState;
    }

    public float getDragSpeed() {
        return this.mDragSpeed;
    }
}
