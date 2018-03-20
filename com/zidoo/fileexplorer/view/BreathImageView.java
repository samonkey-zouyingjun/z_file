package com.zidoo.fileexplorer.view;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.widget.TImageView;

public class BreathImageView extends TImageView {
    private final int mDuration = 3000;
    private boolean mIsAmin = false;
    private long mStartTime;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public BreathImageView(GLContext glContext) {
        super(glContext);
        setAlpha(0.5f);
        start();
    }

    public void update(GL10 gl) {
        if (this.mIsAmin) {
            animAlpha();
        } else {
            start();
            invalidate();
        }
        super.update(gl);
    }

    private void start() {
        this.mStartTime = System.currentTimeMillis();
        this.mIsAmin = true;
    }

    private void stop() {
        this.mIsAmin = false;
        invalidate(3000);
    }

    private void animAlpha() {
        long du = System.currentTimeMillis() - this.mStartTime;
        if (du > 3000) {
            stop();
            return;
        }
        setAlpha(((float) (Math.sin((3.141592653589793d * ((double) du)) / 3000.0d) * 0.5d)) + 0.5f);
        invalidate();
    }
}
