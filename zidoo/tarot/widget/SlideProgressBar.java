package zidoo.tarot.widget;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;

@Deprecated
public class SlideProgressBar extends ProgressBar {
    private int mCurrentProgress = 0;
    private int mCurrentSecondProgress = 0;

    public SlideProgressBar(GLContext glContext) {
        super(glContext);
    }

    protected void onAnimation() {
        super.onAnimation();
        offset(this.mCurrentProgress, this.mProgress, this.mMax);
        offset(this.mCurrentSecondProgress, this.mSecondProgress, this.mMax);
        if (this.mCurrentProgress != this.mProgress || this.mCurrentSecondProgress != this.mSecondProgress) {
            invalidate();
        }
    }

    private void offset(int c, int t, int max) {
        if (t > c) {
            if (c + ((int) ((((double) (t - c)) * 0.7d) + (((double) max) * 0.01d))) > t) {
                c = t;
            }
        } else if (c - ((int) ((((double) (c - t)) * 0.7d) + (((double) max) * 0.01d))) < t) {
            c = t;
        }
    }

    public void update(GL10 gl) {
        if (isShow()) {
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            gl.glEnable(2960);
            gl.glStencilFunc(519, 1, 3);
            gl.glStencilOp(7681, 7681, 7681);
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            float fictitiousWidth = (getWidth() * getDisplay().sWidthRatio) / this.Scale.X;
            gl.glPushMatrix();
            gl.glStencilFunc(514, 1, 3);
            gl.glStencilOp(7680, 7680, 7682);
            gl.glTranslatef(((((float) this.mCurrentSecondProgress) * fictitiousWidth) / ((float) this.mMax)) - fictitiousWidth, 0.0f, 0.0f);
            onMaterial(this.mMaterials[1], gl);
            onMesh(this.mMeshes[1], gl);
            gl.glPopMatrix();
            gl.glPushMatrix();
            gl.glStencilFunc(514, 2, 3);
            gl.glStencilOp(7680, 7680, 7680);
            gl.glTranslatef(((((float) this.mCurrentProgress) * fictitiousWidth) / ((float) this.mMax)) - fictitiousWidth, 0.0f, 0.0f);
            onMaterial(this.mMaterials[2], gl);
            onMesh(this.mMeshes[2], gl);
            gl.glPopMatrix();
            gl.glDisable(2960);
            gl.glPopMatrix();
        }
    }
}
