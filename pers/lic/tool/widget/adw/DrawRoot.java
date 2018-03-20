package pers.lic.tool.widget.adw;

import android.graphics.Canvas;
import android.view.View;

public interface DrawRoot {
    View getRoot();

    void postAnimation();

    void superDraw(Canvas canvas);
}
