package zidoo.tarot.widget;

import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;

public class Layout extends GameObject {
    private LinkedList<GameObject> mGameObjectList = new LinkedList();
    private float mHeight;
    private float mScaleX = 0.0f;
    private float mScaleY = 0.0f;
    private float mWidth;

    public Layout(GLContext glContext) {
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

    protected void onUpdate(GL10 gl) {
        LinkedList<GameObject> children = this.mGameObjectList;
        for (int i = 0; i < children.size(); i++) {
            GameObject item = (GameObject) children.get(i);
            item.setAlpha(getAlpha());
            item.update(gl);
        }
    }

    public boolean addGameObject(GameObject child) {
        return this.mGameObjectList.add(child);
    }

    public boolean removeGameObject(GameObject child) {
        return this.mGameObjectList.remove(child);
    }

    public GameObject removeGameObject(int index) {
        return (GameObject) this.mGameObjectList.remove(index);
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
        Iterator<GameObject> itr = this.mGameObjectList.iterator();
        while (itr.hasNext()) {
            GameObject child = (GameObject) itr.next();
            if (child.isShow() && child.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return onKeyEvent(event);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        Iterator<GameObject> itr = this.mGameObjectList.iterator();
        while (itr.hasNext()) {
            GameObject child = (GameObject) itr.next();
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
        Iterator<GameObject> itr = this.mGameObjectList.iterator();
        while (itr.hasNext()) {
            GameObject child = (GameObject) itr.next();
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
