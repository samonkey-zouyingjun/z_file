package zidoo.tarot.widget;

import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class TImageView extends GameObject {
    public static final String TAG = "TImageView";
    int mResid;

    public TImageView(GLContext glContext) {
        super(glContext);
        this.mResid = 0;
    }

    public TImageView(GLContext glContext, int resid, int width, int height) {
        this(glContext);
        setWidth((float) width);
        setHeight((float) height);
        setImageResource(resid);
    }

    public TImageView(GLContext glContext, int resid) {
        this(glContext);
        setImageResource(resid);
        setWidth(this.mMaterials[0].texture.getWidth());
        setHeight(this.mMaterials[0].texture.getHeight());
    }

    public void setImageResource(int resid) {
        if (this.mResid != resid) {
            this.mResid = resid;
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
            gl.glPopMatrix();
        }
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[1];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[0];
        short[] sArr = new short[6];
        sArr[1] = (short) 1;
        sArr[2] = (short) 2;
        sArr[3] = (short) 2;
        sArr[4] = (short) 3;
        mesh.setIndices(sArr);
    }

    protected void initMaterial() {
        Material imageMaterial = new Material();
        imageMaterial.MaterialName = "BoardMaterial";
        imageMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        imageMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        imageMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        imageMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        imageMaterial.setAlpha(0.99999f);
        imageMaterial.setOpticalDensity(1.5f);
        imageMaterial.setShininess(30.0f);
        imageMaterial.setTransparent(ZidooAnimationHolder.F);
        imageMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        imageMaterial.setIllumination(2);
        this.mMaterials = new Material[1];
        this.mMaterials[0] = imageMaterial;
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mResid);
        this.mNeedGenTexture = false;
    }
}
