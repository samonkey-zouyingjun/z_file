package zidoo.tarot;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import zidoo.tarot.kernel.Camera;
import zidoo.tarot.kernel.Position;
import zidoo.tarot.kernel.Vector3;

@SuppressLint({"NewApi"})
public class TarotRenderer implements Renderer {
    public Camera Camera = null;
    protected GLContext mGLContext;
    private Queue<Runnable> mRunnables = new LinkedBlockingDeque();
    private TarotScene mScenes = null;

    public TarotRenderer(GLContext glContext, TarotScene scene) {
        this.mGLContext = glContext;
        this.mScenes = scene;
    }

    @Deprecated
    public void startScene(TarotScene current, Class<? extends TarotScene> cls) {
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGLSystem(gl);
        this.Camera = new Camera(gl);
        this.Camera.setPosition(new Position(0.0f, 0.0f, 5.0f));
        this.Camera.setLookAt(new Vector3(0.0f, 0.0f, 0.0f));
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.Camera.setup(width, height);
        this.mGLContext.getConfig().getDisplay().init(width, height);
        this.mGLContext.getConfig().getEngineInfo().setScreenOrientation(width >= height);
    }

    public void onDrawFrame(GL10 gl) {
        resetFrame(gl);
        this.Camera.update();
        drawFrame(gl);
        while (true) {
            Runnable run = (Runnable) this.mRunnables.poll();
            if (run != null) {
                run.run();
            } else {
                return;
            }
        }
    }

    public void drawFrame(GL10 gl) {
        this.mScenes.update(gl);
    }

    private void resetFrame(GL10 gl) {
        gl.glColorMask(true, true, true, true);
        gl.glClear(17664);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
    }

    private void initGLSystem(GL10 gl) {
        gl.glEnable(3024);
        gl.glHint(3152, 4353);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glClearStencil(0);
        gl.glShadeModel(7425);
        gl.glEnable(2929);
        gl.glDepthFunc(515);
        gl.glDepthRangef(0.0f, 1.0f);
        gl.glDepthMask(true);
        gl.glEnable(2960);
        gl.glStencilMask(255);
        gl.glEnable(3042);
        gl.glBlendFunc(770, 771);
        gl.glEnable(3008);
        gl.glAlphaFunc(516, 0.01f);
        gl.glEnable(2903);
        gl.glEnable(2977);
        gl.glEnable(32826);
        gl.glEnable(2832);
        gl.glHint(3153, 4354);
        gl.glEnable(2848);
        gl.glHint(3154, 4354);
        gl.glEnable(32823);
        gl.glHint(3155, 4354);
        gl.glEnable(32925);
        gl.glSampleCoverage(32926.0f, true);
        gl.glPointSize(1.5f);
        gl.glLineWidth(1.5f);
    }

    public void invalidate() {
        this.mGLContext.requestRender();
    }

    public void postRender(Runnable run) {
        this.mRunnables.add(run);
    }

    public void onResume() {
    }

    public void onPause() {
    }

    @Deprecated
    public boolean addScene(TarotScene scene) {
        return false;
    }

    @Deprecated
    public boolean removeScene(TarotScene scene) {
        return false;
    }

    @Deprecated
    public TarotScene removeScene(int index) {
        return null;
    }

    @Deprecated
    public TarotScene getScene(int index) {
        return null;
    }

    @Deprecated
    public int indexOfScene(TarotScene scene) {
        return -1;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mScenes.dispatchKeyEvent(event);
    }

    public void dispatchTouchEvent(MotionEvent event) {
        this.mScenes.dispatchTouchEvent(event);
    }

    public void dispatchGenericMotionEvent(MotionEvent event) {
        this.mScenes.dispatchGenericMotionEvent(event);
    }
}
