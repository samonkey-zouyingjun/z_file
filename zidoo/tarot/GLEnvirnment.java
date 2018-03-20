package zidoo.tarot;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class GLEnvirnment {
    public EGL10 mEgl = null;
    public EGLConfig mEglConfig = null;
    public EGLContext mEglContext = null;
    public EGLDisplay mEglDisplay = null;
}
