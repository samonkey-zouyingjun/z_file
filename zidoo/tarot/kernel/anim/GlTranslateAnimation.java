package zidoo.tarot.kernel.anim;

import zidoo.tarot.kernel.Vector3;

public class GlTranslateAnimation extends GlAnimation {
    float mEndX;
    float mEndY;
    float mEndZ;
    float mStartX;
    float mStartY;
    float mStartZ;

    public GlTranslateAnimation(Vector3 startTranslate, Vector3 targetTranslate) {
        this.mStartX = startTranslate.X;
        this.mStartY = startTranslate.Y;
        this.mStartZ = startTranslate.Z;
        this.mEndX = targetTranslate.X;
        this.mEndY = targetTranslate.Y;
        this.mEndZ = targetTranslate.Z;
    }

    protected void setupStartValues() {
        this.mTarget.translate(this.mStartX, this.mStartY, this.mStartZ);
    }

    protected void setupEndValues() {
        this.mTarget.translate(this.mEndX, this.mEndY, this.mEndZ);
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
                float p = this.mInterpolator.getInterpolation(((float) (ct - this.mStartTime)) / ((float) this.mDuration));
                this.mTarget.translate(this.mStartX + ((this.mEndX - this.mStartX) * p), this.mStartY + ((this.mEndY - this.mStartY) * p), this.mStartZ + ((this.mEndZ - this.mStartZ) * p));
                return;
            }
            end();
        }
    }
}
