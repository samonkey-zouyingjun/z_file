package zidoo.tarot.kernel;

public class Rotate extends Vector3 {
    public static final String TAG = "Rotate";
    public Quat Quaternion = new Quat();

    public Rotate(Quat quat) {
        setQuatRotate(quat);
    }

    public Rotate(float x, float y, float z) {
        setRotate(x, y, z);
    }

    public Quat getQuatRotate() {
        return this.Quaternion;
    }

    public void setQuatRotate(Quat quaternion) {
        if (quaternion != null) {
            this.Quaternion.W = quaternion.W;
            this.Quaternion.X = quaternion.X;
            this.Quaternion.Y = quaternion.Y;
            this.Quaternion.Z = quaternion.Z;
        }
    }

    public void setRotate(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.Quaternion = Quat.toQuat(this.X, this.Y, this.Z);
    }
}
