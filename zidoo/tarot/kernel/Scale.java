package zidoo.tarot.kernel;

public class Scale extends Vector3 {
    public static final String TAG = "Scale";

    public Scale() {
        this.X = 1.0f;
        this.Y = 1.0f;
        this.Z = 1.0f;
    }

    public Scale(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public void set(Vector3 scale) {
        this.X = scale.X;
        this.Y = scale.Y;
        this.Z = scale.Z;
    }

    public void set(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
