package zidoo.tarot.gameobject;

import android.graphics.Bitmap;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Material;
import zidoo.tarot.kernel.Mesh;
import zidoo.tarot.kernel.Texture;

public class Sphere extends GameObject {
    public static final String TAG = "Sphere";

    public Sphere(GLContext glContext) {
        super(glContext);
    }

    public Sphere(GLContext glContext, int imageResid) {
        this(glContext);
        setImage(imageResid);
    }

    public Sphere(GLContext glContext, Texture texture) {
        this(glContext);
        setImage(texture);
    }

    protected void initMesh() {
        int latNum;
        int vpdItr = 0;
        int nmdItr = 0;
        int tcdItr = 0;
        int idxItr = 0;
        int unitDataSize = 61 * 61;
        float[] vertexPosData = new float[11163];
        float[] normalData = new float[11163];
        float[] textureCoordData = new float[7442];
        short[] indexData = new short[21600];
        for (latNum = 0; latNum <= 60; latNum++) {
            int longNum;
            float theta = (float) ((((double) latNum) * 3.141592653589793d) / ((double) 60));
            float sinTheta = (float) Math.sin((double) theta);
            float cosTheta = (float) Math.cos((double) theta);
            for (longNum = 0; longNum <= 60; longNum++) {
                float phi = (float) ((((double) (longNum * 2)) * 3.141592653589793d) / ((double) 60));
                float x = ((float) Math.cos((double) phi)) * sinTheta;
                float y = cosTheta;
                float z = ((float) Math.sin((double) phi)) * sinTheta;
                float u = 1.0f - (((float) longNum) / ((float) 60));
                float v = 1.0f - (((float) latNum) / ((float) 60));
                normalData[nmdItr] = x;
                nmdItr++;
                normalData[nmdItr] = y;
                nmdItr++;
                normalData[nmdItr] = z;
                nmdItr++;
                textureCoordData[tcdItr] = u;
                tcdItr++;
                textureCoordData[tcdItr] = v;
                tcdItr++;
                vertexPosData[vpdItr] = 10.0f * x;
                vpdItr++;
                vertexPosData[vpdItr] = 10.0f * y;
                vpdItr++;
                vertexPosData[vpdItr] = 10.0f * z;
                vpdItr++;
            }
        }
        for (latNum = 0; latNum < 60; latNum++) {
            for (longNum = 0; longNum < 60; longNum++) {
                int first = (latNum * 61) + longNum;
                int second = (first + 60) + 1;
                indexData[idxItr] = (short) first;
                idxItr++;
                indexData[idxItr] = (short) second;
                idxItr++;
                indexData[idxItr] = (short) (first + 1);
                idxItr++;
                indexData[idxItr] = (short) second;
                idxItr++;
                indexData[idxItr] = (short) (second + 1);
                idxItr++;
                indexData[idxItr] = (short) (first + 1);
                idxItr++;
            }
        }
        this.mMeshes = new Mesh[1];
        this.mMeshes[0] = new Mesh();
        this.mMeshes[0].setVertexes(vertexPosData);
        this.mMeshes[0].setNormals(normalData);
        this.mMeshes[0].setCoordinates(textureCoordData);
        this.mMeshes[0].setIndices(indexData);
    }

    protected void initMaterial() {
        Material material = new Material();
        this.mMaterials = new Material[1];
        material.MaterialName = "SphereMaterial";
        material.setAmbient(0.5882f, 0.5882f, 0.5882f, 1.0f);
        material.setDiffuse(0.5882f, 0.5882f, 0.5882f, 1.0f);
        material.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        material.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
        material.setAlpha(1.0f);
        material.setOpticalDensity(1.5f);
        material.setShininess(30.0f);
        material.setTransparent(0.0f);
        material.setTransmissionFilter(1.0f, 1.0f, 1.0f, 1.0f);
        material.setIllumination(2);
        this.mMaterials[0] = material;
    }

    public void setImage(Bitmap bitmap) {
        this.mMaterials[0].texture = GLResources.genTextrue(bitmap);
    }

    public void setImage(int resid) {
        this.mMaterials[0].texture = getContext().getTexture(resid);
    }

    private void setImage(Texture texture) {
        this.mMaterials[0].texture = texture;
    }
}
