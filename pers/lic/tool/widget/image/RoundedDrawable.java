package pers.lic.tool.widget.image;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView.ScaleType;

public class RoundedDrawable extends Drawable {
    public static final int DEFAULT_BORDER_COLOR = -16777216;
    public static final String TAG = "RoundedDrawable";
    private final int mBitmapHeight;
    private final Paint mBitmapPaint;
    private final RectF mBitmapRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final int mBitmapWidth;
    private ColorStateList mBorderColor = ColorStateList.valueOf(-16777216);
    private final Paint mBorderPaint;
    private final RectF mBorderRect = new RectF();
    private float mBorderWidth = 0.0f;
    private final RectF mBounds = new RectF();
    private float mCornerRadius = 0.0f;
    private final RectF mDrawableRect = new RectF();
    private boolean mOval = false;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;
    private final Matrix mShaderMatrix = new Matrix();

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];

        static {
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_START.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_XY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public RoundedDrawable(Bitmap bitmap) {
        this.mBitmapWidth = bitmap.getWidth();
        this.mBitmapHeight = bitmap.getHeight();
        this.mBitmapRect.set(0.0f, 0.0f, (float) this.mBitmapWidth, (float) this.mBitmapHeight);
        this.mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setStyle(Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        this.mBitmapPaint.setShader(this.mBitmapShader);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), -16777216));
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
    }

    public static RoundedDrawable fromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new RoundedDrawable(bitmap);
        }
        return null;
    }

    public static Drawable fromDrawable(Drawable drawable) {
        if (drawable == null || (drawable instanceof RoundedDrawable)) {
            return drawable;
        }
        if (drawable instanceof LayerDrawable) {
            Drawable ld = (LayerDrawable) drawable;
            int num = ld.getNumberOfLayers();
            for (int i = 0; i < num; i++) {
                ld.setDrawableByLayerId(ld.getId(i), fromDrawable(ld.getDrawable(i)));
            }
            return ld;
        }
        Bitmap bm = drawableToBitmap(drawable);
        if (bm != null) {
            return new RoundedDrawable(bm);
        }
        Log.w(TAG, "Failed to create bitmap from drawable!");
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(Math.max(drawable.getIntrinsicWidth(), 1), Math.max(drawable.getIntrinsicHeight(), 1), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isStateful() {
        return this.mBorderColor.isStateful();
    }

    protected boolean onStateChange(int[] state) {
        int newColor = this.mBorderColor.getColorForState(state, 0);
        if (this.mBorderPaint.getColor() == newColor) {
            return super.onStateChange(state);
        }
        this.mBorderPaint.setColor(newColor);
        return true;
    }

    private void updateShaderMatrix() {
        float dx;
        float dy;
        float scale;
        switch (AnonymousClass1.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
            case 1:
                this.mBorderRect.set(this.mBounds);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.set(null);
                this.mShaderMatrix.setTranslate((float) ((int) (((this.mBorderRect.width() - ((float) this.mBitmapWidth)) * 0.5f) + 0.5f)), (float) ((int) (((this.mBorderRect.height() - ((float) this.mBitmapHeight)) * 0.5f) + 0.5f)));
                break;
            case 2:
                this.mBorderRect.set(this.mBounds);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.set(null);
                dx = 0.0f;
                dy = 0.0f;
                if (((float) this.mBitmapWidth) * this.mBorderRect.height() > this.mBorderRect.width() * ((float) this.mBitmapHeight)) {
                    scale = this.mBorderRect.height() / ((float) this.mBitmapHeight);
                    dx = (this.mBorderRect.width() - (((float) this.mBitmapWidth) * scale)) * 0.5f;
                } else {
                    scale = this.mBorderRect.width() / ((float) this.mBitmapWidth);
                    dy = (this.mBorderRect.height() - (((float) this.mBitmapHeight) * scale)) * 0.5f;
                }
                this.mShaderMatrix.setScale(scale, scale);
                this.mShaderMatrix.postTranslate(((float) ((int) (dx + 0.5f))) + this.mBorderWidth, ((float) ((int) (dy + 0.5f))) + this.mBorderWidth);
                break;
            case 3:
                this.mShaderMatrix.set(null);
                if (((float) this.mBitmapWidth) > this.mBounds.width() || ((float) this.mBitmapHeight) > this.mBounds.height()) {
                    scale = Math.min(this.mBounds.width() / ((float) this.mBitmapWidth), this.mBounds.height() / ((float) this.mBitmapHeight));
                } else {
                    scale = 1.0f;
                }
                dx = (float) ((int) (((this.mBounds.width() - (((float) this.mBitmapWidth) * scale)) * 0.5f) + 0.5f));
                dy = (float) ((int) (((this.mBounds.height() - (((float) this.mBitmapHeight) * scale)) * 0.5f) + 0.5f));
                this.mShaderMatrix.setScale(scale, scale);
                this.mShaderMatrix.postTranslate(dx, dy);
                this.mBorderRect.set(this.mBitmapRect);
                this.mShaderMatrix.mapRect(this.mBorderRect);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, ScaleToFit.FILL);
                break;
            case 5:
                this.mBorderRect.set(this.mBitmapRect);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, ScaleToFit.END);
                this.mShaderMatrix.mapRect(this.mBorderRect);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, ScaleToFit.FILL);
                break;
            case 6:
                this.mBorderRect.set(this.mBitmapRect);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, ScaleToFit.START);
                this.mShaderMatrix.mapRect(this.mBorderRect);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, ScaleToFit.FILL);
                break;
            case 7:
                this.mBorderRect.set(this.mBounds);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.set(null);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, ScaleToFit.FILL);
                break;
            default:
                this.mBorderRect.set(this.mBitmapRect);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, ScaleToFit.CENTER);
                this.mShaderMatrix.mapRect(this.mBorderRect);
                this.mBorderRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
                this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, ScaleToFit.FILL);
                break;
        }
        this.mDrawableRect.set(this.mBorderRect);
        this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mBounds.set(bounds);
        updateShaderMatrix();
    }

    public void draw(Canvas canvas) {
        if (this.mOval) {
            if (this.mBorderWidth > 0.0f) {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
                canvas.drawOval(this.mBorderRect, this.mBorderPaint);
                return;
            }
            canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
        } else if (this.mBorderWidth > 0.0f) {
            canvas.drawRoundRect(this.mDrawableRect, Math.max(this.mCornerRadius, 0.0f), Math.max(this.mCornerRadius, 0.0f), this.mBitmapPaint);
            canvas.drawRoundRect(this.mBorderRect, this.mCornerRadius, this.mCornerRadius, this.mBorderPaint);
        } else {
            canvas.drawRoundRect(this.mDrawableRect, this.mCornerRadius, this.mCornerRadius, this.mBitmapPaint);
        }
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int alpha) {
        this.mBitmapPaint.setAlpha(alpha);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter cf) {
        this.mBitmapPaint.setColorFilter(cf);
        invalidateSelf();
    }

    public void setDither(boolean dither) {
        this.mBitmapPaint.setDither(dither);
        invalidateSelf();
    }

    public void setFilterBitmap(boolean filter) {
        this.mBitmapPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public RoundedDrawable setCornerRadius(float radius) {
        this.mCornerRadius = radius;
        return this;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public RoundedDrawable setBorderWidth(float width) {
        this.mBorderWidth = width;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        return this;
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public RoundedDrawable setBorderColor(int color) {
        return setBorderColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public RoundedDrawable setBorderColor(ColorStateList colors) {
        if (colors == null) {
            colors = ColorStateList.valueOf(0);
        }
        this.mBorderColor = colors;
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), -16777216));
        return this;
    }

    public boolean isOval() {
        return this.mOval;
    }

    public RoundedDrawable setOval(boolean oval) {
        this.mOval = oval;
        return this;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public RoundedDrawable setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER;
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            updateShaderMatrix();
        }
        return this;
    }

    public Bitmap toBitmap() {
        return drawableToBitmap(this);
    }
}
