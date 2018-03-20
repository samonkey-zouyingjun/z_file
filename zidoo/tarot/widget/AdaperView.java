package zidoo.tarot.widget;

import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public abstract class AdaperView<T extends Adapter> extends GameObject {
    protected final int UPDATE_NORMAL = 0;
    protected final int UPDATE_PAGE_SCROLL = 1118209;
    protected final int UPDATE_PAGE_TURN = 69633;
    protected final int UPDATE_SCROLL = 4096;
    protected final int UPDATE_UPDATE = 1;
    protected final int UPDATE_UPDATE_SCROOL = 4097;
    protected T mAdapter = null;
    int mBgdResid = 0;
    protected DataSetObserver mDataSetObserver = null;
    private boolean mDrawSelectorOnTop = false;
    protected int mFirstVisiblePosition = 0;
    protected float mHeight = 0.0f;
    protected int mLastVisiblePosition = 0;
    protected OnItemClickListener mOnItemClickListener = null;
    protected OnItemLongClickListener mOnItemLongClickListener = null;
    protected OnItemSelectedListener mOnItemSelectedListener = null;
    protected RecycleBin mRecycler = new RecycleBin();
    protected float mScaleX;
    protected float mScaleY;
    protected ScrollBar mScrollBar = null;
    protected Scroller mScroller = null;
    protected int mSelectedPosition = 0;
    protected GameObject mSelector = null;
    protected float mWidth = 0.0f;

    public interface OnItemClickListener {
        void onItemClick(AdaperView<?> adaperView, GameObject gameObject, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdaperView<?> adaperView, GameObject gameObject, int i);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdaperView<?> adaperView, GameObject gameObject, int i);

        void onNothingSelected(AdaperView<?> adaperView);
    }

    protected class RecycleBin {
        GameObject[] activedViews = new GameObject[0];
        int firstActivePosition;
        Queue<GameObject> recycles = new LinkedBlockingDeque();
        ArrayList<GameObject> tempActiveis = new ArrayList();

        protected RecycleBin() {
        }

        GameObject obtainView(int position) {
            GameObject view;
            int index = position - this.firstActivePosition;
            if (index < 0 || index >= this.activedViews.length) {
                view = AdaperView.this.mAdapter.getView(position, (GameObject) this.recycles.poll(), null);
            } else {
                view = this.activedViews[index];
                this.activedViews[index] = null;
            }
            view.setParent(AdaperView.this);
            this.tempActiveis.add(view);
            return view;
        }

        int getCount() {
            return this.activedViews.length;
        }

        void arrange() {
            for (int i = 0; i < this.activedViews.length; i++) {
                if (this.activedViews[i] != null) {
                    this.activedViews[i].removeParent();
                    this.activedViews[i].endAnimation();
                    this.recycles.offer(this.activedViews[i]);
                }
            }
            this.activedViews = new GameObject[this.tempActiveis.size()];
            this.tempActiveis.toArray(this.activedViews);
            this.tempActiveis.clear();
            this.firstActivePosition = AdaperView.this.mFirstVisiblePosition;
        }

        void clear() {
            this.firstActivePosition = 0;
            this.recycles.clear();
            this.tempActiveis.clear();
            this.activedViews = new GameObject[0];
            this.recycles.clear();
        }

        GameObject[] getActiveViews() {
            return this.activedViews;
        }

        GameObject getActiveView(int position) {
            int index = position - this.firstActivePosition;
            if (index < 0 || index >= this.activedViews.length) {
                return null;
            }
            return this.activedViews[index];
        }

        void recycleAll() {
            try {
                for (GameObject v : this.activedViews) {
                    v.endAnimation();
                }
                this.recycles.addAll(Arrays.asList(this.activedViews));
                this.activedViews = new GameObject[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void dataSetChange() {
            for (int i = 0; i < this.activedViews.length; i++) {
                this.activedViews[i] = AdaperView.this.mAdapter.getView(AdaperView.this.mFirstVisiblePosition + i, this.activedViews[i], null);
            }
        }

        GameObject getRecycleView(int position) {
            GameObject view = AdaperView.this.mAdapter.getView(position, (GameObject) this.recycles.poll(), null);
            this.tempActiveis.add(view);
            return view;
        }

        void addToActive() {
            ArrayList<GameObject> temp = new ArrayList(Arrays.asList(this.activedViews));
            temp.addAll(this.tempActiveis);
            this.activedViews = new GameObject[(this.activedViews.length + this.tempActiveis.size())];
            temp.toArray(this.activedViews);
            this.tempActiveis.clear();
        }

        void fillViewsInActivedViews(GameObject[] views) {
            this.activedViews = views;
        }

        GameObject getScrapView(int position) {
            return AdaperView.this.mAdapter.getView(position, (GameObject) this.recycles.poll(), null);
        }
    }

    public abstract void setAdapter(T t);

    public abstract void setSelection(int i);

    protected abstract void updateContent(GL10 gl10);

    protected abstract void updateSelector(GL10 gl10);

    public AdaperView(GLContext glContext) {
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

    public void setDrawSelectorOnTop(boolean drawSelectorOnTop) {
        this.mDrawSelectorOnTop = drawSelectorOnTop;
    }

    public boolean isDrawSelectorOnTop() {
        return this.mDrawSelectorOnTop;
    }

    public void setBackgroundResource(int resid) {
        if (this.mBgdResid != resid) {
            this.mBgdResid = resid;
            this.mNeedGenTexture = true;
        }
    }

    public int getFirstVisiblePosition() {
        return this.mFirstVisiblePosition;
    }

    public int getLastVisiblePosition() {
        return this.mLastVisiblePosition;
    }

    public GameObject getFirstVisibleView() {
        return this.mRecycler.getActiveView(this.mFirstVisiblePosition);
    }

    public GameObject getLastVisibleView() {
        return this.mRecycler.getActiveView(this.mLastVisiblePosition);
    }

    public int getVisibleChildCount() {
        return (this.mLastVisiblePosition - this.mFirstVisiblePosition) + 1;
    }

    public int getCount() {
        return this.mAdapter == null ? 0 : this.mAdapter.getCount();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return this.mOnItemSelectedListener;
    }

    public void setOnItemLongPressListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemLongClickListener getOnItemLongPressListener() {
        return this.mOnItemLongClickListener;
    }

    protected boolean performClick() {
        setState(getState() & -257);
        if (this.mOnItemClickListener == null || getCount() <= 0) {
            return super.performClick();
        }
        this.mOnItemClickListener.onItemClick(this, getSelectedView(), this.mSelectedPosition);
        return true;
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setSelector(GameObject selector) {
        this.mSelector = selector;
    }

    public GameObject getSelector() {
        return this.mSelector;
    }

    public void setScrollBar(ScrollBar scrollBar) {
        this.mScrollBar = scrollBar;
    }

    public ScrollBar getScrollBar() {
        return this.mScrollBar;
    }

    protected void updateScrollBar(GL10 gl) {
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }

    public GameObject getSelectedView() {
        return this.mRecycler.getActiveView(this.mSelectedPosition);
    }

    protected GameObject[] getActiveViews() {
        return this.mRecycler.getActiveViews();
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
            gl.glClear(1024);
            gl.glEnable(2960);
            gl.glStencilFunc(519, 1, 1);
            gl.glStencilOp(7681, 7681, 7681);
            gl.glPushMatrix();
            gl.glScalef(this.mScaleX, this.mScaleY, 1.0f);
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glPopMatrix();
            gl.glStencilFunc(514, 1, 1);
            gl.glStencilOp(7680, 7680, 7680);
            gl.glPushMatrix();
            if (this.mDrawSelectorOnTop) {
                updateContent(gl);
                updateScrollBar(gl);
                updateSelector(gl);
            } else {
                updateSelector(gl);
                updateContent(gl);
                updateScrollBar(gl);
            }
            gl.glPopMatrix();
            onUpdate(gl);
            gl.glDisable(2960);
            gl.glPopMatrix();
        }
    }

    protected void initMesh() {
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
    }

    protected void initMaterial() {
        Material backgroundMaterial = new Material();
        this.mMaterials = new Material[1];
        backgroundMaterial.MaterialName = "BackgroundMaterial";
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

    boolean isScrollTouched(MotionEvent e) {
        float hw = this.mScrollBar.getWidth() / 2.0f;
        float hh = this.mScrollBar.getHeight() / 2.0f;
        float x = this.mScrollBar.getX() + (this.mWidth / 2.0f);
        float y = (-this.mScrollBar.getY()) + (this.mHeight / 2.0f);
        float ex = e.getX();
        float ey = e.getY();
        return ex > x - hw && ex < x + hw && ey > y - hh && ey < y + hh;
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mBgdResid);
        this.mNeedGenTexture = false;
    }

    public boolean isTouched(MotionEvent event) {
        RectF rectF = this.mAABBBox.getScreenRect();
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float w = (rectF.right - rectF.left) * this.mScaleX;
        float h = (rectF.bottom - rectF.top) * this.mScaleY;
        return new RectF(cx - (w / 2.0f), cy - (h / 2.0f), (w / 2.0f) + cx, (h / 2.0f) + cy).contains(event.getX(), event.getY());
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        boolean state = false;
        RectF rectF = this.mAABBBox.getScreenRect();
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float w = (rectF.right - rectF.left) * this.mScaleX;
        float h = (rectF.bottom - rectF.top) * this.mScaleY;
        RectF realBound = new RectF(cx - (w / 2.0f), cy - (h / 2.0f), (w / 2.0f) + cx, (h / 2.0f) + cy);
        if (realBound.contains(event.getX(), event.getY())) {
            MotionEvent normalizedEvent = MotionEvent.obtain(event);
            normalizedEvent.setLocation(event.getX() - realBound.left, event.getY() - realBound.top);
            state = onGenericMotionEvent(normalizedEvent);
            normalizedEvent.recycle();
        }
        if (state) {
            setTouchMode(true);
        }
        return state;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean state = false;
        RectF rectF = this.mAABBBox.getScreenRect();
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float w = (rectF.right - rectF.left) * this.mScaleX;
        float h = (rectF.bottom - rectF.top) * this.mScaleY;
        RectF realBound = new RectF(cx - (w / 2.0f), cy - (h / 2.0f), (w / 2.0f) + cx, (h / 2.0f) + cy);
        if (realBound.contains(event.getX(), event.getY())) {
            MotionEvent normalizedEvent = MotionEvent.obtain(event);
            normalizedEvent.setLocation(event.getX() - realBound.left, event.getY() - realBound.top);
            state = onTouchEvent(normalizedEvent);
            normalizedEvent.recycle();
        }
        if (state) {
            setTouchMode(true);
        }
        return state;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!super.dispatchKeyEvent(event)) {
            return false;
        }
        setTouchMode(false);
        return true;
    }
}
