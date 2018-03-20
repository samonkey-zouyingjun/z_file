package com.zidoo.fileexplorer.view;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.GR;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.anim.GlTranslateAnimation;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;
import zidoo.tarot.widget.ViewGroup;

public class Screens extends ViewGroup {
    int current = 0;
    private GestureDetector mGestureDetector;
    int selected = 0;
    TImageView selectedBackground;
    TImageView selector;
    TTextView[] tvScreens;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Screens(GLContext glContext, int selected, String[] screensNames) {
        super(glContext);
        this.current = selected;
        this.selected = selected;
        init(glContext, screensNames);
    }

    private void init(GLContext glContext, String[] screensNames) {
        this.selectedBackground = new TImageView(glContext);
        this.selectedBackground.setWidth(334.0f);
        this.selectedBackground.setHeight(133.0f);
        this.selectedBackground.setImageResource(R.drawable.screen_selected);
        addGameObject(this.selectedBackground);
        this.selector = new TImageView(glContext);
        this.selector.setWidth(334.0f);
        this.selector.setHeight(133.0f);
        this.selector.setImageResource(R.drawable.selector_screen);
        addGameObject(this.selector);
        this.tvScreens = new TTextView[5];
        for (int i = 0; i < 5; i++) {
            this.tvScreens[i] = new TTextView(glContext);
            this.tvScreens[i].setId(GR.screens_0 + i);
            this.tvScreens[i].setText(screensNames[i]);
            this.tvScreens[i].setTextColor(-1);
            this.tvScreens[i].setWidth(160.0f);
            this.tvScreens[i].setHeight(50.0f);
            this.tvScreens[i].setTextSize(40.0f);
            this.tvScreens[i].setTextGravity(17);
            addGameObject(this.tvScreens[i]);
        }
        this.tvScreens[0].setX(-600.0f);
        this.tvScreens[1].setX(-300.0f);
        this.tvScreens[3].setX(300.0f);
        this.tvScreens[4].setX(600.0f);
        adjustView(this.selectedBackground, this.selected);
        adjustView(this.selector, this.current);
        this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                return Screens.this.performClick();
            }

            public void onShowPress(MotionEvent e) {
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            public void onLongPress(MotionEvent e) {
                Screens.this.performLongClick();
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            public boolean onDown(MotionEvent e) {
                for (int i = 0; i < Screens.this.tvScreens.length; i++) {
                    if (Screens.this.tvScreens[i].isTouched(e)) {
                        if (Screens.this.current != i) {
                            Screens.this.current = i;
                            Screens.this.move(Screens.this.selector, i);
                            Screens.this.invalidate();
                        }
                        return false;
                    }
                }
                return true;
            }

            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                return super.onDoubleTapEvent(e);
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }
        }, new Handler(Looper.getMainLooper()));
        this.mGestureDetector.setIsLongpressEnabled(true);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mOnKeyListener != null && this.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        if (event.getAction() == 0) {
            return onKeyDown(event);
        }
        if (event.getAction() == 1) {
            return onKeyUp(event);
        }
        return onKeyEvent(event);
    }

    protected boolean onKeyDown(KeyEvent event) {
        switch (event.getKeyCode()) {
            case MotionEventCompat.AXIS_WHEEL /*21*/:
                if (this.current > 0) {
                    this.current--;
                    move(this.selector, this.current);
                    invalidate();
                    break;
                }
                break;
            case MotionEventCompat.AXIS_GAS /*22*/:
                if (this.current < 4) {
                    this.current++;
                    move(this.selector, this.current);
                    invalidate();
                    break;
                }
                break;
        }
        return super.onKeyDown(event);
    }

    private void adjustView(GameObject v, int p) {
        v.setX(this.tvScreens[p].getX());
    }

    private void move(GameObject v, int p) {
        GlTranslateAnimation animation = new GlTranslateAnimation(v.Position, this.tvScreens[p].Position);
        animation.setDuration(150);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return this.mGestureDetector.onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public GameObject findFocus() {
        return super.findFocus();
    }

    protected boolean performClick() {
        if (this.selected != this.current) {
            this.selected = this.current;
            adjustView(this.selectedBackground, this.selected);
            if (this.mOnClickListener != null) {
                this.mOnClickListener.onClick(this.tvScreens[this.selected]);
                return true;
            }
        }
        return false;
    }
}
