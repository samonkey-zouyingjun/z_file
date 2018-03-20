package zidoo.tarot.kernel.shader;

import android.opengl.GLES20;
import java.util.HashMap;

public abstract class Shader {
    public static final int FRAGMENT_SHADER = 35632;
    public static final int VALID_SHADER = 0;
    public static final int VERTEX_SHADER = 35633;
    protected HashMap<String, ShaderParameter> mAttributeParameterList = null;
    protected boolean mIsCompiled = false;
    protected int mShaderID = 0;
    protected String mShaderLog = null;
    protected String mShaderSource = null;
    protected HashMap<String, ShaderParameter> mUniformParameterList = null;
    protected HashMap<String, ShaderParameter> mVaryingParameterList = null;

    public abstract boolean compileShader();

    @Deprecated
    public static Shader loadShader(String shaderFile) {
        return null;
    }

    public void setShaderSource(String source) {
        this.mShaderSource = source;
    }

    @Deprecated
    private void filterShaderParameters() {
        if (this.mShaderSource != null) {
            this.mShaderSource.isEmpty();
        }
    }

    @Deprecated
    public Shader autoExtractShaderParameters() {
        filterShaderParameters();
        return this;
    }

    public Shader manualExtractShaderParameters(int paramType, String paramName) {
        ShaderParameter parameter = new ShaderParameter();
        switch (paramType) {
            case 257:
                parameter.setParameterType(257);
                parameter.setParameterName(paramName);
                if (!this.mUniformParameterList.containsKey(paramName)) {
                    this.mUniformParameterList.put(paramName, parameter);
                    break;
                }
                break;
            case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                parameter.setParameterType(ShaderParameter.ATTRIBUTE_PARAMETER);
                parameter.setParameterName(paramName);
                if (!this.mAttributeParameterList.containsKey(paramName)) {
                    this.mAttributeParameterList.put(paramName, parameter);
                    break;
                }
                break;
            case ShaderParameter.VARYING_PARAMETER /*259*/:
                parameter.setParameterType(ShaderParameter.VARYING_PARAMETER);
                parameter.setParameterName(paramName);
                if (!this.mVaryingParameterList.containsKey(paramName)) {
                    this.mVaryingParameterList.put(paramName, parameter);
                    break;
                }
                break;
        }
        return this;
    }

    public HashMap<String, ShaderParameter> getUniformParameterList() {
        return this.mUniformParameterList;
    }

    public HashMap<String, ShaderParameter> getAttributeParameterList() {
        return this.mAttributeParameterList;
    }

    public HashMap<String, ShaderParameter> getVaryingParameterList() {
        return this.mVaryingParameterList;
    }

    public void destoryShader() {
        if (this.mShaderID != 0 && GLES20.glIsShader(this.mShaderID)) {
            GLES20.glDeleteShader(this.mShaderID);
            this.mShaderID = 0;
            this.mIsCompiled = false;
        }
    }

    public String getShaderLog() {
        return this.mShaderLog;
    }

    public int getShaderID() {
        return this.mShaderID;
    }

    public String getShaderSource() {
        return this.mShaderSource;
    }

    public boolean isCompiled() {
        return this.mIsCompiled;
    }
}
