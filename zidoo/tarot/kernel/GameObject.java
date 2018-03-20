package zidoo.tarot.kernel;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.Config.DisplayConfig;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.anim.GlAnimation;

public class GameObject extends Transform implements GLObject {
    public static final int STATE_FOCUSED = 16;
    public static final int STATE_LONG_PRESS = 4096;
    public static final int STATE_MOVE = 65536;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PRESS = 256;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_UP = 17;
    public static final String TAG = "GameObject";
    public static final int VIEW_FIT_TEXTURE = -1;
    public static final int VIEW_KEEP_RATIO_BY_HEIGHT = -3;
    public static final int VIEW_KEEP_RATIO_BY_WIDTH = -2;
    public AABBBox mAABBBox = new AABBBox();
    private float mAlpha = 1.0f;
    private GlAnimation mAnimation = null;
    private GLContext mGLContext;
    private GestureDetector mGestureDetector;
    private int mId;
    private boolean mIgnoreDepthSort = false;
    private boolean mIgnoreMaterialRecycle = false;
    private boolean mIsTouchMode = false;
    private boolean mIsVisible = true;
    protected long mLastTouchUpTime = -1;
    protected Material[] mMaterials = null;
    protected Mesh[] mMeshes = null;
    protected boolean mNeedGenTexture = false;
    protected OnClickListener mOnClickListener = null;
    protected OnKeyListener mOnKeyListener = null;
    protected OnLongClickListener mOnLongClickListener = null;
    GameObject mParent = null;
    private int mState = 0;
    Object mTag = null;

    public interface OnClickListener {
        void onClick(GameObject gameObject);
    }

    public interface OnKeyListener {
        boolean onKey(GameObject gameObject, int i, KeyEvent keyEvent);
    }

    public interface OnLongClickListener {
        boolean onLongClick(GameObject gameObject);
    }

    public GameObject(GLContext glContext) {
        this.mGLContext = glContext;
        initMesh();
        initMaterial();
        initAABBBox();
        this.mGestureDetector = new GestureDetector(this.mGLContext, new SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                return GameObject.this.performClick();
            }

            public void onShowPress(MotionEvent e) {
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            public void onLongPress(MotionEvent e) {
                GameObject.this.performLongClick();
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null) {
                    e1 = e2;
                }
                if (Math.abs(e2.getY() - e1.getY()) >= 15.0f) {
                    return false;
                }
                if (e2.getEventTime() - e1.getEventTime() <= 1000) {
                    return onSingleTapUp(e2);
                }
                onLongPress(e2);
                return true;
            }

            public boolean onDown(MotionEvent e) {
                return false;
            }

            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                return super.onDoubleTapEvent(e);
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }
        }, new Handler(Looper.getMainLooper()));
        this.mGestureDetector.setIsLongpressEnabled(true);
    }

    public GLContext getContext() {
        return this.mGLContext;
    }

    protected DisplayConfig getDisplay() {
        return this.mGLContext.getConfig().getDisplay();
    }

    public GameObject clone() {
        GameObject cloned = (GameObject) super.clone();
        cloned.mGLContext = this.mGLContext;
        cloned.mMeshes = (Mesh[]) this.mMeshes.clone();
        cloned.mMaterials = (Material[]) this.mMaterials.clone();
        cloned.mAABBBox = this.mAABBBox.clone();
        return cloned;
    }

    protected void initMesh() {
    }

    protected void initMaterial() {
    }

    protected void initAABBBox() {
        this.mAABBBox.setParent(this);
        this.mAABBBox.scanMeshes();
    }

    public void setMaterials(Material[] materials) {
        this.mMaterials = materials;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    protected float getMaterialAlpha() {
        return this.mParent == null ? this.mAlpha : this.mAlpha * this.mParent.getMaterialAlpha();
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return this.mTag;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public void setParent(GameObject parent) {
        this.mParent = parent;
    }

    public void removeParent() {
        this.mParent = null;
    }

    public GameObject getParent() {
        return this.mParent;
    }

    public GameObject findFocus() {
        return (this.mState & 16) != 0 ? this : null;
    }

    public void requestFocus() {
        this.mState = 16;
        if (this.mParent != null) {
            this.mParent.requestFocus(this);
        }
    }

    public void removeFocus() {
        this.mState &= -17;
    }

    protected void requestFocus(GameObject requestView) {
        if (this.mParent != null) {
            this.mParent.requestFocus(this);
        }
    }

    public boolean isFocused() {
        return (this.mState & 16) != 0;
    }

    public void setPosition(Position position) {
        this.Position = position;
    }

    public void setRotate(Rotate rotate) {
        this.Rotate = rotate;
    }

    public void setScale(Scale scale) {
        this.Scale = scale;
    }

    public void setWidth(float w) {
        this.Scale.X = getDisplay().sScaleX * w;
    }

    public void setHeight(float h) {
        this.Scale.Y = getDisplay().sScaleY * h;
    }

    public float getWidth() {
        return this.Scale.X / getDisplay().sScaleX;
    }

    public float getHeight() {
        return this.Scale.Y / getDisplay().sScaleY;
    }

    public float getMeasureWidth() {
        return getWidth() * getDisplay().sRatioX;
    }

    public float getMeasureHeight() {
        return getHeight() * getDisplay().sRatioY;
    }

    public void setPositionPixel(float x, float y) {
        this.Position.X = getDisplay().sWidthRatio * x;
        this.Position.Y = getDisplay().sHeightRatio * y;
    }

    public void setX(float x) {
        this.Position.X = getDisplay().sWidthRatio * x;
    }

    public void setY(float y) {
        this.Position.Y = getDisplay().sHeightRatio * y;
    }

    public float getX() {
        return this.Position.X / getDisplay().sWidthRatio;
    }

    public float getY() {
        return this.Position.Y / getDisplay().sHeightRatio;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public int getState() {
        return this.mState;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
    }

    public OnKeyListener getOnKeyListener() {
        return this.mOnKeyListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }

    public OnLongClickListener getOnLongClickListener() {
        return this.mOnLongClickListener;
    }

    public void setIgnoredDepthSort(boolean isIgnored) {
        this.mIgnoreDepthSort = isIgnored;
    }

    public boolean getIngoredDepthSort() {
        return this.mIgnoreDepthSort;
    }

    public void setIgnoreMaterialRecycle(boolean isIgnored) {
        this.mIgnoreMaterialRecycle = isIgnored;
    }

    public boolean getIgnoreMaterialRecycle() {
        return this.mIgnoreMaterialRecycle;
    }

    public void startAnimation(GlAnimation animation) {
        if (this.mAnimation != null && this.mAnimation.isRunning()) {
            this.mAnimation.cancel();
        }
        this.mAnimation = animation;
        this.mAnimation.setTarget(this);
        this.mAnimation.start();
        invalidate();
    }

    public boolean isAnimating() {
        return (this.mAnimation == null || this.mAnimation.isFinish()) ? false : true;
    }

    public GlAnimation getAnimation() {
        return this.mAnimation;
    }

    public void clearAnimation() {
        if (this.mAnimation != null) {
            this.mAnimation.cancel();
            this.mAnimation = null;
        }
    }

    public void endAnimation() {
        if (this.mAnimation != null) {
            this.mAnimation.end();
            this.mAnimation = null;
        }
    }

    public void invalidate() {
        this.mGLContext.requestRender();
    }

    public void invalidate(long delay) {
        this.mGLContext.requestRender(delay);
    }

    public void runInGlThread(Runnable runnable) {
        this.mGLContext.queueEvent(runnable);
    }

    public void update(GL10 gl) {
        gl.glPushMatrix();
        onAnimation();
        if (this.mIsVisible) {
            onTransform(gl);
            onRayCast();
            int count = this.mMeshes == null ? 0 : this.mMeshes.length;
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    if (this.mMeshes[i].isEnabled()) {
                        onMaterial(this.mMaterials[i], gl);
                        onMesh(this.mMeshes[i], gl);
                    }
                }
            }
            onUpdate(gl);
            gl.glPopMatrix();
            return;
        }
        gl.glPopMatrix();
    }

    protected void onUpdate(GL10 gl) {
    }

    protected void onRayCast() {
        this.mAABBBox.update();
    }

    protected void onGenarateTexture() {
        this.mNeedGenTexture = false;
    }

    protected void onAnimation() {
        GlAnimation animation = this.mAnimation;
        if (animation != null && !animation.isFinish()) {
            animation.computeAnimation();
            if (animation.isFinish()) {
                this.mAnimation = null;
            } else {
                invalidate();
            }
        }
    }

    protected void onTransform(GL10 gl) {
        AngleAxis angleAxis = this.Rotate.Quaternion.toAngleAxis();
        gl.glTranslatef(this.Position.X, this.Position.Y, this.Position.Z);
        gl.glRotatef(Radian.toDegree(angleAxis.Angle), angleAxis.xAxis, angleAxis.yAxis, angleAxis.zAxis);
        gl.glScalef(this.Scale.X, this.Scale.Y, this.Scale.Z);
    }

    protected void onMaterial(Material material, GL10 gl) {
        if (material != null) {
            float alpha = getMaterialAlpha();
            if (this.mIgnoreDepthSort) {
                gl.glEnable(3042);
                gl.glEnable(2929);
            } else if (alpha < 1.0f || material.getAlpha() < 1.0f) {
                gl.glEnable(3042);
                gl.glDisable(2929);
            } else {
                gl.glDisable(3042);
                gl.glEnable(2929);
            }
            gl.glFrontFace(2305);
            gl.glMaterialfv(1032, 4608, material.getAmbient(), 0);
            gl.glMaterialfv(1032, 4609, material.getDiffuse(), 0);
            gl.glMaterialfv(1032, 4610, material.getSpecular(), 0);
            gl.glMaterialfv(1032, 5632, material.getEmission(), 0);
            gl.glMaterialf(1032, 5633, material.getShininess());
            gl.glLightModelf(2898, 1.0f);
            float[] mainColor = material.getMainColor();
            gl.glColor4f(mainColor[0], mainColor[1], mainColor[2], material.getAlpha() * alpha);
            if (material.texture == null || !material.texture.isValid()) {
                gl.glDisable(3553);
                gl.glColorMask(false, false, false, false);
                return;
            }
            gl.glEnable(3553);
            gl.glBindTexture(3553, material.texture.getTextureID());
        }
    }

    protected void onMesh(Mesh mesh, GL10 gl) {
        if (mesh != null) {
            if (mesh.VertexBuffer != null) {
                gl.glEnableClientState(32884);
                mesh.VertexBuffer.rewind();
                gl.glVertexPointer(3, 5126, 0, mesh.VertexBuffer);
            }
            if (mesh.ColorBuffer != null) {
                gl.glEnableClientState(32886);
                mesh.ColorBuffer.rewind();
                gl.glColorPointer(4, 5126, 0, mesh.ColorBuffer);
            }
            if (mesh.CoordBuffer != null) {
                gl.glEnableClientState(32888);
                mesh.CoordBuffer.rewind();
                gl.glTexCoordPointer(2, 5126, 0, mesh.CoordBuffer);
            }
            if (mesh.NormBuffer != null) {
                gl.glEnableClientState(32885);
                mesh.NormBuffer.rewind();
                gl.glNormalPointer(5126, 0, mesh.NormBuffer);
            }
            if (mesh.IndexBuffer != null) {
                mesh.IndexBuffer.rewind();
                gl.glDrawElements(mesh.TriangleType, mesh.IndexBuffer.capacity(), 5123, mesh.IndexBuffer);
            }
            gl.glColorMask(true, true, true, true);
            gl.glDisableClientState(32884);
            gl.glDisableClientState(32888);
            gl.glDisableClientState(32885);
            gl.glDisableClientState(32886);
        }
    }

    public void setVisibility(boolean visible) {
        this.mIsVisible = visible;
        invalidate();
    }

    public boolean isShow() {
        return this.mIsVisible;
    }

    public void setTouchMode(boolean isTouchMode) {
        this.mIsTouchMode = isTouchMode;
    }

    public boolean isTouchMode() {
        return this.mIsTouchMode;
    }

    public void dispose() {
        if (this.mMaterials != null && !this.mIgnoreMaterialRecycle) {
            for (Material recycle : this.mMaterials) {
                recycle.recycle();
            }
        }
    }

    public Transform getFinalTransform() {
        return this;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        float[] innerHitPoint = new float[2];
        if (!this.mAABBBox.isHit(event.getX(), event.getY(), innerHitPoint)) {
            return false;
        }
        MotionEvent normalizedEvent = MotionEvent.obtain(event);
        normalizedEvent.setLocation(innerHitPoint[0], innerHitPoint[1]);
        boolean state = onTouchEvent(normalizedEvent);
        normalizedEvent.recycle();
        return state;
    }

    public boolean isTouched(MotionEvent event) {
        return this.mAABBBox.isHit(event.getX(), event.getY(), null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mGestureDetector.onTouchEvent(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mOnKeyListener != null && this.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        if (event.getAction() == 0) {
            return onKeyDown(event);
        }
        if (event.getAction() == 1) {
            return onKeyUp(event);
        }
        return onKeyEvent(event);
    }

    public boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    protected boolean onKeyDown(KeyEvent event) {
        this.mState &= -257;
        return false;
    }

    protected boolean onKeyUp(KeyEvent event) {
        switch (event.getKeyCode()) {
            case MotionEventCompat.AXIS_BRAKE /*23*/:
            case 66:
            case 160:
                return performClick();
            default:
                return false;
        }
    }

    protected boolean performClick() {
        if (this.mOnClickListener == null) {
            return false;
        }
        this.mState &= -257;
        this.mOnClickListener.onClick(this);
        return true;
    }

    protected boolean performLongClick() {
        this.mState |= 4096;
        if (this.mOnLongClickListener == null) {
            return false;
        }
        this.mOnLongClickListener.onLongClick(this);
        return true;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        float[] innerHitPoint = new float[2];
        if (!this.mAABBBox.isHit(event.getX(), event.getY(), innerHitPoint)) {
            return false;
        }
        MotionEvent normalizedEvent = MotionEvent.obtain(event);
        normalizedEvent.setLocation(innerHitPoint[0], innerHitPoint[1]);
        boolean state = onGenericMotionEvent(normalizedEvent);
        normalizedEvent.recycle();
        return state;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public void sendAccessibilityEvent(int eventType) {
        dispatchAccessibilityEvent(AccessibilityEvent.obtain(eventType));
    }

    public void sendAccessibilityEvent(AccessibilityEvent event) {
        dispatchAccessibilityEvent(event);
    }

    public void dispatchAccessibilityEvent(AccessibilityEvent event) {
    }
}
