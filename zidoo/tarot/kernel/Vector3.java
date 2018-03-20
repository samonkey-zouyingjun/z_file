package zidoo.tarot.kernel;

public class Vector3 extends Vector2 implements Cloneable {
    public static final Vector3 BACK_AXIS = new Vector3(0.0f, 0.0f, -1.0f);
    public static final Vector3 DOWN_AXIS = new Vector3(0.0f, -1.0f, 0.0f);
    public static final Vector3 FRONT_AXIS = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 LEFT_AXIS = new Vector3(-1.0f, 0.0f, 0.0f);
    public static final Vector3 RIGHT_AXIS = new Vector3(1.0f, 0.0f, 0.0f);
    public static final String TAG = "Vector3";
    public static final Vector3 UP_AXIS = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 ZERO = new Vector3(0.0f, 0.0f, 0.0f);
    public float Z = 0.0f;

    public Vector3 clone() {
        Vector3 cloned = (Vector3) super.clone();
        cloned.X = this.X;
        cloned.Y = this.Y;
        cloned.Z = this.Z;
        return cloned;
    }

    public Vector3(Vector3 vector) {
        this.X = vector.X;
        this.Y = vector.Y;
        this.Z = vector.Z;
    }

    public Vector3(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public boolean equals(Vector3 vector) {
        return equals(this, vector);
    }

    public float normalize() {
        float mag = magnitude(this);
        if (mag != 0.0f) {
            this.X /= mag;
            this.Y /= mag;
            this.Z /= mag;
        }
        return mag;
    }

    public float magnitude() {
        return magnitude(this);
    }

    public static boolean equals(Vector3 vectorA, Vector3 vectorB) {
        return vectorA.X == vectorB.X && vectorA.Y == vectorB.Y && vectorA.Z == vectorB.Z;
    }

    public static Vector3 product(Vector3 vector, float factor) {
        return new Vector3(vector.X * factor, vector.Y * factor, vector.Z * factor);
    }

    public static Vector3 product(Vector3 vectorA, Vector3 vectorB) {
        return new Vector3(vectorA.X * vectorB.X, vectorA.Y * vectorB.Y, vectorA.Z * vectorB.Z);
    }

    public static Vector3 crossProduct(Vector3 vectorA, Vector3 vectorB) {
        return new Vector3((vectorA.Y * vectorB.Z) - (vectorA.Z * vectorB.Y), (vectorA.Z * vectorB.X) - (vectorA.X * vectorB.Z), (vectorA.X * vectorB.Y) - (vectorA.Y * vectorB.X));
    }

    public static float magnitude(Vector3 vector) {
        return (float) Math.sqrt((Math.pow((double) vector.X, 2.0d) + Math.pow((double) vector.Y, 2.0d)) + Math.pow((double) vector.Z, 2.0d));
    }

    public static float squaredLength(Vector3 vector) {
        return ((vector.X * vector.X) + (vector.Y * vector.Y)) + (vector.Z * vector.Z);
    }

    public static float dotProduct(Vector3 vectorA, Vector3 vectorB) {
        return ((vectorA.X * vectorB.X) + (vectorA.Y * vectorB.Y)) + (vectorA.Z * vectorB.Z);
    }

    public static Vector3 normalize(Vector3 vector) {
        float mag = magnitude(vector);
        if (mag != 0.0f) {
            vector.X /= mag;
            vector.Y /= mag;
            vector.Z /= mag;
        }
        return vector;
    }

    public static float angleBetween(Vector3 vectorA, Vector3 vectorB) {
        float lengthProduce = magnitude(vectorA) * magnitude(vectorB);
        if (lengthProduce < 1.0E-8f) {
            lengthProduce = 1.0E-8f;
        }
        float factor = dotProduct(vectorA, vectorB) / lengthProduce;
        if (factor < -1.0f) {
            factor = -1.0f;
        } else if (factor > 1.0f) {
            factor = 1.0f;
        }
        return (float) Math.acos((double) factor);
    }

    public static Vector3 addition(Vector3 vectorA, Vector3 vectorB) {
        return new Vector3(vectorA.X + vectorB.X, vectorA.Y + vectorB.Y, vectorA.Z + vectorB.Z);
    }

    public static Vector3 subtraction(Vector3 vectorA, Vector3 vectorB) {
        return new Vector3(vectorA.X - vectorB.X, vectorA.Y - vectorB.Y, vectorA.Z - vectorB.Z);
    }

    public static float distance(Vector3 pointA, Vector3 PointB) {
        return magnitude(subtraction(pointA, PointB));
    }

    public float[] get() {
        return new float[]{this.X, this.Y, this.Z};
    }

    public void set(float[] src) {
        if (src == null) {
            this.X = 0.0f;
            this.Y = 0.0f;
            this.Z = 0.0f;
        } else if (src.length == 1) {
            this.X = src[0];
            this.Y = 0.0f;
            this.Z = 0.0f;
        } else if (src.length == 2) {
            this.X = src[0];
            this.Y = src[1];
            this.Z = 0.0f;
        } else if (src.length >= 3) {
            this.X = src[0];
            this.Y = src[1];
            this.Z = src[2];
        } else {
            this.X = 0.0f;
            this.Y = 0.0f;
            this.Z = 0.0f;
        }
    }

    public String toString() {
        return "(" + this.X + ", " + this.Y + ", " + this.Z + ")";
    }
}
