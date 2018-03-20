package com.zidoo.fileexplorer.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BackAnimationView extends ImageView {
    static final float MAXSCALE = 0.7f;
    static final float MINSCALE = 0.1f;
    static final int RUNNING = 0;
    private static final int RUNNINGSPEED = 500;
    private static final float SPEED = 0.05f;
    float mCurrScale = MAXSCALE;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    BackAnimationView.this.mHandler.removeMessages(0);
                    BackAnimationView backAnimationView = BackAnimationView.this;
                    backAnimationView.mCurrScale += BackAnimationView.this.mSpeed;
                    BackAnimationView.this.setAlpha(BackAnimationView.this.mCurrScale);
                    BackAnimationView.this.invalidate();
                    if (Math.abs(BackAnimationView.this.mTagScale - BackAnimationView.this.mCurrScale) <= 0.01f) {
                        if (BackAnimationView.this.mTagScale == BackAnimationView.MAXSCALE) {
                            BackAnimationView.this.mTagScale = BackAnimationView.MINSCALE;
                            BackAnimationView.this.mSpeed = -0.05f;
                        } else if (BackAnimationView.this.mTagScale == BackAnimationView.MINSCALE) {
                            BackAnimationView.this.mTagScale = BackAnimationView.MAXSCALE;
                            BackAnimationView.this.mSpeed = BackAnimationView.SPEED;
                        }
                    }
                    BackAnimationView.this.mHandler.sendEmptyMessageDelayed(0, 500);
                    System.out.println("bob  mCurrScale = " + BackAnimationView.this.mCurrScale + "     mTagScale = " + BackAnimationView.this.mTagScale);
                    return;
                default:
                    return;
            }
        }
    };
    float mSpeed = -0.05f;
    float mTagScale = MINSCALE;

    public BackAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public BackAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BackAnimationView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.mHandler.sendEmptyMessageDelayed(0, 500);
    }
}
