package zidoo.tarot.kernel;

public class Position extends Vector3 {
    public static final String TAG = "Position";

    public Position(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public void set(Vector3 position) {
        this.X = position.X;
        this.Y = position.Y;
        this.Z = position.Z;
    }
}
