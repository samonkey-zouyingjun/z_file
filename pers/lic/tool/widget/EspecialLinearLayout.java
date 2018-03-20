package pers.lic.tool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EspecialLinearLayout extends LinearLayout {
    boolean mComplete = false;
    int mMaxWidth = 0;

    public EspecialLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void complete() {
        this.mComplete = true;
        invalidate();
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mComplete) {
            int i;
            int len = getChildCount();
            int maxWidth = 0;
            for (i = 0; i < len; i++) {
                LinearLayout layout = (LinearLayout) getChildAt(i);
                if (layout.isShown()) {
                    int w = layout.getChildAt(0).getWidth();
                    if (w > maxWidth) {
                        maxWidth = w;
                    }
                }
            }
            if (maxWidth > this.mMaxWidth) {
                this.mMaxWidth = maxWidth;
                for (i = 0; i < len; i++) {
                    ((TextView) ((LinearLayout) getChildAt(i)).getChildAt(0)).setWidth(maxWidth);
                }
                requestLayout();
            }
        }
    }
}
