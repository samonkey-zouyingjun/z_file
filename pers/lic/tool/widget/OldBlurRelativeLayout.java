package pers.lic.tool.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import pers.lic.tool.Toolc;

public class OldBlurRelativeLayout extends RelativeLayout {
    Bitmap mBlurBitmap = null;
    boolean mIsBlur = false;

    @RequiresApi(api = 17)
    private class BlurRunn implements Runnable {
        private BlurRunn() {
        }

        public void run() {
            final Bitmap bmp = Toolc.blurBitmap(OldBlurRelativeLayout.this.getContext(), OldBlurRelativeLayout.this.scale(), 2.0f);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    OldBlurRelativeLayout.this.mBlurBitmap = bmp;
                    OldBlurRelativeLayout.this.invalidate();
                    OldBlurRelativeLayout.this.clearAnimation();
                    AlphaAnimation aa = new AlphaAnimation(0.0f, 0.5f);
                    aa.setDuration(500);
                    aa.setFillAfter(true);
                    OldBlurRelativeLayout.this.startAnimation(aa);
                }
            });
        }
    }

    public OldBlurRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void dispatchDraw(Canvas canvas) {
        if (!this.mIsBlur) {
            if (this.mBlurBitmap != null) {
                this.mBlurBitmap.recycle();
                this.mBlurBitmap = null;
                System.gc();
            }
            super.dispatchDraw(canvas);
        } else if (this.mBlurBitmap == null) {
            new Thread(new BlurRunn()).start();
            super.dispatchDraw(canvas);
        } else {
            canvas.drawBitmap(this.mBlurBitmap, null, new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), null);
        }
    }

    public void setBlur(boolean blur) {
        if (this.mIsBlur != blur) {
            this.mIsBlur = blur;
            AlphaAnimation aa;
            if (blur) {
                aa = new AlphaAnimation(1.0f, 0.0f);
                aa.setDuration(300);
                aa.setFillAfter(true);
                startAnimation(aa);
            } else {
                aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(500);
                aa.setFillAfter(true);
                startAnimation(aa);
            }
            invalidate();
        }
    }

    public boolean isBlur() {
        return this.mIsBlur;
    }

    private Bitmap scale() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth() / 10, getHeight() / 10, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.scale(0.1f, 0.1f);
        super.dispatchDraw(canvas);
        canvas.restore();
        return bitmap;
    }
}
