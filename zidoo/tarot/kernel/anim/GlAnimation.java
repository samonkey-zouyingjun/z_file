package zidoo.tarot.kernel.anim;

import android.animation.TimeInterpolator;
import zidoo.tarot.kernel.GameObject;

public abstract class GlAnimation {
    private static final TimeInterpolator DEFULT_INTERPOLATOR = new TimeInterpolator() {
        public float getInterpolation(float input) {
            return input;
        }
    };
    AnimatorListener mAnimatiorListener = null;
    long mDuration = 500;
    boolean mFillAfter = false;
    boolean mFinish = true;
    TimeInterpolator mInterpolator = DEFULT_INTERPOLATOR;
    boolean mIsRunning = false;
    int mRepeatTimes = 1;
    long mStartDelay = 0;
    long mStartTime = 0;
    GameObject mTarget = null;

    public interface AnimatorListener {
        void onAnimationCancel(GlAnimation glAnimation);

        void onAnimationEnd(GlAnimation glAnimation);

        void onAnimationRepeat(GlAnimation glAnimation);

        void onAnimationStart(GlAnimation glAnimation);
    }

    public abstract void computeAnimation();

    protected abstract void setupEndValues();

    protected abstract void setupStartValues();

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public void setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setInterpolator(TimeInterpolator value) {
        this.mInterpolator = value;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public boolean isFinish() {
        return this.mFinish;
    }

    public void setFillAfter(boolean fillAfter) {
        this.mFillAfter = fillAfter;
    }

    public void start() {
        this.mStartTime = System.currentTimeMillis() + this.mStartDelay;
        this.mIsRunning = false;
        this.mFinish = false;
        setupStartValues();
    }

    public void cancel() {
        this.mFinish = true;
        this.mIsRunning = false;
        setupStartValues();
        if (this.mAnimatiorListener != null) {
            this.mAnimatiorListener.onAnimationCancel(this);
        }
    }

    public void end() {
        this.mFinish = true;
        this.mIsRunning = false;
        if (this.mFillAfter) {
            setupEndValues();
        } else {
            setupStartValues();
        }
        if (this.mAnimatiorListener != null) {
            this.mAnimatiorListener.onAnimationEnd(this);
        }
    }

    public void setTarget(GameObject target) {
        this.mTarget = target;
    }

    public GameObject getTarget() {
        return this.mTarget;
    }

    public void setAnimatorListener(AnimatorListener animatorListener) {
        this.mAnimatiorListener = animatorListener;
    }

    public AnimatorListener getAnimatiorListener() {
        return this.mAnimatiorListener;
    }
}
