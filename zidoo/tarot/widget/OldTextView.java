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

@Deprecated
public class OldTextView extends GameObject {
    public static final String TAG = "TTextView";
    protected static final int UPDATE_HEIGHT = 16;
    protected static final int UPDATE_INITIAL = 0;
    protected static final int UPDATE_NEED = 273;
    protected static final int UPDATE_STATE_CHANGE = 256;
    protected static final int UPDATE_WIDTH = 1;
    private boolean isDrawMarquee;
    private boolean isLineText;
    private boolean isMarquee;
    private boolean isShadow;
    private boolean isSingleLine;
    private Marquee mMarquee;
    private TextGravity mTGravity;
    private String mText;
    private float mTextAbsoluteSpacing;
    private Align mTextAlign;
    private Alignment mTextAlignment;
    private int mTextGravity;
    private int mTextHeight;
    private int mTextLines;
    private StaticLayout mTextMarqueeLayout;
    protected TextPaint mTextPaint;
    private float mTextRelativeSpacing;
    private int mTextUpdateState;
    private int mTextWidth;

    class Marquee {
        int curMarqueeTimes = 0;
        long currentTime = 0;
        int pixels = 0;
        int pixlesPerSecond = 100;
        long startTime = 0;
        int times = 1;
        int width = 0;

        Marquee() {
        }
    }

    private enum TextGravity {
        TOP,
        CENTER,
        BOTTOM
    }

    public OldTextView(GLContext glContext) {
        super(glContext);
        this.mText = "";
        this.mTextAlignment = Alignment.ALIGN_NORMAL;
        this.mTextAlign = Align.LEFT;
        this.mTextRelativeSpacing = 1.0f;
        this.mTextAbsoluteSpacing = 0.0f;
        this.mTextGravity = 0;
        this.mTGravity = TextGravity.TOP;
        this.mTextLines = 1;
        this.isSingleLine = false;
        this.isShadow = false;
        this.isLineText = false;
        this.isMarquee = false;
        this.mMarquee = null;
        this.isDrawMarquee = true;
        this.mTextUpdateState = 0;
        this.mTextMarqueeLayout = null;
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTypeface(glContext.getConfig().getTypeface());
    }

    public OldTextView(GLContext glContext, String text) {
        this(glContext);
        this.mText = text;
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
        if (this.mTextUpdateState == UPDATE_NEED) {
            drawText();
        }
        super.update(gl);
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
            if (this.isMarquee) {
                this.mTextMarqueeLayout = null;
                this.isDrawMarquee = true;
            }
        }
    }

    private void drawText() {
        Bitmap bitmap;
        if (this.isLineText) {
            bitmap = drawTextWithCount(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlign, this.mTextRelativeSpacing, this.mTextAbsoluteSpacing, this.mTextLines, this.isShadow);
            this.mTextUpdateState &= -257;
        } else if (!this.isSingleLine) {
            bitmap = drawText(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlignment, this.mTextRelativeSpacing, this.mTextAbsoluteSpacing);
            this.mTextUpdateState &= -257;
        } else if (this.isMarquee && this.isDrawMarquee) {
            if (this.mTextMarqueeLayout == null) {
                this.mTextMarqueeLayout = getMarqueeLayout(this.mText, this.mTextAlignment);
            }
            if (this.mMarquee.width < this.mTextWidth) {
                bitmap = drawSinglineText(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlign, this.isShadow);
                this.mTextUpdateState &= -257;
                if (bitmap != null) {
                    this.mMaterials[0].recycle();
                    this.mMaterials[0].texture = GLResources.genTTextrue(bitmap, this.mTextWidth, this.mTextHeight);
                    return;
                }
            }
            this.mMarquee.currentTime = System.currentTimeMillis();
            this.mMarquee.pixels = (int) ((0.001d * ((double) this.mMarquee.pixlesPerSecond)) * ((double) (this.mMarquee.currentTime - this.mMarquee.startTime)));
            bitmap = marquee(this.mMarquee.pixels);
            if (this.mMarquee.pixels > this.mMarquee.width + 65) {
                if (this.mMarquee.times > 0) {
                    if (this.mMarquee.curMarqueeTimes < this.mMarquee.times) {
                        Marquee marquee = this.mMarquee;
                        marquee.curMarqueeTimes++;
                    } else {
                        this.mMaterials[0].recycle();
                        this.mMaterials[0].texture = GLResources.genTTextrue(bitmap, this.mTextWidth, this.mTextHeight);
                        return;
                    }
                }
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            OldTextView.this.isDrawMarquee = false;
                            Thread.sleep(3000);
                            OldTextView.this.isDrawMarquee = true;
                            Marquee access$1 = OldTextView.this.mMarquee;
                            Marquee access$12 = OldTextView.this.mMarquee;
                            long currentTimeMillis = System.currentTimeMillis();
                            access$12.currentTime = currentTimeMillis;
                            access$1.startTime = currentTimeMillis;
                            OldTextView.this.invalidate();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                invalidate();
            }
        } else {
            bitmap = drawSinglineText(this.mTextWidth, this.mTextHeight, this.mText, this.mTextAlign, this.isShadow);
            if (!this.isMarquee) {
                this.mTextUpdateState &= -257;
            }
        }
        if (bitmap != null) {
            this.mMaterials[0].recycle();
            this.mMaterials[0].texture = GLResources.genTTextrue(bitmap, this.mTextWidth, this.mTextHeight);
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
        return this.mTextPaint.getTextSize();
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
        this.isSingleLine = isSingleLine;
    }

    public boolean isSingleLine() {
        return this.isSingleLine;
    }

    public void setMarquee(int times, int pixelsPerSecond) {
        this.isMarquee = true;
        this.mTextUpdateState |= 256;
        this.isDrawMarquee = true;
        initMarquee(times, pixelsPerSecond);
    }

    public void setMarquee(boolean isMarquee) {
        this.isMarquee = isMarquee;
        this.mTextUpdateState |= 256;
        this.isDrawMarquee = true;
        initMarquee(1, 100);
    }

    public boolean isMarquee() {
        return this.isMarquee;
    }

    public void setLineText(boolean isLineText) {
        this.isLineText = isLineText;
    }

    public boolean isLineText() {
        return this.isLineText;
    }

    public void setTextMaxLines(int mTextLines) {
        this.mTextLines = mTextLines;
        this.isLineText = true;
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
        this.isShadow = true;
        this.mTextPaint.setShadowLayer(radius, dx, dy, color);
    }

    public void clearShadowLayer() {
        this.isShadow = false;
        this.mTextPaint.clearShadowLayer();
    }

    public boolean isShadow() {
        return this.isShadow;
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

    private Bitmap drawTextWithCount(int width, int height, String text, Align align, float textRelaSpac, float textAbsSpac, int count, boolean isShadow) {
        int i;
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        this.mTextPaint.setTextAlign(align);
        int baseX = 0;
        if (align == Align.CENTER) {
            baseX = width / 2;
        } else if (align == Align.RIGHT) {
            baseX = width;
        }
        FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
        int h = fontMetricsInt.descent - fontMetricsInt.ascent;
        ArrayList<String> arrayList = new ArrayList(count);
        int c = 0;
        for (i = 0; i < count; i++) {
            int n = this.mTextPaint.breakText(text, true, (float) width, null);
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
                    int t = this.mTextPaint.breakText(temp, true, (float) width, null);
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
            canvas.drawText((String) arrayList.get(i), (float) baseX, (float) baseY, this.mTextPaint);
            baseY = (int) (((float) baseY) + ((((float) h) * textRelaSpac) + textAbsSpac));
        }
        return textBitmap;
    }

    private Bitmap drawSinglineText(int width, int height, String text, Align align, boolean isShadow) {
        int baseY;
        if (this.isMarquee && !this.isDrawMarquee) {
            align = Align.LEFT;
        }
        TextPaint paint = new TextPaint(this.mTextPaint);
        paint.setTextAlign(this.mTextAlign);
        if (paint.measureText(text) > ((float) width) && !this.isMarquee) {
            String str = text;
            text = str.substring(0, paint.breakText(text, true, (float) width, null) - 2) + "...";
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

    private StaticLayout getMarqueeLayout(String text, Alignment textAlignment) {
        text = text.replaceAll("[\r\n]|[\n]", "");
        this.mMarquee.width = (int) this.mTextPaint.measureText(text);
        return new StaticLayout(text, this.mTextPaint, this.mMarquee.width, textAlignment, 1.0f, 0.0f, false);
    }

    private Bitmap marquee(int x) {
        Bitmap textBitmap = Bitmap.createBitmap(this.mTextWidth, this.mTextHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        int baseY = 0;
        int h = this.mTextMarqueeLayout.getHeight();
        if (h < this.mTextHeight) {
            if (this.mTGravity == TextGravity.CENTER) {
                baseY = (this.mTextHeight - h) / 2;
            } else if (this.mTGravity == TextGravity.BOTTOM) {
                baseY = this.mTextHeight - h;
            }
        }
        canvas.translate((float) (-x), (float) baseY);
        this.mTextMarqueeLayout.draw(canvas);
        if (x > (this.mMarquee.width + 70) - this.mTextWidth) {
            canvas.translate((float) (this.mMarquee.width + 70), 0.0f);
            this.mTextMarqueeLayout.draw(canvas);
        }
        return textBitmap;
    }

    private void initMarquee(int times, int pps) {
        this.mMarquee = new Marquee();
        if (this.mMarquee.times > 0) {
            this.mMarquee.times = times;
        } else {
            this.mMarquee.times = 0;
        }
        this.mMarquee.curMarqueeTimes = 1;
        this.mMarquee.pixlesPerSecond = pps;
        this.mTextMarqueeLayout = null;
        Marquee marquee = this.mMarquee;
        Marquee marquee2 = this.mMarquee;
        long currentTimeMillis = System.currentTimeMillis();
        marquee2.currentTime = currentTimeMillis;
        marquee.startTime = currentTimeMillis;
    }
}
