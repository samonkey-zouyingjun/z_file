package zidoo.tarot.gameobject;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;
import zidoo.tarot.kernel.Scale;
import zidoo.tarot.kernel.Texture;

public class SeekBar extends GameObject {
    private Material mActiveThumbMaterial = null;
    private Material mActivedBackgroundMaterial = null;
    private float mAlphaError = 0.1f;
    private float mAlphaFactor = 0.05f;
    private float mAlphaHide = 0.0f;
    private float mAlphaShow = 0.9999f;
    private float mBackgroundAspectRatio = 1.0f;
    private Material mBackgroundMaterial = null;
    private float mCurrentAlpha = 0.0f;
    private boolean mEnableHintMoving = false;
    private Material mForegroundMaterial = null;
    private Material mHighLightMaterial = null;
    private boolean mIsActived = false;
    private int mMaxProgress = 100;
    private float mMovingError = 0.1f;
    private float mMovingFactor = 0.05f;
    private OnSeekBarChangeListener mOnSeekBarChangeListener = null;
    private int mProgress = 0;
    private long mProgressHintIdleDuration = 750;
    private long mProgressHintLoopStartTime = 0;
    private long mProgressHintMovingDuration = 1500;
    private float mProgressHintXOffset = 0.0f;
    private float mProgressXDistance = 0.0f;
    private float mProgressXOffset = 0.0f;
    private Material mThumbMaterial = null;
    private Scale mThumbScale = new Scale();
    private boolean mTokenAspectRatio = false;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int i, boolean z);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }

    public SeekBar(GLContext glContext) {
        super(glContext);
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[6];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{5.0f, 5.0f, -0.001f, -5.0f, 5.0f, -0.001f, -5.0f, -5.0f, -0.001f, 5.0f, -5.0f, -0.001f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[0];
        short[] sArr = new short[6];
        sArr[1] = (short) 1;
        sArr[2] = (short) 2;
        sArr[3] = (short) 2;
        sArr[4] = (short) 3;
        mesh.setIndices(sArr);
        this.mMeshes[0].setEnabled(false);
        this.mMeshes[1] = new Mesh();
        this.mMeshes[1].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[1].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[1].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        sArr = new short[6];
        this.mMeshes[1].setIndices(sArr);
        this.mMeshes[2] = new Mesh();
        this.mMeshes[2].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[2].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[2].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        sArr = new short[6];
        this.mMeshes[2].setIndices(sArr);
        this.mMeshes[3] = new Mesh();
        this.mMeshes[3].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[3].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[3].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        sArr = new short[6];
        this.mMeshes[3].setIndices(sArr);
        this.mMeshes[4] = new Mesh();
        this.mMeshes[4].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[4].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[4].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        sArr = new short[6];
        this.mMeshes[4].setIndices(sArr);
        this.mMeshes[5] = new Mesh();
        this.mMeshes[5].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[5].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[5].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        mesh = this.mMeshes[5];
        sArr = new short[6];
        sArr[1] = (short) 1;
        sArr[2] = (short) 2;
        sArr[3] = (short) 2;
        sArr[4] = (short) 3;
        mesh.setIndices(sArr);
        this.mProgressXDistance = 10.0f;
        this.mProgressXOffset = -10.0f;
        this.mProgressHintXOffset = -10.0f;
    }

    protected void initMaterial() {
        Material mActivedBackgroundMaterial = new Material();
        Material mBackgroundMaterial = new Material();
        Material mForegroundMaterial = new Material();
        Material mHighLightMaterial = new Material();
        Material mActiveThumbMaterial = new Material();
        Material mThumbMaterial = new Material();
        this.mMaterials = new Material[6];
        mActivedBackgroundMaterial.MaterialName = "ActivedBackgroundMaterial";
        mActivedBackgroundMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mActivedBackgroundMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mActivedBackgroundMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mActivedBackgroundMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mActivedBackgroundMaterial.setAlpha(0.99999f);
        mActivedBackgroundMaterial.setOpticalDensity(1.5f);
        mActivedBackgroundMaterial.setShininess(30.0f);
        mActivedBackgroundMaterial.setTransparent(ZidooAnimationHolder.F);
        mActivedBackgroundMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mActivedBackgroundMaterial.setIllumination(2);
        this.mMaterials[0] = mActivedBackgroundMaterial;
        mBackgroundMaterial.MaterialName = "BackgroundMaterial";
        mBackgroundMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mBackgroundMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mBackgroundMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mBackgroundMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mBackgroundMaterial.setAlpha(0.99999f);
        mBackgroundMaterial.setOpticalDensity(1.5f);
        mBackgroundMaterial.setShininess(30.0f);
        mBackgroundMaterial.setTransparent(ZidooAnimationHolder.F);
        mBackgroundMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mBackgroundMaterial.setIllumination(2);
        this.mMaterials[1] = mBackgroundMaterial;
        mForegroundMaterial.MaterialName = "ForegroundMaterial";
        mForegroundMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mForegroundMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mForegroundMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mForegroundMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mForegroundMaterial.setAlpha(0.99999f);
        mForegroundMaterial.setOpticalDensity(1.5f);
        mForegroundMaterial.setShininess(30.0f);
        mForegroundMaterial.setTransparent(ZidooAnimationHolder.F);
        mForegroundMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mForegroundMaterial.setIllumination(2);
        this.mMaterials[2] = mForegroundMaterial;
        mHighLightMaterial.MaterialName = "HighlightMaterial";
        mHighLightMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mHighLightMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mHighLightMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mHighLightMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mHighLightMaterial.setAlpha(0.99999f);
        mHighLightMaterial.setOpticalDensity(1.5f);
        mHighLightMaterial.setShininess(30.0f);
        mHighLightMaterial.setTransparent(ZidooAnimationHolder.F);
        mHighLightMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mHighLightMaterial.setIllumination(2);
        this.mMaterials[3] = mHighLightMaterial;
        mActiveThumbMaterial.MaterialName = "ActivedThumbMaterial";
        mActiveThumbMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mActiveThumbMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mActiveThumbMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mActiveThumbMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mActiveThumbMaterial.setAlpha(0.99999f);
        mActiveThumbMaterial.setOpticalDensity(1.5f);
        mActiveThumbMaterial.setShininess(30.0f);
        mActiveThumbMaterial.setTransparent(ZidooAnimationHolder.F);
        mActiveThumbMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mActiveThumbMaterial.setIllumination(2);
        this.mMaterials[4] = mActiveThumbMaterial;
        mThumbMaterial.MaterialName = "ThumbMaterial";
        mThumbMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        mThumbMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        mThumbMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mThumbMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        mThumbMaterial.setAlpha(0.99999f);
        mThumbMaterial.setOpticalDensity(1.5f);
        mThumbMaterial.setShininess(30.0f);
        mThumbMaterial.setTransparent(ZidooAnimationHolder.F);
        mThumbMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        mThumbMaterial.setIllumination(2);
        this.mMaterials[5] = mThumbMaterial;
    }

    protected void onAnimation() {
        super.onAnimation();
        boolean hasMoreAnimation = false;
        long animationTime = System.currentTimeMillis() - this.mProgressHintLoopStartTime;
        float normalizeTime = ((float) animationTime) / ((float) this.mProgressHintMovingDuration);
        float normalizeProgress = ((float) this.mProgress) / ((float) this.mMaxProgress);
        if (normalizeProgress > 1.0f) {
            normalizeProgress = 1.0f;
        } else if (normalizeProgress < 0.0f) {
            normalizeProgress = 0.0f;
        }
        if (normalizeTime > 1.0f) {
            normalizeTime = 1.0f;
        } else if (normalizeTime < 0.0f) {
            normalizeTime = 0.0f;
        }
        float destinationOffset = (normalizeProgress - 1.0f) * this.mProgressXDistance;
        this.mProgressXOffset += (destinationOffset - this.mProgressXOffset) * this.mMovingFactor;
        this.mEnableHintMoving = true;
        if (this.mEnableHintMoving) {
            this.mProgressHintXOffset = (-this.mProgressXDistance) + ((2.0f * this.mProgressXDistance) * normalizeTime);
            if (animationTime > this.mProgressHintMovingDuration + this.mProgressHintIdleDuration) {
                this.mProgressHintLoopStartTime = System.currentTimeMillis();
                this.mEnableHintMoving = this.mIsActived;
            }
            hasMoreAnimation = this.mEnableHintMoving;
        }
        if (this.mIsActived) {
            this.mCurrentAlpha += (this.mAlphaShow - this.mCurrentAlpha) * this.mAlphaFactor;
        } else {
            this.mCurrentAlpha += (this.mAlphaHide - this.mCurrentAlpha) * this.mAlphaFactor;
        }
        this.mActivedBackgroundMaterial.setAlpha(this.mCurrentAlpha);
        this.mActiveThumbMaterial.setAlpha(this.mCurrentAlpha);
        this.mThumbMaterial.setAlpha(this.mAlphaShow - this.mCurrentAlpha);
        if (Math.abs(destinationOffset - this.mProgressXOffset) < this.mMovingError) {
            hasMoreAnimation |= 0;
        } else {
            hasMoreAnimation |= 1;
        }
        if (hasMoreAnimation) {
            invalidate();
        }
    }

    public void update(GL10 gl) {
        if (isShow()) {
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glEnable(2960);
            gl.glStencilFunc(519, 1, 3);
            gl.glStencilOp(7681, 7681, 7681);
            onMaterial(this.mMaterials[1], gl);
            onMesh(this.mMeshes[1], gl);
            gl.glPushMatrix();
            gl.glStencilFunc(514, 1, 3);
            gl.glStencilOp(7680, 7680, 7682);
            gl.glTranslatef(this.mProgressXOffset, 0.0f, 0.0f);
            onMaterial(this.mMaterials[2], gl);
            onMesh(this.mMeshes[2], gl);
            gl.glStencilFunc(514, 2, 3);
            gl.glStencilOp(7680, 7680, 7680);
            gl.glPushMatrix();
            gl.glTranslatef(this.mProgressHintXOffset, 0.0f, 0.0f);
            onMaterial(this.mMaterials[3], gl);
            onMesh(this.mMeshes[3], gl);
            gl.glPopMatrix();
            gl.glPopMatrix();
            gl.glDisable(2960);
            gl.glPushMatrix();
            float scaleX = this.mThumbMaterial.texture.getWidth() / this.mThumbMaterial.texture.getHeight();
            float scaleY = getFinalTransform().Scale.X / getFinalTransform().Scale.Y;
            gl.glTranslatef(this.mProgressXOffset + (this.mProgressXDistance / 2.0f), 0.0f, 0.0f);
            gl.glScalef(this.mThumbScale.Y * scaleX, this.mThumbScale.Y * scaleY, 1.0f);
            onMaterial(this.mMaterials[4], gl);
            onMesh(this.mMeshes[4], gl);
            onMaterial(this.mMaterials[5], gl);
            onMesh(this.mMeshes[5], gl);
            gl.glPopMatrix();
            gl.glPopMatrix();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setBackground(int resid) {
        setBackground(getContext().getTexture(resid));
    }

    public void setBackground(Bitmap bitmap) {
        setBackground(GLResources.genTextrue(bitmap));
    }

    public void setBackground(Texture texture) {
        if (this.mBackgroundMaterial != null) {
            this.mBackgroundMaterial.texture = texture;
            this.mBackgroundAspectRatio = this.mBackgroundMaterial.texture.getWidth() / this.mBackgroundMaterial.texture.getHeight();
        }
    }

    public void setActivedBackground(int resid) {
        setActivedBackground(getContext().getTexture(resid));
    }

    public void setActivedBackground(Bitmap bitmap) {
        setActivedBackground(GLResources.genTextrue(bitmap));
    }

    public void setActivedBackground(Texture texture) {
        if (this.mActivedBackgroundMaterial != null) {
            this.mActivedBackgroundMaterial.texture = texture;
        }
    }

    public void setProgressImage(int resid) {
        setProgressImage(getContext().getTexture(resid));
    }

    public void setProgressImage(Bitmap bitmap) {
        setProgressImage(GLResources.genTextrue(bitmap));
    }

    public void setProgressImage(Texture texture) {
        if (this.mForegroundMaterial != null) {
            this.mForegroundMaterial.texture = texture;
        }
    }

    public void setThumbImage(int resid) {
        setThumbImage(getContext().getTexture(resid));
    }

    public void setThumbImage(Bitmap bitmap) {
        setThumbImage(GLResources.genTextrue(bitmap));
    }

    public void setThumbImage(Texture texture) {
        if (this.mThumbMaterial != null) {
            this.mThumbMaterial.texture = texture;
        }
    }

    public void setActivedThumbImage(int resid) {
        setActivedThumbImage(getContext().getTexture(resid));
    }

    public void setActivedThumbImage(Bitmap bitmap) {
        setActivedThumbImage(GLResources.genTextrue(bitmap));
    }

    public void setActivedThumbImage(Texture texture) {
        if (this.mActiveThumbMaterial != null) {
            this.mActiveThumbMaterial.texture = texture;
        }
    }

    public void setHighLightImage(int resid) {
        setHighLightImage(getContext().getTexture(resid));
    }

    public void setHighLightImage(Bitmap bitmap) {
        setHighLightImage(GLResources.genTextrue(bitmap));
    }

    public void setHighLightImage(Texture texture) {
        if (this.mHighLightMaterial != null) {
            this.mHighLightMaterial.texture = texture;
        }
    }

    public void setThumbScale(float x, float y, float z) {
        this.mThumbScale.set(x, y, z);
    }

    public void setActived(boolean isActived) {
        this.mIsActived = isActived;
        this.mProgressHintLoopStartTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean getActived() {
        return this.mIsActived;
    }

    public void setMax(int max) {
        this.mMaxProgress = max;
    }

    public void setProgress(int progress) {
        if (progress >= 0 && progress <= this.mMaxProgress) {
            if (this.mOnSeekBarChangeListener != null) {
                this.mOnSeekBarChangeListener.onStartTrackingTouch(this);
                this.mOnSeekBarChangeListener.onProgressChanged(this, this.mProgress, false);
                this.mOnSeekBarChangeListener.onStopTrackingTouch(this);
            }
            this.mProgress = progress;
        }
    }

    public int getMax() {
        return this.mMaxProgress;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.mOnSeekBarChangeListener = listener;
    }

    public void takeAspectRatio(boolean isToken) {
        this.mTokenAspectRatio = isToken;
        if (this.mTokenAspectRatio) {
            if (this.Scale.X > this.Scale.Y) {
                this.Scale.Y = this.Scale.X / this.mBackgroundAspectRatio;
            } else {
                this.Scale.X = this.Scale.Y * this.mBackgroundAspectRatio;
            }
        }
        invalidate();
    }
}
