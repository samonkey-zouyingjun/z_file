package com.zidoo.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class ZidooEditText extends EditText {
    public ZidooEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ZidooEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZidooEditText(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setTypeface(ZidooTypeface.getIsoTypeface(context));
    }
}
