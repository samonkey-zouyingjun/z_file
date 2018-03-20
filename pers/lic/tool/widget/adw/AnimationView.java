package pers.lic.tool.widget.adw;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class AnimationView extends RelativeLayout implements DrawRoot {
    private AnimationDrawer drawer = null;

    public AnimationView(Context context) {
        super(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public <T extends AnimationDrawer> T startAnimation(T drawer) {
        this.drawer = drawer;
        drawer.setDrawRoot(this);
        return drawer;
    }

    public void draw(Canvas canvas) {
        if (this.drawer == null || !this.drawer.isAnimating()) {
            super.draw(canvas);
        } else {
            this.drawer.draw(canvas);
        }
    }

    public View getRoot() {
        return this;
    }

    public void superDraw(Canvas canvas) {
        super.draw(canvas);
    }

    public void postAnimation() {
        postInvalidateOnAnimation();
    }
}
