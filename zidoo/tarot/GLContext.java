package zidoo.tarot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import zidoo.tarot.kernel.GLResources;
import zidoo.tarot.kernel.Texture;

@SuppressLint({"UseSparseArrays"})
public class GLContext extends ContextWrapper {
    private Config config = new Config();
    private GLResources glResource = new GLResources();
    private TarotView tarotView;

    public GLContext(Context base, TarotView tarotView) {
        super(base);
        this.tarotView = tarotView;
    }

    public TarotView getGLView() {
        return this.tarotView;
    }

    public Config getConfig() {
        return this.config;
    }

    public void requestRender() {
        this.tarotView.requestRender();
    }

    public void postRender(Runnable run) {
        this.tarotView.postRender(run);
    }

    public void requestRender(long delay) {
        this.tarotView.queueEvent(new Runnable() {
            public void run() {
                GLContext.this.tarotView.requestRender();
            }
        }, delay);
    }

    public void queueEvent(Runnable runnable) {
        this.tarotView.queueEvent(runnable);
    }

    public Texture getTexture(int resid) {
        return this.glResource.getResource(this, resid);
    }

    public GLResources getGLResource() {
        return this.glResource;
    }
}
