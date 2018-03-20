package zidoo.tarot.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class ScrollBar extends GameObject {
    private final float ANIM_SPEED = 0.02f;
    private final int SHOW_DURATION = 2000;
    private final int STATE_GONE = 16;
    private final int STATE_GONING = 272;
    private final int STATE_SHOW = 1;
    private final int STATE_SHOWING = 257;
    NinePatchDrawable drawable;
    long mLastShowTime = -1;
    int mMinHeight = 10;
    int mResid = 0;
    int state = 16;

    public ScrollBar(GLContext glContext) {
        super(glContext);
        setVisibility(false);
        setAlpha(0.0f);
    }

    public void setHeight(float h) {
        if (h < ((float) this.mMinHeight)) {
            h = (float) this.mMinHeight;
        }
        super.setHeight(h);
    }

    public void setMinHeight(int minHeight) {
        this.mMinHeight = minHeight;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setNinePathResource(int resid) {
        if (this.mResid != resid) {
            this.mResid = resid;
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resid);
            this.drawable = new NinePatchDrawable(getContext().getResources(), new NinePatch(bitmap, bitmap.getNinePatchChunk(), null));
            this.mNeedGenTexture = true;
        }
    }

    public void show() {
        switch (this.state) {
            case 1:
                this.mLastShowTime = System.currentTimeMillis();
                return;
            case 16:
                setVisibility(true);
                break;
            case 272:
                break;
            default:
                return;
        }
        this.state = 257;
    }

    public void reset() {
        this.mNeedGenTexture = true;
    }

    protected void onAnimation() {
        super.onAnimation();
        switch (this.state) {
            case 1:
                if (System.currentTimeMillis() - this.mLastShowTime > 2000) {
                    this.state = 272;
                }
                invalidate();
                return;
            case 257:
                float as = getAlpha() + 0.02f;
                if (as >= 1.0f) {
                    as = 1.0f;
                    this.state = 1;
                    this.mLastShowTime = System.currentTimeMillis();
                }
                setAlpha(as);
                invalidate();
                return;
            case 272:
                float ag = getAlpha() - 0.02f;
                if (ag <= 0.0f) {
                    ag = 0.0f;
                    this.state = 16;
                    setVisibility(false);
                }
                setAlpha(ag);
                invalidate();
                return;
            default:
                return;
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
        Bitmap bitmap = Bitmap.createBitmap((int) getWidth(), (int) getHeight(), Config.ARGB_8888);
        this.drawable.setBounds(0, 0, (int) getWidth(), (int) getHeight());
        this.drawable.draw(new Canvas(bitmap));
        if (this.mMaterials[0].texture != null) {
            this.mMaterials[0].texture.recycle();
        }
        this.mMaterials[0].texture = GLResources.genTextrue(bitmap);
        this.mNeedGenTexture = false;
    }
}
