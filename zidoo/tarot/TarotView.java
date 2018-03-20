package zidoo.tarot;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

@SuppressLint({"NewApi", "Recycle"})
public class TarotView extends GLSurfaceView {
    public static final int HANDLE_READY_TO_RENDER = 2;
    public static final int HANDLE_REQUEST_RENDER_DELAY = 1;
    public static final int OPENGLES_1_1 = 1;
    public static final int OPENGLES_2_0 = 2;
    public static final int OPENGLES_3_0 = 3;
    public static final String TAG = "TarotView";
    private GLContext glContext;
    private boolean mBackgroundTransparentEnable = false;
    private int mFSAASamples = 4;
    private TarotRenderer mRenderer = null;
    private TarotCallback mTarotCallback = null;

    public interface TarotCallback {
        void notifyHandleMessage(Message message);

        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    public TarotView(Context context) {
        super(context);
        initialize(context);
    }

    public TarotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        this.glContext = new GLContext(context, this);
        initParameters();
    }

    public GLContext getGlContext() {
        return this.glContext;
    }

    public void setRenderer(Renderer renderer) {
        if (renderer instanceof TarotRenderer) {
            this.glContext.getConfig().getEngineInfo().setFSAASamples(this.mFSAASamples);
            this.mRenderer = (TarotRenderer) renderer;
        }
        super.setRenderer(renderer);
    }

    public void setTarotCallback(TarotCallback callback) {
        this.mTarotCallback = callback;
    }

    public void notifyHandleMessage(Message msg) {
        if (this.mTarotCallback != null) {
            this.mTarotCallback.notifyHandleMessage(msg);
        }
    }

    private void initParameters() {
        setEGLConfigChooser(new EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                EGLConfig[] eglConfigList = new EGLConfig[1];
                int[] eglAttributesList = new int[]{12339, 4, 12321, 8, 12324, 8, 12323, 8, 12322, 8, 12325, 16, 12326, 8, 12338, 1, 12337, TarotView.this.mFSAASamples, 12344};
                egl.eglChooseConfig(display, eglAttributesList, eglConfigList, 1, new int[1]);
                return eglConfigList[0];
            }
        });
        setEGLContextFactory(new EGLContextFactory() {
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                egl.eglDestroyContext(display, context);
            }

            public EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
                EGLContext eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
                GLEnvirnment glEnvirnment = new GLEnvirnment();
                glEnvirnment.mEgl = egl;
                glEnvirnment.mEglDisplay = eglDisplay;
                glEnvirnment.mEglConfig = eglConfig;
                glEnvirnment.mEglContext = eglContext;
                TarotView.this.glContext.getConfig().setGlEnvirnment(glEnvirnment);
                return eglContext;
            }
        });
        setEGLContextClientVersion(1);
    }

    public void setFSAA(final int samples) {
        this.mFSAASamples = samples;
        setEGLConfigChooser(new EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                EGLConfig[] eglConfigList = new EGLConfig[1];
                int[] eglAttributesList = new int[]{12339, 4, 12324, 8, 12323, 8, 12322, 8, 12325, 16, 12326, 8, 12338, 1, 12337, samples, 12344};
                egl.eglChooseConfig(display, eglAttributesList, eglConfigList, 1, new int[1]);
                return eglConfigList[0];
            }
        });
    }

    public void setOpenGLESAPI(int apiVersion) {
        ConfigurationInfo configurationInfo = ((ActivityManager) getContext().getSystemService("activity")).getDeviceConfigurationInfo();
        if (apiVersion == 3) {
            if (configurationInfo.reqGlEsVersion >= 196608) {
                setEGLContextClientVersion(3);
            } else {
                setOpenGLESAPI(2);
            }
        } else if (apiVersion != 2) {
            setEGLContextClientVersion(1);
        } else if (configurationInfo.reqGlEsVersion >= 131072) {
            setEGLContextClientVersion(2);
        } else {
            setOpenGLESAPI(1);
        }
    }

    public void setBackgroundTransparentEnable(boolean enabled) {
        this.mBackgroundTransparentEnable = enabled;
        queueEvent(new Runnable() {
            public void run() {
                if (TarotView.this.mBackgroundTransparentEnable) {
                    TarotView.this.getHolder().setFormat(-2);
                    TarotView.this.setZOrderOnTop(true);
                    return;
                }
                TarotView.this.getHolder().setFormat(-1);
                TarotView.this.setZOrderOnTop(false);
            }
        });
    }

    public boolean getBackgroundTransparentEnable() {
        return this.mBackgroundTransparentEnable;
    }

    public void postRender(Runnable run) {
        this.mRenderer.postRender(run);
    }

    public void queueEvent(final Runnable runnable, long delayMilliSecond) {
        postDelayed(new Runnable() {
            public void run() {
                TarotView.this.queueEvent(runnable);
            }
        }, delayMilliSecond);
    }

    public boolean dispatchKeyEvent(final KeyEvent event) {
        if (this.mRenderer == null) {
            return super.dispatchKeyEvent(event);
        }
        queueEvent(new Runnable() {
            public void run() {
                if (!TarotView.this.mRenderer.dispatchKeyEvent(event) && TarotView.this.mTarotCallback != null) {
                    TarotView.this.mTarotCallback.onDispatchKeyEvent(event);
                }
            }
        });
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.mRenderer == null) {
            return super.dispatchTouchEvent(event);
        }
        final MotionEvent motionEvent = MotionEvent.obtain(event);
        queueEvent(new Runnable() {
            public void run() {
                TarotView.this.mRenderer.dispatchTouchEvent(motionEvent);
                motionEvent.recycle();
            }
        });
        return true;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (this.mRenderer == null) {
            return super.dispatchTouchEvent(event);
        }
        final MotionEvent motionEvent = MotionEvent.obtain(event);
        queueEvent(new Runnable() {
            public void run() {
                TarotView.this.mRenderer.dispatchGenericMotionEvent(motionEvent);
                motionEvent.recycle();
            }
        });
        return true;
    }
}
