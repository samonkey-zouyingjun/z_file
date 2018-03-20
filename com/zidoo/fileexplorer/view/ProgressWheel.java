package com.zidoo.fileexplorer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import com.zidoo.fileexplorer.R;

public class ProgressWheel extends View {
    public static final int[] ProgressWheel = new int[]{R.anim.scale_dialog_show, R.anim.set_dialog_gone, R.anim.translate_letf, R.anim.translate_right, 2130771972, 2130771973, 2130771974, 2130771975, 2130771976};
    private int barColor = Color.parseColor("#00aeff");
    private float barExtraLength = 0.0f;
    private boolean barGrowingFromFront = true;
    private final int barLength = 40;
    private final int barMaxLength = 270;
    private Paint barPaint = new Paint();
    private double barSpinCycleTime = 1000.0d;
    private int barWidth = 5;
    private RectF circleBounds = new RectF();
    private int circleRadius = 80;
    private boolean fillRadius = false;
    private boolean isSpinning = false;
    private long lastTimeAnimated = 0;
    private float mProgress = 0.0f;
    private float mTargetProgress = 0.0f;
    private final long pauseGrowingTime = 300;
    private long pausedTimeWithoutGrowing = 0;
    private int rimColor = ViewCompat.MEASURED_SIZE_MASK;
    private Paint rimPaint = new Paint();
    private int rimWidth = 5;
    private float spinSpeed = 270.0f;
    private double timeStartGrowing = 0.0d;

    static class WheelSavedState extends BaseSavedState {
        public static final Creator<WheelSavedState> CREATOR = new Creator<WheelSavedState>() {
            public WheelSavedState createFromParcel(Parcel in) {
                return new WheelSavedState(in);
            }

            public WheelSavedState[] newArray(int size) {
                return new WheelSavedState[size];
            }
        };
        int barColor;
        int barWidth;
        int circleRadius;
        boolean isSpinning;
        float mProgress;
        float mTargetProgress;
        int rimColor;
        int rimWidth;
        float spinSpeed;

        WheelSavedState(Parcelable superState) {
            super(superState);
        }

        private WheelSavedState(Parcel in) {
            super(in);
            this.mProgress = in.readFloat();
            this.mTargetProgress = in.readFloat();
            this.isSpinning = in.readByte() != (byte) 0;
            this.spinSpeed = in.readFloat();
            this.barWidth = in.readInt();
            this.barColor = in.readInt();
            this.rimWidth = in.readInt();
            this.rimColor = in.readInt();
            this.circleRadius = in.readInt();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.mProgress);
            out.writeFloat(this.mTargetProgress);
            out.writeByte((byte) (this.isSpinning ? 1 : 0));
            out.writeFloat(this.spinSpeed);
            out.writeInt(this.barWidth);
            out.writeInt(this.barColor);
            out.writeInt(this.rimWidth);
            out.writeInt(this.rimColor);
            out.writeInt(this.circleRadius);
        }
    }

    public ProgressWheel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel, 0, 0));
    }

    public ProgressWheel(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = (this.circleRadius + getPaddingLeft()) + getPaddingRight();
        int viewHeight = (this.circleRadius + getPaddingTop()) + getPaddingBottom();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(viewWidth, widthSize);
        } else {
            width = viewWidth;
        }
        if (heightMode == 1073741824 || widthMode == 1073741824) {
            height = heightSize;
        } else if (heightMode == Integer.MIN_VALUE) {
            height = Math.min(viewHeight, heightSize);
        } else {
            height = viewHeight;
        }
        setMeasuredDimension(width, height);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
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

    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        if (this.fillRadius) {
            this.circleBounds = new RectF((float) (this.barWidth + paddingLeft), (float) (this.barWidth + paddingTop), (float) ((layout_width - paddingRight) - this.barWidth), (float) ((layout_height - paddingBottom) - this.barWidth));
            return;
        }
        int circleDiameter = Math.min(Math.min((layout_width - paddingLeft) - paddingRight, (layout_height - paddingBottom) - paddingTop), (this.circleRadius * 2) - (this.barWidth * 2));
        int xOffset = ((((layout_width - paddingLeft) - paddingRight) - circleDiameter) / 2) + paddingLeft;
        int yOffset = ((((layout_height - paddingTop) - paddingBottom) - circleDiameter) / 2) + paddingTop;
        this.circleBounds = new RectF((float) (this.barWidth + xOffset), (float) (this.barWidth + yOffset), (float) ((xOffset + circleDiameter) - this.barWidth), (float) ((yOffset + circleDiameter) - this.barWidth));
    }

    private void parseAttributes(TypedArray a) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        this.barWidth = (int) TypedValue.applyDimension(1, (float) this.barWidth, metrics);
        this.rimWidth = (int) TypedValue.applyDimension(1, (float) this.rimWidth, metrics);
        this.circleRadius = (int) a.getDimension(4, (float) this.circleRadius);
        this.fillRadius = a.getBoolean(5, false);
        this.barWidth = (int) a.getDimension(2, (float) this.barWidth);
        this.rimWidth = (int) a.getDimension(7, (float) this.rimWidth);
        this.spinSpeed = a.getFloat(3, this.spinSpeed / 360.0f) * 360.0f;
        this.barSpinCycleTime = (double) a.getInt(1, (int) this.barSpinCycleTime);
        this.barColor = a.getColor(0, this.barColor);
        this.rimColor = a.getColor(6, this.rimColor);
        spin();
        a.recycle();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(this.circleBounds, 360.0f, 360.0f, false, this.rimPaint);
        boolean mustInvalidate = false;
        if (this.isSpinning) {
            mustInvalidate = true;
            long deltaTime = SystemClock.uptimeMillis() - this.lastTimeAnimated;
            float deltaNormalized = (((float) deltaTime) * this.spinSpeed) / 1000.0f;
            updateBarLength(deltaTime);
            this.mProgress += deltaNormalized;
            if (this.mProgress > 360.0f) {
                this.mProgress -= 360.0f;
            }
            this.lastTimeAnimated = SystemClock.uptimeMillis();
            Canvas canvas2 = canvas;
            canvas2.drawArc(this.circleBounds, this.mProgress - 90.0f, 40.0f + this.barExtraLength, false, this.barPaint);
        } else {
            if (this.mProgress != this.mTargetProgress) {
                mustInvalidate = true;
                this.mProgress = Math.min(this.mProgress + ((((float) (SystemClock.uptimeMillis() - this.lastTimeAnimated)) / 1000.0f) * this.spinSpeed), this.mTargetProgress);
                this.lastTimeAnimated = SystemClock.uptimeMillis();
            }
            canvas.drawArc(this.circleBounds, -90.0f, this.mProgress, false, this.barPaint);
        }
        if (mustInvalidate) {
            invalidate();
        }
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

    public boolean isSpinning() {
        return this.isSpinning;
    }

    public void resetCount() {
        this.mProgress = 0.0f;
        this.mTargetProgress = 0.0f;
        invalidate();
    }

    public void stopSpinning() {
        this.isSpinning = false;
        this.mProgress = 0.0f;
        this.mTargetProgress = 0.0f;
        invalidate();
    }

    public void spin() {
        this.lastTimeAnimated = SystemClock.uptimeMillis();
        this.isSpinning = true;
        invalidate();
    }

    public void setProgress(float progress) {
        if (this.isSpinning) {
            this.mProgress = 0.0f;
            this.isSpinning = false;
        }
        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (progress != this.mTargetProgress) {
            if (this.mProgress == this.mTargetProgress) {
                this.lastTimeAnimated = SystemClock.uptimeMillis();
            }
            this.mTargetProgress = Math.min(progress * 360.0f, 360.0f);
            invalidate();
        }
    }

    public void setInstantProgress(float progress) {
        if (this.isSpinning) {
            this.mProgress = 0.0f;
            this.isSpinning = false;
        }
        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (progress != this.mTargetProgress) {
            this.mTargetProgress = Math.min(progress * 360.0f, 360.0f);
            this.mProgress = this.mTargetProgress;
            this.lastTimeAnimated = SystemClock.uptimeMillis();
            invalidate();
        }
    }

    public Parcelable onSaveInstanceState() {
        WheelSavedState ss = new WheelSavedState(super.onSaveInstanceState());
        ss.mProgress = this.mProgress;
        ss.mTargetProgress = this.mTargetProgress;
        ss.isSpinning = this.isSpinning;
        ss.spinSpeed = this.spinSpeed;
        ss.barWidth = this.barWidth;
        ss.barColor = this.barColor;
        ss.rimWidth = this.rimWidth;
        ss.rimColor = this.rimColor;
        ss.circleRadius = this.circleRadius;
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof WheelSavedState) {
            WheelSavedState ss = (WheelSavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            this.mProgress = ss.mProgress;
            this.mTargetProgress = ss.mTargetProgress;
            this.isSpinning = ss.isSpinning;
            this.spinSpeed = ss.spinSpeed;
            this.barWidth = ss.barWidth;
            this.barColor = ss.barColor;
            this.rimWidth = ss.rimWidth;
            this.rimColor = ss.rimColor;
            this.circleRadius = ss.circleRadius;
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float getProgress() {
        return this.isSpinning ? -1.0f : this.mProgress / 360.0f;
    }

    public int getCircleRadius() {
        return this.circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        if (!this.isSpinning) {
            invalidate();
        }
    }

    public int getBarWidth() {
        return this.barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
        if (!this.isSpinning) {
            invalidate();
        }
    }

    public int getBarColor() {
        return this.barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        setupPaints();
        if (!this.isSpinning) {
            invalidate();
        }
    }

    public int getRimColor() {
        return this.rimColor;
    }

    public void setRimColor(int rimColor) {
        this.rimColor = rimColor;
        setupPaints();
        if (!this.isSpinning) {
            invalidate();
        }
    }

    public float getSpinSpeed() {
        return this.spinSpeed / 360.0f;
    }

    public void setSpinSpeed(float spinSpeed) {
        this.spinSpeed = 360.0f * spinSpeed;
    }

    public int getRimWidth() {
        return this.rimWidth;
    }

    public void setRimWidth(int rimWidth) {
        this.rimWidth = rimWidth;
        if (!this.isSpinning) {
            invalidate();
        }
    }
}
