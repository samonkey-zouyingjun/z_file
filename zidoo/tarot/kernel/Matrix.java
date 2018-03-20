package zidoo.tarot.kernel;

public class Matrix {
    public static final String TAG = "Matrix";
    private float[] mMatrix;

    public Matrix() {
        this.mMatrix = new float[16];
        reset();
    }

    public Matrix(Matrix src) {
        this.mMatrix = new float[16];
        set(src);
    }

    public Matrix(float[] src) {
        this.mMatrix = new float[16];
        set(src);
    }

    public void reset() {
        android.opengl.Matrix.setIdentityM(this.mMatrix, 0);
    }

    public void set(Matrix src) {
        if (src == null) {
            reset();
            return;
        }
        float[] srcMatrix = src.get();
        for (int i = 0; i < 16; i++) {
            this.mMatrix[i] = srcMatrix[i];
        }
    }

    public void set(float[] src) {
        if (src == null) {
            reset();
        } else if (src.length == 9) {
            this.mMatrix[0] = src[0];
            this.mMatrix[1] = src[1];
            this.mMatrix[2] = src[2];
            this.mMatrix[3] = 0.0f;
            this.mMatrix[4] = src[3];
            this.mMatrix[5] = src[4];
            this.mMatrix[6] = src[5];
            this.mMatrix[7] = 0.0f;
            this.mMatrix[8] = src[6];
            this.mMatrix[9] = src[7];
            this.mMatrix[10] = src[8];
            this.mMatrix[11] = 0.0f;
            this.mMatrix[12] = 0.0f;
            this.mMatrix[13] = 0.0f;
            this.mMatrix[14] = 0.0f;
            this.mMatrix[15] = 1.0f;
        } else if (src.length == 16) {
            for (int i = 0; i < 16; i++) {
                this.mMatrix[i] = src[i];
            }
        } else {
            reset();
        }
    }

    public float[] get() {
        return this.mMatrix;
    }

    public void preConcat(Matrix other) {
        if (other != null) {
            float[] result = new float[16];
            android.opengl.Matrix.multiplyMM(result, 0, this.mMatrix, 0, other.get(), 0);
            for (int i = 0; i < 16; i++) {
                this.mMatrix[i] = result[i];
            }
        }
    }

    public void postConcat(Matrix other) {
        if (other != null) {
            float[] result = new float[16];
            android.opengl.Matrix.multiplyMM(result, 0, other.get(), 0, this.mMatrix, 0);
            for (int i = 0; i < 16; i++) {
                this.mMatrix[i] = result[i];
            }
        }
    }

    public Vector3 concatVector(Vector3 vector) {
        if (vector == null) {
            return null;
        }
        result = new float[4];
        android.opengl.Matrix.multiplyMV(result, 0, this.mMatrix, 0, new float[]{vector.X, vector.Y, vector.Z, 1.0f}, 0);
        if (result[3] == 0.0f) {
            return new Vector3(result[0], result[1], result[2]);
        }
        return new Vector3(result[0] / result[3], result[1] / result[2], result[2] / result[3]);
    }

    public void setTranslate(float dx, float dy, float dz) {
        float[] result = new Matrix().get();
        result[12] = dx;
        result[13] = dy;
        result[14] = dz;
    }

    public void setScale(float dx, float dy, float dz) {
        float[] result = new Matrix().get();
        result[0] = dx;
        result[5] = dy;
        result[10] = dz;
    }

    public void setRotate(float angle, float ax, float ay, float az) {
        float[] result = new Matrix().get();
        float xy = ax * ay;
        float xz = ax * az;
        float yy = ay * ay;
        float yz = ay * az;
        float zz = az * az;
        float sinAngle = (float) Math.sin((double) angle);
        float cosAngle = (float) Math.cos((double) angle);
        float complementaryCosAngle = 1.0f - cosAngle;
        result[0] = ((ax * ax) * complementaryCosAngle) + cosAngle;
        result[1] = (xy * complementaryCosAngle) + (az * sinAngle);
        result[2] = (xz * complementaryCosAngle) - (ay * sinAngle);
        result[3] = 0.0f;
        result[4] = (xy * complementaryCosAngle) - (az * sinAngle);
        result[5] = (yy * complementaryCosAngle) + cosAngle;
        result[6] = (yz * complementaryCosAngle) + (ax * sinAngle);
        result[7] = 0.0f;
        result[8] = (xz * complementaryCosAngle) + (ay * sinAngle);
        result[9] = (yz * complementaryCosAngle) - (ax * sinAngle);
        result[10] = (zz * complementaryCosAngle) + cosAngle;
        result[11] = 0.0f;
        result[12] = 0.0f;
        result[13] = 0.0f;
        result[14] = 0.0f;
        result[15] = 1.0f;
    }

    public void invert() {
        set(invert(this).get());
    }

    public static Matrix invert(Matrix matrix) {
        float[] invArray = new float[16];
        android.opengl.Matrix.invertM(invArray, 0, matrix.get(), 0);
        return new Matrix(invArray);
    }
}
