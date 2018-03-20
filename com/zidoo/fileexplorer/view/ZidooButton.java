package com.zidoo.fileexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.zidoo.fileexplorer.tool.ZidooTypeface;

public class ZidooButton extends Button {
    public ZidooButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ZidooButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZidooButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
    }
}
