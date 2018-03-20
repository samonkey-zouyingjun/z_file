package zidoo.tarot.kernel.effect;

public class LinearGradient {
    private int[] mColors = null;
    private float[] mPositions = null;

    public LinearGradient(int[] colors, float[] positions) {
        this.mColors = colors;
        this.mPositions = positions;
    }

    public int[] getColors() {
        return this.mColors;
    }

    public void setColors(int[] colors) {
        this.mColors = colors;
    }

    public float[] getPositions() {
        return this.mPositions;
    }

    public void setPositions(float[] positions) {
        this.mPositions = positions;
    }
}
