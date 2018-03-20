package zidoo.tarot.kernel.effect;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.kernel.GLObject;

public class NightSight implements GLObject {
    private boolean mEnabled = true;

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    public void update(GL10 gl) {
        if (this.mEnabled) {
            gl.glColorMask(false, true, false, true);
        }
    }

    public void dispose() {
    }
}
