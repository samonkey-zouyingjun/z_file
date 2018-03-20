package zidoo.tarot.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import com.zidoo.custom.animation.ZidooAnimationHolder;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;

public class TTextView extends GameObject {
    public static final String TAG = "TTextView";
    static final int TEXT_LINE = 1;
    static final int TEXT_MAQUEUE = 256;
    static final int TEXT_MAQUEUE_VALUE = 4096;
    static final int TEXT_MAQUEUE_WAIT = 65536;
    static final int TEXT_NORMAL = 0;
    static final int TEXT_SINGLELINE = 16;
    protected static final int UPDATE_HEIGHT = 16;
    protected static final int UPDATE_INITIAL = 0;
    protected static final int UPDATE_NEED = 273;
    protected static final int UPDATE_STATE_CHANGE = 256;
    protected static final int UPDATE_WIDTH = 1;
    private Marquee mMarquee;
    private TextGravity mTGravity;
    private String mText;
    private float mTextAbsoluteSpacing;
    private Align mTextAlign;
    private Alignment mTextAlignment;
    private int mTextGravity;
    private int mTextHeight;
    private int mTextLines;
    protected TextPaint mTextPaint;
    private float mTextRelativeSpacing;
    int mTextState;
    private int mTextUpdateState;
    private int mTextWidth;

    class Marquee {
        int cTimes = 0;
        float offset = 0.0f;
        float speed;
        int times;
        long waitTime;
        float width;

        Marquee(int times, float speed) {
            this.times = times;
            this.speed = speed;
        }
    }

    private enum TextGravity {
        TOP,
        CENTER,
        BOTTOM
    }

    public TTextView(GLContext glContext) {
        super(glContext);
        this.mText = "";
        this.mTextAlignment = Alignment.ALIGN_NORMAL;
        this.mTextAlign = Align.LEFT;
        this.mTextRelativeSpacing = 1.0f;
        this.mTextAbsoluteSpacing = 0.0f;
        this.mTextGravity = 0;
        this.mTGravity = TextGravity.TOP;
        this.mTextLines = 1;
        this.mMarquee = null;
        this.mTextUpdateState = 0;
        this.mTextState = 0;
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTypeface(glContext.getConfig().getTypeface());
    }

    public TTextView(GLContext glContext, String text) {
        this(glContext);
        setText(this.mText);
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
        textMaterial.MaterialName = "BoardMaterial";
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

    public void update(GL10 gl) {
        if (isShow()) {
            if (this.mTextUpdateState == UPDATE_NEED) {
                Bitmap bitmap = null;
                if (isLineText()) {
                    bitmap = drawTextWithCount(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlign, this.mTextRelativeSpacing, this.mTextAbsoluteSpacing, this.mTextLines, new TextPaint(this.mTextPaint));
                    this.mTextUpdateState &= -257;
                } else if (!isSingleLine()) {
                    bitmap = drawText(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlignment, this.mTextRelativeSpacing, this.mTextAbsoluteSpacing);
                    this.mTextUpdateState &= -257;
                } else if (!isMarquee() || (this.mTextState & 4096) == 0) {
                    bitmap = drawSingleLineText(this.mText, this.mTextWidth, this.mTextHeight, this.mTextAlign, this.mTextPaint);
                    this.mTextUpdateState &= -257;
                } else if ((this.mTextState & 65536) != 0) {
                    if (System.currentTimeMillis() > this.mMarquee.waitTime + 2000) {
                        this.mMarquee.offset = 0.0f;
                        this.mTextState &= -65537;
                    }
                    invalidate();
                } else {
                    bitmap = drawMarqueue(this.mText, this.mTextWidth, this.mTextHeight, this.mMarquee.offset, this.mMarquee.width, this.mTextPaint);
                    Marquee marquee;
                    if (this.mMarquee.offset > this.mMarquee.width + 70.0f) {
                        if (this.mMarquee.times > 0) {
                            marquee = this.mMarquee;
                            int i = marquee.cTimes + 1;
                            marquee.cTimes = i;
                            if (i >= this.mMarquee.times) {
                                this.mTextUpdateState &= -257;
                            }
                        }
                        this.mMarquee.waitTime = System.currentTimeMillis();
                        this.mTextState |= 65536;
                    } else {
                        marquee = this.mMarquee;
                        marquee.offset += this.mMarquee.speed;
                    }
                    invalidate();
                }
                if (bitmap != null) {
                    this.mMaterials[0].recycle();
                    this.mMaterials[0].texture = GLResources.genTTextrue(bitmap, this.mTextWidth, this.mTextHeight);
                }
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

    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setWidth(float w) {
        super.setWidth(w);
        setTextWidth((int) (getDisplay().sRatioX * w));
    }

    public void setHeight(float h) {
        super.setHeight(h);
        setTextHeight((int) (getDisplay().sRatioY * h));
    }

    public void setTextWidth(int width) {
        this.mTextWidth = width;
        if (width > 0) {
            this.mTextUpdateState |= 1;
        }
    }

    public void setTextHeight(int height) {
        this.mTextHeight = height;
        if (height > 0) {
            this.mTextUpdateState |= 16;
        }
    }

    public void setText(String text) {
        if (!this.mText.equals(text)) {
            this.mText = text;
            this.mTextUpdateState |= 256;
            if (isMarquee()) {
                resetMarquee(this.mMarquee.times, this.mMarquee.speed);
            }
        }
    }

    public void setTextResource(int resId) {
        setText(getContext().getString(resId));
    }

    public String getText() {
        return this.mText;
    }

    public void setTextSize(float textSize) {
        this.mTextPaint.setTextSize(getDisplay().sRatioY * textSize);
    }

    public float getTextSize() {
        return this.mTextPaint.getTextSize() / getDisplay().sRatioY;
    }

    public void setTextGravity(int gravity) {
        this.mTextGravity = gravity;
        switch (this.mTextGravity) {
            case 1:
                this.mTextAlign = Align.CENTER;
                this.mTextAlignment = Alignment.ALIGN_CENTER;
                return;
            case 3:
                this.mTextAlign = Align.LEFT;
                this.mTextAlignment = Alignment.ALIGN_NORMAL;
                return;
            case 5:
                this.mTextAlign = Align.RIGHT;
                this.mTextAlignment = Alignment.ALIGN_OPPOSITE;
                return;
            case 16:
                this.mTGravity = TextGravity.CENTER;
                return;
            case 17:
                this.mTextAlign = Align.CENTER;
                this.mTextAlignment = Alignment.ALIGN_CENTER;
                this.mTGravity = TextGravity.CENTER;
                return;
            case 19:
                this.mTextAlign = Align.LEFT;
                this.mTextAlignment = Alignment.ALIGN_NORMAL;
                this.mTGravity = TextGravity.CENTER;
                return;
            case MotionEventCompat.AXIS_WHEEL /*21*/:
                this.mTextAlign = Align.RIGHT;
                this.mTextAlignment = Alignment.ALIGN_OPPOSITE;
                this.mTGravity = TextGravity.CENTER;
                return;
            case 48:
                this.mTGravity = TextGravity.TOP;
                return;
            case 49:
                this.mTextAlign = Align.CENTER;
                this.mTextAlignment = Alignment.ALIGN_CENTER;
                this.mTGravity = TextGravity.TOP;
                return;
            case 51:
                this.mTextAlign = Align.LEFT;
                this.mTextAlignment = Alignment.ALIGN_NORMAL;
                this.mTGravity = TextGravity.TOP;
                return;
            case 53:
                this.mTextAlign = Align.RIGHT;
                this.mTextAlignment = Alignment.ALIGN_OPPOSITE;
                this.mTGravity = TextGravity.TOP;
                return;
            case 80:
                this.mTGravity = TextGravity.BOTTOM;
                return;
            case 81:
                this.mTextAlign = Align.CENTER;
                this.mTextAlignment = Alignment.ALIGN_CENTER;
                this.mTGravity = TextGravity.BOTTOM;
                return;
            case 83:
                this.mTextAlign = Align.LEFT;
                this.mTextAlignment = Alignment.ALIGN_NORMAL;
                this.mTGravity = TextGravity.BOTTOM;
                return;
            case 85:
                this.mTextAlign = Align.RIGHT;
                this.mTextAlignment = Alignment.ALIGN_OPPOSITE;
                this.mTGravity = TextGravity.BOTTOM;
                return;
            default:
                return;
        }
    }

    public int getTextGravity() {
        return this.mTextGravity;
    }

    public void setSingleLine(boolean isSingleLine) {
        if (isSingleLine) {
            this.mTextState |= 16;
            setLineText(false);
            return;
        }
        this.mTextState &= -17;
    }

    public boolean isSingleLine() {
        return (this.mTextState & 16) != 0;
    }

    public void setMarquee(int times, float pixelsPerSecond) {
        this.mTextUpdateState |= 256;
        this.mTextState |= 256;
        resetMarquee(times, pixelsPerSecond);
    }

    public void setMarquee(int times) {
        setMarquee(times, 1.5f);
    }

    public void setMarquee(boolean isMarquee) {
        if (isMarquee) {
            setMarquee(0, 1.5f);
        } else {
            this.mTextState &= -257;
        }
    }

    public boolean isMarquee() {
        return (this.mTextState & 256) != 0;
    }

    public void setLineText(boolean isLineText) {
        if (isLineText) {
            this.mTextState |= 1;
            setSingleLine(false);
            return;
        }
        this.mTextState &= -2;
    }

    public boolean isLineText() {
        return (this.mTextState & 1) != 0;
    }

    public void setTextMaxLines(int mTextLines) {
        this.mTextLines = mTextLines;
        setLineText(true);
    }

    public int getTextLines() {
        return this.mTextLines;
    }

    public void setTextColor(int color) {
        if (this.mTextPaint.getColor() != color) {
            this.mTextUpdateState |= 256;
            this.mTextPaint.setColor(color);
        }
    }

    public int getTextColor() {
        return this.mTextPaint.getColor();
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.mTextPaint.setShadowLayer(radius, dx, dy, color);
    }

    public void clearShadowLayer() {
        this.mTextPaint.clearShadowLayer();
    }

    public int getTextAlpha() {
        return this.mTextPaint.getAlpha();
    }

    public void setTextAlpha(int textAlpha) {
        this.mTextPaint.setAlpha(textAlpha);
    }

    public float getTextScaleX() {
        return this.mTextPaint.getTextScaleX();
    }

    public void setTextScaleX(float textScaleX) {
        this.mTextPaint.setTextScaleX(textScaleX);
    }

    public Typeface getTextTypeface() {
        return this.mTextPaint.getTypeface();
    }

    public void setTextTypeface(Typeface textTypeface) {
        this.mTextPaint.setTypeface(textTypeface);
    }

    public float getTextRelativeSpacing() {
        return this.mTextRelativeSpacing;
    }

    public void setTextRelativeSpacing(float textRelativeSpacing) {
        this.mTextRelativeSpacing = textRelativeSpacing;
    }

    public float getTextAbsoluteSpacing() {
        return this.mTextAbsoluteSpacing;
    }

    public void setTextAbsoluteSpacing(float textAbsoluteSpacing) {
        this.mTextAbsoluteSpacing = textAbsoluteSpacing;
    }

    private Bitmap drawText(int width, int height, String text, Alignment textAlignment, float textRelaSpac, float textAbsSpac) {
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        StaticLayout sl = new StaticLayout(text, this.mTextPaint, width, textAlignment, textRelaSpac, textAbsSpac, false);
        if (this.mTGravity == TextGravity.CENTER) {
            int h = sl.getHeight();
            if (h < height) {
                canvas.translate(0.0f, (float) ((height - h) / 2));
            }
        } else if (this.mTGravity == TextGravity.BOTTOM) {
            canvas.translate(0.0f, (float) (height - sl.getHeight()));
        }
        sl.draw(canvas);
        return textBitmap;
    }

    private Bitmap drawTextWithCount(int width, int height, String text, Align align, float textRelaSpac, float textAbsSpac, int count, TextPaint paint) {
        int i;
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        paint.setTextAlign(align);
        int baseX = 0;
        if (align == Align.CENTER) {
            baseX = width / 2;
        } else if (align == Align.RIGHT) {
            baseX = width;
        }
        FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int h = fontMetricsInt.descent - fontMetricsInt.ascent;
        ArrayList<String> arrayList = new ArrayList(count);
        int c = 0;
        for (i = 0; i < count; i++) {
            int n = paint.breakText(text, true, (float) width, null);
            if (n == 0) {
                break;
            }
            String temp = text.substring(0, n);
            if (temp.contains("\n")) {
                n = temp.indexOf("\n");
                temp = temp.substring(0, n);
                text = text.substring(n + 1);
            } else {
                text = text.substring(n);
                if (count - 1 == i) {
                    int t = paint.breakText(temp, true, (float) width, null);
                    if (text.length() > 0) {
                        temp = temp.substring(0, t - 2) + "...";
                    }
                }
            }
            arrayList.add(temp);
            c++;
        }
        int H = 0;
        if (c > 0) {
            H = (int) (((((float) (c * h)) * textRelaSpac) - (((float) h) * (textRelaSpac - 1.0f))) + (((float) (c - 1)) * textAbsSpac));
        }
        int baseY = 0;
        if (this.mTGravity == TextGravity.TOP || H > height) {
            baseY = -fontMetricsInt.ascent;
        } else {
            if (this.mTGravity == TextGravity.CENTER) {
                baseY = ((height - H) / 2) - fontMetricsInt.ascent;
            } else {
                if (this.mTGravity == TextGravity.BOTTOM) {
                    baseY = (height - H) - fontMetricsInt.ascent;
                }
            }
        }
        for (i = 0; i < arrayList.size(); i++) {
            canvas.drawText((String) arrayList.get(i), (float) baseX, (float) baseY, paint);
            baseY = (int) (((float) baseY) + ((((float) h) * textRelaSpac) + textAbsSpac));
        }
        return textBitmap;
    }

    private Bitmap drawSingleLineText(String text, int width, int height, Align align, TextPaint paint) {
        int baseY;
        paint.setTextAlign(align);
        if (paint.measureText(text) > ((float) width)) {
            text = text.substring(0, paint.breakText(text, true, (float) width, null) - 1) + "..";
        }
        int baseX = 0;
        FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
        if (align == Align.CENTER) {
            baseX = width / 2;
        } else if (align == Align.RIGHT) {
            baseX = width;
        }
        if (this.mTGravity == TextGravity.TOP) {
            baseY = -fontMetricsInt.ascent;
        } else if (this.mTGravity == TextGravity.CENTER) {
            baseY = (height - ((height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2)) - fontMetricsInt.bottom;
        } else {
            baseY = height - fontMetricsInt.bottom;
        }
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(textBitmap).drawText(text, (float) baseX, (float) baseY, paint);
        return textBitmap;
    }

    private Bitmap drawMarqueue(String text, int width, int height, float offset, float textWidth, TextPaint paint) {
        float baseY;
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        if (this.mTGravity == TextGravity.TOP) {
            baseY = (float) (-fontMetricsInt.ascent);
        } else if (this.mTGravity == TextGravity.CENTER) {
            baseY = (float) ((height - ((height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2)) - fontMetricsInt.bottom);
        } else {
            baseY = (float) (height - fontMetricsInt.bottom);
        }
        if (offset > (textWidth + 70.0f) - ((float) width)) {
            int nextX = (int) ((textWidth - offset) + 70.0f);
            canvas.save();
            canvas.drawText(text, (float) nextX, baseY, paint);
            canvas.restore();
        }
        canvas.drawText(text, -offset, baseY, paint);
        return textBitmap;
    }

    private void resetMarquee(int times, float speed) {
        float tw = this.mTextPaint.measureText(this.mText);
        this.mMarquee = new Marquee(times, speed);
        if (tw > ((float) this.mTextWidth)) {
            this.mMarquee.width = tw;
            this.mTextState |= 4096;
            return;
        }
        this.mTextState &= -4097;
    }
}
