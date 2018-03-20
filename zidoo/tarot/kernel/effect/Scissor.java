package zidoo.tarot.kernel.effect;

import android.graphics.Rect;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.kernel.GLObject;

public class Scissor implements GLObject {
    private boolean mEnabled = true;
    private Rect mScissorRect = new Rect();

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setScissorRect(int left, int top, int right, int bottom) {
        this.mScissorRect.set(left, top, right, bottom);
    }

    public void beginScissor(GL10 gl) {
        if (this.mEnabled) {
            gl.glEnable(3089);
            gl.glScissor(this.mScissorRect.left, this.mScissorRect.top, this.mScissorRect.width(), this.mScissorRect.height());
        }
    }

    public void endScissor(GL10 gl) {
        gl.glDisable(3089);
    }

    public void dispose() {
    }
}
