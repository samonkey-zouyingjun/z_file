package zidoo.tarot.widget;

import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.gameobject.dataview.DataSetObserver;

public class WrapListView extends ListView {
    private float mMaxHeight;

    public void setAdapter(Adapter adapter) {
        resetList();
        if (this.mAdapter != adapter) {
            adapter.registerDataSetObserver(this.mDataSetObserver);
            this.mScroller.setFinalY(0);
            this.mSelectedPosition = 0;
            this.mAdapter = adapter;
            this.mBottom = ((((float) this.mAdapter.getCount()) * this.mRowHeight) + ((float) this.mPadding.top)) + ((float) this.mPadding.bottom);
            setHeight(this.mBottom > this.mMaxHeight ? this.mMaxHeight : this.mBottom);
            this.mUpdateState = 1;
            invalidate();
        }
    }

    public void setMaxHeight(float maxHeight) {
        this.mMaxHeight = maxHeight;
    }

    public WrapListView(GLContext glContext) {
        super(glContext);
        this.mMaxHeight = 720.0f;
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                WrapListView.this.mBottom = ((((float) WrapListView.this.mAdapter.getCount()) * WrapListView.this.mRowHeight) + ((float) WrapListView.this.mPadding.top)) + ((float) WrapListView.this.mPadding.bottom);
                WrapListView.this.setHeight(WrapListView.this.mBottom > WrapListView.this.mMaxHeight ? WrapListView.this.mMaxHeight : WrapListView.this.mBottom);
                if (WrapListView.this.mBottom > WrapListView.this.mHeight && WrapListView.this.mBottom < ((float) WrapListView.this.mScroller.getCurrY()) + WrapListView.this.mHeight) {
                    WrapListView.this.mScroller.setFinalY((int) (WrapListView.this.mBottom - WrapListView.this.mHeight));
                }
                if (WrapListView.this.mSelectedPosition > WrapListView.this.mAdapter.getCount() - 1) {
                    WrapListView.this.mSelectedPosition = WrapListView.this.mAdapter.getCount() - 1;
                }
                WrapListView.this.mRecycler.recycleAll();
                WrapListView.this.mUpdateState = 1;
                WrapListView.this.invalidate();
            }

            public void onInvalidated() {
            }
        };
    }
}
