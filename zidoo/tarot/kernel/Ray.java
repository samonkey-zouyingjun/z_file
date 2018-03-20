package zidoo.tarot.kernel;

import android.opengl.GLU;
import android.support.v4.widget.AutoScrollHelper;

public class Ray {
    private float[] mModelViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float mRayCastDistance = AutoScrollHelper.NO_MAX;
    private Vector3 mRayCastPoint = null;
    private Vector3 mRayDirection = null;
    private Vector3 mRayEndPoint = null;
    private Vector3 mRayStartPoint = null;
    private int[] mViewportMatrix = new int[4];

    public void generateRay(float screenX, float screenY, float[] modelViewMatrix, float[] projectionMatrix, int[] viewportMatrix, int cameraScreenHeight) {
        float winX = screenX;
        float winY = (float) cameraScreenHeight;
        float[] rayStartPoint = new float[4];
        float[] rayStartEnd = new float[4];
        this.mModelViewMatrix = modelViewMatrix;
        this.mProjectionMatrix = projectionMatrix;
        this.mViewportMatrix = viewportMatrix;
        GLU.gluUnProject(winX, winY, 0.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, rayStartPoint, 0);
        GLU.gluUnProject(winX, winY, 1.0f, this.mModelViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewportMatrix, 0, rayStartEnd, 0);
        this.mRayStartPoint = new Vector3(rayStartPoint[0] / rayStartPoint[3], rayStartPoint[1] / rayStartPoint[3], rayStartPoint[2] / rayStartPoint[3]);
        this.mRayEndPoint = new Vector3(rayStartEnd[0] / rayStartEnd[3], rayStartEnd[1] / rayStartEnd[3], rayStartEnd[2] / rayStartEnd[3]);
        this.mRayDirection = Vector3.normalize(Vector3.subtraction(this.mRayEndPoint, this.mRayStartPoint));
    }

    public Matrix getModelViewMatrix() {
        return new Matrix(this.mModelViewMatrix);
    }

    public Matrix getProjectionMatrix() {
        return new Matrix(this.mProjectionMatrix);
    }

    public boolean intersectSphere(BoundSphere mBoundSphere) {
        Vector3 diff = Vector3.subtraction(this.mRayStartPoint, mBoundSphere.CenterPoint);
        float areaDistance = Vector3.dotProduct(diff, diff) - (mBoundSphere.Radius * mBoundSphere.Radius);
        if (areaDistance <= 0.0f) {
            return true;
        }
        float directDistance = Vector3.dotProduct(this.mRayDirection, diff);
        if (directDistance >= 0.0f) {
            return false;
        }
        return (directDistance * directDistance) - areaDistance >= 0.0f;
    }

    public boolean intersectMesh(Mesh[] meshes) {
        boolean isIntersect = false;
        int i = 4;
        float[] intersetLocation = new float[]{0.0f, 0.0f, 0.0f, AutoScrollHelper.NO_MAX};
        this.mRayCastPoint = null;
        this.mRayCastDistance = AutoScrollHelper.NO_MAX;
        if (meshes == null || meshes.length <= 0) {
            return false;
        }
        for (Mesh mesh : meshes) {
            if (mesh == null) {
                return false;
            }
            if (mesh.VertexBuffer.capacity() <= 0) {
                return false;
            }
            short[] indiceArray;
            float[] vertexArray = new float[mesh.VertexBuffer.capacity()];
            mesh.VertexBuffer.position(0);
            mesh.VertexBuffer.get(vertexArray);
            if (mesh.IndexBuffer.capacity() <= 0) {
                short indiceSize = vertexArray.length / 3;
                indiceArray = new short[indiceSize];
                for (short j = (short) 0; j < indiceSize; j = (short) (j + 1)) {
                    indiceArray[j] = j;
                }
            } else {
                indiceArray = new short[mesh.IndexBuffer.capacity()];
                mesh.IndexBuffer.position(0);
                mesh.IndexBuffer.get(indiceArray);
            }
            int faceSize = indiceArray.length / 3;
            int pointSize = indiceArray.length;
            int pointItr = 0;
            for (int faceItr = 0; faceItr < faceSize && pointItr < pointSize; faceItr++) {
                Vector3 pointA = new Vector3(vertexArray[(indiceArray[pointItr] * 3) + 0], vertexArray[(indiceArray[pointItr] * 3) + 1], vertexArray[(indiceArray[pointItr] * 3) + 2]);
                pointItr++;
                Vector3 pointB = new Vector3(vertexArray[(indiceArray[pointItr] * 3) + 0], vertexArray[(indiceArray[pointItr] * 3) + 1], vertexArray[(indiceArray[pointItr] * 3) + 2]);
                pointItr++;
                Vector3 pointC = new Vector3(vertexArray[(indiceArray[pointItr] * 3) + 0], vertexArray[(indiceArray[pointItr] * 3) + 1], vertexArray[(indiceArray[pointItr] * 3) + 2]);
                pointItr++;
                float[] intersectPoint = intersectTriangle(pointA, pointB, pointC);
                if (intersectPoint != null) {
                    if (!isIntersect) {
                        isIntersect = true;
                        intersetLocation = intersectPoint;
                    } else if (intersetLocation[3] > intersectPoint[3]) {
                        intersetLocation = intersectPoint;
                    }
                }
            }
            this.mRayCastPoint = new Vector3(intersetLocation[0], intersetLocation[1], intersetLocation[2]);
            this.mRayCastDistance = intersetLocation[3];
        }
        return isIntersect;
    }

    private float[] intersectTriangle(Vector3 pointA, Vector3 pointB, Vector3 pointC) {
        int sign;
        Vector3 diff = Vector3.subtraction(this.mRayStartPoint, pointA);
        Vector3 edgeA = Vector3.subtraction(pointB, pointA);
        Vector3 edgeB = Vector3.subtraction(pointC, pointA);
        Vector3 norm = Vector3.crossProduct(edgeA, edgeB);
        float parameterC = Vector3.dotProduct(this.mRayDirection, norm);
        if (((double) parameterC) > 1.0E-5d) {
            sign = 1;
        } else if (((double) parameterC) >= -1.0E-5d) {
            return null;
        } else {
            sign = -1;
            parameterC = -parameterC;
        }
        float parameterA = ((float) sign) * Vector3.dotProduct(this.mRayDirection, Vector3.crossProduct(diff, edgeB));
        if (parameterA >= 0.0f) {
            float parameterB = ((float) sign) * Vector3.dotProduct(this.mRayDirection, Vector3.crossProduct(edgeA, diff));
            if (parameterB >= 0.0f && parameterA + parameterB <= parameterC) {
                float parameterD = ((float) (-sign)) * Vector3.dotProduct(diff, norm);
                if (parameterD >= 0.0f) {
                    float intersectPointDistance = parameterD / parameterC;
                    return new float[]{this.mRayStartPoint.X + (this.mRayDirection.X * intersectPointDistance), this.mRayStartPoint.Y + (this.mRayDirection.Y * intersectPointDistance), this.mRayStartPoint.Z + (this.mRayDirection.Z * intersectPointDistance), intersectPointDistance};
                }
            }
        }
        return null;
    }

    public static Ray tranformFromWorldToModelView(Ray ray, Matrix invertTransfrom) {
        Vector3 startPoint = ray.mRayStartPoint;
        Vector3 endPoint = ray.mRayEndPoint;
        Ray transformedRay = new Ray();
        transformedRay.mRayStartPoint = invertTransfrom.concatVector(startPoint);
        transformedRay.mRayEndPoint = invertTransfrom.concatVector(endPoint);
        transformedRay.mRayDirection = Vector3.subtraction(endPoint, startPoint);
        transformedRay.mModelViewMatrix = ray.mModelViewMatrix;
        transformedRay.mProjectionMatrix = ray.mProjectionMatrix;
        transformedRay.mViewportMatrix = ray.mViewportMatrix;
        return transformedRay;
    }
}
