package zidoo.tarot.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.text.TextPaint;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class WrapSingleLineTextView extends GameObject {
    static final int TEXT_MAQUEUE = 256;
    static final int TEXT_MAQUEUE_WAIT = 4352;
    static final int TEXT_NORMAL = 0;
    static final int TEXT_SINGLELINE = 16;
    static final int TEXT_UPDATE = 1;
    float mMarqueeOffset;
    float mMarqueeSpeed = 1.5f;
    long mMarqueeWaitTime;
    private float mMaxWidth = 200.0f;
    float mMeasureWidth;
    TextPaint mPaint = new TextPaint();
    private String mText = "";
    private int mTextHeight = 0;
    int mTextState = 0;
    private int mTextWidth = 0;

    public WrapSingleLineTextView(GLContext glContext) {
        super(glContext);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTypeface(glContext.getConfig().getTypeface());
    }

    public WrapSingleLineTextView(GLContext glContext, float textSize, int color, int textAlpha, float textScaleX, Typeface typeface, float skewX) {
        super(glContext);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(textSize);
        this.mPaint.setColor(color);
        this.mPaint.setAlpha(textAlpha);
        this.mPaint.setTextScaleX(textScaleX);
        this.mPaint.setTypeface(typeface);
        this.mPaint.setTextSkewX(skewX);
        this.mPaint.setTypeface(glContext.getConfig().getTypeface());
    }

    public WrapSingleLineTextView(GLContext glContext, float textSize, int color) {
        super(glContext);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(textSize);
        this.mPaint.setColor(color);
        this.mPaint.setTypeface(glContext.getConfig().getTypeface());
    }

    public void setText(String text) {
        if (!this.mText.equals(text)) {
            this.mText = text;
            this.mMeasureWidth = this.mPaint.measureText(text);
            if (this.mMeasureWidth == 0.0f) {
                this.mMeasureWidth = 1.0f;
                this.mTextWidth = 1;
                this.mTextState = 1;
            } else if (this.mMeasureWidth > this.mMaxWidth) {
                this.mTextWidth = (int) this.mMaxWidth;
                this.mTextState = 16;
            } else {
                this.mTextWidth = (int) this.mMeasureWidth;
                if (this.mTextWidth == 0) {
                    this.mTextWidth = 1;
                }
                this.mTextState = 1;
            }
            setWidth(((float) this.mTextWidth) / getDisplay().sRatioX);
            invalidate();
        }
    }

    public String getText() {
        return this.mText;
    }

    public void setHeight(float h) {
        super.setHeight(h);
        this.mTextHeight = (int) (getDisplay().sRatioY * h);
    }

    public TextPaint getTextPaint() {
        return this.mPaint;
    }

    public void setTextSize(float textSize) {
        this.mPaint.setTextSize(getDisplay().sRatioY * textSize);
    }

    public float getTextSize() {
        return this.mPaint.getTextSize();
    }

    public void setTextColor(int color) {
        this.mPaint.setColor(color);
    }

    public int getTextColor() {
        return this.mPaint.getColor();
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.mPaint.setShadowLayer(radius, dx, dy, color);
    }

    public void clearShadowLayer() {
        this.mPaint.clearShadowLayer();
    }

    public int getTextAlpha() {
        return this.mPaint.getAlpha();
    }

    public void setTextAlpha(int textAlpha) {
        this.mPaint.setAlpha(textAlpha);
    }

    public float getTextScaleX() {
        return this.mPaint.getTextScaleX();
    }

    public void setTextScaleX(float textScaleX) {
        this.mPaint.setTextScaleX(textScaleX);
    }

    public Typeface getTextTypeface() {
        return this.mPaint.getTypeface();
    }

    public void setTextTypeface(Typeface typeface) {
    }

    @Deprecated
    public void setWidth(float w) {
        super.setWidth(w);
    }

    public void setMaxWidth(float width) {
        this.mMaxWidth = getDisplay().sRatioX * width;
    }

    public void setMarqueeSpeed(float pixel) {
        this.mMarqueeSpeed = pixel;
    }

    public float getMarqueeSpeed() {
        return this.mMarqueeSpeed;
    }

    public void setTextSkewX(float skewX) {
        this.mPaint.setTextSkewX(skewX);
    }

    public float getTextSkewX() {
        return this.mPaint.getTextSkewX();
    }

    public float getMaxWidth() {
        return this.mMaxWidth;
    }

    public void update(GL10 gl) {
        if (isShow()) {
            switch (this.mTextState) {
                case 1:
                    Bitmap ub = drawText(this.mText, this.mTextWidth, this.mTextHeight, this.mPaint);
                    this.mMaterials[0].recycle();
                    this.mMaterials[0].texture = GLResources.genTTextrue(ub, this.mTextWidth, this.mTextHeight);
                    this.mTextState = 0;
                    break;
                case 16:
                    Bitmap sb = drawSingleLineText(this.mText, this.mTextWidth, this.mTextHeight, this.mPaint, this.mMeasureWidth);
                    this.mMaterials[0].recycle();
                    this.mMaterials[0].texture = GLResources.genTTextrue(sb, this.mTextWidth, this.mTextHeight);
                    this.mTextState = 0;
                    break;
                case 256:
                    Bitmap mb = drawMarqueue(this.mText, this.mTextWidth, this.mTextHeight, this.mPaint, this.mMarqueeOffset, this.mMeasureWidth);
                    this.mMaterials[0].recycle();
                    this.mMaterials[0].texture = GLResources.genTTextrue(mb, this.mTextWidth, this.mTextHeight);
                    if (this.mMarqueeOffset > this.mMeasureWidth + 70.0f) {
                        this.mMarqueeWaitTime = System.currentTimeMillis();
                        this.mTextState = TEXT_MAQUEUE_WAIT;
                    } else {
                        this.mMarqueeOffset += this.mMarqueeSpeed;
                    }
                    invalidate();
                    break;
                case TEXT_MAQUEUE_WAIT /*4352*/:
                    if (System.currentTimeMillis() > this.mMarqueeWaitTime + 2000) {
                        this.mMarqueeOffset = 0.0f;
                        this.mTextState = 256;
                    }
                    invalidate();
                    break;
            }
            gl.glPushMatrix();
            onAnimation();
            onTransform(gl);
            onRayCast();
            onMaterial(this.mMaterials[0], gl);
            onMesh(this.mMeshes[0], gl);
            gl.glPopMatrix();
        }
    }

    public void marquee() {
        if (this.mMeasureWidth > this.mMaxWidth && (this.mTextState & 256) == 0) {
            this.mMarqueeOffset = 0.0f;
            this.mTextState = 256;
            invalidate();
        }
    }

    public void stopMarquee() {
        if ((this.mTextState & 256) != 0) {
            this.mTextState = this.mMeasureWidth > this.mMaxWidth ? 16 : 1;
            invalidate();
        }
    }

    private Bitmap drawText(String text, int width, int height, TextPaint paint) {
        FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(textBitmap).drawText(text, 0.0f, (float) ((height - (((fontMetricsInt.ascent + height) - fontMetricsInt.descent) / 2)) - fontMetricsInt.bottom), paint);
        return textBitmap;
    }

    private Bitmap drawSingleLineText(String text, int width, int height, TextPaint paint, float measureWidth) {
        if (measureWidth > ((float) width)) {
            text = text.substring(0, paint.breakText(text, true, (float) width, null) - 1) + "..";
        }
        FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(textBitmap).drawText(text, 0.0f, (float) ((height - (((fontMetricsInt.ascent + height) - fontMetricsInt.descent) / 2)) - fontMetricsInt.bottom), paint);
        return textBitmap;
    }

    private Bitmap drawMarqueue(String text, int width, int height, TextPaint paint, float offset, float textWidth) {
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        float baseY = (float) ((height - (((fontMetricsInt.ascent + height) - fontMetricsInt.descent) / 2)) - fontMetricsInt.bottom);
        if (offset > (textWidth + 70.0f) - ((float) width)) {
            int nextX = (int) ((textWidth - offset) + 70.0f);
            canvas.save();
            canvas.drawText(text, (float) nextX, baseY, paint);
            canvas.restore();
        }
        canvas.drawText(text, -offset, baseY, paint);
        return textBitmap;
    }

    protected void initMesh() {
        this.mMeshes = new Mesh[1];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(new float[]{5.0f, 5.0f, 0.0f, -5.0f, 5.0f, 0.0f, -5.0f, -5.0f, -0.0f, 5.0f, -5.0f, -0.0f});
        this.mMeshes[0].setNormals(new float[]{0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f, 0.0f, -0.0f, 1.0f});
        this.mMeshes[0].setCoordinates(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        Mesh mesh = this.mMeshes[0];
        short[] sArr = new short[6];
        sArr[1] = (short) 1;
        sArr[2] = (short) 2;
        sArr[3] = (short) 2;
        sArr[4] = (short) 3;
        mesh.setIndices(sArr);
    }

    protected void initMaterial() {
        Material textMaterial = new Material();
        this.mMaterials = new Material[1];
        textMaterial.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
        textMaterial.setDiffuse(0.8f, 0.8f, 0.8f, 1.0f);
        textMaterial.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        textMaterial.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        textMaterial.setAlpha(0.99999f);
        textMaterial.setOpticalDensity(1.5f);
        textMaterial.setShininess(30.0f);
        textMaterial.setTransparent(ZidooAnimationHolder.F);
        textMaterial.setTransmissionFilter(0.7f, 0.7f, 0.7f, 0.7f);
        textMaterial.setIllumination(2);
        this.mMaterials[0] = textMaterial;
    }
}
