package zidoo.tarot.kernel.anim;

import zidoo.tarot.kernel.Vector3;

public class GlScaleAnimation extends GlAnimation {
    float mEndScaleX;
    float mEndScaleY;
    float mEndScaleZ;
    float mStartScaleX;
    float mStartScaleY;
    float mStartScaleZ;

    public GlScaleAnimation(Vector3 startScale, Vector3 targetScale) {
        this.mStartScaleX = startScale.X;
        this.mStartScaleY = startScale.Y;
        this.mStartScaleZ = startScale.Z;
        this.mEndScaleX = targetScale.X;
        this.mEndScaleY = targetScale.Y;
        this.mEndScaleZ = targetScale.Z;
    }

    protected void setupStartValues() {
        this.mTarget.scale(this.mStartScaleX, this.mStartScaleY, this.mStartScaleZ);
    }

    protected void setupEndValues() {
        this.mTarget.scale(this.mEndScaleX, this.mEndScaleY, this.mEndScaleZ);
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
                this.mTarget.scale(this.mStartScaleX + ((this.mEndScaleX - this.mStartScaleX) * p), this.mStartScaleY + ((this.mEndScaleY - this.mStartScaleY) * p), this.mStartScaleZ + ((this.mEndScaleZ - this.mStartScaleZ) * p));
                return;
            }
            end();
        }
    }
}
