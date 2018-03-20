package com.zidoo.fileexplorer.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import com.zidoo.fileexplorer.R;

public class SwitchButton extends CompoundButton {
    private static final int[] CHECKED_STATE_SET = new int[]{16842912};
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private Context mContext;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker;

    public SwitchButton(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchStyle);
        this.mContext = context;
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mContext = context;
        this.mTextPaint = new TextPaint(1);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyle, 0);
        this.mThumbDrawable = a.getDrawable(5);
        this.mTrackDrawable = a.getDrawable(7);
        this.mTextOn = a.getText(4);
        this.mTextOff = a.getText(3);
        this.mThumbTextPadding = a.getDimensionPixelSize(6, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(0, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(1, 0);
        int appearance = a.getResourceId(2, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        this.mContext = context;
        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(1);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = appearance.getDimensionPixelSize(5, 0);
        if (!(ts == 0 || ((float) ts) == this.mTextPaint.getTextSize())) {
            this.mTextPaint.setTextSize((float) ts);
            requestLayout();
        }
        setSwitchTypefaceByIndex(appearance.getInt(7, -1), appearance.getInt(6, -1));
        appearance.recycle();
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case 1:
                tf = Typeface.SANS_SERIF;
                break;
            case 2:
                tf = Typeface.SERIF;
                break;
            case 3:
                tf = Typeface.MONOSPACE;
                break;
        }
        setSwitchTypeface(tf, styleIndex);
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        boolean z = false;
        if (style > 0) {
            int typefaceStyle;
            float f;
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf);
            if (tf != null) {
                typefaceStyle = tf.getStyle();
            } else {
                typefaceStyle = 0;
            }
            int need = style & (typefaceStyle ^ -1);
            TextPaint textPaint = this.mTextPaint;
            if ((need & 1) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.mTextPaint;
            if ((need & 2) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            textPaint.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (this.mOnLayout == null) {
            this.mOnLayout = makeLayout(this.mTextOn);
        }
        if (this.mOffLayout == null) {
            this.mOffLayout = makeLayout(this.mTextOff);
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        int switchWidth = this.mSwitchMinWidth;
        int switchHeight = this.mTrackDrawable.getIntrinsicHeight();
        this.mThumbWidth = this.mThumbTextPadding * 2;
        switch (widthMode) {
            case Integer.MIN_VALUE:
                widthSize = Math.min(widthSize, switchWidth);
                break;
            case 0:
                widthSize = switchWidth;
                break;
        }
        switch (heightMode) {
            case Integer.MIN_VALUE:
                heightSize = Math.min(heightSize, switchHeight);
                break;
            case 0:
                heightSize = switchHeight;
                break;
        }
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < switchHeight) {
            setMeasuredDimension(getMeasuredWidth(), switchHeight);
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        populateAccessibilityEvent(event);
        return false;
    }

    public void populateAccessibilityEvent(AccessibilityEvent event) {
        CharSequence text;
        if (isChecked()) {
            text = this.mOnLayout.getText();
            if (TextUtils.isEmpty(text)) {
                text = this.mContext.getString(R.string.switch_on);
            }
            event.getText().add(text);
            return;
        }
        text = this.mOffLayout.getText();
        if (TextUtils.isEmpty(text)) {
            text = this.mContext.getString(R.string.switch_off);
        }
        event.getText().add(text);
    }

    private Layout makeLayout(CharSequence text) {
        return new StaticLayout(text, this.mTextPaint, (int) Math.ceil((double) Layout.getDesiredWidth(text, this.mTextPaint)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float x, float y) {
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbLeft = (this.mSwitchLeft + ((int) (this.mThumbPosition + 0.5f))) - this.mTouchSlop;
        return x > ((float) thumbLeft) && x < ((float) ((((this.mThumbWidth + thumbLeft) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop)) && y > ((float) (this.mSwitchTop - this.mTouchSlop)) && y < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        float x;
        float y;
        switch (ev.getActionMasked()) {
            case 0:
                x = ev.getX();
                y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = 1;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
            case 1:
            case 3:
                if (this.mTouchMode != 2) {
                    this.mTouchMode = 0;
                    this.mVelocityTracker.clear();
                    break;
                }
                stopDrag(ev);
                return true;
            case 2:
                switch (this.mTouchMode) {
                    case 0:
                        return true;
                    case 1:
                        x = ev.getX();
                        y = ev.getY();
                        if (Math.abs(x - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x;
                            this.mTouchY = y;
                            return true;
                        }
                    case 2:
                        x = ev.getX();
                        float newPos = Math.max(0.0f, Math.min(this.mThumbPosition + (x - this.mTouchX), (float) getThumbScrollRange()));
                        if (newPos == this.mThumbPosition) {
                            return true;
                        }
                        this.mThumbPosition = newPos;
                        this.mTouchX = x;
                        invalidate();
                        return true;
                    default:
                        break;
                }
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(3);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        boolean commitChange;
        this.mTouchMode = 0;
        if (ev.getAction() == 1 && isEnabled()) {
            commitChange = true;
        } else {
            commitChange = false;
        }
        cancelSuperTouch(ev);
        if (commitChange) {
            boolean newState;
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) <= ((float) this.mMinFlingVelocity)) {
                newState = getTargetCheckedState();
            } else if (xvel > 0.0f) {
                newState = true;
            } else {
                newState = false;
            }
            animateThumbToCheckedState(newState);
            return;
        }
        animateThumbToCheckedState(isChecked());
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        setChecked(newCheckedState);
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition >= ((float) (getThumbScrollRange() / 2));
    }

    public void setChecked(boolean checked) {
        super.setChecked(checked);
        this.mThumbPosition = checked ? (float) getThumbScrollRange() : 0.0f;
        invalidate();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchTop;
        int switchBottom;
        super.onLayout(changed, left, top, right, bottom);
        this.mThumbPosition = isChecked() ? (float) getThumbScrollRange() : 0.0f;
        int switchRight = getWidth() - getPaddingRight();
        int switchLeft = switchRight - this.mSwitchWidth;
        switch (getGravity() & 112) {
            case 16:
                switchTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case 80:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - this.mSwitchHeight;
                break;
            default:
                switchTop = getPaddingTop();
                switchBottom = switchTop + this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        this.mTrackDrawable.setBounds(switchLeft, switchTop, switchRight, switchBottom);
        this.mTrackDrawable.draw(canvas);
        canvas.save();
        this.mTrackDrawable.getPadding(this.mTempRect);
        int switchInnerLeft = switchLeft + this.mTempRect.left;
        int switchInnerTop = switchTop + this.mTempRect.top;
        int switchInnerBottom = switchBottom - this.mTempRect.bottom;
        canvas.clipRect(switchInnerLeft, switchTop, switchRight - this.mTempRect.right, switchBottom);
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbPos = (int) this.mThumbPosition;
        int thumbLeft = (switchInnerLeft - this.mTempRect.left) + thumbPos;
        int thumbRight = ((switchInnerLeft + thumbPos) + this.mThumbWidth) + this.mTempRect.right;
        this.mThumbDrawable.setBounds(thumbLeft + 2, switchTop + 2, thumbRight - 2, switchBottom - 2);
        this.mThumbDrawable.draw(canvas);
        if (this.mTextColors != null) {
            this.mTextPaint.setColor(this.mTextColors.getColorForState(getDrawableState(), this.mTextColors.getDefaultColor()));
        }
        this.mTextPaint.drawableState = getDrawableState();
        Layout switchText = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        canvas.translate((float) (((thumbLeft + thumbRight) / 2) - (switchText.getWidth() / 2)), (float) (((switchInnerTop + switchInnerBottom) / 2) - (switchText.getHeight() / 2)));
        switchText.draw(canvas);
        canvas.restore();
    }

    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (TextUtils.isEmpty(getText())) {
            return padding;
        }
        return padding + this.mSwitchPadding;
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        return (((this.mSwitchWidth - this.mThumbWidth) - this.mTempRect.left) - this.mTempRect.right) - 2;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(myDrawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(myDrawableState);
        }
        invalidate();
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }
}
