package pers.lic.tool.widget.adw;

import android.graphics.Canvas;

public abstract class AnimationDrawer {
    protected DrawRoot drawRoot;

    public abstract void draw(Canvas canvas);

    public abstract boolean isAnimating();

    public void setDrawRoot(DrawRoot drawRoot) {
        this.drawRoot = drawRoot;
    }
}
