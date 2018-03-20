package zidoo.tarot.kernel.shader;

import android.opengl.GLES20;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class ShaderProgram {
    public static final int VALID_SHADER_PROGRAM = 0;
    private LinkedList<FragmentShader> mFragmentShaders;
    private boolean mIsCompiled;
    private HashMap<String, ShaderParameter> mParametersList;
    private int mShaderProgramID;
    private String mShaderProgramLog;
    private LinkedList<VertexShader> mVertexShaders;

    public class ShaderParameterCountInvalidException extends Exception {
        private static final long serialVersionUID = 3213995928030066764L;

        public ShaderParameterCountInvalidException(String msg) {
            super(msg);
        }
    }

    public ShaderProgram() {
        this.mVertexShaders = null;
        this.mFragmentShaders = null;
        this.mParametersList = null;
        this.mShaderProgramID = 0;
        this.mShaderProgramLog = null;
        this.mIsCompiled = false;
        this.mVertexShaders = new LinkedList();
        this.mFragmentShaders = new LinkedList();
        this.mParametersList = new HashMap();
    }

    @Deprecated
    public static ShaderProgram loadShaderProgram(String shaderProgramFile) {
        return null;
    }

    public boolean addVertexShader(VertexShader vertexShader) {
        if (vertexShader == null) {
            return false;
        }
        boolean installResult = vertexShader.compileShader();
        this.mVertexShaders.add(vertexShader);
        return installResult;
    }

    public boolean addFragmentShader(FragmentShader fragmentShader) {
        if (fragmentShader == null) {
            return false;
        }
        boolean installResult = fragmentShader.compileShader();
        this.mFragmentShaders.add(fragmentShader);
        return installResult;
    }

    private void extractShaderParameters(HashMap<String, ShaderParameter> paramList) {
        if (paramList != null) {
            for (Entry<String, ShaderParameter> entry : paramList.entrySet()) {
                String key = (String) entry.getKey();
                ShaderParameter value = (ShaderParameter) entry.getValue();
                if (value != null) {
                    switch (value.getParameterType()) {
                        case 257:
                            value.setParameterID(GLES20.glGetUniformLocation(this.mShaderProgramID, key));
                            break;
                        case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                            value.setParameterID(GLES20.glGetAttribLocation(this.mShaderProgramID, key));
                            break;
                        case ShaderParameter.VARYING_PARAMETER /*259*/:
                            value.setParameterID(0);
                            break;
                        default:
                            break;
                    }
                }
                this.mParametersList.put(key, value);
            }
        }
    }

    private int getShaderParameterID(String paramName) {
        ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
        if (parameter == null || !parameter.isValid()) {
            return 0;
        }
        return parameter.getParameterID();
    }

    public boolean compileShaderProgram() {
        int[] linkStatus = new int[1];
        boolean compileResult = false;
        this.mShaderProgramID = GLES20.glCreateProgram();
        if (this.mShaderProgramID != 0) {
            int i;
            int vertexShadersSize = this.mVertexShaders.size();
            for (i = 0; i < vertexShadersSize; i++) {
                GLES20.glAttachShader(this.mShaderProgramID, ((VertexShader) this.mVertexShaders.get(i)).getShaderID());
            }
            int fragmentShadersSize = this.mFragmentShaders.size();
            for (i = 0; i < fragmentShadersSize; i++) {
                GLES20.glAttachShader(this.mShaderProgramID, ((FragmentShader) this.mFragmentShaders.get(i)).getShaderID());
            }
            GLES20.glLinkProgram(this.mShaderProgramID);
            GLES20.glGetProgramiv(this.mShaderProgramID, 35714, linkStatus, 0);
            this.mShaderProgramLog = GLES20.glGetProgramInfoLog(this.mShaderProgramID);
            if (linkStatus[0] != 0) {
                compileResult = true;
                vertexShadersSize = this.mVertexShaders.size();
                for (i = 0; i < vertexShadersSize; i++) {
                    extractShaderParameters(((VertexShader) this.mVertexShaders.get(i)).getUniformParameterList());
                    extractShaderParameters(((VertexShader) this.mVertexShaders.get(i)).getAttributeParameterList());
                    extractShaderParameters(((VertexShader) this.mVertexShaders.get(i)).getVaryingParameterList());
                }
                fragmentShadersSize = this.mFragmentShaders.size();
                for (i = 0; i < fragmentShadersSize; i++) {
                    extractShaderParameters(((FragmentShader) this.mFragmentShaders.get(i)).getUniformParameterList());
                    extractShaderParameters(((FragmentShader) this.mFragmentShaders.get(i)).getAttributeParameterList());
                    extractShaderParameters(((FragmentShader) this.mFragmentShaders.get(i)).getVaryingParameterList());
                }
            } else {
                compileResult = false;
                GLES20.glDeleteProgram(this.mShaderProgramID);
                this.mShaderProgramID = 0;
            }
        }
        this.mIsCompiled = compileResult;
        return compileResult;
    }

    public boolean validateShaderProgram() {
        int[] validateStatus = new int[1];
        if (this.mShaderProgramID != 0 && GLES20.glIsProgram(this.mShaderProgramID) && this.mIsCompiled) {
            GLES20.glValidateProgram(this.mShaderProgramID);
            GLES20.glGetProgramiv(this.mShaderProgramID, 35715, validateStatus, 0);
        }
        if (validateStatus[0] == 1) {
            return true;
        }
        return false;
    }

    public void destoryShaderProgram() {
        if (this.mShaderProgramID != 0 && GLES20.glIsProgram(this.mShaderProgramID)) {
            GLES20.glDeleteProgram(this.mShaderProgramID);
            this.mShaderProgramID = 0;
            this.mIsCompiled = false;
        }
    }

    public void run() {
        if (this.mIsCompiled) {
            GLES20.glUseProgram(this.mShaderProgramID);
        }
    }

    public boolean isCompiled() {
        return this.mIsCompiled;
    }

    public void setShaderParameters(String paramName, int value) {
        int parameterID = 0;
        int parameterType = 256;
        ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
        if (parameter != null) {
            parameterID = parameter.getParameterID();
            parameterType = parameter.getParameterType();
        }
        if (parameterID != 0) {
            switch (parameterType) {
                case 257:
                    GLES20.glUniform1i(parameterID, value);
                    return;
                case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                    GLES20.glVertexAttrib1f(parameterID, (float) value);
                    return;
                default:
                    return;
            }
        }
    }

    public void setShaderParameters(String paramName, float value) {
        int parameterID = 0;
        int parameterType = 256;
        ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
        if (parameter != null) {
            parameterID = parameter.getParameterID();
            parameterType = parameter.getParameterType();
        }
        if (parameterID != 0) {
            switch (parameterType) {
                case 257:
                    GLES20.glUniform1f(parameterID, value);
                    return;
                case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                    GLES20.glVertexAttrib1f(parameterID, value);
                    return;
                default:
                    return;
            }
        }
    }

    public void setShaderParameters(String paramName, int[] value) throws ShaderParameterCountInvalidException {
        if (value != null && value.length > 0) {
            int parameterID = 0;
            int parameterType = 256;
            ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
            if (parameter != null) {
                parameterID = parameter.getParameterID();
                parameterType = parameter.getParameterType();
            }
            switch (parameterType) {
                case 257:
                    if (value.length == 1) {
                        GLES20.glUniform1i(parameterID, value[0]);
                        return;
                    } else if (value.length == 2) {
                        GLES20.glUniform2i(parameterID, value[0], value[1]);
                        return;
                    } else if (value.length == 3) {
                        GLES20.glUniform3i(parameterID, value[0], value[2], value[3]);
                        return;
                    } else if (value.length == 4) {
                        GLES20.glUniform4i(parameterID, value[0], value[2], value[3], value[4]);
                        return;
                    } else {
                        throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be bwteen 1 and 4");
                    }
                case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                    if (value.length == 1) {
                        GLES20.glVertexAttrib1f(parameterID, (float) value[0]);
                        return;
                    } else if (value.length == 2) {
                        GLES20.glVertexAttrib2f(parameterID, (float) value[0], (float) value[1]);
                        return;
                    } else if (value.length == 3) {
                        GLES20.glVertexAttrib3f(parameterID, (float) value[0], (float) value[2], (float) value[3]);
                        return;
                    } else if (value.length == 4) {
                        GLES20.glVertexAttrib4f(parameterID, (float) value[0], (float) value[2], (float) value[3], (float) value[4]);
                        return;
                    } else {
                        throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be bwteen 1 and 4");
                    }
                default:
                    return;
            }
        }
    }

    public void setShaderParameters(String paramName, float[] value) throws ShaderParameterCountInvalidException {
        if (value != null && value.length > 0) {
            int parameterID = 0;
            int parameterType = 256;
            ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
            if (parameter != null) {
                parameterID = parameter.getParameterID();
                parameterType = parameter.getParameterType();
            }
            switch (parameterType) {
                case 257:
                    if (value.length == 1) {
                        GLES20.glUniform1f(parameterID, value[0]);
                        return;
                    } else if (value.length == 2) {
                        GLES20.glUniform2f(parameterID, value[0], value[1]);
                        return;
                    } else if (value.length == 3) {
                        GLES20.glUniform3f(parameterID, value[0], value[2], value[3]);
                        return;
                    } else if (value.length == 4) {
                        GLES20.glUniform4f(parameterID, value[0], value[2], value[3], value[4]);
                        return;
                    } else {
                        throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be bwteen 1 and 4");
                    }
                case ShaderParameter.ATTRIBUTE_PARAMETER /*258*/:
                    if (value.length == 1) {
                        GLES20.glVertexAttrib1f(parameterID, value[0]);
                        return;
                    } else if (value.length == 2) {
                        GLES20.glVertexAttrib2f(parameterID, value[0], value[1]);
                        return;
                    } else if (value.length == 3) {
                        GLES20.glVertexAttrib3f(parameterID, value[0], value[2], value[3]);
                        return;
                    } else if (value.length == 4) {
                        GLES20.glVertexAttrib4f(parameterID, value[0], value[2], value[3], value[4]);
                        return;
                    } else {
                        throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be bwteen 1 and 4");
                    }
                default:
                    return;
            }
        }
    }

    public void setShaderMatrixParameters(String paramName, float[] value) throws ShaderParameterCountInvalidException {
        if (value != null && value.length > 0) {
            int parameterID = 0;
            int parameterType = 256;
            ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
            if (parameter != null) {
                parameterID = parameter.getParameterID();
                parameterType = parameter.getParameterType();
            }
            if (parameterID != 0) {
                switch (parameterType) {
                    case 257:
                        if (value.length == 4) {
                            GLES20.glUniformMatrix2fv(parameterID, 4, false, value, 0);
                            return;
                        } else if (value.length == 9) {
                            GLES20.glUniformMatrix3fv(parameterID, 9, false, value, 0);
                            return;
                        } else if (value.length == 16) {
                            GLES20.glUniformMatrix4fv(parameterID, 16, false, value, 0);
                            return;
                        } else {
                            throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be 4(2x2) or 9(3x3) or 16(4x4)");
                        }
                    default:
                        return;
                }
            }
        }
    }

    public void setShaderTransposeMatrixParameters(String paramName, float[] value) throws ShaderParameterCountInvalidException {
        if (value != null && value.length > 0) {
            int parameterID = 0;
            int parameterType = 256;
            ShaderParameter parameter = (ShaderParameter) this.mParametersList.get(paramName);
            if (parameter != null) {
                parameterID = parameter.getParameterID();
                parameterType = parameter.getParameterType();
            }
            if (parameterID != 0) {
                switch (parameterType) {
                    case 257:
                        if (value.length == 4) {
                            GLES20.glUniformMatrix2fv(parameterID, 4, true, value, 0);
                            return;
                        } else if (value.length == 9) {
                            GLES20.glUniformMatrix3fv(parameterID, 9, true, value, 0);
                            return;
                        } else if (value.length == 16) {
                            GLES20.glUniformMatrix4fv(parameterID, 16, true, value, 0);
                            return;
                        } else {
                            throw new ShaderParameterCountInvalidException("Length of value is " + value.length + ", length should be 4(2x2) or 9(3x3) or 16(4x4)");
                        }
                    default:
                        return;
                }
            }
        }
    }
}
