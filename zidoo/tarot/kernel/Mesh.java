package zidoo.tarot.kernel;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Mesh {
    public static final String TAG = "Mesh";
    public static final int TRIANGLE_TYPE_FAN = 6;
    public static final int TRIANGLE_TYPE_INDEPENDENT = 4;
    public static final int TRIANGLE_TYPE_STRIP = 5;
    public FloatBuffer ColorBuffer = null;
    public FloatBuffer CoordBuffer = null;
    public boolean Enabled = true;
    public ShortBuffer IndexBuffer = null;
    public FloatBuffer NormBuffer = null;
    public int TriangleType = 4;
    public FloatBuffer VertexBuffer = null;

    public void setVertexes(FloatBuffer vertexBuffer) {
        this.VertexBuffer = vertexBuffer;
    }

    public void setVertexes(float[] vertexes) {
        this.VertexBuffer = GLResources.floatBuffer(vertexes);
    }

    public void setCoordinates(FloatBuffer coordBuffer) {
        this.CoordBuffer = coordBuffer;
    }

    public void setCoordinates(float[] coordinates) {
        this.CoordBuffer = GLResources.floatBuffer(coordinates);
    }

    public void setNormals(FloatBuffer normalBuffer) {
        this.NormBuffer = normalBuffer;
    }

    public void setNormals(float[] normals) {
        this.NormBuffer = GLResources.floatBuffer(normals);
    }

    public void setIndices(ShortBuffer indexBuffer) {
        this.IndexBuffer = indexBuffer;
    }

    public void setIndices(short[] indexes) {
        this.IndexBuffer = GLResources.shortBuffer(indexes);
    }

    public void setColors(FloatBuffer colorBuffer) {
        this.ColorBuffer = colorBuffer;
    }

    public void setColors(float[] colors) {
        this.ColorBuffer = GLResources.floatBuffer(colors);
    }

    public boolean isEnabled() {
        return this.Enabled;
    }

    public void setEnabled(boolean enabled) {
        this.Enabled = enabled;
    }
}
