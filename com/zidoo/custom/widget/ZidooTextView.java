package com.zidoo.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ZidooTextView extends TextView {
    public ZidooTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ZidooTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZidooTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setTypeface(ZidooTypeface.getIsoTypeface(context));
    }
}
