package com.zidoo.fileexplorer.view;

import android.graphics.RectF;
import android.text.TextPaint;
import android.view.MotionEvent;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.PathInfo;
import com.zidoo.fileexplorer.tool.ZidooTypeface;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;

public class ExpandPathView extends GameObject {
    private final int MAX_WIDTH = 600;
    private final int POINTER_WIDTH = 30;
    private final int SIZE = 26;
    int mBgdResid = 0;
    GameObject mHeadPointer;
    float mHeight = 0.0f;
    boolean mNeedUpdate = false;
    float mOffset = 0.0f;
    OnPathClickListener mOnPathClickListener = null;
    TextPaint mPaint = new TextPaint();
    ArrayList<PathInfo> mPathNames = new ArrayList();
    Queue<TTextView> mPathRecycles = new LinkedBlockingDeque();
    ArrayList<TTextView> mPaths = new ArrayList();
    Queue<TImageView> mPointerRecycles = new LinkedBlockingDeque();
    ArrayList<TImageView> mPointers = new ArrayList();
    float mScaleX;
    float mScaleY;
    float mWidth = 0.0f;

    public interface OnPathClickListener {
        void onPath(PathInfo pathInfo, int i);
    }

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ExpandPathView(GLContext glContext) {
        super(glContext);
        this.mPaint.setTextSize(26.0f);
        this.mPaint.setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
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

    public void addPath(PathInfo path) {
        this.mPathNames.add(path);
        this.mNeedUpdate = true;
        invalidate();
    }

    public void backUp() {
        if (this.mPathNames.size() > 0) {
            this.mPathNames.remove(this.mPathNames.size() - 1);
            this.mNeedUpdate = true;
            invalidate();
        }
    }

    public void backTo(int index) {
        ArrayList<PathInfo> temp = new ArrayList();
        for (int i = 0; i <= index; i++) {
            temp.add(this.mPathNames.get(i));
        }
        this.mPathNames = temp;
        this.mNeedUpdate = true;
        invalidate();
    }

    public void clear() {
        this.mPathNames.clear();
        this.mNeedUpdate = true;
        invalidate();
    }

    public void setBackgroundResource(int resid) {
        if (this.mBgdResid != resid) {
            this.mBgdResid = resid;
            this.mNeedGenTexture = true;
        }
    }

    public void setOnPathClickListener(OnPathClickListener onPathClickListener) {
        this.mOnPathClickListener = onPathClickListener;
    }

    public int getCount() {
        return this.mPathNames.size();
    }

    public void refreash() {
        this.mNeedUpdate = true;
        invalidate();
    }

    public void update(GL10 gl) {
        if (isShow()) {
            if (this.mNeedUpdate) {
                recycleAll();
                this.mOffset = 0.0f;
                int i = this.mPathNames.size() - 1;
                while (i >= 0) {
                    float w = this.mOffset;
                    TImageView pointer = getPointer();
                    pointer.setX(w - 15.0f);
                    w -= 30.0f;
                    TTextView path = obtainView();
                    String name = ((PathInfo) this.mPathNames.get(i)).getName();
                    float tw = this.mPaint.measureText(name) + 10.0f;
                    if (tw > 600.0f) {
                        tw = 600.0f;
                    }
                    path.setWidth(tw);
                    path.setText(name);
                    path.setX(w - (tw / 2.0f));
                    w -= tw;
                    if ((-w) + 30.0f > this.mWidth) {
                        break;
                    }
                    path.setTag(this.mPathNames.get(i));
                    this.mPaths.add(path);
                    this.mPointers.add(pointer);
                    this.mOffset = w;
                    i--;
                }
                this.mHeadPointer = i < 0 ? getPointer() : getBackPathView();
                this.mHeadPointer.setX(this.mOffset - 15.0f);
                this.mOffset -= 30.0f;
                this.mNeedUpdate = false;
            }
            gl.glPushMatrix();
            onAnimation();
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
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glPopMatrix();
            gl.glStencilFunc(514, 1, 1);
            gl.glStencilOp(7680, 7680, 7680);
            gl.glPushMatrix();
            gl.glTranslatef((-((this.mWidth / 2.0f) + this.mOffset)) * getDisplay().sWidthRatio, 0.0f, 0.0f);
            for (int j = 0; j < this.mPaths.size(); j++) {
                ((TTextView) this.mPaths.get(j)).update(gl);
                ((TImageView) this.mPointers.get(j)).update(gl);
            }
            if (this.mHeadPointer != null) {
                this.mHeadPointer.update(gl);
            }
            gl.glPopMatrix();
            gl.glDisable(2960);
            gl.glPopMatrix();
        }
    }

    void recycleAll() {
        this.mPathRecycles.addAll(this.mPaths);
        this.mPointerRecycles.addAll(this.mPointers);
        this.mPaths.clear();
        this.mPointers.clear();
    }

    TTextView obtainView() {
        TTextView item = (TTextView) this.mPathRecycles.poll();
        if (item != null) {
            return item;
        }
        item = new TTextView(getContext());
        item.setTextColor(-1);
        item.setTextSize(26.0f);
        item.setSingleLine(true);
        item.setHeight(30.0f);
        item.setTextGravity(17);
        return item;
    }

    TImageView getPointer() {
        TImageView pointer = (TImageView) this.mPointerRecycles.poll();
        if (pointer != null) {
            return pointer;
        }
        pointer = new TImageView(getContext());
        pointer.setWidth(30.0f);
        pointer.setHeight(30.0f);
        pointer.setImageResource(R.drawable.img_file_pointer);
        return pointer;
    }

    TImageView getBackPathView() {
        TImageView backPathView = new TImageView(getContext());
        backPathView.setWidth(30.0f);
        backPathView.setHeight(30.0f);
        backPathView.setImageResource(R.drawable.img_path_back);
        return backPathView;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.mOnPathClickListener != null && event.getAction() == 1 && isTouched(event)) {
            ArrayList<TTextView> children = this.mPaths;
            for (int i = 0; i < children.size(); i++) {
                TTextView child = (TTextView) children.get(i);
                if (child.isTouched(event)) {
                    int index = (this.mPathNames.size() - 1) - i;
                    backTo(index);
                    this.mOnPathClickListener.onPath((PathInfo) child.getTag(), index);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTouched(MotionEvent event) {
        RectF rectF = this.mAABBBox.getScreenRect();
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float w = (rectF.right - rectF.left) * this.mScaleX;
        float h = (rectF.bottom - rectF.top) * this.mScaleY;
        return new RectF(cx - (w / 2.0f), cy - (h / 2.0f), (w / 2.0f) + cx, (h / 2.0f) + cy).contains(event.getX(), event.getY());
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[1];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, -0.5f, -0.0f, 0.5f, -0.5f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.mMeshes[0].setIndices(new short[]{(short) 0, (short) 1, (short) 2, (short) 2, (short) 3, (short) 0});
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

    protected void onGenarateTexture() {
        this.mMaterials[0].texture = getContext().getTexture(this.mBgdResid);
        this.mNeedGenTexture = false;
    }
}
