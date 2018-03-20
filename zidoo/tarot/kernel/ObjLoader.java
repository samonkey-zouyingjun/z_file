package zidoo.tarot.kernel;

import android.graphics.BitmapFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import zidoo.tarot.GLContext;

public class ObjLoader {
    private static final String TAG = "ObjLoader";
    protected static final String TAG_ALPHA = "d ";
    protected static final String TAG_AMBIENT = "Ka ";
    protected static final String TAG_DIFFUSE = "Kd ";
    protected static final String TAG_EMISSIVE = "Ke ";
    protected static final String TAG_FACE = "f ";
    protected static final String TAG_GROUP = "g ";
    protected static final String TAG_ILLUMINATION = "illum ";
    protected static final String TAG_MAP_AMBIENT = "map_Ka ";
    protected static final String TAG_MAP_DIFFUSE = "map_Kd ";
    protected static final String TAG_MAP_SPECULAR = "map_Ks ";
    protected static final String TAG_MTLLIB = "mtllib ";
    protected static final String TAG_NEWMTL = "newmtl ";
    protected static final String TAG_NORMAL = "vn ";
    protected static final String TAG_OPTICAL_DENSITY = "Ni ";
    protected static final String TAG_SHARPNESS = "sharpness ";
    protected static final String TAG_SHININESS = "Ns ";
    protected static final String TAG_SPECULAR = "Ks ";
    protected static final String TAG_TEXTURE = "vt ";
    protected static final String TAG_TRANSMISSION_FILTER = "Tf ";
    protected static final String TAG_TRANSPARENT = "Tr ";
    protected static final String TAG_USEMTL = "usemtl ";
    protected static final String TAG_VERTEX = "v ";
    private int mFaceSize = 0;
    private GLContext mGlContext = null;
    private int mGroupSize = 0;
    private int mMaterialSize = 0;
    private List<MaterialPackage> mMaterials = null;
    private List<MeshPackage> mMeshes = null;
    private String mMtlFile = null;
    private int mNormalSize = 0;
    private String mObjFile = null;
    private GameObject mProduct = null;
    private BufferedReader mReader = null;
    private int mTextureSize = 0;
    private int mVertexSize = 0;

    class MaterialPackage {
        public Material mMaterial;
        public String mMaterialName;

        public MaterialPackage() {
            this.mMaterialName = null;
            this.mMaterial = null;
            this.mMaterial = new Material();
        }
    }

    class MeshPackage {
        public int mIndiceCursor = 0;
        public short[] mIndices = null;
        public int mNormalCursor = 0;
        public float[] mNormals = null;
        public int mTextureCursor = 0;
        public float[] mTextures = null;
        public int mVertexCursor = 0;
        public float[] mVertexes = null;

        MeshPackage() {
        }
    }

    public ObjLoader(GLContext glContext) {
        this.mGlContext = glContext;
    }

    public GameObject loadAsset(String assetFileName) throws IOException {
        this.mObjFile = assetFileName;
        this.mReader = new BufferedReader(new InputStreamReader(this.mGlContext.getAssets().open(assetFileName)));
        try {
            scanObjFile();
            scanMTLFile();
            this.mReader = new BufferedReader(new InputStreamReader(this.mGlContext.getAssets().open(assetFileName)));
            loadMesh();
            loadMaterial(true);
            assembleGameObject();
            recycle();
            return this.mProduct;
        } catch (IOException exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public GameObject loadFile(String fileName) throws FileNotFoundException {
        this.mObjFile = fileName;
        File file = new File(fileName);
        if (!file.isFile() || !file.exists()) {
            return null;
        }
        this.mReader = new BufferedReader(new FileReader(file));
        try {
            scanObjFile();
            scanMTLFile();
            this.mReader = new BufferedReader(new FileReader(file));
            loadMesh();
            loadMaterial(false);
            assembleGameObject();
            recycle();
            return this.mProduct;
        } catch (IOException exp) {
            exp.printStackTrace();
            return null;
        }
    }

    private void assembleGameObject() {
        int i;
        this.mProduct = new GameObject(this.mGlContext);
        int meshSize = this.mMeshes.size();
        this.mProduct.mMeshes = new Mesh[meshSize];
        for (i = 0; i < meshSize; i++) {
            MeshPackage meshPackage = (MeshPackage) this.mMeshes.get(i);
            this.mProduct.mMeshes[i] = new Mesh();
            if (meshPackage.mVertexes != null) {
                this.mProduct.mMeshes[i].setVertexes(meshPackage.mVertexes);
            }
            if (meshPackage.mNormals != null) {
                this.mProduct.mMeshes[i].setNormals(meshPackage.mNormals);
            }
            if (meshPackage.mTextures != null) {
                this.mProduct.mMeshes[i].setCoordinates(meshPackage.mTextures);
            }
            if (meshPackage.mIndices != null) {
                this.mProduct.mMeshes[i].setIndices(meshPackage.mIndices);
            }
        }
        if (this.mMaterials != null && this.mMaterials.size() > 0) {
            int materialSize = this.mMaterials.size();
            Material[] materials = new Material[materialSize];
            for (i = 0; i < materialSize; i++) {
                MaterialPackage materialPackage = (MaterialPackage) this.mMaterials.get(i);
                Material material = new Material();
                material = materialPackage.mMaterial;
                material.MaterialName = materialPackage.mMaterialName;
                materials[i] = material;
            }
        }
    }

    private void loadMesh() throws IOException {
        int cursor = -1;
        boolean isNewObj = false;
        ArrayList<Float> vertexData = new ArrayList();
        ArrayList<Float> normalData = new ArrayList();
        ArrayList<Float> textureData = new ArrayList();
        int scanedVertexSize = 0;
        int scanedNormalSize = 0;
        int scanedTextureSize = 0;
        int meshVertexSize = 0;
        int meshNormalSize = 0;
        int meshTextureSize = 0;
        MeshPackage mesh = null;
        if (this.mMeshes.size() != 0) {
            while (true) {
                String buffer = this.mReader.readLine();
                if (buffer != null) {
                    buffer = buffer.trim();
                    if (!(buffer.startsWith(TAG_MTLLIB) || buffer.startsWith(TAG_USEMTL))) {
                        int i;
                        int size;
                        if (buffer.startsWith(TAG_VERTEX)) {
                            if (!isNewObj) {
                                isNewObj = true;
                                cursor++;
                                vertexData.clear();
                                normalData.clear();
                                textureData.clear();
                                mesh = (MeshPackage) this.mMeshes.get(cursor);
                                scanedVertexSize += meshVertexSize;
                                scanedNormalSize += meshNormalSize;
                                scanedTextureSize += meshTextureSize;
                                meshVertexSize = 0;
                                meshNormalSize = 0;
                                meshTextureSize = 0;
                            }
                            meshVertexSize++;
                            for (float valueOf : getFloatVectorValue(buffer, TAG_VERTEX)) {
                                vertexData.add(Float.valueOf(valueOf));
                            }
                        } else if (buffer.startsWith(TAG_NORMAL)) {
                            meshNormalSize++;
                            for (float valueOf2 : getFloatVectorValue(buffer, TAG_NORMAL)) {
                                normalData.add(Float.valueOf(valueOf2));
                            }
                        } else if (buffer.startsWith(TAG_TEXTURE)) {
                            meshTextureSize++;
                            for (float valueOf22 : getFloatVectorValue(buffer, TAG_TEXTURE)) {
                                textureData.add(Float.valueOf(valueOf22));
                            }
                        } else if (buffer.startsWith(TAG_FACE)) {
                            short[] faceNode = getShortVectorValue(buffer, TAG_FACE);
                            isNewObj = false;
                            size = faceNode.length;
                            i = 0;
                            while (i < size) {
                                int pointIndex;
                                float[] fArr;
                                int i2;
                                if (i % 3 == 0 && faceNode[i] != (short) 0 && mesh.mVertexes.length > mesh.mVertexCursor) {
                                    pointIndex = ((faceNode[i] - 1) - scanedVertexSize) * 3;
                                    fArr = mesh.mVertexes;
                                    i2 = mesh.mVertexCursor;
                                    mesh.mVertexCursor = i2 + 1;
                                    fArr[i2] = ((Float) vertexData.get(pointIndex + 0)).floatValue();
                                    fArr = mesh.mVertexes;
                                    i2 = mesh.mVertexCursor;
                                    mesh.mVertexCursor = i2 + 1;
                                    fArr[i2] = ((Float) vertexData.get(pointIndex + 1)).floatValue();
                                    fArr = mesh.mVertexes;
                                    i2 = mesh.mVertexCursor;
                                    mesh.mVertexCursor = i2 + 1;
                                    fArr[i2] = ((Float) vertexData.get(pointIndex + 2)).floatValue();
                                    short[] sArr = mesh.mIndices;
                                    int i3 = mesh.mIndiceCursor;
                                    mesh.mIndiceCursor = i3 + 1;
                                    sArr[i3] = (short) (mesh.mIndiceCursor - 1);
                                }
                                if (i % 3 == 1 && faceNode[i] != (short) 0 && mesh.mTextures.length > mesh.mTextureCursor) {
                                    pointIndex = ((faceNode[i] - 1) - scanedTextureSize) * 3;
                                    fArr = mesh.mTextures;
                                    i2 = mesh.mTextureCursor;
                                    mesh.mTextureCursor = i2 + 1;
                                    fArr[i2] = ((Float) textureData.get(pointIndex + 0)).floatValue();
                                    fArr = mesh.mTextures;
                                    i2 = mesh.mTextureCursor;
                                    mesh.mTextureCursor = i2 + 1;
                                    fArr[i2] = ((Float) textureData.get(pointIndex + 1)).floatValue();
                                }
                                if (i % 3 == 2 && faceNode[i] != (short) 0 && mesh.mNormals.length > mesh.mNormalCursor) {
                                    pointIndex = ((faceNode[i] - 1) - scanedNormalSize) * 3;
                                    fArr = mesh.mNormals;
                                    i2 = mesh.mNormalCursor;
                                    mesh.mNormalCursor = i2 + 1;
                                    fArr[i2] = ((Float) normalData.get(pointIndex + 0)).floatValue();
                                    fArr = mesh.mNormals;
                                    i2 = mesh.mNormalCursor;
                                    mesh.mNormalCursor = i2 + 1;
                                    fArr[i2] = ((Float) normalData.get(pointIndex + 1)).floatValue();
                                    fArr = mesh.mNormals;
                                    i2 = mesh.mNormalCursor;
                                    mesh.mNormalCursor = i2 + 1;
                                    fArr[i2] = ((Float) normalData.get(pointIndex + 2)).floatValue();
                                }
                                i++;
                            }
                        } else {
                            buffer.startsWith(TAG_GROUP);
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void loadMaterial(boolean isAssetFile) throws IOException {
        BufferedReader reader;
        int cursor = -1;
        MaterialPackage material = null;
        if (isAssetFile) {
            reader = new BufferedReader(new InputStreamReader(this.mGlContext.getAssets().open(this.mMtlFile)));
        } else {
            reader = new BufferedReader(new FileReader(this.mMtlFile));
        }
        while (true) {
            String buffer = reader.readLine();
            if (buffer == null) {
                reader.close();
                return;
            }
            buffer = buffer.trim();
            if (buffer.startsWith(TAG_NEWMTL)) {
                cursor++;
                material = new MaterialPackage();
                material.mMaterialName = getStringValue(buffer, TAG_NEWMTL);
                if (this.mMaterials == null) {
                    this.mMaterials = new ArrayList();
                }
                this.mMaterials.add(material);
            } else if (buffer.startsWith(TAG_SHININESS)) {
                material.mMaterial.setShininess(getFloatValue(buffer, TAG_SHININESS));
            } else if (buffer.startsWith(TAG_OPTICAL_DENSITY)) {
                material.mMaterial.setOpticalDensity(getFloatValue(buffer, TAG_OPTICAL_DENSITY));
            } else if (buffer.startsWith(TAG_ALPHA)) {
                material.mMaterial.setAlpha(getFloatValue(buffer, TAG_ALPHA));
            } else if (buffer.startsWith(TAG_TRANSPARENT)) {
                material.mMaterial.setTransparent(getFloatValue(buffer, TAG_TRANSPARENT));
            } else if (buffer.startsWith(TAG_TRANSMISSION_FILTER)) {
                material.mMaterial.setTransmissionFilter(getFloatVectorValue(buffer, TAG_TRANSMISSION_FILTER));
            } else if (buffer.startsWith(TAG_ILLUMINATION)) {
                material.mMaterial.setIllumination((int) getFloatValue(buffer, TAG_ILLUMINATION));
            } else if (buffer.startsWith(TAG_SHARPNESS)) {
                material.mMaterial.setSharpness(getFloatValue(buffer, TAG_SHARPNESS));
            } else if (buffer.startsWith(TAG_AMBIENT)) {
                material.mMaterial.setAmbient(getFloatVectorValue(buffer, TAG_AMBIENT));
            } else if (buffer.startsWith(TAG_DIFFUSE)) {
                material.mMaterial.setDiffuse(getFloatVectorValue(buffer, TAG_DIFFUSE));
            } else if (buffer.startsWith(TAG_SPECULAR)) {
                material.mMaterial.setSpecular(getFloatVectorValue(buffer, TAG_SPECULAR));
            } else if (buffer.startsWith(TAG_EMISSIVE)) {
                material.mMaterial.setEmission(getFloatVectorValue(buffer, TAG_EMISSIVE));
            } else if (!buffer.startsWith(TAG_MAP_AMBIENT)) {
                if (buffer.startsWith(TAG_MAP_DIFFUSE)) {
                    Texture texture;
                    String textureFileName = new StringBuilder(String.valueOf(this.mObjFile.substring(0, this.mObjFile.lastIndexOf("/")))).append("/").append(getStringValue(buffer, TAG_MAP_DIFFUSE)).toString();
                    if (isAssetFile) {
                        texture = GLResources.genTextrue(BitmapFactory.decodeStream(this.mGlContext.getAssets().open(textureFileName)));
                    } else {
                        texture = GLResources.genTextrue(BitmapFactory.decodeFile(textureFileName));
                    }
                    material.mMaterial.texture = texture;
                } else {
                    buffer.startsWith(TAG_MAP_SPECULAR);
                }
            }
        }
    }

    private void scanObjFile() throws IOException {
        MeshPackage meshPackage;
        int meshVertexSize = 0;
        int meshNormalSize = 0;
        int meshTextureSize = 0;
        int meshIndiceSize = 0;
        int meshFaceSize = 0;
        boolean mIsFaceEnd = true;
        this.mMeshes = new ArrayList();
        while (true) {
            String buffer = this.mReader.readLine();
            if (buffer == null) {
                break;
            } else if (buffer.startsWith(TAG_MTLLIB)) {
                this.mMtlFile = getStringValue(buffer, TAG_MTLLIB);
                this.mMtlFile = new StringBuilder(String.valueOf(this.mObjFile.substring(0, this.mObjFile.lastIndexOf("/")))).append("/").append(this.mMtlFile).toString();
            } else if (buffer.startsWith(TAG_USEMTL)) {
                this.mMaterialSize++;
            } else if (buffer.startsWith(TAG_VERTEX)) {
                if (!mIsFaceEnd) {
                    mIsFaceEnd = true;
                    meshPackage = new MeshPackage();
                    if (meshVertexSize > 0) {
                        meshPackage.mVertexes = new float[(meshFaceSize * 9)];
                    }
                    if (meshNormalSize > 0) {
                        meshPackage.mNormals = new float[(meshFaceSize * 9)];
                    }
                    if (meshTextureSize > 0) {
                        meshPackage.mTextures = new float[(meshFaceSize * 6)];
                    }
                    meshPackage.mIndices = new short[(meshFaceSize * 3)];
                    this.mMeshes.add(meshPackage);
                    meshVertexSize = 0;
                    meshNormalSize = 0;
                    meshTextureSize = 0;
                    meshFaceSize = 0;
                }
                this.mVertexSize++;
                meshVertexSize++;
            } else if (buffer.startsWith(TAG_NORMAL)) {
                this.mNormalSize++;
                meshNormalSize++;
            } else if (buffer.startsWith(TAG_TEXTURE)) {
                this.mTextureSize++;
                meshTextureSize++;
            } else if (buffer.startsWith(TAG_FACE)) {
                this.mFaceSize++;
                meshIndiceSize += 3;
                meshFaceSize++;
                mIsFaceEnd = false;
            } else if (buffer.startsWith(TAG_GROUP)) {
                this.mGroupSize++;
            }
        }
        meshPackage = new MeshPackage();
        if (meshVertexSize > 0) {
            meshPackage.mVertexes = new float[(meshFaceSize * 9)];
        }
        if (meshNormalSize > 0) {
            meshPackage.mNormals = new float[(meshFaceSize * 9)];
        }
        if (meshTextureSize > 0) {
            meshPackage.mTextures = new float[(meshFaceSize * 6)];
        }
        meshPackage.mIndices = new short[(meshFaceSize * 3)];
        this.mMeshes.add(meshPackage);
        this.mReader.close();
    }

    private void scanMTLFile() {
    }

    private void recycle() {
        if (this.mReader != null) {
            try {
                this.mReader.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            } finally {
                this.mReader = null;
            }
        }
        if (this.mMeshes != null) {
            this.mMeshes.clear();
            this.mMeshes = null;
        }
        if (this.mMaterials != null) {
            this.mMaterials.clear();
            this.mMaterials = null;
        }
    }

    private String getStringValue(String buffer, String TAG) {
        return buffer.trim().substring(TAG.length()).trim();
    }

    private float getFloatValue(String buffer, String TAG) {
        return Float.valueOf(buffer.trim().substring(TAG.length()).trim()).floatValue();
    }

    private float[] getFloatVectorValue(String buffer, String TAG) {
        String[] splitedString = buffer.trim().substring(TAG.length()).trim().split(" ");
        float[] values = new float[splitedString.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Float.valueOf(splitedString[i]).floatValue();
        }
        return values;
    }

    private short[] getShortVectorValue(String buffer, String TAG) {
        String[] splitedString = buffer.trim().substring(TAG.length()).trim().split(" ");
        short[] values = new short[(splitedString.length * 3)];
        int size = splitedString.length;
        for (int i = 0; i < size; i++) {
            String[] subSplitedString = splitedString[i].trim().split("/");
            for (int j = 0; j < subSplitedString.length; j++) {
                try {
                    values[(i * 3) + j] = Short.valueOf(subSplitedString[j]).shortValue();
                } catch (NumberFormatException e) {
                    values[(i * 3) + j] = (short) 0;
                }
            }
        }
        return values;
    }

    private float[] getFloatArray(List<Float> list) {
        int size = list.size();
        float[] values = new float[size];
        for (int i = 0; i < size; i++) {
            values[i] = ((Float) list.get(i)).floatValue();
        }
        return values;
    }
}
