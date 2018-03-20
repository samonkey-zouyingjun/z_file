package zidoo.tarot.kernel;

public class Vector2 implements Cloneable {
    public static final Vector2 DOWN_AXIS = new Vector2(0.0f, -1.0f);
    public static final Vector2 LEFT_AXIS = new Vector2(-1.0f, 0.0f);
    public static final Vector2 RIGHT_AXIS = new Vector2(1.0f, 0.0f);
    public static final String TAG = "Vector2";
    public static final Vector2 UP_AXIS = new Vector2(0.0f, 1.0f);
    public static final Vector2 ZERO = new Vector2(0.0f, 0.0f);
    public float X = 0.0f;
    public float Y = 0.0f;

    public Vector2 clone() {
        Vector2 cloned = null;
        try {
            return (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloned.X = this.X;
            cloned.Y = this.Y;
            return cloned;
        }
    }

    public Vector2(Vector2 vector) {
        this.X = vector.X;
        this.Y = vector.Y;
    }

    public Vector2(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    public Vector2(Vector3 vector) {
        this.X = vector.X;
        this.Y = vector.Y;
    }

    public boolean equals(Vector2 vector) {
        return equals(this, vector);
    }

    public void set(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    public static boolean equals(Vector2 vectorA, Vector2 vectorB) {
        return vectorA.X == vectorB.X && vectorA.Y == vectorB.Y;
    }

    public String toString() {
        return "(" + this.X + ", " + this.Y + ")";
    }
}
