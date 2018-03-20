package zidoo.tarot;

import android.view.KeyEvent;
import android.view.MotionEvent;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.kernel.GameObject;

public abstract class TarotScene {
    public static final int STATE_CREATE = 0;
    public static final int STATE_DESTORY = 2;
    public static final int STATE_RUN = 1;
    private GameObject mContentView = null;
    protected GLContext mGlContext;
    private boolean mHasCreate = false;
    private int mState = 0;

    protected abstract void onCreate();

    public TarotScene(GLContext glContext) {
        this.mGlContext = glContext;
    }

    public void setContentView(GameObject view) {
        this.mContentView = view;
    }

    public GameObject getContentView() {
        return this.mContentView;
    }

    public GLContext getContext() {
        return this.mGlContext;
    }

    public int getState() {
        return this.mState;
    }

    public void update(GL10 gl) {
        if (!this.mHasCreate) {
            onCreate();
            this.mHasCreate = true;
        }
        if (this.mContentView != null) {
            this.mContentView.update(gl);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return (this.mContentView == null || !this.mContentView.dispatchKeyEvent(event)) ? onKeyEvent(event) : true;
    }

    public boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return (this.mContentView == null || !this.mContentView.dispatchTouchEvent(event)) ? onTouchEvent(event) : true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return (this.mContentView == null || !this.mContentView.dispatchGenericMotionEvent(event)) ? onGenericMotionEvent(event) : true;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }
}
