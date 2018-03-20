package pers.lic.tool.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import pers.lic.tool.R;

public class SnapRecyclerView extends RecyclerView {
    private AutoFocus mAutoFocus;
    private AutoSnap mAutoSnap;
    private int mBottomSnap;
    private int mLeftSnap;
    private int mRightSnap;
    private int mTopSnap;

    private class AutoFocus {
        private int position;

        private AutoFocus() {
            this.position = -1;
        }

        private void focus(int position) {
            this.position = position;
        }

        private void tryFocus() {
            if (this.position != -1) {
                View view = SnapRecyclerView.this.getLayoutManager().findViewByPosition(this.position);
                if (view != null) {
                    view.requestFocus();
                    SnapRecyclerView.this.snapToView(view);
                    this.position = -1;
                }
            }
        }
    }

    private class AutoSnap {
        private View lastFocus;

        private AutoSnap() {
            this.lastFocus = null;
        }

        private void snap() {
            View focus = SnapRecyclerView.this.getLayoutManager().getFocusedChild();
            if (focus != null && !focus.equals(this.lastFocus)) {
                this.lastFocus = focus;
                SnapRecyclerView.this.snapToView(focus);
            }
        }
    }

    public SnapRecyclerView(Context context) {
        this(context, null);
    }

    public SnapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAutoFocus = new AutoFocus();
        this.mAutoSnap = new AutoSnap();
        this.mLeftSnap = -1;
        this.mRightSnap = -1;
        this.mTopSnap = -1;
        this.mBottomSnap = -1;
        setFocusable(false);
        addOnScrollListener(new OnScrollListener() {
            private int lastState = 0;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (this.lastState != newState) {
                    this.lastState = newState;
                    if (newState == 0) {
                        View focus = SnapRecyclerView.this.getLayoutManager().getFocusedChild();
                        if (focus != null) {
                            SnapRecyclerView.this.snapToView(focus);
                        }
                    }
                }
            }
        });
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnapRecyclerView, defStyle, 0);
            int snap = a.getDimensionPixelOffset(R.styleable.SnapRecyclerView_snap, -1);
            if (snap != -1) {
                this.mBottomSnap = snap;
                this.mTopSnap = snap;
                this.mRightSnap = snap;
                this.mLeftSnap = snap;
            }
            this.mLeftSnap = a.getDimensionPixelOffset(R.styleable.SnapRecyclerView_snapLeft, this.mLeftSnap);
            this.mRightSnap = a.getDimensionPixelOffset(R.styleable.SnapRecyclerView_snapRight, this.mRightSnap);
            this.mTopSnap = a.getDimensionPixelOffset(R.styleable.SnapRecyclerView_snapTop, this.mTopSnap);
            this.mBottomSnap = a.getDimensionPixelOffset(R.styleable.SnapRecyclerView_snapBottom, this.mBottomSnap);
            a.recycle();
        }
    }

    public void setSnap(int left, int top, int right, int bottom) {
        this.mLeftSnap = left;
        this.mTopSnap = top;
        this.mRightSnap = right;
        this.mBottomSnap = bottom;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        this.mAutoSnap.snap();
    }

    public void draw(Canvas c) {
        super.draw(c);
        this.mAutoFocus.tryFocus();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (!(layoutManager instanceof GridLayoutManager)) {
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    if (linearLayoutManager.getOrientation() != 0) {
                        if (event.getAction() == 0) {
                            switch (event.getKeyCode()) {
                                case 19:
                                    if (last(linearLayoutManager)) {
                                        return true;
                                    }
                                    break;
                                case 20:
                                    if (next(linearLayoutManager)) {
                                        return true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (event.getAction() == 0) {
                        switch (event.getKeyCode()) {
                            case MotionEventCompat.AXIS_WHEEL /*21*/:
                                if (last(linearLayoutManager)) {
                                    return true;
                                }
                                break;
                            case MotionEventCompat.AXIS_GAS /*22*/:
                                if (next(linearLayoutManager)) {
                                    return true;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else if (event.getAction() == 0) {
                switch (event.getKeyCode()) {
                    case 19:
                        if (lastRow((GridLayoutManager) layoutManager)) {
                            return true;
                        }
                        break;
                    case 20:
                        if (nextRow((GridLayoutManager) layoutManager)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean lastRow(GridLayoutManager layoutManager) {
        View focus = layoutManager.getFocusedChild();
        if (focus == null) {
            return false;
        }
        int position = layoutManager.getPosition(focus);
        int column = layoutManager.getSpanCount();
        if (position < column) {
            return false;
        }
        int nextPosition = position - column;
        View view = layoutManager.findViewByPosition(nextPosition);
        if (view != null) {
            return view.requestFocus(33);
        }
        if (getScrollState() == 0) {
            smoothScrollBy(0, -OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurement(focus));
        }
        this.mAutoFocus.focus(nextPosition);
        return true;
    }

    private boolean nextRow(GridLayoutManager layoutManager) {
        View focus = layoutManager.getFocusedChild();
        if (focus == null) {
            return true;
        }
        int position = layoutManager.getPosition(focus);
        int column = layoutManager.getSpanCount();
        int endPosition = getAdapter().getItemCount() - 1;
        if (position / column >= endPosition / column) {
            return false;
        }
        int nextPosition = Math.min(position + column, endPosition);
        View view = layoutManager.findViewByPosition(nextPosition);
        if (view != null) {
            return view.requestFocus(130);
        }
        if (getScrollState() == 0) {
            smoothScrollBy(0, OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurement(focus));
        }
        this.mAutoFocus.focus(nextPosition);
        return true;
    }

    private boolean last(LinearLayoutManager layoutManager) {
        View focus = layoutManager.getFocusedChild();
        if (focus == null) {
            return false;
        }
        int position = layoutManager.getPosition(focus);
        if (position <= 0) {
            return false;
        }
        int nextPosition = position - 1;
        View view = layoutManager.findViewByPosition(nextPosition);
        if (view != null) {
            return view.requestFocus(33);
        }
        if (getScrollState() == 0) {
            if (layoutManager.canScrollVertically()) {
                smoothScrollBy(0, -OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurement(focus));
            } else {
                smoothScrollBy(-OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurementInOther(focus), 0);
            }
        }
        this.mAutoFocus.focus(nextPosition);
        return true;
    }

    private boolean next(LinearLayoutManager layoutManager) {
        View focus = layoutManager.getFocusedChild();
        if (focus == null) {
            return true;
        }
        int position = layoutManager.getPosition(focus);
        if (position >= getAdapter().getItemCount() - 1) {
            return false;
        }
        int nextPosition = position + 1;
        View view = layoutManager.findViewByPosition(nextPosition);
        if (view != null) {
            return view.requestFocus(130);
        }
        if (getScrollState() == 0) {
            if (layoutManager.canScrollVertically()) {
                smoothScrollBy(0, OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurement(focus));
            } else {
                smoothScrollBy(OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurementInOther(focus), 0);
            }
        }
        this.mAutoFocus.focus(nextPosition);
        return true;
    }

    private void snapToView(View view) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager.canScrollVertically()) {
            if (this.mTopSnap == -1 || this.mBottomSnap == -1) {
                centerVerticalSnap(view, layoutManager);
            } else {
                verticalSnap(view, layoutManager);
            }
        } else if (this.mLeftSnap == -1 || this.mRightSnap == -1) {
            centerHorizontalSnap(view, layoutManager);
        } else {
            horizontalSnap(view, layoutManager);
        }
    }

    private void centerVerticalSnap(View view, LayoutManager layoutManager) {
        int containerCenter;
        int decoratedTop = layoutManager.getDecoratedTop(view) + ((LayoutParams) view.getLayoutParams()).topMargin;
        OrientationHelper helper = OrientationHelper.createVerticalHelper(layoutManager);
        int childCenter = decoratedTop + (helper.getDecoratedMeasurement(view) / 2);
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + (helper.getTotalSpace() / 2);
        } else {
            containerCenter = helper.getEnd() / 2;
        }
        int dy = childCenter - containerCenter;
        if (dy != 0) {
            if (dy < 0) {
                View first = layoutManager.findViewByPosition(0);
                if (first != null) {
                    dy = Math.max(dy, layoutManager.getDecoratedTop(first));
                }
            } else {
                View last = layoutManager.findViewByPosition(layoutManager.getItemCount() - 1);
                if (last != null) {
                    dy = Math.min(dy, layoutManager.getDecoratedBottom(last) - helper.getEnd());
                }
            }
            smoothScrollBy(0, dy);
        }
    }

    private void verticalSnap(View view, LayoutManager layoutManager) {
        int top;
        int bottom;
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int decoratedTop = layoutManager.getDecoratedTop(view) + params.topMargin;
        if (layoutManager.getClipToPadding()) {
            top = getPaddingTop();
            bottom = getHeight() - getPaddingBottom();
        } else {
            top = 0;
            bottom = layoutManager.getHeight();
        }
        int dy;
        if (decoratedTop - this.mTopSnap < top) {
            dy = (decoratedTop - this.mTopSnap) - top;
            View first = layoutManager.findViewByPosition(0);
            if (first != null) {
                dy = Math.max(dy, layoutManager.getDecoratedTop(first));
            }
            smoothScrollBy(0, dy);
            return;
        }
        int decoratedBottom = (view.getBottom() + layoutManager.getBottomDecorationHeight(view)) + params.bottomMargin;
        if (this.mBottomSnap + decoratedBottom > bottom) {
            dy = (this.mBottomSnap + decoratedBottom) - bottom;
            View last = layoutManager.findViewByPosition(layoutManager.getItemCount() - 1);
            if (last != null) {
                dy = Math.min(dy, layoutManager.getDecoratedBottom(last) - bottom);
            }
            smoothScrollBy(0, dy);
        }
    }

    private void centerHorizontalSnap(View view, LayoutManager layoutManager) {
        int containerCenter;
        int childCenter = (layoutManager.getDecoratedLeft(view) + ((LayoutParams) view.getLayoutParams()).leftMargin) + (OrientationHelper.createVerticalHelper(layoutManager).getDecoratedMeasurementInOther(view) / 2);
        if (layoutManager.getClipToPadding()) {
            containerCenter = layoutManager.getPaddingLeft() + (((layoutManager.getWidth() - layoutManager.getPaddingLeft()) - layoutManager.getPaddingRight()) / 2);
        } else {
            containerCenter = layoutManager.getWidth() / 2;
        }
        int dx = childCenter - containerCenter;
        if (dx != 0) {
            if (dx < 0) {
                View first = layoutManager.findViewByPosition(0);
                if (first != null) {
                    dx = Math.max(dx, layoutManager.getDecoratedLeft(first));
                }
            } else {
                View last = layoutManager.findViewByPosition(layoutManager.getItemCount() - 1);
                if (last != null) {
                    dx = Math.min(dx, layoutManager.getDecoratedRight(last) - layoutManager.getWidth());
                }
            }
            smoothScrollBy(dx, 0);
        }
    }

    private void horizontalSnap(View view, LayoutManager layoutManager) {
        int left;
        int right;
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int decoratedStart = layoutManager.getDecoratedLeft(view) + params.leftMargin;
        if (layoutManager.getClipToPadding()) {
            left = getPaddingLeft();
            right = getWidth() - getPaddingRight();
        } else {
            left = 0;
            right = getWidth();
        }
        int dx;
        if (decoratedStart - this.mLeftSnap < left) {
            dx = (decoratedStart - this.mLeftSnap) - left;
            View first = layoutManager.findViewByPosition(0);
            if (first != null) {
                dx = Math.max(dx, layoutManager.getDecoratedLeft(first));
            }
            smoothScrollBy(dx, 0);
            return;
        }
        int decoratedEnd = (view.getRight() + layoutManager.getRightDecorationWidth(view)) + params.rightMargin;
        if (this.mRightSnap + decoratedEnd > right) {
            dx = (this.mRightSnap + decoratedEnd) - right;
            View last = layoutManager.findViewByPosition(layoutManager.getItemCount() - 1);
            if (last != null) {
                dx = Math.min(dx, layoutManager.getDecoratedRight(last) - right);
            }
            smoothScrollBy(dx, 0);
        }
    }
}
