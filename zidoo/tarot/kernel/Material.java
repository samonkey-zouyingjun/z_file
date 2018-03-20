package zidoo.tarot.kernel;

public class Material {
    public static final String TAG = "Material";
    public String MaterialName;
    private float[] mAmbient;
    private float[] mDiffuse;
    private float[] mEmission;
    private int mIllumination;
    private float[] mMainColor;
    private float mOpticalDensity;
    private float mSharpness;
    private float mShininess;
    private float[] mSpecular;
    private float[] mTransmissionFilter;
    private float mTransparent;
    public Texture texture;

    public Material() {
        this.MaterialName = null;
        this.texture = null;
        this.mIllumination = 2;
        this.mMainColor = null;
        this.mSpecular = null;
        this.mDiffuse = null;
        this.mAmbient = null;
        this.mEmission = null;
        this.mTransmissionFilter = null;
        this.mShininess = 0.0f;
        this.mOpticalDensity = 1.0f;
        this.mTransparent = 1.0f;
        this.mSharpness = 60.0f;
        this.mMainColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.mSpecular = new float[4];
        this.mDiffuse = new float[4];
        this.mAmbient = new float[4];
        this.mEmission = new float[4];
        this.mTransmissionFilter = new float[4];
    }

    public void setAmbient(float r, float g, float b, float a) {
        this.mAmbient[0] = r;
        this.mAmbient[1] = g;
        this.mAmbient[2] = b;
        this.mAmbient[3] = a;
    }

    public void setAmbient(float[] ambient) {
        int size = ambient.length;
        for (int i = 0; i < size; i++) {
            this.mAmbient[i] = ambient[i];
        }
        if (this.mAmbient.length < 4) {
            this.mAmbient[3] = this.mMainColor[3];
        }
    }

    public float[] getAmbient() {
        return this.mAmbient;
    }

    public void setSpecular(float r, float g, float b, float a) {
        this.mSpecular[0] = r;
        this.mSpecular[1] = g;
        this.mSpecular[2] = b;
        this.mSpecular[3] = a;
    }

    public void setSpecular(float[] specular) {
        int size = specular.length;
        for (int i = 0; i < size; i++) {
            this.mSpecular[i] = specular[i];
        }
        if (this.mSpecular.length < 4) {
            this.mSpecular[3] = this.mMainColor[3];
        }
    }

    public float[] getSpecular() {
        return this.mSpecular;
    }

    public void setDiffuse(float r, float g, float b, float a) {
        this.mDiffuse[0] = r;
        this.mDiffuse[1] = g;
        this.mDiffuse[2] = b;
        this.mDiffuse[3] = a;
    }

    public void setDiffuse(float[] diffuse) {
        int size = diffuse.length;
        for (int i = 0; i < size; i++) {
            this.mDiffuse[i] = diffuse[i];
        }
        if (this.mDiffuse.length < 4) {
            this.mDiffuse[3] = this.mMainColor[3];
        }
    }

    public float[] getDiffuse() {
        return this.mDiffuse;
    }

    public void setEmission(float r, float g, float b, float a) {
        this.mEmission[0] = r;
        this.mEmission[1] = g;
        this.mEmission[2] = b;
        this.mEmission[3] = a;
    }

    public void setEmission(float[] emission) {
        int size = emission.length;
        for (int i = 0; i < size; i++) {
            this.mEmission[i] = emission[i];
        }
        if (this.mEmission.length < 4) {
            this.mEmission[3] = this.mMainColor[3];
        }
    }

    public float[] getEmission() {
        return this.mEmission;
    }

    public void setShininess(float shininess) {
        this.mShininess = shininess;
    }

    public float getShininess() {
        return this.mShininess;
    }

    public void setOpticalDensity(float opticalDensity) {
        this.mOpticalDensity = opticalDensity;
    }

    public float getOpticalDensity() {
        return this.mOpticalDensity;
    }

    public void setTransparent(float transparent) {
        this.mTransparent = transparent;
    }

    public float getTransparent() {
        return this.mTransparent;
    }

    public void setAlpha(float alpha) {
        this.mMainColor[3] = alpha;
        this.mAmbient[3] = this.mMainColor[3];
        this.mDiffuse[3] = this.mMainColor[3];
        this.mSpecular[3] = this.mMainColor[3];
        this.mEmission[3] = this.mMainColor[3];
    }

    public float getAlpha() {
        return this.mMainColor[3];
    }

    public float[] getMainColor() {
        return this.mMainColor;
    }

    public void setMainColor(float r, float g, float b, float a) {
        this.mMainColor[0] = r;
        this.mMainColor[1] = g;
        this.mMainColor[2] = b;
        this.mMainColor[3] = a;
    }

    public void setMainColor(float[] mainColor) {
        this.mMainColor = mainColor;
    }

    public void setTransmissionFilter(float r, float g, float b, float a) {
        this.mTransmissionFilter[0] = r;
        this.mTransmissionFilter[1] = g;
        this.mTransmissionFilter[2] = b;
        this.mTransmissionFilter[3] = a;
    }

    public void setTransmissionFilter(float[] transmissionFilter) {
        this.mTransmissionFilter = transmissionFilter;
    }

    public float[] getTransmissionFilter() {
        return this.mTransmissionFilter;
    }

    public void setSharpness(float sharpness) {
        this.mSharpness = sharpness;
    }

    public float getSharpness() {
        return this.mSharpness;
    }

    public void setIllumination(int illumination) {
        this.mIllumination = illumination;
    }

    public int getIllumination() {
        return this.mIllumination;
    }

    public void recycle() {
        if (this.texture != null) {
            this.texture.recycle();
        }
    }
}
