package pers.lic.tool.widget.adw;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class BlurDrawer extends AnimationDrawer {
    private BitmapBlur mBitmapBlur = null;
    private boolean mIsBlur = false;

    private class BitmapBlur extends Thread {
        private DynamicGaussianBlur blur;

        private BitmapBlur(Bitmap bitmap) {
            this.blur = new DynamicGaussianBlur(bitmap);
        }

        public void run() {
            this.blur.center();
        }

        private Bitmap getBlurBitmap() {
            return this.blur.out();
        }

        private boolean isComplete() {
            return this.blur.isComplete();
        }
    }

    public boolean isAnimating() {
        return this.mIsBlur;
    }

    public void draw(Canvas canvas) {
        View parent = this.drawRoot.getRoot();
        int w = parent.getWidth();
        int h = parent.getHeight();
        if (this.mBitmapBlur == null) {
            Bitmap temp = Bitmap.createBitmap((int) (((float) w) * 0.1f), (int) (((float) h) * 0.1f), Config.ARGB_8888);
            Canvas scaleCanvas = new Canvas(temp);
            scaleCanvas.save();
            scaleCanvas.scale(0.1f, 0.1f);
            this.drawRoot.superDraw(scaleCanvas);
            scaleCanvas.restore();
            this.mBitmapBlur = new BitmapBlur(temp);
            this.mBitmapBlur.start();
            ObjectAnimator.ofFloat(this.drawRoot.getRoot(), "alpha", new float[]{0.5f}).setDuration(300).start();
        }
        this.drawRoot.superDraw(canvas);
        canvas.drawBitmap(this.mBitmapBlur.getBlurBitmap(), null, new Rect(0, 0, w, h), null);
        if (!this.mBitmapBlur.isComplete()) {
            this.drawRoot.postAnimation();
        }
    }

    public void setBlur(boolean blur) {
        if (this.mIsBlur != blur) {
            this.mIsBlur = blur;
            if (!blur) {
                ObjectAnimator.ofFloat(this.drawRoot.getRoot(), "alpha", new float[]{1.0f}).setDuration(300).start();
                if (this.mBitmapBlur != null) {
                    this.mBitmapBlur = null;
                    System.gc();
                }
            }
            this.drawRoot.postAnimation();
        }
    }
}
