package zidoo.tarot.kernel.anim;

import zidoo.tarot.kernel.Quat;
import zidoo.tarot.kernel.Rotate;
import zidoo.tarot.kernel.Vector3;

public class GlRotateAnimation extends GlAnimation {
    Vector3 mAxis;
    Rotate mEndRotate;
    float mFromRadian;
    Rotate mStartRotate;
    float mToRadian;

    public GlRotateAnimation(float fromRadian, float toRadian, Vector3 axis) {
        this.mFromRadian = fromRadian;
        this.mToRadian = toRadian;
        this.mAxis = axis;
        float halfStartRadian = fromRadian / 2.0f;
        this.mStartRotate = new Rotate(new Quat((float) Math.cos((double) halfStartRadian), (float) (Math.sin((double) halfStartRadian) * ((double) axis.X)), (float) (Math.sin((double) halfStartRadian) * ((double) axis.Y)), (float) (Math.sin((double) halfStartRadian) * ((double) axis.Z))));
        float halfEndRadian = toRadian / 2.0f;
        this.mEndRotate = new Rotate(new Quat((float) Math.cos((double) halfEndRadian), (float) (Math.sin((double) halfEndRadian) * ((double) axis.X)), (float) (Math.sin((double) halfEndRadian) * ((double) axis.Y)), (float) (Math.sin((double) halfEndRadian) * ((double) axis.Z))));
    }

    protected void setupStartValues() {
        this.mTarget.Rotate = this.mStartRotate;
    }

    protected void setupEndValues() {
        this.mTarget.Rotate = this.mEndRotate;
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
                float halfCurrentRadian = (this.mFromRadian + ((this.mToRadian - this.mFromRadian) * this.mInterpolator.getInterpolation(((float) (ct - this.mStartTime)) / ((float) this.mDuration)))) / 2.0f;
                this.mTarget.Rotate = new Rotate(new Quat((float) Math.cos((double) halfCurrentRadian), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.X)), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.Y)), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.Z))));
                return;
            }
            end();
        }
    }
}
