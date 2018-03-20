package zidoo.tarot.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v4.view.MotionEventCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.adapter.AdjustableAdapter;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Position;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;
import zidoo.tarot.kernel.input.GestureEvent;

@SuppressLint({"NewApi"})
@TargetApi(9)
public class XGridview extends AdjustableAdapterView {
    final float SCROLL_A = 1024.0f;
    private int mColumn = 6;
    private float mColumnSpacing = 0.0f;
    private float mColumnWidth = 280.0f;
    float mDownY;
    private DefaultGestureRecogniser mGestureRecogniser;
    float mLastMoveY;
    private float mRowHeight = 240.0f;
    private float mRowSpacing = 0.0f;

    public XGridview(GLContext glContext) {
        super(glContext);
        init();
    }

    private void init() {
        resetScroller();
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                XGridview.this.mDataChanged = true;
                XGridview.this.resetBottom();
                XGridview.this.mRecycler.recycleAll();
                XGridview.this.mUpdateState = 1;
                XGridview.this.mSelectedPosition = 0;
                XGridview.this.mScroller.setFinalY(0);
                XGridview.this.invalidate();
            }

            public void onInvalidated() {
            }
        };
        this.mGestureRecogniser = new DefaultGestureRecogniser(getContext()) {
            public boolean onSingleTapUp(MotionEvent e) {
                if (XGridview.this.mIsScrollBarMoved) {
                    return true;
                }
                if (XGridview.this.mOnItemClickListener != null && XGridview.this.findFocus(e)) {
                    XGridview.this.mOnItemClickListener.onItemClick(XGridview.this, XGridview.this.getSelectedView(), XGridview.this.mSelectedPosition);
                    return true;
                } else if (XGridview.this.mOnClickListener == null) {
                    return false;
                } else {
                    XGridview.this.mOnClickListener.onClick(XGridview.this);
                    return true;
                }
            }

            public void onShowPress(MotionEvent e) {
            }

            public boolean onScroll(MotionEvent e, GestureEvent last) {
                if (XGridview.this.mBottom > XGridview.this.mHeight) {
                    float ty;
                    if (XGridview.this.mIsScrollBarMoved) {
                        float y = e.getY();
                        float cy = XGridview.this.mScrollBar.getY();
                        float h = XGridview.this.mScrollBar.getHeight();
                        if (y > XGridview.this.mLastMoveY) {
                            ty = cy - (y - XGridview.this.mLastMoveY);
                            if (ty - (h / 2.0f) < (-XGridview.this.mHeight) / 2.0f) {
                                ty = ((-XGridview.this.mHeight) / 2.0f) + (h / 2.0f);
                            }
                        } else {
                            ty = cy + (XGridview.this.mLastMoveY - y);
                            if ((h / 2.0f) + ty > XGridview.this.mHeight / 2.0f) {
                                ty = (XGridview.this.mHeight / 2.0f) - (h / 2.0f);
                            }
                        }
                        XGridview.this.mScrollBar.setY(ty);
                        XGridview.this.mScroller.startScroll(0, XGridview.this.mScroller.getCurrY(), 0, (int) (((XGridview.this.mBottom * ((XGridview.this.mHeight / 2.0f) - ((h / 2.0f) + ty))) / XGridview.this.mHeight) - ((float) XGridview.this.mScroller.getCurrY())), 500);
                        XGridview.this.mLastMoveY = y;
                        XGridview.this.mUpdateState = 4096;
                    } else {
                        ty = (((float) XGridview.this.mScroller.getFinalY()) + last.getY()) - e.getY();
                        if (ty < 0.0f) {
                            ty = 0.0f;
                        } else if (ty > XGridview.this.mBottom - XGridview.this.mHeight) {
                            ty = XGridview.this.mBottom - XGridview.this.mHeight;
                        }
                        XGridview.this.mScroller.setFinalY((int) ty);
                        XGridview.this.mUpdateState = 4097;
                    }
                    XGridview.this.invalidate();
                }
                return true;
            }

            public void onLongPress(MotionEvent e) {
                if (!XGridview.this.mIsScrollBarMoved) {
                    if ((XGridview.this.mOnItemLongClickListener == null || XGridview.this.findFocus(e) || !XGridview.this.mOnItemLongClickListener.onItemLongClick(XGridview.this, XGridview.this.getSelectedView(), XGridview.this.mSelectedPosition)) && XGridview.this.mOnLongClickListener != null) {
                        XGridview.this.mOnLongClickListener.onLongClick(XGridview.this);
                    }
                }
            }

            public boolean onFling(GestureEvent last, MotionEvent e, float velocityX, float velocityY) {
                if (XGridview.this.mBottom > XGridview.this.mHeight && !XGridview.this.mIsScrollBarMoved) {
                    float dy = (float) ((int) ((velocityY * velocityY) / 1024.0f));
                    float cy = (float) XGridview.this.mScroller.getFinalY();
                    if (velocityY > 0.0f) {
                        dy = cy < dy ? -cy : -dy;
                    } else if (cy + dy > XGridview.this.mBottom - XGridview.this.mHeight) {
                        dy = (XGridview.this.mBottom - XGridview.this.mHeight) - cy;
                    }
                    XGridview.this.mScroller.startScroll(XGridview.this.mScroller.getFinalX(), XGridview.this.mScroller.getFinalY(), 0, (int) dy);
                    XGridview.this.mUpdateState = 4096;
                    XGridview.this.invalidate();
                }
                return true;
            }

            public boolean onDown(MotionEvent e) {
                if (XGridview.this.mScrollBar == null || !XGridview.this.isScrollTouched(e)) {
                    XGridview.this.findFocus(e);
                    XGridview.this.onItemSelectedListener();
                } else {
                    XGridview.this.mIsScrollBarMoved = true;
                    XGridview.this.mLastMoveY = (float) ((int) e.getY());
                    XGridview.this.mScrollBar.show();
                }
                return true;
            }
        };
        this.mGestureRecogniser.setIgnoreMoveDistance(15);
    }

    public void setAdapter(AdjustableAdapter adjustableAdapter) {
        resetList();
        if (this.mAdapter != adjustableAdapter) {
            if (this.mAdapter != null) {
                ((AdjustableAdapter) this.mAdapter).unregisterDataSetObserver(this.mDataSetObserver);
            }
            adjustableAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mScroller.setFinalY(0);
            this.mScroller.computeScrollOffset();
            this.mSelectedPosition = 0;
            this.mAdapter = adjustableAdapter;
            resetBottom();
            if (this.mSelector != null) {
                this.mSelector.setPositionPixel((this.mColumnWidth - this.mWidth) / 2.0f, (this.mHeight - this.mRowHeight) / 2.0f);
            }
            this.mUpdateState = 1;
            invalidate();
        }
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public float getColumnWidth() {
        return this.mColumnWidth;
    }

    public void setColumnWidth(float width) {
        this.mColumnWidth = width;
    }

    public float getRowHeight() {
        return this.mRowHeight;
    }

    public void setRowHeight(float height) {
        this.mRowHeight = height;
    }

    public float getColumnSpacing() {
        return this.mColumnSpacing;
    }

    public void setColumnSpacing(float spacing) {
        this.mColumnSpacing = spacing;
    }

    public float getRowspacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(float spacing) {
        this.mRowSpacing = spacing;
    }

    public int getColumn() {
        return this.mColumn;
    }

    public void setColumn(int column) {
        this.mColumn = column;
    }

    public void refreash() {
        this.mDataChanged = true;
        this.mUpdateState = 257;
        this.mBottom = (((float) ((((AdjustableAdapter) this.mAdapter).getCount() - 1) / this.mColumn)) * (this.mRowHeight + this.mRowSpacing)) + this.mRowHeight;
        if (this.mScrollBar != null) {
            this.mScrollBar.setHeight(this.mBottom < this.mHeight ? this.mHeight : (this.mHeight * this.mHeight) / this.mBottom);
            this.mScrollBar.setY(((this.mHeight - this.mScrollBar.getHeight()) / 2.0f) - ((this.mHeight * ((float) this.mScroller.getCurrY())) / this.mBottom));
            this.mScrollBar.reset();
        }
        invalidate();
    }

    public void setSelection(int position) {
        if (position >= 0 && position < getCount()) {
            this.mSelectedPosition = position;
            if (this.mBottom > this.mHeight) {
                int y = (int) (((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing));
                int fy = ((float) y) + this.mHeight < this.mBottom ? y : (int) (this.mBottom - this.mHeight);
                this.mScroller.setFinalY(fy);
                this.mScrollBar.setY(((this.mHeight - this.mScrollBar.getHeight()) / 2.0f) - ((this.mHeight * ((float) fy)) / this.mBottom));
                this.mUpdateState = 1;
            } else {
                this.mScroller.setFinalY(0);
            }
            invalidate();
        }
    }

    public void scrollToPosition(int position, int duration) {
        if (position >= 0 && position < getCount()) {
            this.mSelectedPosition = position;
            if (this.mBottom > this.mHeight) {
                int y = (int) (((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing));
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
                this.mUpdateState = 4097;
            } else {
                this.mScroller.setFinalY(0);
                this.mUpdateState = 1;
            }
            invalidate();
        }
    }

    public GameObject getSelectedView() {
        return this.mRecycler.getActiveView(this.mSelectedPosition);
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }

    public int getCount() {
        return this.mAdapter == null ? 0 : ((AdjustableAdapter) this.mAdapter).getCount();
    }

    public int getVisibleChildCount() {
        return (this.mLastVisiblePosition - this.mFirstVisiblePosition) + 1;
    }

    void resetBottom() {
        this.mBottom = (((float) ((((AdjustableAdapter) this.mAdapter).getCount() - 1) / this.mColumn)) * (this.mRowHeight + this.mRowSpacing)) + this.mRowHeight;
        if (this.mScrollBar != null) {
            this.mScrollBar.setHeight(this.mBottom < this.mHeight ? this.mHeight : (this.mHeight * this.mHeight) / this.mBottom);
            this.mScrollBar.setY((this.mHeight - this.mScrollBar.getHeight()) / 2.0f);
            this.mScrollBar.reset();
        }
    }

    private void next() {
        if (this.mSelectedPosition < ((AdjustableAdapter) this.mAdapter).getCount() - 1) {
            this.mSelectedPosition++;
            if (this.mSelectedPosition % this.mColumn == 0) {
                float top = ((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
                if (this.mRowHeight + top > ((float) this.mScroller.getCurrY()) + this.mHeight) {
                    if (this.mPageturnEnable) {
                        int currY;
                        Scroller scroller = this.mScroller;
                        int currX = this.mScroller.getCurrX();
                        int currY2 = this.mScroller.getCurrY();
                        if (this.mHeight + top > this.mBottom) {
                            currY = (int) ((this.mBottom - this.mHeight) - ((float) this.mScroller.getCurrY()));
                        } else {
                            currY = ((int) top) - this.mScroller.getCurrY();
                        }
                        scroller.startScroll(currX, currY2, 0, currY);
                        this.mUpdateState = 1118209;
                    } else {
                        this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY())));
                        this.mUpdateState = 4097;
                    }
                }
            }
            onItemSelectedListener();
            invalidate();
        }
    }

    private void last() {
        if (this.mSelectedPosition > 0) {
            this.mSelectedPosition--;
            if (this.mSelectedPosition % this.mColumn == this.mColumn - 1) {
                float top = ((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
                if (top < ((float) this.mScroller.getCurrY())) {
                    int dy;
                    if (this.mPageturnEnable) {
                        dy = (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY()));
                        if (this.mScroller.getCurrY() < (-dy)) {
                            dy = 0 - this.mScroller.getCurrY();
                        }
                        this.mUpdateState = 1118209;
                    } else {
                        dy = ((int) top) - this.mScroller.getCurrY();
                        this.mUpdateState = 4097;
                    }
                    this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy);
                }
            }
            onItemSelectedListener();
            invalidate();
        }
    }

    private void nextRow(boolean touch) {
        if (this.mSelectedPosition / this.mColumn < (((AdjustableAdapter) this.mAdapter).getCount() - 1) / this.mColumn) {
            if (touch) {
                this.mSelectedPosition = getLastVisiblePosition();
            }
            this.mSelectedPosition = Math.min(this.mColumn + this.mSelectedPosition, ((AdjustableAdapter) this.mAdapter).getCount() - 1);
            float top = ((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
            if (this.mRowHeight + top > ((float) this.mScroller.getCurrY()) + this.mHeight) {
                if (!this.mPageturnEnable || touch) {
                    this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY())));
                    this.mUpdateState = 4097;
                } else {
                    int currY;
                    Scroller scroller = this.mScroller;
                    int currX = this.mScroller.getCurrX();
                    int currY2 = this.mScroller.getCurrY();
                    if (this.mHeight + top > this.mBottom) {
                        currY = (int) ((this.mBottom - this.mHeight) - ((float) this.mScroller.getCurrY()));
                    } else {
                        currY = ((int) top) - this.mScroller.getCurrY();
                    }
                    scroller.startScroll(currX, currY2, 0, currY);
                    this.mUpdateState = 1118209;
                }
            }
            onItemSelectedListener();
            invalidate();
        }
    }

    private void lastRow(boolean touch) {
        if (this.mSelectedPosition >= this.mColumn) {
            if (touch) {
                this.mSelectedPosition = getFirstVisiblePosition();
            }
            this.mSelectedPosition = Math.max(this.mSelectedPosition - this.mColumn, 0);
            float top = ((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
            if (top < ((float) this.mScroller.getCurrY())) {
                int dy;
                if (!this.mPageturnEnable || touch) {
                    dy = ((int) top) - this.mScroller.getCurrY();
                    this.mUpdateState = 4097;
                } else {
                    dy = (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY()));
                    if (this.mScroller.getCurrY() < (-dy)) {
                        dy = 0 - this.mScroller.getCurrY();
                    }
                    this.mUpdateState = 1118209;
                }
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy);
            }
            onItemSelectedListener();
            invalidate();
        }
    }

    public void pageUp() {
        if (this.mScroller.getCurrY() > 0) {
            if (this.mSelectedPosition >= this.mColumn * 3) {
                this.mSelectedPosition -= this.mColumn * 3;
            } else {
                this.mSelectedPosition %= this.mColumn;
            }
            if (((float) this.mScroller.getCurrY()) < this.mHeight) {
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, -this.mScroller.getCurrY());
            } else {
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) (-this.mHeight));
            }
            this.mUpdateState = 69633;
            invalidate();
        }
    }

    public void pageDown() {
        if (((float) this.mScroller.getCurrY()) < this.mBottom - this.mHeight) {
            int lastPosition = ((AdjustableAdapter) this.mAdapter).getCount() - 1;
            if (this.mSelectedPosition <= lastPosition - (this.mColumn * 3)) {
                this.mSelectedPosition += this.mColumn * 3;
            } else {
                this.mSelectedPosition = ((lastPosition / this.mColumn) * this.mColumn) + (this.mSelectedPosition % this.mColumn);
                if (this.mSelectedPosition > lastPosition) {
                    this.mSelectedPosition = lastPosition;
                }
            }
            if (((float) this.mScroller.getCurrY()) > (this.mBottom - this.mHeight) - this.mHeight) {
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) ((this.mBottom - this.mHeight) - ((float) this.mScroller.getCurrY())));
            } else {
                this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) this.mHeight);
            }
            this.mUpdateState = 69633;
            invalidate();
        }
    }

    protected void updateContent(GL10 gl) {
        float top;
        float bottom;
        int position;
        float y;
        float x;
        float realY;
        GameObject item;
        int i;
        if ((this.mUpdateState & 1) != 0) {
            this.mScroller.computeScrollOffset();
            if (((AdjustableAdapter) this.mAdapter).getCount() > 0 && !((AdjustableAdapter) this.mAdapter).isLoading()) {
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = ((int) (top / (this.mRowHeight + this.mRowSpacing))) * this.mColumn;
                this.mFirstVisiblePosition = position;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    y = ((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
                    if (this.mRowHeight + y >= top) {
                        if (y > bottom) {
                            break;
                        }
                        x = (((-this.mWidth) / 2.0f) + (((float) (position % this.mColumn)) * (this.mColumnWidth + this.mColumnSpacing))) + (0.5f * this.mColumnWidth);
                        realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
                        item = this.mRecycler.obtainView(position);
                        item.setPositionPixel(x, realY);
                        item.update(gl);
                    } else {
                        this.mFirstVisiblePosition++;
                    }
                    position++;
                }
                if (position > this.mFirstVisiblePosition) {
                    i = position - 1;
                } else {
                    i = this.mFirstVisiblePosition;
                }
                this.mLastVisiblePosition = i;
                this.mRecycler.arrange();
            }
            if (this.mScroller.isFinished()) {
                if (this.mUpdateState == 4097) {
                    adjustSelectPosition();
                }
                onItemSelectedListener();
                this.mUpdateState = 0;
            } else {
                if (this.mDataChanged) {
                    onItemSelectedListener();
                }
                invalidate();
            }
            this.mDataChanged = false;
        } else if (this.mUpdateState == 257) {
            children = this.mRecycler.getActiveViews();
            if (this.mLastVisiblePosition <= ((AdjustableAdapter) this.mAdapter).getCount()) {
                this.mRecycler.dataSetChange();
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = children.length == 0 ? 0 : this.mLastVisiblePosition + 1;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    y = ((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
                    if (y > bottom) {
                        break;
                    }
                    x = (((-this.mWidth) / 2.0f) + (((float) (position % this.mColumn)) * (this.mColumnWidth + this.mColumnSpacing))) + (0.5f * this.mColumnWidth);
                    realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
                    item = this.mRecycler.getRecycleView(position);
                    item.setPositionPixel(x, realY);
                    this.mAnimator.setAddAnimation(item);
                    position++;
                }
                this.mLastVisiblePosition = position - 1;
                this.mRecycler.addToActive();
                if (this.mSelectedPosition >= ((AdjustableAdapter) this.mAdapter).getCount()) {
                    this.mSelectedPosition = ((AdjustableAdapter) this.mAdapter).getCount() == 0 ? 0 : ((AdjustableAdapter) this.mAdapter).getCount() - 1;
                }
                onItemSelectedListener();
                this.mUpdateState = 0;
            } else {
                if (this.mFirstVisiblePosition > ((AdjustableAdapter) this.mAdapter).getCount()) {
                    this.mRecycler.recycleAll();
                    this.mScroller.setFinalY((int) ((((float) ((((AdjustableAdapter) this.mAdapter).getCount() - 1) / this.mColumn)) * (this.mRowHeight + this.mRowSpacing)) + this.mRowHeight));
                } else if (children.length > 0) {
                    int length = ((AdjustableAdapter) this.mAdapter).getCount() - this.mFirstVisiblePosition;
                    newChildren = new GameObject[length];
                    System.arraycopy(children, 0, newChildren, 0, length);
                    this.mRecycler.fillViewsInActivedViews(newChildren);
                }
                onItemSelectedListener();
                scrollToPosition(this.mSelectedPosition, 200);
            }
            for (GameObject update : children) {
                update.update(gl);
            }
            invalidate();
        } else if ((this.mUpdateState & 16) == 16) {
            children = this.mRecycler.getActiveViews();
            for (i = 0; i < children.length; i++) {
                children[i] = ((AdjustableAdapter) this.mAdapter).getView(this.mFirstVisiblePosition + i, children[i], null);
                children[i].update(gl);
            }
            for (i = 0; i < this.mBeRemovedViews.size(); i++) {
                ((GameObject) this.mBeRemovedViews.get(i)).update(gl);
            }
            if (this.mBeMoveOutViews != null && this.mBeMoveOutViews.size() > 0) {
                for (i = 0; i < this.mBeMoveOutViews.size(); i++) {
                    ((GameObject) this.mBeMoveOutViews.get(i)).update(gl);
                }
                if (!((GameObject) this.mBeMoveOutViews.get(this.mBeMoveOutViews.size() - 1)).isAnimating()) {
                    this.mBeMoveOutViews.clear();
                }
            }
            if (this.mUpdateState == 65552) {
                if (children.length < getVisibleChildCount()) {
                    position = this.mFirstVisiblePosition + children.length;
                    if (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                        x = (((-this.mWidth) / 2.0f) + (((float) (position % this.mColumn)) * (this.mColumnWidth + this.mColumnSpacing))) + (0.5f * this.mColumnWidth);
                        realY = (((this.mHeight / 2.0f) + ((float) this.mScroller.getCurrY())) - (((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing))) - (0.5f * this.mRowHeight);
                        item = this.mRecycler.getScrapView(position);
                        item.setPositionPixel(x, realY);
                        this.mAnimator.setAddAnimation(item);
                        item.update(gl);
                        newChildren = new GameObject[(children.length + 1)];
                        System.arraycopy(children, 0, newChildren, 0, children.length);
                        newChildren[newChildren.length - 1] = item;
                        this.mRecycler.fillViewsInActivedViews(newChildren);
                    } else {
                        this.mUpdateState = 16;
                    }
                } else {
                    this.mUpdateState = 16;
                }
            }
            boolean finishRemove = this.mBeRemovedViews.size() == 0 || !((GameObject) this.mBeRemovedViews.get(this.mBeRemovedViews.size() - 1)).isAnimating();
            if (finishRemove) {
                this.mBeRemovedViews.clear();
            }
            if (finishRemove && ((this.mBeMoveOutViews == null || this.mBeMoveOutViews.size() == 0) && this.mUpdateState == 16)) {
                if (this.mSelectedPosition > ((AdjustableAdapter) this.mAdapter).getCount() - 1) {
                    if (((AdjustableAdapter) this.mAdapter).getCount() == 0) {
                        i = 0;
                    } else {
                        i = ((AdjustableAdapter) this.mAdapter).getCount() - 1;
                    }
                    this.mSelectedPosition = i;
                }
                onItemSelectedListener();
                resetBottom();
                if (this.mBottom < this.mHeight) {
                    if (this.mScroller.getCurrY() > 0) {
                        this.mScroller.setFinalY((int) this.mBottom);
                        this.mScroller.computeScrollOffset();
                        this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, -this.mScroller.getCurrY());
                        this.mUpdateState = 1;
                    } else {
                        this.mUpdateState = 0;
                    }
                } else if (((float) this.mScroller.getCurrY()) + this.mHeight > this.mBottom) {
                    this.mScroller.setFinalY((int) this.mBottom);
                    this.mScroller.computeScrollOffset();
                    this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) (-this.mHeight));
                    this.mUpdateState = 1;
                } else {
                    this.mUpdateState = 0;
                }
            }
            invalidate();
        } else if (this.mUpdateState == 4096) {
            this.mScroller.computeScrollOffset();
            int count = this.mAdapter == null ? 0 : ((AdjustableAdapter) this.mAdapter).getCount();
            if (count > 0) {
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = ((int) (top / (this.mRowHeight + this.mRowSpacing))) * this.mColumn;
                this.mFirstVisiblePosition = position;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    if (position >= 0) {
                        y = ((float) (position / this.mColumn)) * (this.mRowHeight + this.mRowSpacing);
                        if (this.mRowHeight + y >= top) {
                            if (y > bottom) {
                                break;
                            }
                            x = (((-this.mWidth) / 2.0f) + (((float) (position % this.mColumn)) * (this.mColumnWidth + this.mColumnSpacing))) + (0.5f * this.mColumnWidth);
                            realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
                            item = this.mRecycler.obtainView(position);
                            item.endAnimation();
                            item.setPositionPixel(x, realY);
                            item.update(gl);
                        } else {
                            this.mFirstVisiblePosition++;
                        }
                    } else {
                        this.mFirstVisiblePosition++;
                    }
                    position++;
                }
                if (position > this.mFirstVisiblePosition) {
                    i = position - 1;
                } else {
                    i = this.mFirstVisiblePosition;
                }
                this.mLastVisiblePosition = i;
                this.mRecycler.arrange();
                if (this.mSelector != null) {
                    this.mSelectedPosition = (((int) ((((this.mHeight / 2.0f) + top) - this.mSelector.getY()) / (this.mRowHeight + this.mRowSpacing))) * this.mColumn) + (this.mSelectedPosition % this.mColumn);
                    if (this.mSelectedPosition > count - 1) {
                        this.mSelectedPosition = count - 1;
                    }
                    onItemSelectedListener();
                }
            }
            if (this.mScroller.isFinished()) {
                this.mUpdateState = 0;
            }
            invalidate();
        } else {
            children = this.mRecycler.getActiveViews();
            for (GameObject update2 : children) {
                update2.update(gl);
            }
        }
    }

    protected void updateSelector(GL10 gl) {
        if (this.mSelector == null) {
            return;
        }
        if (!isShowSelector() && (!isFocused() || this.mAdapter == null || ((AdjustableAdapter) this.mAdapter).getCount() <= 0)) {
            return;
        }
        if (this.mUpdateState == 4096) {
            this.mSelector.update(gl);
            return;
        }
        int currY;
        float cx = this.mSelector.Position.X;
        float tx = ((((-this.mWidth) / 2.0f) + (((float) (this.mSelectedPosition % this.mColumn)) * (this.mColumnWidth + this.mColumnSpacing))) + (0.5f * this.mColumnWidth)) * getDisplay().sWidthRatio;
        if (tx > cx) {
            float f;
            cx += ((tx - cx) * 0.7f) + 0.001f;
            Position position = this.mSelector.Position;
            if (cx < tx) {
                f = cx;
            } else {
                f = tx;
            }
            position.X = f;
        } else {
            cx -= ((cx - tx) * 0.7f) + 0.001f;
            this.mSelector.Position.X = cx > tx ? cx : tx;
        }
        float cy = this.mSelector.Position.Y;
        float f2 = this.mHeight / 2.0f;
        if (this.mUpdateState == 1118209) {
            currY = this.mScroller.getCurrY();
        } else {
            currY = this.mScroller.getFinalY();
        }
        float ty = (((((float) currY) + f2) - (((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing))) - (0.5f * this.mRowHeight)) * getDisplay().sHeightRatio;
        float h = ((this.mHeight / 2.0f) - (0.5f * this.mRowHeight)) * getDisplay().sHeightRatio;
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
        if (this.mUpdateState == 1118209) {
            float border = ((this.mHeight - this.mRowHeight) / 2.0f) * getDisplay().sHeightRatio;
            if (this.mSelector.Position.Y > border) {
                this.mSelector.Position.Y = border;
            } else if (this.mSelector.Position.Y < (-border)) {
                this.mSelector.Position.Y = -border;
            }
        }
        this.mSelector.update(gl);
        if (this.mSelector.Position.X != tx || this.mSelector.Position.Y != ty) {
            invalidate();
        }
    }

    private void resetList() {
        this.mRecycler.clear();
        this.mLastVisiblePosition = 0;
        this.mFirstVisiblePosition = 0;
        this.mUpdateState = 0;
    }

    private void adjustSelectPosition() {
        float top = (float) this.mScroller.getCurrY();
        this.mSelectedPosition = (((int) ((((this.mHeight / 2.0f) + top) - this.mSelector.getY()) / (this.mRowHeight + this.mRowSpacing))) * this.mColumn) + (this.mSelectedPosition % this.mColumn);
        if (this.mSelectedPosition < this.mFirstVisiblePosition) {
            this.mSelectedPosition = this.mFirstVisiblePosition;
        } else if (this.mSelectedPosition > this.mLastVisiblePosition) {
            this.mSelectedPosition = this.mLastVisiblePosition;
        }
        while (true) {
            if (this.mSelectedPosition >= this.mFirstVisiblePosition && ((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing) >= top) {
                break;
            }
            this.mSelectedPosition += this.mColumn;
        }
        while (true) {
            if (this.mSelectedPosition > this.mLastVisiblePosition || (((float) (this.mSelectedPosition / this.mColumn)) * (this.mRowHeight + this.mRowSpacing)) + this.mRowHeight > this.mHeight + top) {
                this.mSelectedPosition -= this.mColumn;
            } else {
                return;
            }
        }
    }

    private void onItemSelectedListener() {
        if (this.mOnItemSelectedListener == null) {
            return;
        }
        if (((AdjustableAdapter) this.mAdapter).getCount() == 0) {
            this.mOnItemSelectedListener.onNothingSelected(this);
        } else {
            this.mOnItemSelectedListener.onItemSelected(this, getSelectedView(), this.mSelectedPosition);
        }
    }

    protected boolean onKeyDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19:
                if ((this.mUpdateState & 69633) != 69633) {
                    lastRow(false);
                    return true;
                }
                break;
            case 20:
                if ((this.mUpdateState & 69633) != 69633) {
                    nextRow(false);
                    return true;
                }
                break;
            case MotionEventCompat.AXIS_WHEEL /*21*/:
                if ((this.mUpdateState & 69633) != 69633) {
                    last();
                    return true;
                }
                break;
            case MotionEventCompat.AXIS_GAS /*22*/:
                if ((this.mUpdateState & 69633) != 69633) {
                    next();
                    return true;
                }
                break;
        }
        return super.onKeyDown(event);
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (isFocused()) {
            switch (event.getAction()) {
                case 7:
                    if (this.mScrollBar != null && event.getX() > this.mWidth - 25.0f) {
                        this.mScrollBar.show();
                        invalidate();
                        return true;
                    }
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
        }
        return false;
    }

    private boolean findFocus(MotionEvent event) {
        float x = event.getX() / getDisplay().sRatioX;
        float y = (event.getY() / getDisplay().sRatioY) + ((float) this.mScroller.getCurrY());
        int column = (int) (x / (this.mColumnWidth + this.mColumnSpacing));
        int row = (int) (y / (this.mRowHeight + this.mRowSpacing));
        if ((((float) column) * (this.mColumnWidth + this.mColumnSpacing)) + this.mColumnWidth >= x && (((float) row) * (this.mRowHeight + this.mRowSpacing)) + this.mRowHeight > y) {
            int position = (this.mColumn * row) + column;
            if (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                this.mSelectedPosition = position;
                invalidate();
                return true;
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1) {
            this.mIsScrollBarMoved = false;
        }
        return this.mGestureRecogniser.onTouch(event);
    }
}
