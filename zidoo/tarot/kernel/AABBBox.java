package zidoo.tarot.kernel;

import android.graphics.RectF;
import android.opengl.GLES11;
import android.opengl.GLU;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AABBBox implements Cloneable {
    private static final String TAG = "AABBBox";
    private BoundBox mBoundBox;
    private BoundSphere mBoundSphere;
    private boolean mIsHitted;
    private boolean mIsInitBound;
    private List<float[]> mMeshVertexes;
    private float[] mModelViewMatrix;
    private GameObject mParent;
    private float[] mProjectionMatrix;
    private RectF mScreenRect;
    private Vector2 mScreenTouch;
    private int[] mViewportMatrix;

    public AABBBox() {
        this.mParent = null;
        this.mIsHitted = false;
        this.mIsInitBound = false;
        this.mScreenTouch = null;
        this.mBoundBox = new BoundBox();
        this.mBoundSphere = new BoundSphere();
        this.mScreenRect = new RectF();
        this.mViewportMatrix = new int[4];
        this.mModelViewMatrix = new float[16];
        this.mProjectionMatrix = new float[16];
        this.mMeshVertexes = null;
        this.mMeshVertexes = new ArrayList();
    }

    protected AABBBox clone() {
        AABBBox cloned;
        try {
            cloned = (AABBBox) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloned = new AABBBox();
        }
        cloned.mParent = this.mParent;
        cloned.mScreenTouch = this.mScreenTouch;
        cloned.mBoundBox = this.mBoundBox.clone();
        cloned.mBoundSphere = this.mBoundSphere.clone();
        cloned.mScreenRect.set(this.mScreenRect);
        cloned.mViewportMatrix = (int[]) this.mViewportMatrix.clone();
        cloned.mModelViewMatrix = (float[]) this.mModelViewMatrix.clone();
        cloned.mProjectionMatrix = (float[]) this.mProjectionMatrix.clone();
        cloned.mMeshVertexes = null;
        if (this.mMeshVertexes != null) {
            cloned.mMeshVertexes = new ArrayList(this.mMeshVertexes);
        }
        return cloned;
    }

    public AABBBox(GameObject parent) {
        this.mParent = null;
        this.mIsHitted = false;
        this.mIsInitBound = false;
        this.mScreenTouch = null;
        this.mBoundBox = new BoundBox();
        this.mBoundSphere = new BoundSphere();
        this.mScreenRect = new RectF();
        this.mViewportMatrix = new int[4];
        this.mModelViewMatrix = new float[16];
        this.mProjectionMatrix = new float[16];
        this.mMeshVertexes = null;
        setParent(parent);
    }

    public void setParent(GameObject parent) {
        this.mParent = parent;
    }

    public void scanMeshes() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;
        if (this.mParent.mMeshes != null && this.mParent.mMeshes.length > 0) {
            int meshSize = this.mParent.mMeshes.length;
            for (int i = 0; i < meshSize; i++) {
                if (this.mParent.mMeshes[i].VertexBuffer.capacity() > 0) {
                    int j;
                    float[] array = new float[this.mParent.mMeshes[i].VertexBuffer.capacity()];
                    this.mParent.mMeshes[i].VertexBuffer.position(0);
                    this.mParent.mMeshes[i].VertexBuffer.get(array);
                    this.mMeshVertexes.add(Arrays.copyOf(array, array.length));
                    int arraySize = array.length;
                    for (j = 0; j < arraySize; j += 3) {
                        if (minX >= array[j]) {
                            minX = array[j];
                        }
                        if (maxX <= array[j]) {
                            maxX = array[j];
                        }
                    }
                    for (j = 1; j < arraySize; j += 3) {
                        if (minY >= array[j]) {
                            minY = array[j];
                        }
                        if (maxY <= array[j]) {
                            maxY = array[j];
                        }
                    }
                    for (j = 2; j < arraySize; j += 3) {
                        if (minZ >= array[j]) {
                            minZ = array[j];
                        }
                        if (maxZ <= array[j]) {
                            maxZ = array[j];
                        }
                    }
                }
            }
            this.mIsInitBound = true;
        }
        this.mBoundSphere.generateBoundSphere(minX, minY, minZ, maxX, maxY, maxZ);
        this.mBoundBox.generateBoundBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void setBoundBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.mIsInitBound = true;
        this.mBoundSphere.generateBoundSphere(minX, minY, minZ, maxX, maxY, maxZ);
        this.mBoundBox.generateBoundBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean isHit(float screenX, float screenY, float[] innerHitPoint) {
        float[] win = new float[3];
        this.mScreenRect = new RectF();
        if (!this.mIsInitBound) {
            scanMeshes();
        }
        if (GLU.gluProject(this.mBoundBox.getMinX(), this.mBoundBox.getMaxY(), 0.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, win, 0) == 1) {
            this.mScreenRect.left = win[0];
            this.mScreenRect.top = ((float) this.mViewportMatrix[3]) - win[1];
        }
        if (GLU.gluProject(this.mBoundBox.getMaxX(), this.mBoundBox.getMinY(), 0.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, win, 0) == 1) {
            this.mScreenRect.right = win[0];
            this.mScreenRect.bottom = ((float) this.mViewportMatrix[3]) - win[1];
        }
        boolean result = this.mScreenRect.contains(screenX, screenY);
        if (innerHitPoint != null && result) {
            innerHitPoint[0] = screenX - this.mScreenRect.left;
            innerHitPoint[1] = screenY - this.mScreenRect.bottom;
        }
        return result;
    }

    public void update() {
        this.mIsHitted = false;
        GLES11.glGetIntegerv(2978, this.mViewportMatrix, 0);
        GLES11.glGetFloatv(2983, this.mProjectionMatrix, 0);
        GLES11.glGetFloatv(2982, this.mModelViewMatrix, 0);
        if (this.mScreenTouch != null) {
            this.mIsHitted = isHit(this.mScreenTouch.X, this.mScreenTouch.Y, null);
            this.mScreenTouch = null;
        }
    }

    public void injectScreenPosition(float x, float y) {
        this.mScreenTouch = new Vector2(x, y);
    }

    public boolean rayCast(float x, float y, float[] innerHitPoint) {
        return isHit(x, y, innerHitPoint);
    }

    public boolean getRayCastResult() {
        return this.mIsHitted;
    }

    public RectF getScreenRect() {
        float[] win = new float[3];
        if (GLU.gluProject(this.mBoundBox.getMinX(), this.mBoundBox.getMaxY(), 0.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, win, 0) == 1) {
            this.mScreenRect.left = win[0];
            this.mScreenRect.top = ((float) this.mViewportMatrix[3]) - win[1];
        }
        if (GLU.gluProject(this.mBoundBox.getMaxX(), this.mBoundBox.getMinY(), 0.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, win, 0) == 1) {
            this.mScreenRect.right = win[0];
            this.mScreenRect.bottom = ((float) this.mViewportMatrix[3]) - win[1];
        }
        return this.mScreenRect;
    }

    public float getAABBBoxWidth() {
        return this.mBoundBox.getMaxX() - this.mBoundBox.getMinX();
    }

    public float getAABBBoxHeight() {
        return this.mBoundBox.getMaxY() - this.mBoundBox.getMinY();
    }

    public float getAABBBoxDepth() {
        return this.mBoundBox.getMaxZ() - this.mBoundBox.getMinZ();
    }
}
