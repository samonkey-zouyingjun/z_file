package com.zidoo.custom.moveview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MoveView extends RelativeLayout {
    private float mDownX = 0.0f;
    private float mDownY = 0.0f;
    private int mHeight = 0;
    private int mMaxHeight = 120;
    private int mSystemHeight = 0;
    private int mSystemWidth = 0;
    private int mWidth = 0;

    public MoveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MoveView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float density1 = dm.density;
        this.mSystemWidth = dm.widthPixels;
        this.mSystemHeight = dm.heightPixels;
        if (this.mSystemWidth == 1920) {
            this.mSystemHeight = 1080;
            this.mMaxHeight = (int) (((double) this.mMaxHeight) * 1.5d);
        } else if (this.mSystemWidth == 1280) {
            this.mSystemHeight = 720;
        }
        Log.v("bob", "mSystemWidth = " + this.mSystemWidth + "    mSystemHeight = " + this.mSystemHeight + "   mMaxHeight = " + this.mMaxHeight);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mWidth = right - left;
        this.mHeight = bottom - top;
        Log.v("bob", "mWidth = " + this.mWidth + "    mHeight = " + this.mHeight);
        super.onLayout(changed, left, top, right, bottom);
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            statusHeight = context.getResources().getDimensionPixelSize(Integer.parseInt(clazz.getField("status_bar_height").get(clazz.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            this.mDownX = event.getX();
            this.mDownY = event.getY();
            startMove(event);
        } else if (event.getAction() == 1) {
            stopMove(event);
            if (isMove(event)) {
                return true;
            }
        } else if (event.getAction() == 2) {
            move(event);
        }
        return super.onTouchEvent(event);
    }

    private boolean isMove(MotionEvent event) {
        if (Math.abs(event.getX() - this.mDownX) <= 20.0f && Math.abs(event.getY() - this.mDownY) <= 20.0f) {
            return false;
        }
        return true;
    }

    private void startMove(MotionEvent event) {
        ((MoveParentView) getParent()).setOrderChild(this);
    }

    private void stopMove(MotionEvent event) {
        ((MoveParentView) getParent()).repeatDis();
    }

    private void move(MotionEvent event) {
        Log.v("bob", "RawX = " + event.getRawX() + " RawY = " + event.getRawY());
        float mLastX = event.getRawX() - ((float) (this.mWidth / 2));
        float mLasty = event.getRawY() - ((float) (this.mHeight / 2));
        if (mLastX < 0.0f) {
            mLastX = 0.0f;
        } else if (event.getRawX() + ((float) this.mWidth) >= ((float) this.mSystemWidth)) {
            mLastX = (float) (this.mSystemWidth - this.mWidth);
        }
        if (event.getRawY() >= ((float) this.mMaxHeight)) {
            mLasty = (float) (this.mMaxHeight - this.mHeight);
        } else if (mLasty < 0.0f) {
            mLasty = 0.0f;
        } else if (event.getRawY() + ((float) this.mHeight) >= ((float) this.mSystemHeight)) {
            mLasty = (float) (this.mSystemHeight - this.mHeight);
        }
        setX(mLastX);
        setY(mLasty);
    }
}
