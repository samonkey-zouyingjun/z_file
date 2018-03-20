package zidoo.tarot.widget;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.GLContext;

public class DelayTextView extends TTextView {
    public static int SHARE_DELAY_TIME;
    private int mDelayUpdate = 0;

    public DelayTextView(GLContext glContext) {
        super(glContext);
    }

    public DelayTextView(GLContext glContext, String text) {
        super(glContext, text);
    }

    public void delayUpdate() {
        SHARE_DELAY_TIME++;
        this.mDelayUpdate += (SHARE_DELAY_TIME / 5) + 1;
    }

    public void update(GL10 gl) {
        if (this.mDelayUpdate > 0) {
            SHARE_DELAY_TIME--;
            this.mDelayUpdate--;
            invalidate();
            return;
        }
        super.update(gl);
    }
}
