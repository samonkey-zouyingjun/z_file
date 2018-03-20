package zidoo.tarot.widget;

import android.widget.Scroller;
import java.util.HashMap;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;

public class ViewPage extends GameObject {
    HashMap<Integer, GameObject> mChildren = new HashMap();
    private float mContentHeight;
    private float mContentWidth;
    private float mHeight = 0.0f;
    private boolean mIsHorizontal = true;
    private float mScaleX;
    private float mScaleY;
    protected Scroller mScroller = null;
    private int mSelectedPosition = 0;
    private float mWidth = 0.0f;

    public ViewPage(GLContext glContext) {
        super(glContext);
        init();
    }

    private void init() {
    }

    public void setHorizontal(boolean horizontal) {
        this.mIsHorizontal = horizontal;
    }

    public boolean isHorizontal() {
        return this.mIsHorizontal;
    }

    public void setSelection(int position) {
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }
}
