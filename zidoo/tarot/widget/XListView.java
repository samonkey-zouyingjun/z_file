package zidoo.tarot.widget;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.adapter.AdjustableAdapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;
import zidoo.tarot.kernel.input.GestureEvent;

public class XListView extends AdjustableAdapterView {
    final float SCROLL_A = 1024.0f;
    private DefaultGestureRecogniser mGestureRecogniser;
    float mLastMoveY;
    private float mRowHeight = 20.0f;

    public XListView(GLContext glContext) {
        super(glContext);
        init();
    }

    private void init() {
        resetScroller();
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                XListView.this.mDataChanged = true;
                XListView.this.resetBottom();
                XListView.this.mRecycler.recycleAll();
                XListView.this.mUpdateState = 1;
                XListView.this.mScroller.setFinalY(0);
                XListView.this.mSelectedPosition = 0;
                XListView.this.invalidate();
            }

            public void onInvalidated() {
            }
        };
        this.mGestureRecogniser = new DefaultGestureRecogniser(getContext()) {
            public boolean onSingleTapUp(MotionEvent e) {
                if (XListView.this.mIsScrollBarMoved) {
                    return true;
                }
                if (XListView.this.mOnItemClickListener != null && XListView.this.findFocus(e)) {
                    XListView.this.mOnItemClickListener.onItemClick(XListView.this, XListView.this.getSelectedView(), XListView.this.mSelectedPosition);
                    return true;
                } else if (XListView.this.mOnClickListener == null) {
                    return false;
                } else {
                    XListView.this.mOnClickListener.onClick(XListView.this);
                    return true;
                }
            }

            public void onShowPress(MotionEvent e) {
            }

            public boolean onScroll(MotionEvent e, GestureEvent last) {
                if (XListView.this.mBottom > XListView.this.mHeight) {
                    float ty;
                    if (XListView.this.mIsScrollBarMoved) {
                        float y = e.getY();
                        float cy = XListView.this.mScrollBar.getY();
                        float h = XListView.this.mScrollBar.getHeight();
                        if (y > XListView.this.mLastMoveY) {
                            ty = cy - (y - XListView.this.mLastMoveY);
                            if (ty - (h / 2.0f) < (-XListView.this.mHeight) / 2.0f) {
                                ty = ((-XListView.this.mHeight) / 2.0f) + (h / 2.0f);
                            }
                        } else {
                            ty = cy + (XListView.this.mLastMoveY - y);
                            if ((h / 2.0f) + ty > XListView.this.mHeight / 2.0f) {
                                ty = (XListView.this.mHeight / 2.0f) - (h / 2.0f);
                            }
                        }
                        XListView.this.mScrollBar.setY(ty);
                        XListView.this.mScroller.startScroll(0, XListView.this.mScroller.getCurrY(), 0, (int) (((XListView.this.mBottom * ((XListView.this.mHeight / 2.0f) - ((h / 2.0f) + ty))) / XListView.this.mHeight) - ((float) XListView.this.mScroller.getCurrY())), 500);
                        XListView.this.mLastMoveY = y;
                        XListView.this.mUpdateState = 4096;
                    } else {
                        ty = (((float) XListView.this.mScroller.getFinalY()) + last.getY()) - e.getY();
                        if (ty < 0.0f) {
                            ty = 0.0f;
                        } else if (ty > XListView.this.mBottom - XListView.this.mHeight) {
                            ty = XListView.this.mBottom - XListView.this.mHeight;
                        }
                        XListView.this.mScroller.setFinalY((int) ty);
                        XListView.this.mUpdateState = 4097;
                    }
                    XListView.this.invalidate();
                }
                return true;
            }

            public void onLongPress(MotionEvent e) {
                if (!XListView.this.mIsScrollBarMoved) {
                    if ((XListView.this.mOnItemLongClickListener == null || XListView.this.findFocus(e) || !XListView.this.mOnItemLongClickListener.onItemLongClick(XListView.this, XListView.this.getSelectedView(), XListView.this.mSelectedPosition)) && XListView.this.mOnLongClickListener != null) {
                        XListView.this.mOnLongClickListener.onLongClick(XListView.this);
                    }
                }
            }

            public boolean onFling(GestureEvent last, MotionEvent e, float velocityX, float velocityY) {
                if (XListView.this.mBottom > XListView.this.mHeight && !XListView.this.mIsScrollBarMoved) {
                    float dy = (float) ((int) ((velocityY * velocityY) / 1024.0f));
                    float cy = (float) XListView.this.mScroller.getFinalY();
                    if (velocityY > 0.0f) {
                        dy = cy < dy ? -cy : -dy;
                    } else if (cy + dy > XListView.this.mBottom - XListView.this.mHeight) {
                        dy = (XListView.this.mBottom - XListView.this.mHeight) - cy;
                    }
                    XListView.this.mScroller.startScroll(XListView.this.mScroller.getFinalX(), XListView.this.mScroller.getFinalY(), 0, (int) dy);
                    XListView.this.mUpdateState = 4096;
                    XListView.this.invalidate();
                }
                return true;
            }

            public boolean onDown(MotionEvent e) {
                if (XListView.this.mScrollBar == null || !XListView.this.isScrollTouched(e)) {
                    XListView.this.findFocus(e);
                    XListView.this.onItemSelectedListener();
                } else {
                    XListView.this.mIsScrollBarMoved = true;
                    XListView.this.mLastMoveY = (float) ((int) e.getY());
                    XListView.this.mScrollBar.show();
                }
                return true;
            }
        };
        this.mGestureRecogniser.setIgnoreMoveDistance(15);
    }

    public void setAdapter(AdjustableAdapter adapter) {
        resetList();
        if (this.mAdapter != adapter) {
            if (this.mAdapter != null) {
                ((AdjustableAdapter) this.mAdapter).unregisterDataSetObserver(this.mDataSetObserver);
            }
            adapter.registerDataSetObserver(this.mDataSetObserver);
            this.mScroller.setFinalY(0);
            this.mScroller.computeScrollOffset();
            this.mSelectedPosition = 0;
            this.mAdapter = adapter;
            resetBottom();
            if (this.mSelector != null) {
                this.mSelector.setY((this.mHeight - this.mRowHeight) / 2.0f);
            }
            this.mUpdateState = 1;
            invalidate();
        }
    }

    public void setSelection(int position) {
        if (position >= 0 && position < getCount()) {
            this.mSelectedPosition = position;
            if (this.mBottom > this.mHeight) {
                int y = (int) (((float) position) * this.mRowHeight);
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
                this.mUpdateState = 4097;
            } else {
                this.mScroller.setFinalY(0);
                this.mUpdateState = 1;
            }
            invalidate();
        }
    }

    public void setRowHeigth(float height) {
        this.mRowHeight = height;
    }

    private void nextRow(boolean touch) {
        if (this.mSelectedPosition < ((AdjustableAdapter) this.mAdapter).getCount() - 1) {
            if (touch) {
                this.mSelectedPosition = getLastVisiblePosition();
            }
            this.mSelectedPosition = Math.min(this.mSelectedPosition + 1, ((AdjustableAdapter) this.mAdapter).getCount() - 1);
            if (this.mBottom > this.mHeight) {
                float top = ((float) this.mSelectedPosition) * this.mRowHeight;
                if (this.mRowHeight + top > ((float) this.mScroller.getCurrY()) + this.mHeight) {
                    if (!this.mPageturnEnable || touch) {
                        this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY())), 250);
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
                        scroller.startScroll(currX, currY2, 0, currY, 150);
                        this.mUpdateState = 1118209;
                    }
                }
            }
            onItemSelectedListener();
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
                    int dy;
                    if (!this.mPageturnEnable || touch) {
                        dy = ((int) top) - this.mScroller.getCurrY();
                        this.mUpdateState = 4097;
                        this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy, 250);
                    } else {
                        dy = (int) (((this.mRowHeight + top) - this.mHeight) - ((float) this.mScroller.getCurrY()));
                        if (this.mScroller.getCurrY() < (-dy)) {
                            dy = 0 - this.mScroller.getCurrY();
                        }
                        this.mUpdateState = 1118209;
                        this.mScroller.startScroll(this.mScroller.getCurrX(), this.mScroller.getCurrY(), 0, dy, 150);
                    }
                }
            }
            onItemSelectedListener();
            invalidate();
        }
    }

    public void pageUp() {
        int cy = this.mScroller.getCurrY();
        if (cy > 0) {
            if (((float) cy) < this.mHeight) {
                this.mScroller.startScroll(this.mScroller.getCurrX(), cy, 0, -cy);
                this.mSelectedPosition = 0;
            } else {
                this.mSelectedPosition = (int) (((float) this.mSelectedPosition) - (this.mHeight / this.mRowHeight));
                this.mScroller.startScroll(this.mScroller.getCurrX(), cy, 0, (int) (-this.mHeight));
            }
            this.mUpdateState = 69633;
            invalidate();
        }
    }

    public void pageDown() {
        int cy = this.mScroller.getCurrY();
        if (((float) cy) < this.mBottom - this.mHeight) {
            if (((float) cy) + this.mHeight > this.mBottom - this.mHeight) {
                this.mScroller.startScroll(this.mScroller.getCurrX(), cy, 0, (int) ((this.mBottom - this.mHeight) - ((float) cy)));
                this.mSelectedPosition = ((AdjustableAdapter) this.mAdapter).getCount() - 1;
            } else {
                this.mSelectedPosition = (int) (((float) this.mSelectedPosition) + (this.mHeight / this.mRowHeight));
                this.mScroller.startScroll(this.mScroller.getCurrX(), cy, 0, (int) this.mHeight);
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
        float realY;
        GameObject item;
        int i;
        if ((this.mUpdateState & 1) != 0) {
            this.mScroller.computeScrollOffset();
            if ((this.mAdapter == null ? 0 : ((AdjustableAdapter) this.mAdapter).getCount()) > 0 && !((AdjustableAdapter) this.mAdapter).isLoading()) {
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = (int) (top / this.mRowHeight);
                this.mFirstVisiblePosition = position;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    y = ((float) position) * this.mRowHeight;
                    if (this.mRowHeight + y >= top) {
                        if (y > bottom) {
                            break;
                        }
                        realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
                        item = this.mRecycler.obtainView(position);
                        item.setY(realY);
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
                if (this.mUpdateState == 4097 || this.mUpdateState == 69633) {
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
            int count = ((AdjustableAdapter) this.mAdapter).getCount();
            if (this.mLastVisiblePosition <= count) {
                this.mRecycler.dataSetChange();
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = children.length == 0 ? 0 : this.mLastVisiblePosition + 1;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    y = ((float) position) * this.mRowHeight;
                    if (y > bottom) {
                        break;
                    }
                    realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
                    item = this.mRecycler.getRecycleView(position);
                    item.setPositionPixel(0.0f, realY);
                    this.mAnimator.setAddAnimation(item);
                    position++;
                }
                this.mLastVisiblePosition = position - 1;
                this.mRecycler.addToActive();
                onItemSelectedListener();
                this.mUpdateState = 0;
            } else {
                if (this.mFirstVisiblePosition > ((AdjustableAdapter) this.mAdapter).getCount()) {
                    this.mRecycler.recycleAll();
                    this.mScroller.setFinalY((int) (((float) ((AdjustableAdapter) this.mAdapter).getCount()) * this.mRowHeight));
                } else if (children.length > 0) {
                    int length = ((AdjustableAdapter) this.mAdapter).getCount() - this.mFirstVisiblePosition;
                    newChildren = new GameObject[length];
                    System.arraycopy(children, 0, newChildren, 0, length);
                    this.mRecycler.fillViewsInActivedViews(newChildren);
                }
                if (this.mSelectedPosition >= count && count > 0) {
                    this.mSelectedPosition = count - 1;
                }
                if (count > 0) {
                    scrollToPosition(this.mSelectedPosition, 200);
                } else {
                    this.mUpdateState = 1;
                }
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
                        realY = (((this.mHeight / 2.0f) + ((float) this.mScroller.getCurrY())) - (((float) position) * this.mRowHeight)) - (0.5f * this.mRowHeight);
                        item = this.mRecycler.getScrapView(position);
                        item.setPositionPixel(0.0f, realY);
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
            if ((this.mAdapter == null ? 0 : ((AdjustableAdapter) this.mAdapter).getCount()) > 0) {
                top = (float) this.mScroller.getCurrY();
                bottom = top + this.mHeight;
                position = (int) (top / this.mRowHeight);
                this.mFirstVisiblePosition = position;
                while (position < ((AdjustableAdapter) this.mAdapter).getCount()) {
                    if (position >= 0) {
                        y = ((float) position) * this.mRowHeight;
                        if (this.mRowHeight + y >= top) {
                            if (y > bottom) {
                                break;
                            }
                            realY = (((this.mHeight / 2.0f) + top) - y) - (0.5f * this.mRowHeight);
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
                if (position > this.mFirstVisiblePosition) {
                    i = position - 1;
                } else {
                    i = this.mFirstVisiblePosition;
                }
                this.mLastVisiblePosition = i;
                this.mRecycler.arrange();
                if (this.mSelector != null) {
                    this.mSelectedPosition = (int) ((((this.mHeight / 2.0f) - this.mSelector.getY()) + top) / this.mRowHeight);
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
        float cy = this.mSelector.Position.Y;
        float f = this.mHeight / 2.0f;
        if (this.mUpdateState == 1118209) {
            currY = this.mScroller.getCurrY();
        } else {
            currY = this.mScroller.getFinalY();
        }
        float ty = (((((float) currY) + f) - (((float) this.mSelectedPosition) * this.mRowHeight)) - (0.5f * this.mRowHeight)) * getDisplay().sHeightRatio;
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
        if (this.mUpdateState == 1118209) {
            float border = ((this.mHeight - this.mRowHeight) / 2.0f) * getDisplay().sHeightRatio;
            if (this.mSelector.Position.Y > border) {
                this.mSelector.Position.Y = border;
            } else if (this.mSelector.Position.Y < (-border)) {
                this.mSelector.Position.Y = -border;
            }
        }
        this.mSelector.update(gl);
        if (this.mSelector.Position.Y != ty) {
            invalidate();
        }
    }

    private void adjustSelectPosition() {
        float top = (float) this.mScroller.getCurrY();
        this.mSelectedPosition = (int) ((((this.mHeight / 2.0f) + top) - this.mSelector.getY()) / this.mRowHeight);
        if (this.mSelectedPosition < this.mFirstVisiblePosition) {
            this.mSelectedPosition = this.mFirstVisiblePosition;
        } else if (this.mSelectedPosition > this.mLastVisiblePosition) {
            this.mSelectedPosition = this.mLastVisiblePosition;
        }
        while (true) {
            if (((float) this.mSelectedPosition) * this.mRowHeight < top) {
                this.mSelectedPosition++;
            } else if (((float) (this.mSelectedPosition + 1)) * this.mRowHeight > this.mHeight + top) {
                this.mSelectedPosition--;
            } else {
                return;
            }
        }
    }

    private void resetList() {
        this.mRecycler.clear();
        this.mUpdateState = 0;
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
                    setTouchMode(false);
                    return true;
                }
                break;
            case 20:
                if ((this.mUpdateState & 69633) != 69633) {
                    nextRow(false);
                    setTouchMode(false);
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
        int position = (int) (((event.getY() / getDisplay().sRatioY) + ((float) this.mScroller.getCurrY())) / this.mRowHeight);
        if (position >= ((AdjustableAdapter) this.mAdapter).getCount()) {
            return false;
        }
        this.mSelectedPosition = position;
        invalidate();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1) {
            this.mIsScrollBarMoved = false;
        }
        return this.mGestureRecogniser.onTouch(event);
    }

    public void refreash() {
        this.mDataChanged = true;
        this.mBottom = ((float) ((AdjustableAdapter) this.mAdapter).getCount()) * this.mRowHeight;
        if (this.mScrollBar != null) {
            this.mScrollBar.setHeight(this.mBottom < this.mHeight ? this.mHeight : (this.mHeight * this.mHeight) / this.mBottom);
            this.mScrollBar.setY(((this.mHeight - this.mScrollBar.getHeight()) / 2.0f) - ((this.mHeight * ((float) this.mScroller.getCurrY())) / this.mBottom));
            this.mScrollBar.reset();
        }
        this.mUpdateState = 257;
        invalidate();
    }

    void resetBottom() {
        this.mBottom = ((float) ((AdjustableAdapter) this.mAdapter).getCount()) * this.mRowHeight;
        if (this.mScrollBar != null) {
            this.mScrollBar.setHeight(this.mBottom < this.mHeight ? this.mHeight : (this.mHeight * this.mHeight) / this.mBottom);
            this.mScrollBar.setY((this.mHeight - this.mScrollBar.getHeight()) / 2.0f);
            this.mScrollBar.reset();
        }
    }
}
