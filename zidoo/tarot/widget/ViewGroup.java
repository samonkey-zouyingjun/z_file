package zidoo.tarot.widget;

import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import java.util.LinkedList;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class ViewGroup extends GameObject {
    int mBgdResid = 0;
    private GameObject mFocused;
    private LinkedList<GameObject> mGameObjectList = new LinkedList();
    private float mHeight;
    private boolean mMask = false;
    private float mScaleX = 0.0f;
    private float mScaleY = 0.0f;
    private float mWidth;

    public ViewGroup(GLContext glContext) {
        super(glContext);
    }

    public void setWidth(float width) {
        this.mWidth = width;
        this.mScaleX = getDisplay().sWidthRatio * width;
    }

    public void setHeight(float height) {
        this.mHeight = height;
        this.mScaleY = getDisplay().sHeightRatio * height;
    }

    public float getWidth() {
        return this.mWidth;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public GameObject findFocus() {
        if (isFocused()) {
            return this;
        }
        return this.mFocused == null ? null : this.mFocused;
    }

    protected void requestFocus(GameObject requestView) {
        if (!(this.mFocused == null || this.mFocused == requestView)) {
            this.mFocused.removeFocus();
        }
        this.mFocused = requestView;
        super.requestFocus(requestView);
    }

    public void removeFocus() {
        super.removeFocus();
        if (this.mFocused != null) {
            this.mFocused.removeFocus();
        }
    }

    public void setMask(boolean mask) {
        this.mMask = mask;
    }

    public void setBackgroundResource(int resid) {
        if (this.mMeshes == null) {
            initBackground();
        }
        if (this.mBgdResid != resid) {
            this.mBgdResid = resid;
            this.mNeedGenTexture = true;
        }
    }

    private void initBackground() {
        this.mMeshes = new Mesh[1];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, -0.5f, -0.0f, 0.5f, -0.5f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[0];
        short[] sArr = new short[6];
        sArr[1] = (short) 1;
        sArr[2] = (short) 2;
        sArr[3] = (short) 2;
        sArr[4] = (short) 3;
        mesh.setIndices(sArr);
        this.mMaterials = new Material[1];
        Material backgroundMaterial = new Material();
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
    }

    public void update(GL10 gl) {
        if (this.mMask) {
            gl.glPushMatrix();
            onAnimation();
            if (isShow()) {
                onTransform(gl);
                onRayCast();
                if (this.mNeedGenTexture) {
                    onGenarateTexture();
                }
                gl.glEnable(2960);
                gl.glStencilFunc(519, 1, 1);
                gl.glStencilOp(7681, 7681, 7681);
                gl.glPushMatrix();
                gl.glScalef(this.mScaleX, this.mScaleY, 1.0f);
                if (this.mMeshes != null) {
                    onMaterial(this.mMaterials[0], gl);
                    onMesh(this.mMeshes[0], gl);
                }
                gl.glPopMatrix();
                gl.glStencilFunc(514, 1, 1);
                gl.glStencilOp(7680, 7680, 7680);
                onUpdate(gl);
                gl.glDisable(2960);
                gl.glPopMatrix();
                return;
            }
            gl.glPopMatrix();
            return;
        }
        gl.glPushMatrix();
        onAnimation();
        if (isShow()) {
            onTransform(gl);
            onRayCast();
            if (this.mNeedGenTexture) {
                onGenarateTexture();
            }
            gl.glPushMatrix();
            gl.glScalef(this.mScaleX, this.mScaleY, 1.0f);
            if (this.mMeshes != null) {
                onMaterial(this.mMaterials[0], gl);
                onMesh(this.mMeshes[0], gl);
            }
            gl.glPopMatrix();
            onUpdate(gl);
            gl.glPopMatrix();
            return;
        }
        gl.glPopMatrix();
    }

    protected void onUpdate(GL10 gl) {
        LinkedList<GameObject> children = this.mGameObjectList;
        for (int i = 0; i < children.size(); i++) {
            ((GameObject) children.get(i)).update(gl);
        }
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mBgdResid);
        this.mNeedGenTexture = false;
    }

    public boolean addGameObject(GameObject child) {
        child.setParent(this);
        return this.mGameObjectList.add(child);
    }

    public boolean removeGameObject(GameObject child) {
        child.removeParent();
        return this.mGameObjectList.remove(child);
    }

    public void addGameObject(int local, GameObject child) {
        child.setParent(this);
        this.mGameObjectList.add(local, child);
    }

    public GameObject removeGameObject(int local) {
        GameObject child = (GameObject) this.mGameObjectList.remove(local);
        child.removeParent();
        return child;
    }

    public void addFirst(GameObject child) {
        child.setParent(this);
        this.mGameObjectList.addFirst(child);
    }

    public void addLast(GameObject child) {
        child.setParent(this);
        this.mGameObjectList.addLast(child);
    }

    public void removeFirst() {
        ((GameObject) this.mGameObjectList.removeFirst()).removeParent();
    }

    public void removeLast() {
        ((GameObject) this.mGameObjectList.removeLast()).removeParent();
    }

    public void removeAll() {
        this.mGameObjectList.clear();
    }

    public int indexOf(GameObject child) {
        return this.mGameObjectList.indexOf(child);
    }

    public GameObject get(int index) {
        return (GameObject) this.mGameObjectList.get(index);
    }

    public int size() {
        return this.mGameObjectList.size();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mFocused != null) {
            return this.mFocused.dispatchKeyEvent(event);
        }
        if (this.mOnKeyListener != null && this.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        for (int i = 0; i < this.mGameObjectList.size(); i++) {
            GameObject child = (GameObject) this.mGameObjectList.get(i);
            if (child.isShow() && child.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return onKeyEvent(event);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        for (int i = 0; i < this.mGameObjectList.size(); i++) {
            GameObject child = (GameObject) this.mGameObjectList.get(i);
            if (child.isShow() && child.dispatchTouchEvent(event)) {
                return true;
            }
        }
        float cx = ((float) (getDisplay().sScreenWidth / 2)) + (getX() * getDisplay().sRatioX);
        float cy = ((float) (getDisplay().sScreenHeight / 2)) - (getY() * getDisplay().sRatioY);
        RectF realBound = new RectF(cx - ((this.mWidth / 2.0f) * getDisplay().sRatioX), cy - ((this.mHeight / 2.0f) * getDisplay().sRatioY), ((this.mWidth / 2.0f) * getDisplay().sRatioX) + cx, ((this.mHeight / 2.0f) * getDisplay().sRatioY) + cy);
        if (!realBound.contains(event.getX(), event.getY())) {
            return false;
        }
        MotionEvent normalizedEvent = MotionEvent.obtain(event);
        normalizedEvent.setLocation(event.getX() - realBound.left, event.getY() - realBound.top);
        boolean state = onTouchEvent(normalizedEvent);
        normalizedEvent.recycle();
        return state;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        for (int i = 0; i < this.mGameObjectList.size(); i++) {
            GameObject child = (GameObject) this.mGameObjectList.get(i);
            if (child.isShow() && child.dispatchGenericMotionEvent(event)) {
                return true;
            }
        }
        float cx = ((float) (getDisplay().sScreenWidth / 2)) + (getX() * getDisplay().sRatioX);
        float cy = ((float) (getDisplay().sScreenHeight / 2)) - (getY() * getDisplay().sRatioY);
        RectF realBound = new RectF(cx - ((this.mWidth / 2.0f) * getDisplay().sRatioX), cy - ((this.mHeight / 2.0f) * getDisplay().sRatioY), ((this.mWidth / 2.0f) * getDisplay().sRatioX) + cx, ((this.mHeight / 2.0f) * getDisplay().sRatioY) + cy);
        if (!realBound.contains(event.getX(), event.getY())) {
            return false;
        }
        MotionEvent normalizedEvent = MotionEvent.obtain(event);
        normalizedEvent.setLocation(event.getX() - realBound.left, event.getY() - realBound.top);
        boolean state = onGenericMotionEvent(normalizedEvent);
        normalizedEvent.recycle();
        return state;
    }

    public boolean isTouched(MotionEvent event) {
        float cx = ((float) (getDisplay().sScreenWidth / 2)) + (getX() * getDisplay().sRatioX);
        float cy = ((float) (getDisplay().sScreenHeight / 2)) - (getY() * getDisplay().sRatioY);
        return new RectF(cx - ((this.mWidth / 2.0f) * getDisplay().sRatioX), cy - ((this.mHeight / 2.0f) * getDisplay().sRatioY), ((this.mWidth / 2.0f) * getDisplay().sRatioX) + cx, ((this.mHeight / 2.0f) * getDisplay().sRatioY) + cy).contains(event.getX(), event.getY());
    }
}
