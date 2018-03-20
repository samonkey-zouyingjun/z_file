package zidoo.tarot.widget;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;

public class RotateProgressBar extends TImageView {
    public RotateProgressBar(GLContext glContext) {
        super(glContext);
    }

    public void update(GL10 gl) {
        super.update(gl);
        rotateRoll(-0.03141593f);
        invalidate();
    }
}
