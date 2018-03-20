package com.zidoo.fileexplorer.view;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;
import zidoo.tarot.widget.TImageView;

public class TwinkleImageView extends TImageView {
    float offset = -0.03f;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public TwinkleImageView(GLContext glContext) {
        super(glContext);
    }

    public TwinkleImageView(GLContext glContext, int resid) {
        super(glContext, resid);
    }

    public TwinkleImageView(GLContext glContext, int resid, int width, int height) {
        super(glContext, resid, width, height);
    }

    public void update(GL10 gl) {
        super.update(gl);
        float alpha = getAlpha() + this.offset;
        setAlpha(alpha);
        if (alpha >= 1.0f) {
            this.offset = -0.01f;
        } else if (alpha <= 0.333f) {
            this.offset = 0.01f;
        }
        invalidate();
    }
}
