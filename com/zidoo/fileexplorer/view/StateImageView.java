package com.zidoo.fileexplorer.view;

import com.zidoo.custom.animation.ZidooAnimationHolder;
import com.zidoo.fileexplorer.tool.ViewSelectable;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class StateImageView extends GameObject implements ViewSelectable {
    public static final String TAG = "TImageView";
    int[] mIds;
    boolean mSelected = false;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public StateImageView(GLContext glContext) {
        super(glContext);
    }

    public void setImageResource(int nId, int sId) {
        this.mIds = new int[]{nId, sId};
        this.mNeedGenTexture = true;
    }

    public void setSelected(boolean selected) {
        if (this.mSelected != selected) {
            this.mSelected = selected;
            invalidate();
        }
    }

    public boolean isSelected() {
        return this.mSelected;
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
            if (this.mSelected) {
                onMaterial(this.mMaterials[1], gl);
                onMesh(this.mMeshes[1], gl);
            } else {
                onMaterial(this.mMaterials[0], gl);
                onMesh(this.mMeshes[0], gl);
            }
            gl.glPopMatrix();
        }
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[2];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.mMeshes[0].setIndices(new short[]{(short) 0, (short) 1, (short) 2, (short) 2, (short) 3, (short) 0});
        this.mMeshes[1] = new Mesh();
        this.mMeshes[1].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[1].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[1].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.mMeshes[1].setIndices(new short[]{(short) 0, (short) 1, (short) 2, (short) 2, (short) 3, (short) 0});
    }

    protected void initMaterial() {
        Material nMaterial = new Material();
        nMaterial.MaterialName = "BoardMaterial";
        nMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        nMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        nMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        nMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        nMaterial.setAlpha(0.99999f);
        nMaterial.setOpticalDensity(1.5f);
        nMaterial.setShininess(30.0f);
        nMaterial.setTransparent(ZidooAnimationHolder.F);
        nMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        nMaterial.setIllumination(2);
        Material sMaterial = new Material();
        sMaterial.MaterialName = "BoardMaterial";
        sMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        sMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        sMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        sMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        sMaterial.setAlpha(0.99999f);
        sMaterial.setOpticalDensity(1.5f);
        sMaterial.setShininess(30.0f);
        sMaterial.setTransparent(ZidooAnimationHolder.F);
        sMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        sMaterial.setIllumination(2);
        this.mMaterials = new Material[2];
        this.mMaterials[0] = nMaterial;
        this.mMaterials[1] = sMaterial;
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mIds[0]);
        this.mMaterials[1].texture = getContext().getTexture(this.mIds[1]);
        this.mNeedGenTexture = false;
    }
}
