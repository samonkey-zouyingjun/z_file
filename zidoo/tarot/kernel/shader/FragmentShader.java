package zidoo.tarot.kernel.shader;

import android.opengl.GLES20;

public class FragmentShader extends Shader {
    public static final String TAG = "FragmentShader";
    protected static final int mShaderType = 35632;

    public boolean compileShader() {
        boolean compileResult;
        int[] compileStatus = new int[1];
        this.mShaderID = GLES20.glCreateShader(35632);
        if (this.mShaderID == 0 || this.mShaderSource == null || this.mShaderSource.isEmpty()) {
            compileResult = false;
        } else {
            GLES20.glShaderSource(this.mShaderID, this.mShaderSource);
            GLES20.glCompileShader(this.mShaderID);
            GLES20.glGetShaderiv(this.mShaderID, 35713, compileStatus, 0);
            this.mShaderLog = GLES20.glGetShaderInfoLog(this.mShaderID);
            if (compileStatus[0] != 0) {
                compileResult = true;
            } else {
                compileResult = false;
                GLES20.glDeleteShader(this.mShaderID);
                this.mShaderID = 0;
            }
        }
        this.mIsCompiled = compileResult;
        return compileResult;
    }
}
