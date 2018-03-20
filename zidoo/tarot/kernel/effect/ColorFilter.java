package zidoo.tarot.kernel.effect;

import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.kernel.GLObject;

public class ColorFilter implements GLObject {
    public static final boolean[] FILTER_COLOR_BLUE;
    public static final boolean[] FILTER_COLOR_CYAN;
    public static final boolean[] FILTER_COLOR_GREEN;
    public static final boolean[] FILTER_COLOR_MAGENTA;
    public static final boolean[] FILTER_COLOR_NONE = new boolean[]{true, true, true, true};
    public static final boolean[] FILTER_COLOR_RED;
    public static final boolean[] FILTER_COLOR_TRANSPARENT;
    public static final boolean[] FILTER_COLOR_WHITE = new boolean[]{true, true, true, true};
    public static final boolean[] FILTER_COLOR_YELLOW;
    private boolean mEnabled = true;
    private boolean[] mFilterColor = FILTER_COLOR_NONE;

    static {
        boolean[] zArr = new boolean[4];
        zArr[0] = true;
        zArr[1] = true;
        zArr[2] = true;
        FILTER_COLOR_TRANSPARENT = zArr;
        zArr = new boolean[4];
        zArr[0] = true;
        zArr[3] = true;
        FILTER_COLOR_RED = zArr;
        zArr = new boolean[4];
        zArr[1] = true;
        zArr[3] = true;
        FILTER_COLOR_GREEN = zArr;
        zArr = new boolean[4];
        zArr[2] = true;
        zArr[3] = true;
        FILTER_COLOR_BLUE = zArr;
        zArr = new boolean[4];
        zArr[0] = true;
        zArr[1] = true;
        zArr[3] = true;
        FILTER_COLOR_YELLOW = zArr;
        zArr = new boolean[4];
        zArr[1] = true;
        zArr[2] = true;
        zArr[3] = true;
        FILTER_COLOR_CYAN = zArr;
        zArr = new boolean[4];
        zArr[0] = true;
        zArr[2] = true;
        zArr[3] = true;
        FILTER_COLOR_MAGENTA = zArr;
    }

    public void setFilterColor(boolean[] filterColor) {
        if (filterColor != null) {
            this.mFilterColor = filterColor;
        }
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    public void update(GL10 gl) {
        gl.glColorMask(this.mFilterColor[0], this.mFilterColor[1], this.mFilterColor[2], this.mFilterColor[3]);
    }

    public void dispose() {
    }
}
