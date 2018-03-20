package zidoo.tarot.widget;

import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

@Deprecated
public class ProgressBar extends GameObject {
    protected int mMax = 100;
    protected int mProgress = 0;
    protected int mSecondProgress = 0;

    public ProgressBar(GLContext glContext) {
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

    public void setSecondProgress(int secondProgress) {
        this.mSecondProgress = secondProgress;
        invalidate();
    }

    public int getSecondProgress() {
        return this.mSecondProgress;
    }

    public void setBackgroundResource(int resid) {
    }

    public void setSecondProgressResource(int resid) {
    }

    public void setProgressResource(int resid) {
    }

    public void update(GL10 gl) {
        if (isShow()) {
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            gl.glEnable(2960);
            gl.glStencilFunc(515, 1, 1);
            gl.glStencilOp(7680, 7680, 7683);
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            float fictitiousWidth = (getWidth() * getDisplay().sWidthRatio) / this.Scale.X;
            gl.glPushMatrix();
            gl.glTranslatef(((((float) this.mProgress) * fictitiousWidth) / ((float) this.mMax)) - fictitiousWidth, 0.0f, 0.0f);
            onMaterial(this.mMaterials[2], gl);
            onMesh(this.mMeshes[2], gl);
            gl.glPopMatrix();
            gl.glDisable(2960);
            gl.glPopMatrix();
        }
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[3];
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
        r1 = new short[6];
        this.mMeshes[1].setIndices(r1);
        this.mMeshes[2] = new Mesh();
        this.mMeshes[2].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[2].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[2].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[2];
        r1 = new short[6];
        r1[1] = (short) 1;
        r1[2] = (short) 2;
        r1[3] = (short) 2;
        r1[4] = (short) 3;
        mesh.setIndices(r1);
    }

    protected void initMaterial() {
        Material backgroundMaterial = new Material();
        Material secondProgressMaterial = new Material();
        Material progressMaterial = new Material();
        this.mMaterials = new Material[3];
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
        secondProgressMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        secondProgressMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        secondProgressMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        secondProgressMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        secondProgressMaterial.setAlpha(0.99999f);
        secondProgressMaterial.setOpticalDensity(1.5f);
        secondProgressMaterial.setShininess(30.0f);
        secondProgressMaterial.setTransparent(ZidooAnimationHolder.F);
        secondProgressMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        secondProgressMaterial.setIllumination(2);
        this.mMaterials[1] = secondProgressMaterial;
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
        this.mMaterials[2] = progressMaterial;
    }
}
