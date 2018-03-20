package pers.lic.tool.widget.adw;

import android.animation.TimeInterpolator;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class CircularDiffuseDrawer extends AnimationDrawer {
    private TimeInterpolator interpolator = new AccelerateInterpolator();
    private boolean mAnim = false;
    private Paint mBitmapPaint = new Paint(2);
    private float mCenterX;
    private float mCenterY;
    private Paint mCirclePaint = new Paint(1);
    private int mDuration = 300;
    private int mMaxRadius = -1;
    private long mStartTime = 0;
    private OnDiffuseListener onDiffuseListener = null;

    public interface OnDiffuseListener {
        void onDrawComplete();
    }

    public CircularDiffuseDrawer() {
        this.mBitmapPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    }

    public void setOnDiffuseListener(OnDiffuseListener onDiffuseListener) {
        this.onDiffuseListener = onDiffuseListener;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void diffuse(float centerX, float centerY) {
        this.mCenterX = centerX;
        this.mCenterY = centerY;
        this.mMaxRadius = -1;
        this.mStartTime = SystemClock.currentThreadTimeMillis();
        this.mAnim = true;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (dx * dx) + (dy * dy);
    }

    public boolean isAnimating() {
        return this.mAnim;
    }

    public void draw(Canvas canvas) {
        View parent = this.drawRoot.getRoot();
        int w = parent.getWidth();
        int h = parent.getHeight();
        if (this.mMaxRadius == -1) {
            computeMaxRadius(parent, this.mCenterX, this.mCenterY);
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        this.drawRoot.superDraw(new Canvas(bitmap));
        long du = SystemClock.currentThreadTimeMillis() - this.mStartTime;
        float input = this.interpolator.getInterpolation(du < ((long) this.mDuration) ? ((float) du) / ((float) this.mDuration) : 1.0f);
        float radius = ((float) this.mMaxRadius) * input;
        canvas.saveLayer(new RectF(Math.max(0.0f, this.mCenterX - radius), Math.max(0.0f, this.mCenterY - radius), Math.min((float) w, this.mCenterX + radius), Math.min((float) h, this.mCenterY + radius)), null, 31);
        this.mCirclePaint.setShader(new RadialGradient(this.mCenterX, this.mCenterY, 1.2f * radius, new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#00000000")}, new float[]{0.6f, 0.8f}, TileMode.CLAMP));
        canvas.drawCircle(this.mCenterX, this.mCenterY, radius, this.mCirclePaint);
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, w, h), this.mBitmapPaint);
        bitmap.recycle();
        canvas.restore();
        if (input >= 1.0f) {
            this.mAnim = false;
            if (this.onDiffuseListener != null) {
                this.onDiffuseListener.onDrawComplete();
            }
        }
        this.drawRoot.postAnimation();
    }

    private void computeMaxRadius(View parent, float centerX, float centerY) {
        int w = parent.getWidth();
        int h = parent.getHeight();
        float d1 = distance(centerX, centerY, 0.0f, 0.0f);
        float d2 = distance(centerX, centerY, (float) w, 0.0f);
        float d3 = distance(centerX, centerY, 0.0f, (float) h);
        this.mMaxRadius = (int) (Math.sqrt((double) Math.max(Math.max(Math.max(d1, d2), d3), distance(centerX, centerY, (float) w, (float) h))) / 0.7200000286102295d);
    }
}
