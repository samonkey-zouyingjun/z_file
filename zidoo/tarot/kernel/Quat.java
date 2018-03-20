package zidoo.tarot.kernel;

import java.lang.reflect.Array;

public class Quat {
    public static final String TAG = "Quat";
    public float W = 1.0f;
    public float X = 0.0f;
    public float Y = 0.0f;
    public float Z = 0.0f;

    public Quat(float w, float x, float y, float z) {
        this.W = w;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Quat normalize() {
        Quat quat = normalise(this);
        this.W = quat.W;
        this.X = quat.X;
        this.Y = quat.Y;
        this.Z = quat.Z;
        return this;
    }

    public void setAngleAxis(AngleAxis angleAxis) {
        Quat quat = fromAngleAxis(angleAxis);
        this.W = quat.W;
        this.X = quat.X;
        this.Y = quat.Y;
        this.Z = quat.Z;
    }

    public AngleAxis toAngleAxis() {
        return toAngleAxis(this);
    }

    public Vector3 toEular() {
        return toEular(this);
    }

    public float getRoll() {
        return getRoll(this, true);
    }

    public Quat getRollQuat() {
        float halfRollRadian = getRoll() / 2.0f;
        Quat quat = new Quat();
        quat.W = (float) Math.cos((double) halfRollRadian);
        quat.X = 0.0f;
        quat.Y = 0.0f;
        quat.Z = (float) Math.sin((double) halfRollRadian);
        return quat;
    }

    public void rotateRoll(float radian) {
        Quat quat = rotateRoll(this, radian);
        this.W = quat.W;
        this.X = quat.X;
        this.Y = quat.Y;
        this.Z = quat.Z;
    }

    public float getPitch() {
        return getPitch(this, true);
    }

    public Quat getPitchQuat() {
        float halfPitchRadian = getPitch() / 2.0f;
        Quat quat = new Quat();
        quat.W = (float) Math.cos((double) halfPitchRadian);
        quat.X = (float) Math.sin((double) halfPitchRadian);
        quat.Y = 0.0f;
        quat.Z = 0.0f;
        return quat;
    }

    public void rotatePitch(float radian) {
        Quat quat = rotatePitch(this, radian);
        this.W = quat.W;
        this.X = quat.X;
        this.Y = quat.Y;
        this.Z = quat.Z;
    }

    public float getYaw() {
        return getYaw(this, true);
    }

    public Quat getYawQuat() {
        float halfYawRadian = getYaw() / 2.0f;
        Quat quat = new Quat();
        quat.W = (float) Math.cos((double) halfYawRadian);
        quat.X = 0.0f;
        quat.Y = (float) Math.sin((double) halfYawRadian);
        quat.Z = 0.0f;
        return quat;
    }

    public void rotateYaw(float radian) {
        Quat quat = rotateYaw(this, radian);
        this.W = quat.W;
        this.X = quat.X;
        this.Y = quat.Y;
        this.Z = quat.Z;
    }

    public Vector3 getAxes() {
        return getAxes(this, true);
    }

    public String toString() {
        return "(" + this.W + ", " + this.X + ", " + this.Y + ", " + this.Z + ")";
    }

    public static Vector3 toEular(Quat quat) {
        return toEular(quat.W, quat.X, quat.Y, quat.Z);
    }

    public static Vector3 toEular(float quatW, float quatX, float quatY, float quatZ) {
        Vector3 eularVector = new Vector3();
        eularVector.X = (float) Math.asin((double) (2.0f * ((quatW * quatX) - (quatY * quatZ))));
        eularVector.Y = (float) Math.atan2((double) (2.0f * ((quatW * quatY) + (quatX * quatZ))), 1.0d - (2.0d * (Math.pow((double) quatX, 2.0d) + Math.pow((double) quatY, 2.0d))));
        eularVector.Z = (float) Math.atan2((double) (2.0f * ((quatW * quatZ) + (quatX * quatY))), 1.0d - (2.0d * (Math.pow((double) quatZ, 2.0d) + Math.pow((double) quatX, 2.0d))));
        return eularVector;
    }

    public static Quat toQuat(Vector3 vector) {
        return toQuat(vector.X, vector.Y, vector.Z);
    }

    public static Quat toQuat(float eularX, float eularY, float eularZ) {
        Quat quatVector = new Quat();
        double halfX = ((double) eularX) / 2.0d;
        double halfY = ((double) eularY) / 2.0d;
        double halfZ = ((double) eularZ) / 2.0d;
        quatVector.W = (float) (((Math.cos(halfX) * Math.cos(halfY)) * Math.cos(halfZ)) + ((Math.sin(halfX) * Math.sin(halfY)) * Math.sin(halfZ)));
        quatVector.X = (float) (((Math.sin(halfX) * Math.cos(halfY)) * Math.cos(halfZ)) - ((Math.cos(halfX) * Math.sin(halfY)) * Math.sin(halfZ)));
        quatVector.Y = (float) (((Math.cos(halfX) * Math.sin(halfY)) * Math.cos(halfZ)) + ((Math.sin(halfX) * Math.cos(halfY)) * Math.sin(halfZ)));
        quatVector.Z = (float) (((Math.cos(halfX) * Math.cos(halfY)) * Math.sin(halfZ)) - ((Math.sin(halfX) * Math.sin(halfY)) * Math.cos(halfZ)));
        return normalise(quatVector);
    }

    public static Quat Slerp(Quat fromQuat, Quat toQuat, float intervalRadian) {
        Quat wareQuat;
        float cosFactor = dot(fromQuat, toQuat);
        if (cosFactor < 0.0f) {
            cosFactor = -cosFactor;
            wareQuat = new Quat(-toQuat.W, -toQuat.X, -toQuat.Y, -toQuat.Z);
        } else {
            wareQuat = toQuat;
        }
        if (Math.abs(cosFactor) >= 1.0f) {
            return plus(product(fromQuat, 1.0f - intervalRadian), product(wareQuat, intervalRadian)).normalize();
        }
        float sinFactor = (float) Math.sqrt(1.0d - Math.pow((double) cosFactor, 2.0d));
        float angle = (float) Math.atan2((double) sinFactor, (double) cosFactor);
        float invSinFactor = 1.0f / sinFactor;
        return plus(product(fromQuat, (float) (Math.sin((double) ((1.0f - intervalRadian) * angle)) * ((double) invSinFactor))), product(wareQuat, (float) (Math.sin((double) (intervalRadian * angle)) * ((double) invSinFactor))));
    }

    public static Quat inverse(Quat quat) {
        float norm = norm(quat);
        if (((double) norm) <= 0.0d) {
            return null;
        }
        float invNorm = 1.0f / norm;
        return new Quat(quat.W * invNorm, (-quat.X) * invNorm, (-quat.Y) * invNorm, (-quat.Z) * invNorm);
    }

    public static Quat normalise(Quat quat) {
        return product(quat, (float) (1.0d / Math.sqrt((double) norm(quat))));
    }

    public static float norm(Quat quat) {
        return (((quat.W * quat.W) + (quat.X * quat.X)) + (quat.Y * quat.Y)) + (quat.Z * quat.Z);
    }

    public static Quat rotatePitch(Quat quat, float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = (float) Math.sin(halfRadian);
        relativeRotate.Y = 0.0f;
        relativeRotate.Z = 0.0f;
        return product(relativeRotate, quat).normalize();
    }

    public static float getPitch(Quat quat, boolean reprojectAxis) {
        if (!reprojectAxis) {
            return (float) Math.atan2((double) (((quat.Y * quat.Z) + (quat.W * quat.X)) * 2.0f), (double) ((((quat.W * quat.W) - (quat.X * quat.X)) - (quat.Y * quat.Y)) + (quat.Z * quat.Z)));
        }
        float tx = 2.0f * quat.X;
        float tz = 2.0f * quat.Z;
        return (float) Math.atan2((double) ((tz * quat.Y) + (tx * quat.W)), (double) ((1.0f - (tx * quat.X)) - (tz * quat.Z)));
    }

    public static Quat rotateYaw(Quat quat, float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = 0.0f;
        relativeRotate.Y = (float) Math.sin(halfRadian);
        relativeRotate.Z = 0.0f;
        return product(relativeRotate, quat).normalize();
    }

    public static float getYaw(Quat quat, boolean reprojectAxis) {
        if (!reprojectAxis) {
            return (float) Math.asin((double) (-2.0f * ((quat.X * quat.Z) - (quat.W * quat.Y))));
        }
        float ty = 2.0f * quat.Y;
        return (float) Math.atan2((double) (((2.0f * quat.Z) * quat.X) + (ty * quat.W)), (double) ((1.0f - ((2.0f * quat.X) * quat.X)) - (ty * quat.Y)));
    }

    public static Quat rotateRoll(Quat quat, float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = 0.0f;
        relativeRotate.Y = 0.0f;
        relativeRotate.Z = (float) Math.sin(halfRadian);
        return product(relativeRotate, quat).normalize();
    }

    public static float getRoll(Quat quat, boolean reprojectAxis) {
        if (!reprojectAxis) {
            return (float) Math.atan2((double) (((quat.X * quat.Y) + (quat.W * quat.Z)) * 2.0f), (double) ((((quat.W * quat.W) + (quat.X * quat.X)) - (quat.Y * quat.Y)) - (quat.Z * quat.Z)));
        }
        float ty = 2.0f * quat.Y;
        float tz = 2.0f * quat.Z;
        return (float) Math.atan2((double) ((ty * quat.X) + (tz * quat.W)), (double) ((1.0f - (ty * quat.Y)) - (tz * quat.Z)));
    }

    public static Vector3 getAxes(Quat quat, boolean reprojectAxis) {
        if (!reprojectAxis) {
            return new Vector3((float) Math.atan2((double) (2.0f * ((quat.Y * quat.Z) + (quat.W * quat.X))), (double) ((((quat.W * quat.W) - (quat.X * quat.X)) - (quat.Y * quat.Y)) + (quat.Z * quat.Z))), (float) Math.asin((double) (-2.0f * ((quat.X * quat.Z) - (quat.W * quat.Y)))), (float) Math.atan2((double) (2.0f * ((quat.X * quat.Y) + (quat.W * quat.Z))), (double) ((((quat.W * quat.W) + (quat.X * quat.X)) - (quat.Y * quat.Y)) - (quat.Z * quat.Z))));
        }
        float tx = 2.0f * quat.X;
        float ty = 2.0f * quat.Y;
        float tz = 2.0f * quat.Z;
        float txx = tx * quat.X;
        float tyy = ty * quat.Y;
        float tzz = tz * quat.Z;
        return new Vector3((float) Math.atan2((double) ((tz * quat.Y) + (tx * quat.W)), (double) ((1.0f - txx) - tzz)), (float) Math.atan2((double) ((tz * quat.X) + (ty * quat.W)), (double) ((1.0f - txx) - tyy)), (float) Math.atan2((double) ((ty * quat.X) + (tz * quat.W)), (double) ((1.0f - tyy) - tzz)));
    }

    public static Quat setAxes(Vector3 axes) {
        int i;
        Quat quat = new Quat();
        float[][] matrix = (float[][]) Array.newInstance(Float.TYPE, new int[]{3, 3});
        for (i = 0; i < 3; i++) {
            matrix[0][i] = axes.X;
            matrix[1][i] = axes.Y;
            matrix[2][i] = axes.Z;
        }
        float trace = (matrix[0][0] + matrix[1][1]) + matrix[2][2];
        float root;
        if (trace > 0.0f) {
            root = (float) Math.sqrt((double) (1.0f + trace));
            quat.W = 0.5f * root;
            root = 0.5f / root;
            quat.X = (matrix[2][1] - matrix[1][2]) * root;
            quat.Y = (matrix[0][2] - matrix[2][0]) * root;
            quat.Z = (matrix[1][0] - matrix[0][1]) * root;
        } else {
            int[] next = new int[3];
            next[0] = 1;
            next[1] = 2;
            i = 0;
            if (matrix[1][1] > matrix[0][0]) {
                i = 1;
            }
            if (matrix[2][2] > matrix[i][i]) {
                i = 2;
            }
            int j = next[i];
            int k = next[j];
            root = (float) Math.sqrt((double) (((matrix[i][i] - matrix[j][j]) - matrix[k][k]) + 1.0f));
            float[] quatAxes = new float[]{quat.X, quat.Y, quat.Z};
            quatAxes[i] = 0.5f * root;
            root = 0.5f / root;
            quat.W = (matrix[k][j] - matrix[j][k]) * root;
            quatAxes[j] = (matrix[j][i] + matrix[i][j]) * root;
            quatAxes[k] = (matrix[k][i] + matrix[i][k]) * root;
            quat.X = quatAxes[0];
            quat.Y = quatAxes[1];
            quat.Z = quatAxes[2];
        }
        return quat;
    }

    public static AngleAxis toAngleAxis(Quat quat) {
        AngleAxis angleAxis = new AngleAxis();
        float mag = ((quat.X * quat.X) + (quat.Y * quat.Y)) + (quat.Z * quat.Z);
        if (mag > 0.0f) {
            float invMag = (float) (1.0d / Math.sqrt((double) mag));
            angleAxis.Angle = (float) (2.0d * Math.acos((double) quat.W));
            angleAxis.xAxis = quat.X * invMag;
            angleAxis.yAxis = quat.Y * invMag;
            angleAxis.zAxis = quat.Z * invMag;
        } else {
            angleAxis.Angle = 0.0f;
            angleAxis.xAxis = 0.0f;
            angleAxis.yAxis = 0.0f;
            angleAxis.zAxis = 0.0f;
        }
        return angleAxis;
    }

    public static Quat fromAngleAxis(AngleAxis angleAxis) {
        Quat quat = new Quat();
        float halfAngle = angleAxis.Angle * 0.5f;
        float sinHalfAngle = (float) Math.sin((double) halfAngle);
        quat.W = (float) Math.cos((double) halfAngle);
        quat.X = (float) (((double) sinHalfAngle) * Math.cos((double) angleAxis.xAxis));
        quat.Y = (float) (((double) sinHalfAngle) * Math.cos((double) angleAxis.yAxis));
        quat.Z = (float) (((double) sinHalfAngle) * Math.cos((double) angleAxis.zAxis));
        return quat;
    }

    public static float dot(Quat quatA, Quat quatB) {
        return (((quatA.W * quatB.W) + (quatA.X * quatB.X)) + (quatA.Y * quatB.Y)) + (quatA.Z * quatB.Z);
    }

    public static Quat plus(Quat quatA, Quat quatB) {
        return new Quat(quatA.W + quatB.W, quatA.X + quatB.X, quatA.Y + quatB.Y, quatA.Z + quatB.Z);
    }

    public static Quat product(Quat quat, float factor) {
        return new Quat(quat.W * factor, quat.X * factor, quat.Y * factor, quat.Z * factor);
    }

    public static Vector3 product(Quat quat, Vector3 vector) {
        Vector3 quatVector = new Vector3(quat.X, quat.Y, quat.Z);
        Vector3 u1Vector = Vector3.crossProduct(quatVector, vector);
        return Vector3.addition(vector, Vector3.addition(Vector3.product(u1Vector, quat.W * 2.0f), Vector3.product(Vector3.crossProduct(quatVector, u1Vector), 2.0f)));
    }

    public static Quat product(Quat quatA, Quat quatB) {
        return new Quat((((quatA.W * quatB.W) - (quatA.X * quatB.X)) - (quatA.Y * quatB.Y)) - (quatA.Z * quatB.Z), (((quatA.W * quatB.X) + (quatA.X * quatB.W)) + (quatA.Y * quatB.Z)) - (quatA.Z * quatB.Y), (((quatA.W * quatB.Y) + (quatA.Y * quatB.W)) + (quatA.Z * quatB.X)) - (quatA.X * quatB.Z), (((quatA.W * quatB.Z) + (quatA.Z * quatB.W)) + (quatA.X * quatB.Y)) - (quatA.Y * quatB.X));
    }
}
