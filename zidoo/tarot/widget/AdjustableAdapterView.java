package zidoo.tarot.widget;

import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.adapter.AdjustableAdapter;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Vector3;
import zidoo.tarot.kernel.anim.GlTranslateAnimation;

public abstract class AdjustableAdapterView extends AdaperView<AdjustableAdapter> {
    protected final int UPDATE_ADD;
    protected final int UPDATE_ADJUST;
    protected final int UPDATE_REFREASH;
    protected AdjusterAnimator mAnimator;
    protected ArrayList<GameObject> mBeMoveOutViews;
    protected ArrayList<GameObject> mBeRemovedViews;
    protected float mBottom;
    private boolean mCirculateEnable;
    protected boolean mDataChanged;
    protected boolean mIsScrollBarMoved;
    protected boolean mPageturnEnable;
    private boolean mShowSelector;
    protected int mUpdateState;

    public interface AdjusterAnimator {
        long getRemoveDuration();

        void setAddAnimation(GameObject gameObject);

        void setNotifyGoneAnimation(GameObject gameObject);

        void setNotifyShowAnimation(GameObject gameObject);

        void setRemoveAnimation(GameObject gameObject);
    }

    protected class AdjustableRecycleBin extends RecycleBin {
        protected AdjustableRecycleBin() {
            super();
        }

        GameObject obtainView(int position) {
            GameObject view;
            int index = position - this.firstActivePosition;
            if (index < 0 || index >= this.activedViews.length) {
                view = ((AdjustableAdapter) AdjustableAdapterView.this.mAdapter).getView(position, (GameObject) this.recycles.poll(), null);
                if (AdjustableAdapterView.this.mAnimator != null) {
                    if (AdjustableAdapterView.this.mUpdateState == 257) {
                        AdjustableAdapterView.this.mAnimator.setAddAnimation(view);
                    } else {
                        AdjustableAdapterView.this.mAnimator.setNotifyShowAnimation(view);
                    }
                }
            } else {
                view = AdjustableAdapterView.this.mDataChanged ? ((AdjustableAdapter) AdjustableAdapterView.this.mAdapter).getView(position, this.activedViews[index], null) : this.activedViews[index];
                this.activedViews[index] = null;
            }
            view.setParent(AdjustableAdapterView.this);
            this.tempActiveis.add(view);
            return view;
        }
    }

    public abstract void pageDown();

    public abstract void pageUp();

    public abstract void refreash();

    abstract void resetBottom();

    public abstract void scrollToPosition(int i, int i2);

    public AdjustableAdapterView(GLContext glContext) {
        super(glContext);
        this.UPDATE_ADJUST = 16;
        this.UPDATE_ADD = 65552;
        this.UPDATE_REFREASH = 257;
        this.mUpdateState = 0;
        this.mPageturnEnable = false;
        this.mCirculateEnable = false;
        this.mShowSelector = false;
        this.mIsScrollBarMoved = false;
        this.mAnimator = null;
        this.mBeRemovedViews = null;
        this.mBeMoveOutViews = null;
        this.mDataChanged = false;
        this.mRecycler = new AdjustableRecycleBin();
    }

    public void setPageturnEnable(boolean enable) {
        if (this.mPageturnEnable != enable) {
            this.mPageturnEnable = enable;
            resetScroller();
        }
    }

    public void setmCirculateEnable(boolean circulateEnable) {
        this.mCirculateEnable = circulateEnable;
    }

    public boolean isCirculateEnable() {
        return this.mCirculateEnable;
    }

    public boolean isPageturnEnable() {
        return this.mPageturnEnable;
    }

    public void setShowSelector(boolean showSelector) {
        this.mShowSelector = showSelector;
    }

    public boolean isShowSelector() {
        return this.mShowSelector;
    }

    public void setScrollBar(ScrollBar scrollBar) {
        scrollBar.setX((this.mWidth / 2.0f) - 10.0f);
        super.setScrollBar(scrollBar);
    }

    protected void updateScrollBar(GL10 gl) {
        if (this.mScrollBar != null) {
            if (!((this.mUpdateState & 4096) == 0 || this.mIsScrollBarMoved || this.mScroller.isFinished())) {
                this.mScrollBar.show();
                this.mScrollBar.setY(((this.mHeight - this.mScrollBar.getHeight()) / 2.0f) - ((this.mHeight * ((float) this.mScroller.getCurrY())) / this.mBottom));
            }
            this.mScrollBar.update(gl);
        }
    }

    public boolean isTouched(MotionEvent event) {
        RectF rectF = this.mAABBBox.getScreenRect();
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float w = (rectF.right - rectF.left) * this.mScaleX;
        float h = (rectF.bottom - rectF.top) * this.mScaleY;
        return new RectF(cx - (w / 2.0f), cy - (h / 2.0f), (w / 2.0f) + cx, (h / 2.0f) + cy).contains(event.getX(), event.getY());
    }

    void resetScroller() {
        int y = this.mScroller == null ? 0 : this.mScroller.getFinalY();
        if (this.mPageturnEnable) {
            this.mScroller = new Scroller(getContext(), new DecelerateInterpolator(), true);
        } else {
            this.mScroller = new Scroller(getContext(), null, true);
        }
        this.mScroller.setFinalY(y);
        this.mScroller.computeScrollOffset();
    }

    public void setAdjusterAnimator(AdjusterAnimator adjusterAnimator) {
        this.mAnimator = adjusterAnimator;
    }

    public void scrollToPosition(int position) {
        scrollToPosition(position, 500);
    }

    public void removeView(int position) {
        if (this.mAnimator == null) {
            throw new IllegalAccessError("the Animator is null");
        } else if (position > this.mLastVisiblePosition) {
            ((AdjustableAdapter) this.mAdapter).remove(position);
            ((AdjustableAdapter) this.mAdapter).notifyDataSetChanged();
        } else {
            adjustView(position);
            this.mUpdateState = 65552;
            invalidate();
        }
    }

    public void removeViews(ArrayList<Integer> positions) {
        if (this.mAnimator != null) {
            adjustViews(positions);
            this.mUpdateState = 65552;
            invalidate();
            return;
        }
        throw new IllegalAccessError("the adapter is null!");
    }

    public void removeViews(boolean[] removeItems, int removeCount) {
        if (this.mAnimator != null) {
            adjustViews(removeItems, removeCount);
            this.mUpdateState = 65552;
            invalidate();
            return;
        }
        throw new IllegalAccessError("the adapter is null!");
    }

    private void adjustView(int position) {
        if (this.mBeRemovedViews == null) {
            this.mBeRemovedViews = new ArrayList(getVisibleChildCount());
        }
        GameObject[] children = this.mRecycler.getActiveViews();
        GameObject[] newChildren = new GameObject[(children.length - 1)];
        int index = position - this.mFirstVisiblePosition;
        GameObject beRemoved = children[index];
        this.mAnimator.setRemoveAnimation(beRemoved);
        this.mBeRemovedViews.add(beRemoved);
        long startDelay = this.mAnimator.getRemoveDuration();
        for (int i = index + 1; i < children.length; i++) {
            GlTranslateAnimation animation = new GlTranslateAnimation(children[i].Position, children[i - 1].Position);
            animation.setFillAfter(true);
            animation.setDuration(200);
            animation.setStartDelay(startDelay);
            children[i].startAnimation(animation);
        }
        System.arraycopy(children, 0, newChildren, 0, index);
        System.arraycopy(children, index + 1, newChildren, index, newChildren.length - index);
        this.mRecycler.fillViewsInActivedViews(newChildren);
        ((AdjustableAdapter) this.mAdapter).remove(position);
        resetBottom();
    }

    private void adjustViews(boolean[] remove, int removeCount) {
        if (this.mBeRemovedViews == null) {
            this.mBeRemovedViews = new ArrayList(getVisibleChildCount());
        }
        if (this.mBeMoveOutViews == null) {
            this.mBeMoveOutViews = new ArrayList(getVisibleChildCount());
        }
        GameObject[] children = this.mRecycler.getActiveViews();
        ArrayList<GameObject> tempViews = new ArrayList();
        long startDelay = this.mAnimator.getRemoveDuration();
        int count = ((AdjustableAdapter) this.mAdapter).getCount();
        int i = 0;
        int offset = 0;
        while (i < count) {
            if (remove[i]) {
                offset++;
                if (i >= this.mFirstVisiblePosition && i <= this.mLastVisiblePosition) {
                    this.mAnimator.setRemoveAnimation(children[i - this.mFirstVisiblePosition]);
                    this.mBeRemovedViews.add(children[i - this.mFirstVisiblePosition]);
                }
            } else if (i >= this.mFirstVisiblePosition && i <= this.mLastVisiblePosition) {
                Vector3 target;
                GameObject child = children[i - this.mFirstVisiblePosition];
                if (i - offset >= this.mFirstVisiblePosition) {
                    target = children[(i - offset) - this.mFirstVisiblePosition].Position;
                    tempViews.add(child);
                } else {
                    target = new Vector3(child.Position);
                    target.Y += 1080.0f * getDisplay().sHeightRatio;
                    this.mBeMoveOutViews.add(child);
                }
                GlTranslateAnimation animation = new GlTranslateAnimation(child.Position, target);
                animation.setFillAfter(true);
                animation.setDuration(200);
                animation.setStartDelay(startDelay);
                child.startAnimation(animation);
            }
            i++;
        }
        GameObject[] newChildren = new GameObject[tempViews.size()];
        tempViews.toArray(newChildren);
        this.mRecycler.fillViewsInActivedViews(newChildren);
        ((AdjustableAdapter) this.mAdapter).remove(remove, removeCount);
        resetBottom();
    }

    private void adjustViews(ArrayList<Integer> positions) {
        boolean[] remove = new boolean[((AdjustableAdapter) this.mAdapter).getCount()];
        for (int i = 0; i < positions.size(); i++) {
            remove[((Integer) positions.get(i)).intValue()] = true;
        }
        adjustViews(remove, positions.size());
    }

    protected boolean onKeyDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 9:
            case 92:
                if ((this.mUpdateState & 69633) != 69633) {
                    pageUp();
                    return true;
                }
                break;
            case 15:
            case 93:
                if ((this.mUpdateState & 69633) != 69633) {
                    pageDown();
                    return true;
                }
                break;
        }
        return super.onKeyDown(event);
    }
}
