package zidoo.tarot.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;

public class RotateProgressView extends TImageView {
    private int barColor;
    private float barExtraLength;
    private boolean barGrowingFromFront;
    private final int barLength;
    private final int barMaxLength;
    private Paint barPaint;
    private double barSpinCycleTime;
    private int barWidth;
    private long lastTimeAnimated;
    private float mProgress;
    private final long pauseGrowingTime;
    private long pausedTimeWithoutGrowing;
    private int rimColor;
    private Paint rimPaint;
    private int rimWidth;
    private float spinSpeed;
    private double timeStartGrowing;

    public RotateProgressView(GLContext glContext) {
        super(glContext);
        this.barLength = 40;
        this.barMaxLength = 270;
        this.timeStartGrowing = 0.0d;
        this.barSpinCycleTime = 1000.0d;
        this.barExtraLength = 0.0f;
        this.barGrowingFromFront = true;
        this.pausedTimeWithoutGrowing = 0;
        this.pauseGrowingTime = 300;
        this.barWidth = 7;
        this.rimWidth = 7;
        this.barColor = Color.parseColor("#6cb865");
        this.rimColor = ViewCompat.MEASURED_SIZE_MASK;
        this.barPaint = new Paint();
        this.rimPaint = new Paint();
        this.spinSpeed = 270.0f;
        this.lastTimeAnimated = 0;
        this.mProgress = 0.0f;
        this.mNeedGenTexture = true;
        setupPaints();
    }

    private void setupPaints() {
        this.barPaint.setColor(this.barColor);
        this.barPaint.setAntiAlias(true);
        this.barPaint.setStyle(Style.STROKE);
        this.barPaint.setStrokeWidth((float) this.barWidth);
        this.rimPaint.setColor(this.rimColor);
        this.rimPaint.setAntiAlias(true);
        this.rimPaint.setStyle(Style.STROKE);
        this.rimPaint.setStrokeWidth((float) this.rimWidth);
    }

    public void update(GL10 gl) {
        if (isShow()) {
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            drawBitmap();
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glPopMatrix();
        }
    }

    private void drawBitmap() {
        int w = (int) getWidth();
        int h = (int) getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF circleBounds = new RectF((float) this.barWidth, (float) this.barWidth, (float) (w - this.barWidth), (float) (h - this.barWidth));
        canvas.drawArc(circleBounds, 360.0f, 360.0f, false, this.rimPaint);
        long deltaTime = SystemClock.uptimeMillis() - this.lastTimeAnimated;
        float deltaNormalized = (((float) deltaTime) * this.spinSpeed) / 1000.0f;
        updateBarLength(deltaTime);
        this.mProgress += deltaNormalized;
        if (this.mProgress > 360.0f) {
            this.mProgress -= 360.0f;
        }
        this.lastTimeAnimated = SystemClock.uptimeMillis();
        canvas.drawArc(circleBounds, this.mProgress - 90.0f, 40.0f + this.barExtraLength, false, this.barPaint);
        this.mMaterials[0].recycle();
        this.mMaterials[0].texture = GLResources.genTextrue(bitmap);
        invalidate();
    }

    private void updateBarLength(long deltaTimeInMilliSeconds) {
        if (this.pausedTimeWithoutGrowing >= 300) {
            this.timeStartGrowing += (double) deltaTimeInMilliSeconds;
            if (this.timeStartGrowing > this.barSpinCycleTime) {
                this.timeStartGrowing -= this.barSpinCycleTime;
                this.timeStartGrowing = 0.0d;
                if (!this.barGrowingFromFront) {
                    this.pausedTimeWithoutGrowing = 0;
                }
                this.barGrowingFromFront = !this.barGrowingFromFront;
            }
            float distance = (((float) Math.cos(((this.timeStartGrowing / this.barSpinCycleTime) + 1.0d) * 3.141592653589793d)) / 2.0f) + 0.5f;
            if (this.barGrowingFromFront) {
                this.barExtraLength = distance * 230.0f;
                return;
            }
            float newLength = 230.0f * (1.0f - distance);
            this.mProgress += this.barExtraLength - newLength;
            this.barExtraLength = newLength;
            return;
        }
        this.pausedTimeWithoutGrowing += deltaTimeInMilliSeconds;
    }
}
