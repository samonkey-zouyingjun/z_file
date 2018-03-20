package zidoo.tarot.widget;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;
import zidoo.tarot.kernel.GameObject;

@SuppressLint({"UseSparseArrays"})
public class WheelView extends AdaperView<Adapter> {
    private final int STATE_NORMAL = 0;
    private final int STATE_UPDATE = 1;
    private float mContentHeight;
    private MyRecycleBin mMyRecycleBin;
    private float mRowHeight = 50.0f;
    private boolean mScrollEnable = false;
    private int mState = 0;

    class MyRecycleBin {
        HashMap<Integer, GameObject> actives = new HashMap();
        Queue<GameObject> recycles = new LinkedBlockingDeque();
        HashMap<Integer, GameObject> temps = new HashMap();

        MyRecycleBin() {
        }

        GameObject obtainView(int position) {
            GameObject view = (GameObject) this.actives.remove(Integer.valueOf(position));
            if (view == null) {
                view = WheelView.this.mAdapter.getView(position, (GameObject) this.recycles.poll(), null);
            }
            this.temps.put(Integer.valueOf(position), view);
            return view;
        }

        public int getCount() {
            return this.actives.size();
        }

        public HashMap<Integer, GameObject> getActiveViews() {
            return this.actives;
        }

        void arrange() {
            for (GameObject offer : this.actives.values()) {
                this.recycles.offer(offer);
            }
            this.actives = new HashMap(this.temps);
            this.temps.clear();
        }

        void clear() {
            this.actives.clear();
            this.recycles.clear();
        }

        GameObject getActiveView(int position) {
            return (GameObject) this.actives.get(Integer.valueOf(position));
        }
    }

    public WheelView(GLContext glContext) {
        super(glContext);
        init();
    }

    private void init() {
        this.mMyRecycleBin = new MyRecycleBin();
        this.mScroller = new Scroller(getContext(), new DecelerateInterpolator(), true);
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                if (WheelView.this.mSelectedPosition > WheelView.this.mAdapter.getCount() - 1) {
                    WheelView.this.mScroller.setFinalY(0);
                }
                WheelView.this.mContentHeight = WheelView.this.mRowHeight * ((float) WheelView.this.mAdapter.getCount());
                WheelView.this.invalidate();
            }

            public void onInvalidated() {
                super.onInvalidated();
            }
        };
    }

    public void setRowHeight(float height) {
        this.mRowHeight = height;
    }

    public float getRowHeight() {
        return this.mRowHeight;
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.mScrollEnable = scrollEnable;
    }

    public boolean isScrollEnable() {
        return this.mScrollEnable;
    }

    public void setAdapter(Adapter adapter) {
        if (this.mAdapter != adapter) {
            this.mAdapter = adapter;
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mContentHeight = this.mRowHeight * ((float) this.mAdapter.getCount());
            if (this.mSelectedPosition > this.mAdapter.getCount() - 1) {
                this.mScroller.setFinalY(0);
                this.mSelectedPosition = 0;
            }
            this.mState = 1;
            invalidate();
        }
    }

    public void setSelection(int position) {
        if (position >= 0 && position < this.mAdapter.getCount()) {
            this.mSelectedPosition = position;
            this.mScroller.setFinalY((int) (((float) this.mSelectedPosition) * this.mRowHeight));
            this.mState = 1;
            invalidate();
        }
    }

    public void scrollToPosition(int position) {
        if (position >= 0 && position < this.mAdapter.getCount()) {
            this.mSelectedPosition = position;
            this.mScroller.startScroll(0, this.mScroller.getCurrY(), 0, (int) ((((float) this.mSelectedPosition) * this.mRowHeight) - ((float) this.mScroller.getCurrY())));
            this.mState = 1;
            invalidate();
        }
    }

    private void next() {
        this.mScroller.startScroll(0, this.mScroller.getCurrY(), 0, (int) this.mRowHeight);
        this.mState = 1;
        invalidate();
    }

    private void last() {
        this.mScroller.startScroll(0, this.mScroller.getCurrY(), 0, (int) (-this.mRowHeight));
        this.mState = 1;
        invalidate();
    }

    protected void updateContent(GL10 gl) {
        if ((this.mState & 1) == 1) {
            int count;
            int y;
            if (this.mAdapter == null) {
                count = 0;
            } else {
                count = this.mAdapter.getCount();
            }
            if (count > 0) {
                this.mScroller.computeScrollOffset();
                float top = this.mHeight / 2.0f;
                float bottom = (-this.mHeight) / 2.0f;
                y = this.mScroller.getCurrY();
                float realY = ((float) y) % this.mRowHeight;
                this.mSelectedPosition = ((int) (((float) y) / this.mRowHeight)) % count;
                if (this.mSelectedPosition < 0) {
                    this.mSelectedPosition += count;
                }
                GameObject datum = this.mMyRecycleBin.obtainView(this.mSelectedPosition);
                datum.setPositionPixel(0.0f, realY);
                datum.update(gl);
                float upY = realY;
                float downY = realY;
                boolean down = true;
                int offset = 1;
                int i = 1;
                while (i < count) {
                    GameObject item;
                    if (down) {
                        downY -= this.mRowHeight;
                        this.mFirstVisiblePosition = (this.mSelectedPosition + offset) % count;
                        item = this.mMyRecycleBin.obtainView(this.mFirstVisiblePosition);
                        item.setPositionPixel(0.0f, downY);
                        item.update(gl);
                    } else {
                        upY += this.mRowHeight;
                        this.mLastVisiblePosition = (this.mSelectedPosition - offset) % count;
                        if (this.mLastVisiblePosition < 0) {
                            this.mLastVisiblePosition += count;
                        }
                        offset++;
                        item = this.mMyRecycleBin.obtainView(this.mLastVisiblePosition);
                        item.setPositionPixel(0.0f, upY);
                        item.update(gl);
                    }
                    if (downY - (this.mRowHeight / 2.0f) < bottom && (this.mRowHeight / 2.0f) + upY > top) {
                        break;
                    }
                    i++;
                    if (down) {
                        down = false;
                    } else {
                        down = true;
                    }
                }
                this.mMyRecycleBin.arrange();
            }
            if (this.mScroller.isFinished()) {
                y = this.mScroller.getCurrY();
                if (y < 0) {
                    this.mScroller.setFinalY((int) ((((float) y) % this.mContentHeight) + this.mContentHeight));
                    this.mScroller.computeScrollOffset();
                } else if (((float) y) > this.mContentHeight) {
                    this.mScroller.setFinalY((int) (((float) y) % this.mContentHeight));
                    this.mScroller.computeScrollOffset();
                }
                this.mState = 0;
            }
            invalidate();
            return;
        }
        for (GameObject update : this.mMyRecycleBin.getActiveViews().values()) {
            update.update(gl);
        }
    }

    protected void updateSelector(GL10 gl) {
        if (this.mSelector != null) {
            this.mSelector.update(gl);
        }
    }

    public GameObject getFirstVisibleView() {
        return this.mMyRecycleBin.getActiveView(this.mFirstVisiblePosition);
    }

    public GameObject getLastVisibleView() {
        return this.mMyRecycleBin.getActiveView(this.mLastVisiblePosition);
    }

    public int getVisibleChildCount() {
        return this.mMyRecycleBin.getCount();
    }

    public GameObject getSelectedView() {
        return this.mMyRecycleBin.getActiveView(this.mSelectedPosition);
    }

    protected boolean onKeyDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19:
                last();
                return true;
            case 20:
                next();
                return true;
            default:
                return super.onKeyDown(event);
        }
    }
}
