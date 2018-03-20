package com.zidoo.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ZidooButton extends Button {
    public ZidooButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ZidooButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZidooButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setTypeface(ZidooTypeface.getIsoTypeface(context));
    }
}
