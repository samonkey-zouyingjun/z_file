package zidoo.tarot.kernel;

public class BoundSphere implements Cloneable {
    public Vector3 CenterPoint = new Vector3();
    public float Radius = 0.0f;

    protected BoundSphere clone() {
        BoundSphere cloned;
        try {
            cloned = (BoundSphere) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloned = new BoundSphere();
        }
        cloned.CenterPoint = this.CenterPoint.clone();
        return cloned;
    }

    public BoundSphere(Vector3 center, float radius) {
        this.CenterPoint = center;
        this.Radius = radius;
    }

    public void generateBoundSphere(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        float distanceX = maxX - minX;
        float distanceY = maxY - minY;
        float distanceZ = maxZ - minZ;
        this.CenterPoint.X = (distanceX / 2.0f) + minX;
        this.CenterPoint.Y = (distanceY / 2.0f) + minY;
        this.CenterPoint.Z = (distanceZ / 2.0f) + minZ;
        this.Radius = ((float) Math.sqrt((Math.pow((double) distanceX, 2.0d) + Math.pow((double) distanceY, 2.0d)) + Math.pow((double) distanceZ, 2.0d))) / 2.0f;
    }

    public Vector3 getCenter() {
        return this.CenterPoint;
    }

    public float[] getCenterArray() {
        return new float[]{this.CenterPoint.X, this.CenterPoint.Y, this.CenterPoint.Z};
    }

    public float getRadius() {
        return this.Radius;
    }
}
