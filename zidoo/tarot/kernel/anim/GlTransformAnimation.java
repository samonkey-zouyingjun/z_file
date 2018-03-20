package zidoo.tarot.kernel.anim;

import zidoo.tarot.kernel.Quat;
import zidoo.tarot.kernel.Rotate;
import zidoo.tarot.kernel.Vector3;

public class GlTransformAnimation extends GlAnimation {
    Vector3 mAxis;
    Rotate mEndRotate;
    float mFromAlpha;
    Vector3 mFromPosition;
    float mFromRadian;
    Vector3 mFromScale;
    Rotate mStartRotate;
    float mToAlpha;
    Vector3 mToPosition;
    float mToRadian;
    Vector3 mToScale;

    public GlTransformAnimation(Vector3 fromPosition, Vector3 fromScale, float fromAlpha, Vector3 toPosition, Vector3 toScale, float toAlpha, float fromRadian, float toRadian, Vector3 axis) {
        this.mFromPosition = fromPosition;
        this.mFromScale = fromScale;
        this.mToPosition = toPosition;
        this.mToScale = toScale;
        this.mFromAlpha = fromAlpha;
        this.mToAlpha = toAlpha;
        this.mFromRadian = fromRadian;
        this.mToRadian = toRadian;
        this.mAxis = axis;
        float halfStartRadian = fromRadian / 2.0f;
        this.mStartRotate = new Rotate(new Quat((float) Math.cos((double) halfStartRadian), (float) (Math.sin((double) halfStartRadian) * ((double) axis.X)), (float) (Math.sin((double) halfStartRadian) * ((double) axis.Y)), (float) (Math.sin((double) halfStartRadian) * ((double) axis.Z))));
        float halfEndRadian = toRadian / 2.0f;
        this.mEndRotate = new Rotate(new Quat((float) Math.cos((double) halfEndRadian), (float) (Math.sin((double) halfEndRadian) * ((double) axis.X)), (float) (Math.sin((double) halfEndRadian) * ((double) axis.Y)), (float) (Math.sin((double) halfEndRadian) * ((double) axis.Z))));
    }

    protected void setupStartValues() {
        this.mTarget.translate(this.mFromPosition.X, this.mFromPosition.Y, this.mFromPosition.Z);
        this.mTarget.scale(this.mFromScale.X, this.mFromScale.Y, this.mFromScale.Z);
        this.mTarget.Rotate = this.mStartRotate;
        this.mTarget.setAlpha(this.mFromAlpha);
    }

    protected void setupEndValues() {
        this.mTarget.translate(this.mToPosition.X, this.mToPosition.Y, this.mToPosition.Z);
        this.mTarget.scale(this.mToScale.X, this.mToScale.Y, this.mToScale.Z);
        this.mTarget.Rotate = this.mEndRotate;
        this.mTarget.setAlpha(this.mToAlpha);
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
                this.mTarget.translate(this.mFromPosition.X + ((this.mToPosition.X - this.mFromPosition.X) * p), this.mFromPosition.Y + ((this.mToPosition.Y - this.mFromPosition.Y) * p), this.mFromPosition.Z + ((this.mToPosition.Z - this.mFromPosition.Z) * p));
                this.mTarget.scale(this.mFromScale.X + ((this.mToScale.X - this.mFromScale.X) * p), this.mFromScale.Y + ((this.mToScale.Y - this.mFromScale.Y) * p), this.mFromScale.Z + ((this.mToScale.Z - this.mFromScale.Z) * p));
                float halfCurrentRadian = (this.mFromRadian + ((this.mToRadian - this.mFromRadian) * p)) / 2.0f;
                this.mTarget.Rotate = new Rotate(new Quat((float) Math.cos((double) halfCurrentRadian), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.X)), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.Y)), (float) (Math.sin((double) halfCurrentRadian) * ((double) this.mAxis.Z))));
                this.mTarget.setAlpha(this.mFromAlpha + ((this.mToAlpha - this.mFromAlpha) * p));
                return;
            }
            end();
        }
    }
}
