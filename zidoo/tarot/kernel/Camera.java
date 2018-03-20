package zidoo.tarot.kernel;

import android.opengl.GLU;
import javax.microedition.khronos.opengles.GL10;

public class Camera extends Transform {
    public Vector3 LookAt = null;
    private float mFar = 100.0f;
    private float mFovy = 60.0f;
    private GL10 mGl = null;
    private float mNear = 0.01f;
    private int mScreenHeight = 0;
    private int mScreenWidth = 0;

    public Camera(GL10 gl) {
        this.mGl = gl;
        this.Position = new Position(0.0f, 0.0f, 5.0f);
        this.LookAt = new Vector3();
    }

    public void setup(int width, int height) {
        this.mScreenWidth = width;
        this.mScreenHeight = height;
        float ratio = ((float) this.mScreenWidth) / ((float) this.mScreenHeight);
        this.mGl.glViewport(0, 0, width, height);
        this.mGl.glMatrixMode(5889);
        this.mGl.glLoadIdentity();
        GLU.gluPerspective(this.mGl, this.mFovy, ratio, this.mNear, this.mFar);
        this.mGl.glMatrixMode(5888);
        this.mGl.glLoadIdentity();
    }

    public void setFovy(float fovy) {
        this.mFovy = fovy;
        setup(this.mScreenWidth, this.mScreenHeight);
    }

    public void setVisualDistance(float near, float far) {
        this.mNear = near;
        this.mFar = far;
        setup(this.mScreenWidth, this.mScreenHeight);
    }

    public float[] getVisualDistance() {
        return new float[]{this.mNear, this.mFar};
    }

    public void setPosition(Position position) {
        this.Position = position;
    }

    public void setRotate(Quat rotateQuat) {
        this.Rotate.Quaternion = rotateQuat;
        this.LookAt = Quat.product(this.Rotate.Quaternion, Vector3.FRONT_AXIS);
    }

    public void setLookAt(Vector3 lookAt) {
        this.LookAt = lookAt;
    }

    public int getScreenWidth() {
        return this.mScreenWidth;
    }

    public int getScreenHeight() {
        return this.mScreenHeight;
    }

    public void update() {
        GLU.gluLookAt(this.mGl, this.Position.X, this.Position.Y, this.Position.Z, this.LookAt.X, this.LookAt.Y, this.LookAt.Z, 0.0f, 1.0f, 0.0f);
    }
}
