package zidoo.tarot.kernel.shader;

public class ShaderParameter {
    public static final int ATTRIBUTE_PARAMETER = 258;
    public static final String TAG_ATTRIBUTE = "attribute";
    public static final String TAG_BOOL = "bool";
    public static final String TAG_BVEC2 = "bvec2";
    public static final String TAG_BVEC3 = "bvec3";
    public static final String TAG_BVEC4 = "bvec4";
    public static final String TAG_FLOAT = "float";
    public static final String TAG_INT = "int";
    public static final String TAG_ISAMPLER1D = "isampler1D";
    public static final String TAG_ISAMPLER1DARRAY = "isampler1DArray";
    public static final String TAG_ISAMPLER2D = "isampler2D";
    public static final String TAG_ISAMPLER2DARRAY = "isampler2DArray";
    public static final String TAG_ISAMPLER2DRECT = "isampler2DRect";
    public static final String TAG_ISAMPLER3D = "isampler3D";
    public static final String TAG_ISAMPLERBUFFER = "isamplerBuffer";
    public static final String TAG_ISAMPLERCUBE = "isamplerCube";
    public static final String TAG_IVEC2 = "ivec2";
    public static final String TAG_IVEC3 = "ivec3";
    public static final String TAG_IVEC4 = "ivce4";
    public static final String TAG_MAIN = "main";
    public static final String TAG_MAT2 = "mat2";
    public static final String TAG_MAT2X2 = "mat2x2";
    public static final String TAG_MAT2X3 = "mat2x3";
    public static final String TAG_MAT2X4 = "mat2x4";
    public static final String TAG_MAT3 = "mat3";
    public static final String TAG_MAT3X2 = "mat3x2";
    public static final String TAG_MAT3X3 = "mat3x3";
    public static final String TAG_MAT3X4 = "mat3x4";
    public static final String TAG_MAT4 = "mat4";
    public static final String TAG_MAT4X2 = "mat4x2";
    public static final String TAG_MAT4X3 = "mat4x3";
    public static final String TAG_MAT4X4 = "mat4x4";
    public static final String TAG_SAMPLER1D = "sampler1D";
    public static final String TAG_SAMPLER1DARRAY = "sampler1DArray";
    public static final String TAG_SAMPLER1DArrayShadow = "sampler1DArrayShadow";
    public static final String TAG_SAMPLER1DShadow = "sampler1DShadow";
    public static final String TAG_SAMPLER2D = "sampler2D";
    public static final String TAG_SAMPLER2DARRAY = "sampler2DArray";
    public static final String TAG_SAMPLER2DArrayShadow = "sampler2DArrayShadow";
    public static final String TAG_SAMPLER2DRECT = "sampler2DRect";
    public static final String TAG_SAMPLER2DRectShadow = "sampler2DRectShadow";
    public static final String TAG_SAMPLER2DShadow = "sampler2DShadow";
    public static final String TAG_SAMPLER3D = "sampler3D";
    public static final String TAG_SAMPLERBUFFER = "samplerBuffer";
    public static final String TAG_SAMPLERCUBE = "samplerCube";
    public static final String TAG_SAMPLERCubeShadow = "samplerCubeShadow";
    public static final String TAG_UINT = "uint";
    public static final String TAG_UNIFORM = "uniform";
    public static final String TAG_USAMPLER1D = "usampler1D";
    public static final String TAG_USAMPLER1DARRAY = "usampler1DArray";
    public static final String TAG_USAMPLER2D = "usampler2D";
    public static final String TAG_USAMPLER2DARRAY = "usampler2DArray";
    public static final String TAG_USAMPLER2DRECT = "usampler2DRect";
    public static final String TAG_USAMPLER3D = "usampler3D";
    public static final String TAG_USAMPLERBUFFER = "usamplerBuffer";
    public static final String TAG_USAMPLERCUBE = "usamplerCube";
    public static final String TAG_UVEC2 = "uvec2";
    public static final String TAG_UVEC3 = "uvec3";
    public static final String TAG_UVEC4 = "uvec4";
    public static final String TAG_VARYING = "varying";
    public static final String TAG_VEC2 = "vec2";
    public static final String TAG_VEC3 = "vec3";
    public static final String TAG_VEC4 = "vec4";
    public static final int UNIFORM_PARAMETER = 257;
    public static final int UNSPECIFIED_PARAMETER = 256;
    public static final int VALID_PARAMETER = 0;
    public static final int VARYING_PARAMETER = 259;
    private int mParameterID = 0;
    private String mParameterName = null;
    private int mParameterType = 256;

    public void setParameterType(int paramType) {
        this.mParameterType = paramType;
    }

    public int getParameterType() {
        return this.mParameterType;
    }

    public void setParameterName(String paramName) {
        this.mParameterName = paramName;
    }

    public String getParameterName() {
        return this.mParameterName;
    }

    public void setParameterID(int paramID) {
        this.mParameterID = paramID;
    }

    public int getParameterID() {
        return this.mParameterID;
    }

    protected void release() {
        this.mParameterID = 0;
    }

    public boolean isValid() {
        return this.mParameterID != 0;
    }
}
