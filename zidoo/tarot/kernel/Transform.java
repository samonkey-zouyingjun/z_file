package zidoo.tarot.kernel;

public class Transform implements Cloneable {
    public static final String TAG = "Transform";
    public Vector3 Back = new Vector3();
    public Vector3 Down = new Vector3();
    public Vector3 Front = new Vector3();
    public Vector3 Left = new Vector3();
    public Position Position = new Position();
    public Position RelativePosition = new Position();
    public Rotate RelativeRotate = new Rotate();
    public Scale RelativeScale = new Scale();
    public float RenderIndex = -1.0f;
    public Vector3 Right = new Vector3();
    public Rotate Rotate = new Rotate();
    public Scale Scale = new Scale();
    public Vector3 Up = new Vector3();

    public Transform clone() {
        try {
            Transform cloned = (Transform) super.clone();
            cloned.Position.set(this.Position.get());
            cloned.Rotate.set(this.Rotate.get());
            cloned.Scale.set(this.Scale.get());
            cloned.RelativePosition.set(this.RelativePosition.get());
            cloned.RelativeRotate.set(this.RelativeRotate.get());
            cloned.RelativeScale.set(this.RelativeScale.get());
            cloned.Front.set(this.Front.get());
            cloned.Back.set(this.Back.get());
            cloned.Left.set(this.Left.get());
            cloned.Right.set(this.Right.get());
            cloned.Up.set(this.Up.get());
            cloned.Down.set(this.Down.get());
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void translate(float x, float y, float z) {
        this.Position.X = x;
        this.Position.Y = y;
        this.Position.Z = z;
    }

    public void rotateYaw(float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = 0.0f;
        relativeRotate.Y = (float) Math.sin(halfRadian);
        relativeRotate.Z = 0.0f;
        this.Rotate.Quaternion = Quat.product(relativeRotate, this.Rotate.Quaternion).normalize();
    }

    public void rotatePitch(float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = (float) Math.sin(halfRadian);
        relativeRotate.Y = 0.0f;
        relativeRotate.Z = 0.0f;
        this.Rotate.Quaternion = Quat.product(relativeRotate, this.Rotate.Quaternion).normalize();
    }

    public void rotateRoll(float radian) {
        Quat relativeRotate = new Quat();
        double halfRadian = ((double) radian) / 2.0d;
        relativeRotate.W = (float) Math.cos(halfRadian);
        relativeRotate.X = 0.0f;
        relativeRotate.Y = 0.0f;
        relativeRotate.Z = (float) Math.sin(halfRadian);
        this.Rotate.Quaternion = Quat.product(relativeRotate, this.Rotate.Quaternion).normalize();
    }

    public void rotate(Quat rotateQuat) {
        this.Rotate.Quaternion = Quat.product(rotateQuat, this.Rotate.Quaternion).normalize();
    }

    public void scale(float x, float y, float z) {
        this.Scale.X = x;
        this.Scale.Y = y;
        this.Scale.Z = z;
    }
}
