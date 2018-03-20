package zidoo.tarot.kernel.anim;

public class GlAlphaAnimation extends GlAnimation {
    float mEndAlpha;
    float mStartAlpha;

    public GlAlphaAnimation(float startAlpha, float targetAlpha) {
        this.mStartAlpha = startAlpha;
        this.mEndAlpha = targetAlpha;
    }

    protected void setupStartValues() {
        this.mTarget.setAlpha(this.mStartAlpha);
    }

    protected void setupEndValues() {
        this.mTarget.setAlpha(this.mEndAlpha);
    }

    public void computeAnimation() {
        long ct = System.currentTimeMillis();
        if (ct > this.mStartTime) {
            if (!this.mIsRunning) {
                if (this.mAnimatiorListener != null) {
                    this.mAnimatiorListener.onAnimationStart(this);
                }
                this.mIsRunning = true;
            }
            if (ct < this.mStartTime + this.mDuration) {
                this.mTarget.setAlpha(this.mStartAlpha + ((this.mEndAlpha - this.mStartAlpha) * this.mInterpolator.getInterpolation(((float) (ct - this.mStartTime)) / ((float) this.mDuration))));
                return;
            }
            end();
        }
    }
}
