package zidoo.tarot.kernel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.util.SparseArray;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import zidoo.tarot.GLContext;

public class GLResources {
    private SparseArray<Texture> textures = new SparseArray();

    public static Texture genTextrue(Bitmap bitmap) {
        Texture texture = new Texture();
        int[] textureID = new int[1];
        GLES11.glEnable(3553);
        GLES11.glActiveTexture(33984);
        GLES11.glGenTextures(1, textureID, 0);
        GLES11.glBindTexture(3553, textureID[0]);
        GLES11.glTexParameterf(3553, 10241, 9729.0f);
        GLES11.glTexParameterf(3553, 10240, 9729.0f);
        GLES11.glTexParameterf(3553, 10242, 33071.0f);
        GLES11.glTexParameterf(3553, 10243, 33071.0f);
        GLES11.glTexEnvf(8960, 8704, 8448.0f);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        texture.setWidth(bitmap.getWidth());
        texture.setHeight(bitmap.getHeight());
        texture.setTextureID(textureID[0]);
        bitmap.recycle();
        return texture;
    }

    public static Texture genTTextrue(Bitmap bitmap, int width, int height) {
        Texture texture = new Texture();
        int[] textureID = new int[1];
        GLES11.glEnable(3553);
        GLES11.glActiveTexture(33984);
        GLES11.glGenTextures(1, textureID, 0);
        GLES11.glBindTexture(3553, textureID[0]);
        GLES11.glTexParameterf(3553, 10241, 9729.0f);
        GLES11.glTexParameterf(3553, 10240, 9729.0f);
        GLES11.glTexParameterf(3553, 10242, 33071.0f);
        GLES11.glTexParameterf(3553, 10243, 33071.0f);
        GLES11.glTexEnvf(8960, 8704, 8448.0f);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        bitmap.recycle();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setTextureID(textureID[0]);
        return texture;
    }

    public Texture genResource(Context context, int resid) {
        Texture texture = new Texture();
        int[] textureID = new int[1];
        GLES11.glEnable(3553);
        GLES11.glActiveTexture(33984);
        GLES11.glGenTextures(1, textureID, 0);
        GLES11.glBindTexture(3553, textureID[0]);
        GLES11.glTexParameterf(3553, 10241, 9729.0f);
        GLES11.glTexParameterf(3553, 10240, 9729.0f);
        GLES11.glTexParameterf(3553, 10242, 33071.0f);
        GLES11.glTexParameterf(3553, 10243, 33071.0f);
        GLES11.glTexEnvf(8960, 8704, 8448.0f);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resid);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        texture.setWidth(bitmap.getWidth());
        texture.setHeight(bitmap.getHeight());
        texture.setTextureID(textureID[0]);
        texture.setResouceId(resid);
        this.textures.put(resid, texture);
        bitmap.recycle();
        return texture;
    }

    public Texture getResource(Context context, int resid) {
        Texture res = (Texture) this.textures.get(resid);
        return res != null ? res : genResource(context, resid);
    }

    @Deprecated
    public static void genResources(Context context, ArrayList<Texture> textures) {
        int count = textures.size();
        int[] textureID = new int[count];
        GLES11.glEnable(3553);
        GLES11.glActiveTexture(33984);
        GLES11.glGenTextures(count, textureID, 0);
        for (int i = 0; i < count; i++) {
            GLES11.glBindTexture(3553, textureID[i]);
            GLES11.glTexParameterf(3553, 10241, 9729.0f);
            GLES11.glTexParameterf(3553, 10240, 9729.0f);
            GLES11.glTexParameterf(3553, 10242, 33071.0f);
            GLES11.glTexParameterf(3553, 10243, 33071.0f);
            GLES11.glTexEnvf(8960, 8704, 8448.0f);
            Texture texture = (Texture) textures.get(i);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), texture.getResourceId());
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            texture.setWidth(bitmap.getWidth());
            texture.setHeight(bitmap.getHeight());
            texture.setTextureID(textureID[i]);
            bitmap.recycle();
        }
    }

    public static FloatBuffer floatBuffer(float[] src) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(src.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(src);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static IntBuffer intBuffer(int[] src) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(src.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(src);
        intBuffer.position(0);
        return intBuffer;
    }

    public static ShortBuffer shortBuffer(short[] src) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(src.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(src);
        shortBuffer.position(0);
        return shortBuffer;
    }

    public static ByteBuffer byteBuffer(byte[] src) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(src.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(src);
        byteBuffer.position(0);
        return byteBuffer;
    }

    public static GameObject loadObjModel(String assetFile, GLContext glContext) {
        try {
            return new ObjLoader(glContext).loadAsset(assetFile);
        } catch (IOException exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public void saveTexture(Texture texture) {
        this.textures.put(texture.getResourceId(), texture);
    }
}
