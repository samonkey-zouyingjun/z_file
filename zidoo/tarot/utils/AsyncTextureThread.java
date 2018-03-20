package zidoo.tarot.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLSurface;
import zidoo.tarot.GLContext;
import zidoo.tarot.GLEnvirnment;
import zidoo.tarot.kernel.Texture;

public class AsyncTextureThread extends Thread {
    private GLContext context = null;
    GLEnvirnment envirnment = null;
    private int[] mBufferAttribs = new int[]{12375, 1, 12374, 1, 12417, 12380, 12416, 12380, 12344};
    private Texture[] textures = null;

    public AsyncTextureThread(GLContext context, Texture[] textures, GLEnvirnment envirnment) {
        this.textures = textures;
        this.context = context;
        this.envirnment = envirnment;
    }

    public void run() {
        this.envirnment.mEglContext = this.envirnment.mEgl.eglCreateContext(this.envirnment.mEglDisplay, this.envirnment.mEglConfig, this.envirnment.mEglContext, null);
        EGLSurface localSurface = this.envirnment.mEgl.eglCreatePbufferSurface(this.envirnment.mEglDisplay, this.envirnment.mEglConfig, this.mBufferAttribs);
        this.envirnment.mEgl.eglMakeCurrent(this.envirnment.mEglDisplay, localSurface, localSurface, this.envirnment.mEglContext);
        int count = this.textures.length;
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
            Texture texture = this.textures[i];
            Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(), texture.getResourceId());
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            texture.setWidth(bitmap.getWidth());
            texture.setHeight(bitmap.getHeight());
            texture.setTextureID(textureID[i]);
            this.context.getGLResource().saveTexture(texture);
            bitmap.recycle();
        }
    }
}
