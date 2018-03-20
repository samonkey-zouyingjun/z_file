package com.zidoo.fileexplorer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScatterBackgroudImageView extends ImageView {
    private Rect mRect = new Rect();

    public ScatterBackgroudImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showRect(Rect rect) {
        this.mRect = rect;
        invalidate();
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        paint.setColor(-16777216);
        canvas.drawRect(this.mRect, paint);
    }
}
