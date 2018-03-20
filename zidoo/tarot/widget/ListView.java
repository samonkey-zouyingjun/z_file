package zidoo.tarot.widget;

import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;
import zidoo.tarot.kernel.input.GestureEvent;

public class ListView extends AdaperView<Adapter> {
    final float SCROLL_A = 1024.0f;
    protected float mBottom;
    int mDownY;
    private DefaultGestureRecogniser mGestureRecogniser;
    private boolean mIsScrollBarMoved = false;
    float mLastMoveY;
    Rect mPadding;
    protected float mRowHeight = 0.0f;
    protected int mUpdateState = 0;

    public ListView(GLContext glContext) {
        super(glContext);
        init();
    }

    private void init() {
        this.mPadding = new Rect();
        this.mScroller = new Scroller(getContext(), new DecelerateInterpolator(), true);
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                ListView.this.mBottom = ((((float) ListView.this.mAdapter.getCount()) * ListView.this.mRowHeight) + ((float) ListView.this.mPadding.top)) + ((float) ListView.this.mPadding.bottom);
                if (ListView.this.mSelectedPosition > ListView.this.mAdapter.getCount() - 1) {
                    ListView.this.mSelectedPosition = ListView.this.mAdapter.getCount() - 1;
                }
                ListView.this.mRecycler.recycleAll();
                ListView.this.mUpdateState = 1;
                ListView.this.invalidate();
            }

            public void onInvalidated() {
            }
        };
    }

    private void initGestureDetector() {
        this.mGestureRecogniser = new DefaultGestureRecogniser(getContext()) {
            public boolean onSingleTapUp(MotionEvent e) {
                if (ListView.this.mIsScrollBarMoved) {
                    return true;
                }
                if (ListView.this.mOnItemClickListener != null && ListView.this.findFocus(e)) {
                    ListView.this.mOnItemClickListener.onItemClick(ListView.this, ListView.this.getSelectedView(), ListView.this.mSelectedPosition);
                    return true;
                } else if (ListView.this.mOnClickListener == null) {
                    return false;
                } else {
                    ListView.this.mOnClickListener.onClick(ListView.this);
                    return true;
                }
            }

            public void onShowPress(MotionEvent e) {
            }

            public boolean onScroll(MotionEvent e, GestureEvent last) {
                if (ListView.this.mBottom > ListView.this.mHeight) {
                    float ty;
                    if (ListView.this.mIsScrollBarMoved) {
                        float y = e.getY();
                        float cy = ListView.this.mScrollBar.getY();
                        float h = ListView.this.mScrollBar.getHeight();
                        if (y > ListView.this.mLastMoveY) {
                            ty = cy - (y - ListView.this.mLastMoveY);
                            if (ty - (h / 2.0f) < (-ListView.this.mHeight) / 2.0f) {
                                ty = ((-ListView.this.mHeight) / 2.0f) + (h / 2.0f);
                            }
                        } else {
                            ty = cy + (ListView.this.mLastMoveY - y);
                            if ((h / 2.0f) + ty > ListView.this.mHeight / 2.0f) {
                                ty = (ListView.this.mHeight / 2.0f) - (h / 2.0f);
                            }
                        }
                        ListView.this.mScrollBar.setY(ty);
                        ListView.this.mScroller.startScroll(0, ListView.this.mScroller.getCurrY(), 0, (int) (((ListView.this.mBottom * ((ListView.this.mHeight / 2.0f) - ((h / 2.0f) + ty))) / ListView.this.mHeight) - ((float) ListView.this.mScroller.getCurrY())), 500);
                        ListView.this.mLastMoveY = y;
                        ListView.this.mUpdateState = 4096;
                    } else {
                        ty = (((float) ListView.this.mScroller.getFinalY()) + last.getY()) - e.getY();
                        if (ty < 0.0f) {
                            ty = 0.0f;
                        } else if (ty > ListView.this.mBottom - ListView.this.mHeight) {
                            ty = ListView.this.mBottom - ListView.this.mHeight;
                        }
                        ListView.this.mScroller.setFinalY((int) ty);
                        ListView.this.mUpdateState = 4097;
                    }
                    ListView.this.invalidate();
                }
                return true;
            }

            public void onLongPress(MotionEvent e) {
                if (!ListView.this.mIsScrollBarMoved) {
                    if ((ListView.this.mOnItemLongClickListener == null || ListView.this.findFocus(e) || !ListView.this.mOnItemLongClickListener.onItemLongClick(ListView.this, ListView.this.getSelectedView(), ListView.this.mSelectedPosition)) && ListView.this.mOnLongClickListener != null) {
                        ListView.this.mOnLongClickListener.onLongClick(ListView.this);
                    }
                }
            }

            public boolean onFling(GestureEvent last, MotionEvent e, float velocityX, float velocityY) {
                if (ListView.this.mBottom > ListView.this.mHeight && !ListView.this.mIsScrollBarMoved) {
                    float dy = (float) ((int) ((velocityY * velocityY) / 1024.0f));
                    float cy = (float) ListView.this.mScroller.getFinalY();
                    if (velocityY > 0.0f) {
                        dy = cy < dy ? -cy : -dy;
                    } else if (cy + dy > ListView.this.mBottom - ListView.this.mHeight) {
                        dy = (ListView.this.mBottom - ListView.this.mHeight) - cy;
                    }
                    ListView.this.mScroller.startScroll(ListView.this.mScroller.getFinalX(), ListView.this.mScroller.getFinalY(), 0, (int) dy);
                    ListView.this.mUpdateState = 4096;
                    ListView.this.invalidate();
                }
                return true;
            }

            public boolean onDown(MotionEvent e) {
                if (ListView.this.mScrollBar == null || !ListView.this.isScrollTouched(e)) {
                    ListView.this.mDownY = ListView.this.mScroller.getCurrY();
                    ListView.this.findFocus(e);
                    ListView.this.onItemSelectedListener();
                } else {
                    ListView.this.mIsScrollBarMoved = true;
                    ListView.this.mLastMoveY = (float) ((int) e.getY());
                    ListView.this.mScrollBar.show();
                }
                return true;
            }
        };
        this.mGestureRecogniser.setIgnoreMoveDistance(15);
    }

    public void setAdapter(Adapter adapter) {
        resetList();
        if (this.mAdapter != adapter) {
            adapter.registerDataSetObserver(this.mDataSetObserver);
            this.mScroller.setFinalY(0);
            this.mSelectedPosition = 0;
            this.mAdapter = adapter;
            this.mBottom = ((((float) this.mAdapter.getCount()) * this.mRowHeight) + ((float) this.mPadding.top)) + ((float) this.mPadding.bottom);
            this.mUpdateState = 1;
            invalidate();
        }
    }

    public void setSelection(int position) {
        if (position >= 0 && position < getCount()) {
            this.mSelectedPosition = position;
            if (this.mBottom > this.mHeight) {
                int y = (int) (((float) position) * this.mRowHeight);
                Scroller scroller = this.mScroller;
                if (((float) y) + this.mHeight >= this.mBottom) {
                    y = (int) (this.mBottom - this.mHeight);
                }
                scroller.setFinalY(y);
                this.mUpdateState = 1;
            } else {
                this.mScroller.setFinalY(0);
            }
            invalidate();
        }
    }

    public void setPadding(int l, int t, int r, int b) {
        this.mPadding.set(l, t, r, b);
    }

    public void setRowHeight(float rowHeight) {
        this.mRowHeight = rowHeight;
    }

    public float getRowHeight() {
        return this.mRowHeight;
    }

    public void scrollToPosition(int position) {
        scrollToPosition(position, 500);
    }

    public void scrollToPosition(int position, int duration) {
        if (position >= 0 && position < getCount()) {
            this.mSelectedPosition = position;
            if (this.mBottom > this.mHeight) {
                int y = (int) (((float) position) * this.mRowHeight);
                int dy = ((float) y) + this.mHeight > this.mBottom ? (int) ((this.mBottom - this.mHeight) - ((float) this.mScroller.getCurrY())) : y - this.mScroller.getCurrY();
                if (((float) Math.abs(dy)) > this.mHeight) {
                    if (dy > 0) {
                        this.mScroller.setFinalY((this.mScroller.getCurrY() + dy) - ((int) this.mHeight));
                        dy = (int) this.mHeight;
                    } else {
                        this.mScroller.setFinalY((this.mScroller.getCurrY() + dy) + ((int) this.mHeight));
                        dy = (int) (-this.mHeight);
                    }
                    this.mScroller.computeScrollOffset();
                }
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy, duration);
                this.mUpdateState = 1;
            } else {
                this.mScroller.setFinalY(0);
            }
            invalidate();
        }
    }

    private void nextRow(boolean touch) {
        if (this.mSelectedPosition < this.mAdapter.getCount() - 1) {
            if (touch) {
                this.mSelectedPosition = getLastVisiblePosition();
            }
            this.mSelectedPosition = Math.min(this.mSelectedPosition + 1, this.mAdapter.getCount() - 1);
            if (this.mBottom > this.mHeight) {
                float top = (((float) this.mSelectedPosition) * this.mRowHeight) + ((float) this.mPadding.top);
                if (this.mRowHeight + top > (((float) this.mScroller.getCurrY()) + this.mHeight) - ((float) this.mPadding.bottom)) {
                    this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) ((((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY())) + ((float) this.mPadding.bottom)), 300);
                    this.mUpdateState = 1;
                }
            }
            invalidate();
        }
    }

    private void lastRow(boolean touch) {
        if (this.mSelectedPosition > 0) {
            if (touch) {
                this.mSelectedPosition = getFirstVisiblePosition();
            }
            this.mSelectedPosition = Math.max(this.mSelectedPosition - 1, 0);
            if (this.mBottom > this.mHeight) {
                float top = ((float) this.mSelectedPosition) * this.mRowHeight;
                if (top < ((float) this.mScroller.getCurrY())) {
                    int dy = ((int) top) - this.mScroller.getCurrY();
                    this.mUpdateState = 1;
                    this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy, 300);
                }
            }
            invalidate();
        }
    }

    protected void updateContent(GL10 gl) {
        int cy;
        float top;
        float bottom;
        int position;
        float y;
        float realY;
        GameObject item;
        if ((this.mUpdateState & 1) != 0) {
            this.mScroller.computeScrollOffset();
            int count = this.mAdapter == null ? 0 : this.mAdapter.getCount();
            if (count > 0) {
                cy = this.mScroller.getCurrY();
                top = (float) (this.mPadding.top + cy);
                bottom = (((float) cy) + this.mHeight) - ((float) this.mPadding.bottom);
                position = (int) (((float) cy) / this.mRowHeight);
                this.mFirstVisiblePosition = position;
                while (position < count) {
                    y = (((float) position) * this.mRowHeight) + ((float) this.mPadding.top);
                    if (this.mRowHeight + y >= top) {
                        if (y > bottom) {
                            break;
                        }
                        realY = ((((this.mHeight / 2.0f) + top) - ((float) this.mPadding.top)) - y) - (0.5f * this.mRowHeight);
                        item = this.mRecycler.obtainView(position);
                        item.setPositionPixel(0.0f, realY);
                        item.update(gl);
                    } else {
                        this.mFirstVisiblePosition++;
                    }
                    position++;
                }
                this.mLastVisiblePosition = position > this.mFirstVisiblePosition ? position - 1 : this.mFirstVisiblePosition;
                this.mRecycler.arrange();
            }
            if (this.mScroller.isFinished()) {
                this.mUpdateState = 0;
            }
            invalidate();
        } else if (this.mUpdateState == 4096) {
            this.mScroller.computeScrollOffset();
            if ((this.mAdapter == null ? 0 : this.mAdapter.getCount()) > 0) {
                cy = this.mScroller.getCurrY();
                top = (float) (this.mPadding.top + cy);
                bottom = (((float) cy) + this.mHeight) - ((float) this.mPadding.bottom);
                position = (int) (((float) cy) / this.mRowHeight);
                this.mFirstVisiblePosition = position;
                while (position < this.mAdapter.getCount()) {
                    if (position >= 0) {
                        y = (((float) position) * this.mRowHeight) + ((float) this.mPadding.top);
                        if (this.mRowHeight + y >= top) {
                            if (y > bottom) {
                                break;
                            }
                            realY = ((((this.mHeight / 2.0f) + top) - ((float) this.mPadding.top)) - y) - (0.5f * this.mRowHeight);
                            item = this.mRecycler.obtainView(position);
                            item.endAnimation();
                            item.setPositionPixel(0.0f, realY);
                            item.update(gl);
                        } else {
                            this.mFirstVisiblePosition++;
                        }
                    } else {
                        this.mFirstVisiblePosition++;
                    }
                    position++;
                }
                this.mLastVisiblePosition = position > this.mFirstVisiblePosition ? position - 1 : this.mFirstVisiblePosition;
                this.mRecycler.arrange();
                if (this.mSelector != null) {
                    this.mSelectedPosition = (int) (((((this.mHeight / 2.0f) - this.mSelector.getY()) + ((float) cy)) - ((float) this.mPadding.top)) / this.mRowHeight);
                    onItemSelectedListener();
                }
            }
            if (this.mScroller.isFinished()) {
                this.mUpdateState = 0;
            }
            invalidate();
        } else {
            GameObject[] children = this.mRecycler.getActiveViews();
            for (GameObject update : children) {
                update.update(gl);
            }
            onItemSelectedListener();
        }
    }

    protected void updateSelector(GL10 gl) {
        if (this.mSelector != null && !isTouchMode()) {
            if (this.mUpdateState == 4096) {
                this.mSelector.update(gl);
                return;
            }
            int currY;
            float cy = this.mSelector.Position.Y;
            float f = this.mHeight / 2.0f;
            if (this.mUpdateState == 1118209) {
                currY = this.mScroller.getCurrY();
            } else {
                currY = this.mScroller.getFinalY();
            }
            float ty = ((((((float) currY) + f) - (((float) this.mSelectedPosition) * this.mRowHeight)) - ((float) this.mPadding.top)) - (0.5f * this.mRowHeight)) * getDisplay().sHeightRatio;
            float h = (this.mHeight / 2.0f) * getDisplay().sHeightRatio;
            if (ty > cy) {
                cy += ((ty - cy) * 0.7f) + 0.001f;
                if (cy > h) {
                    this.mSelector.Position.Y = h;
                } else {
                    this.mSelector.Position.Y = cy < ty ? cy : ty;
                }
            } else {
                cy -= ((cy - ty) * 0.7f) + 0.001f;
                if (cy < (-h)) {
                    this.mSelector.Position.Y = -h;
                } else {
                    this.mSelector.Position.Y = cy > ty ? cy : ty;
                }
            }
            this.mSelector.update(gl);
            if (this.mSelector.Position.Y != ty) {
                invalidate();
            }
        }
    }

    private void onItemSelectedListener() {
        if (this.mOnItemSelectedListener == null) {
            return;
        }
        if (this.mAdapter.getCount() == 0) {
            this.mOnItemSelectedListener.onNothingSelected(this);
        } else {
            this.mOnItemSelectedListener.onItemSelected(this, getSelectedView(), this.mSelectedPosition);
        }
    }

    void resetList() {
        this.mRecycler.clear();
        this.mUpdateState = 0;
    }

    protected boolean onKeyDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19:
                if (this.mUpdateState != 1118209) {
                    lastRow(false);
                    return true;
                }
                break;
            case 20:
                if (this.mUpdateState != 1118209) {
                    nextRow(false);
                    return true;
                }
                break;
        }
        return super.onKeyDown(event);
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 8:
                float value = event.getAxisValue(9);
                if (value > 0.0f) {
                    lastRow(true);
                    return true;
                } else if (value < 0.0f) {
                    nextRow(true);
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean findFocus(MotionEvent event) {
        int position = (int) (((event.getY() / getDisplay().sRatioY) + ((float) this.mScroller.getCurrY())) / this.mRowHeight);
        if (position >= this.mAdapter.getCount()) {
            return false;
        }
        this.mSelectedPosition = position;
        invalidate();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mGestureRecogniser == null) {
            initGestureDetector();
        }
        if (event.getAction() == 1) {
            this.mIsScrollBarMoved = false;
        }
        return this.mGestureRecogniser.onTouch(event);
    }
}
