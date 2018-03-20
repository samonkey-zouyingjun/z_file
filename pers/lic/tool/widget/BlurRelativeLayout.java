package pers.lic.tool.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import pers.lic.tool.Toolc;

public class BlurRelativeLayout extends RelativeLayout {
    Bitmap mBlurBitmap = null;
    boolean mIsBlur = false;

    public BlurRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = 17)
    protected void dispatchDraw(Canvas canvas) {
        if (this.mIsBlur) {
            if (this.mBlurBitmap != null) {
                this.mBlurBitmap.recycle();
            }
            long t = System.currentTimeMillis();
            this.mBlurBitmap = Bitmap.createBitmap((int) (((float) getWidth()) * 0.1f), (int) (((float) getHeight()) * 0.1f), Config.ARGB_8888);
            Canvas blurCanvas = new Canvas(this.mBlurBitmap);
            blurCanvas.save();
            blurCanvas.scale(0.1f, 0.1f);
            super.dispatchDraw(blurCanvas);
            blurCanvas.restore();
            this.mBlurBitmap = Toolc.blurBitmap(getContext(), this.mBlurBitmap, 1.0f);
            ObjectAnimator.ofFloat(this, "alpha", new float[]{0.5f}).setDuration(300).start();
            canvas.drawBitmap(this.mBlurBitmap, null, new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), null);
            return;
        }
        if (this.mBlurBitmap != null) {
            this.mBlurBitmap.recycle();
            this.mBlurBitmap = null;
            System.gc();
        }
        super.dispatchDraw(canvas);
    }

    public void setBlur(boolean blur) {
        if (this.mIsBlur != blur) {
            this.mIsBlur = blur;
            if (!blur) {
                ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f}).setDuration(300).start();
            }
            invalidate();
        }
    }

    public boolean isBlur() {
        return this.mIsBlur;
    }
}
