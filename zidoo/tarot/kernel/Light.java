package zidoo.tarot.kernel;

import android.opengl.GLES11;
import javax.microedition.khronos.opengles.GL10;

public class Light extends Transform {
    protected Vector3 LookAt;
    protected Material Material;
    protected boolean mEnabled;
    protected boolean mSkyable;

    public Light() {
        this.mEnabled = false;
        this.mSkyable = false;
        this.LookAt = null;
        this.Material = null;
        this.LookAt = new Vector3();
        this.Material = new Material();
        this.Material.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
        this.Material.setDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
        this.Material.setAmbient(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setLightness(float lightness) {
        this.Material.setSpecular(lightness, lightness, lightness, 1.0f);
        this.Material.setDiffuse(lightness, lightness, lightness, 1.0f);
        this.Material.setAmbient(lightness, lightness, lightness, 1.0f);
    }

    public void update(GL10 gl) {
        if (this.mEnabled) {
            gl.glPushMatrix();
            gl.glEnable(16384);
            gl.glLightfv(16384, 4610, this.Material.getSpecular(), 0);
            gl.glLightfv(16384, 4609, this.Material.getDiffuse(), 0);
            gl.glLightfv(16384, 4608, this.Material.getAmbient(), 0);
            gl.glLightfv(16384, 4612, new float[]{this.LookAt.X, this.LookAt.Y, this.LookAt.Z}, 0);
            if (this.mSkyable) {
                gl.glLightfv(16384, 4611, new float[]{this.Position.X, this.Position.Y, this.Position.Z, 0.0f}, 0);
            } else {
                gl.glLightfv(16384, 4611, new float[]{this.Position.X, this.Position.Y, this.Position.Z, 1.0f}, 0);
            }
            gl.glPopMatrix();
            return;
        }
        gl.glDisable(16384);
    }

    public void setEnable(boolean enabled) {
        this.mEnabled = enabled;
        if (this.mEnabled) {
            GLES11.glEnable(2896);
        } else {
            GLES11.glDisable(2896);
        }
    }

    public boolean getEnable() {
        return this.mEnabled;
    }

    public void setSkyable(boolean skyable) {
        this.mSkyable = skyable;
    }

    public boolean getSkyable() {
        return this.mSkyable;
    }

    public void setPosition(Position position) {
        this.Position = position;
    }

    public void setLookAt(Vector3 lookAt) {
        this.LookAt = lookAt;
    }
}
