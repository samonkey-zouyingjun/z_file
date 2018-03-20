package zidoo.tarot.kernel;

import javax.microedition.khronos.opengles.GL10;

public class PointLight extends Light {
    private float mConstantAttenuation = 1.0f;
    private float mLinearAttenuation = 0.1f;
    private float mQuadraticAttenuation = 0.01f;

    public void update(GL10 gl) {
        if (this.mEnabled) {
            gl.glPushMatrix();
            gl.glEnable(16384);
            gl.glLightfv(16384, 4610, this.Material.getSpecular(), 0);
            gl.glLightfv(16384, 4609, this.Material.getDiffuse(), 0);
            gl.glLightfv(16384, 4608, this.Material.getAmbient(), 0);
            gl.glLightfv(16384, 4612, new float[]{this.LookAt.X, this.LookAt.Y, this.LookAt.Z}, 0);
            gl.glLightfv(16384, 4611, new float[]{this.Position.X, this.Position.Y, this.Position.Z, 1.0f}, 0);
            gl.glLightf(16384, 4615, this.mConstantAttenuation);
            gl.glLightf(16384, 4616, this.mLinearAttenuation);
            gl.glLightf(16384, 4617, this.mQuadraticAttenuation);
            gl.glPopMatrix();
            return;
        }
        gl.glDisable(16384);
    }

    public float getConstantAttenuation() {
        return this.mConstantAttenuation;
    }

    public void setConstantAttenuation(float constantAttenuation) {
        this.mConstantAttenuation = constantAttenuation;
    }

    public float getLinearAttenuation() {
        return this.mLinearAttenuation;
    }

    public void setLinearAttenuation(float linearAttenuation) {
        this.mLinearAttenuation = linearAttenuation;
    }

    public float getQuadraticAttenuation() {
        return this.mQuadraticAttenuation;
    }

    public void setQuadraticAttenuation(float quadraticAttenuation) {
        this.mQuadraticAttenuation = quadraticAttenuation;
    }
}
