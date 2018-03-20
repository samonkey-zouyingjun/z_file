package zidoo.tarot.widget;

import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class SimplifyProgress extends GameObject {
    int mBgdResid;
    int mMax = 100;
    int mPgsResid;
    int mProgress = 0;

    public SimplifyProgress(GLContext glContext) {
        super(glContext);
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public int getMax() {
        return this.mMax;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setBackgroundResource(int resid) {
        if (this.mBgdResid != resid) {
            this.mBgdResid = resid;
            this.mNeedGenTexture = true;
        }
    }

    public void setProgressResource(int resid) {
        if (this.mPgsResid != resid) {
            this.mPgsResid = resid;
            this.mNeedGenTexture = true;
        }
    }

    public void update(GL10 gl) {
        if (isShow()) {
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            if (this.mNeedGenTexture) {
                onGenarateTexture();
            }
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glPushMatrix();
            float p = ((float) this.mProgress) / ((float) this.mMax);
            gl.glScalef(p, 1.0f, 1.0f);
            gl.glTranslatef((((getDisplay().sWidthRatio / getDisplay().sScaleX) / p) * (p - 1.0f)) * 0.5f, 0.0f, 0.0f);
            onMaterial(this.mMaterials[1], gl);
            onMesh(this.mMeshes[1], gl);
            gl.glPopMatrix();
            gl.glPopMatrix();
        }
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[2];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        r1 = new short[6];
        this.mMeshes[0].setIndices(r1);
        this.mMeshes[1] = new Mesh();
        this.mMeshes[1].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[1].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[1].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[1];
        r1 = new short[6];
        r1[1] = (short) 1;
        r1[2] = (short) 2;
        r1[3] = (short) 2;
        r1[4] = (short) 3;
        mesh.setIndices(r1);
    }

    protected void initMaterial() {
        Material backgroundMaterial = new Material();
        Material progressMaterial = new Material();
        this.mMaterials = new Material[2];
        backgroundMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        backgroundMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        backgroundMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        backgroundMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        backgroundMaterial.setAlpha(0.99999f);
        backgroundMaterial.setOpticalDensity(1.5f);
        backgroundMaterial.setShininess(30.0f);
        backgroundMaterial.setTransparent(ZidooAnimationHolder.F);
        backgroundMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        backgroundMaterial.setIllumination(2);
        this.mMaterials[0] = backgroundMaterial;
        progressMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        progressMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        progressMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        progressMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        progressMaterial.setAlpha(0.99999f);
        progressMaterial.setOpticalDensity(1.5f);
        progressMaterial.setShininess(30.0f);
        progressMaterial.setTransparent(ZidooAnimationHolder.F);
        progressMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        progressMaterial.setIllumination(2);
        this.mMaterials[1] = progressMaterial;
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mBgdResid);
        this.mMaterials[1].texture = getContext().getTexture(this.mPgsResid);
        this.mNeedGenTexture = false;
    }
}
