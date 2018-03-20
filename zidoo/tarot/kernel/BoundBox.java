package zidoo.tarot.kernel;

public class BoundBox implements Cloneable {
    private static final String TAG = null;
    private float mBack = 0.0f;
    private float mBottom = 0.0f;
    private float mFront = 0.0f;
    private float mLeft = 0.0f;
    private Vector3 mLeftBottomBack = null;
    private Vector3 mLeftBottomFront = null;
    private Vector3 mLeftTopBack = null;
    private Vector3 mLeftTopFront = null;
    private float mRight = 0.0f;
    private Vector3 mRightBottomBack = null;
    private Vector3 mRightBottomFront = null;
    private Vector3 mRightTopBack = null;
    private Vector3 mRightTopFront = null;
    private float mTop = 0.0f;

    protected BoundBox clone() {
        BoundBox cloned;
        try {
            cloned = (BoundBox) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloned = new BoundBox();
        }
        cloned.mLeftTopBack = this.mLeftTopBack.clone();
        cloned.mLeftTopFront = this.mLeftTopFront.clone();
        cloned.mRightTopBack = this.mRightTopBack.clone();
        cloned.mRightTopFront = this.mRightTopFront.clone();
        cloned.mLeftBottomBack = this.mLeftBottomBack.clone();
        cloned.mLeftBottomFront = this.mLeftBottomFront.clone();
        cloned.mRightBottomBack = this.mRightBottomBack.clone();
        cloned.mRightBottomFront = this.mRightBottomFront.clone();
        return cloned;
    }

    public BoundBox(float left, float right, float top, float bottom, float front, float back) {
        this.mLeft = left;
        this.mRight = right;
        this.mTop = top;
        this.mBottom = bottom;
        this.mFront = front;
        this.mBack = back;
        this.mLeftTopBack = new Vector3(left, top, back);
        this.mLeftTopFront = new Vector3(left, top, front);
        this.mRightTopBack = new Vector3(right, top, back);
        this.mRightTopFront = new Vector3(right, top, front);
        this.mLeftBottomBack = new Vector3(left, bottom, back);
        this.mLeftBottomFront = new Vector3(left, bottom, front);
        this.mRightBottomBack = new Vector3(right, bottom, back);
        this.mRightBottomFront = new Vector3(right, bottom, front);
    }

    public void generateBoundBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.mLeft = minX;
        this.mRight = maxX;
        this.mTop = maxY;
        this.mBottom = minY;
        this.mFront = maxZ;
        this.mBack = minZ;
        this.mLeftTopBack = new Vector3(this.mLeft, this.mTop, this.mBack);
        this.mLeftTopFront = new Vector3(this.mLeft, this.mTop, this.mFront);
        this.mRightTopBack = new Vector3(this.mRight, this.mTop, this.mBack);
        this.mRightTopFront = new Vector3(this.mRight, this.mTop, this.mFront);
        this.mLeftBottomBack = new Vector3(this.mLeft, this.mBottom, this.mBack);
        this.mLeftBottomFront = new Vector3(this.mLeft, this.mBottom, this.mFront);
        this.mRightBottomBack = new Vector3(this.mRight, this.mBottom, this.mBack);
        this.mRightBottomFront = new Vector3(this.mRight, this.mBottom, this.mFront);
    }

    public float[] getBoundVertexes() {
        return new float[]{this.mLeftTopBack.X, this.mLeftTopBack.Y, this.mLeftTopBack.Z, this.mLeftTopFront.X, this.mLeftTopFront.Y, this.mLeftTopFront.Z, this.mRightTopBack.X, this.mRightTopBack.Y, this.mRightTopBack.Z, this.mRightTopFront.X, this.mRightTopFront.Y, this.mRightTopFront.Z, this.mLeftBottomBack.X, this.mLeftBottomBack.Y, this.mLeftBottomBack.Z, this.mLeftBottomFront.X, this.mLeftBottomFront.Y, this.mLeftBottomFront.Z, this.mRightBottomBack.X, this.mRightBottomBack.Y, this.mRightBottomBack.Z, this.mRightBottomFront.X, this.mRightBottomFront.Y, this.mRightBottomFront.Z};
    }

    public Vector3 getCenter() {
        return new Vector3(this.mLeft + ((this.mRight - this.mLeft) / 2.0f), this.mTop + ((this.mBottom - this.mTop) / 2.0f), this.mBack + ((this.mFront - this.mBack) / 2.0f));
    }

    public float[] getCenterArray() {
        return new float[]{this.mLeft + ((this.mRight - this.mLeft) / 2.0f), this.mTop + ((this.mBottom - this.mTop) / 2.0f), this.mBack + ((this.mFront - this.mBack) / 2.0f)};
    }

    public float getMinX() {
        return this.mLeft;
    }

    public float getMaxX() {
        return this.mRight;
    }

    public float getMinY() {
        return this.mBottom;
    }

    public float getMaxY() {
        return this.mTop;
    }

    public float getMinZ() {
        return this.mBack;
    }

    public float getMaxZ() {
        return this.mFront;
    }
}
