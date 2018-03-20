package com.zidoo.custom.moveview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MoveParentView extends RelativeLayout {
    private boolean isFirstAdd = true;
    private int mFindOrderIndex = 0;
    private HashMap<String, MoveView> mMovViewHash = new HashMap();
    private ArrayList<View> mViews = new ArrayList();

    class SortComparator implements Comparator {
        SortComparator() {
        }

        public int compare(Object lhs, Object rhs) {
            return ((View) lhs).getX() - ((View) rhs).getX() > 0.0f ? 0 : -1;
        }
    }

    public MoveParentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MoveParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MoveParentView(Context context) {
        super(context);
        init(context);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.isFirstAdd) {
            this.isFirstAdd = false;
            initLayout();
        }
        super.onLayout(changed, l, t, r, b);
    }

    private void init(Context context) {
        setChildrenDrawingOrderEnabled(true);
    }

    private void initLayout() {
        this.mViews.clear();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            this.mViews.add(getChildAt(i));
        }
    }

    public void setOrderChild(View view) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i).equals(view)) {
                this.mFindOrderIndex = i;
                invalidate();
                return;
            }
        }
    }

    public void repeatDis() {
        Collections.sort(this.mViews, new SortComparator());
        for (int i = 0; i < this.mViews.size(); i++) {
            Log.v("bob", "Width = " + ((View) this.mViews.get(i)).getX());
        }
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (i >= this.mFindOrderIndex) {
            return ((this.mFindOrderIndex + childCount) - 1) - i;
        }
        return super.getChildDrawingOrder(childCount, i);
    }
}
