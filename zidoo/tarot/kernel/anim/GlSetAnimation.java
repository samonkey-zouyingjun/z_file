package zidoo.tarot.kernel.anim;

import java.util.ArrayList;
import zidoo.tarot.kernel.GameObject;

public class GlSetAnimation extends GlAnimation {
    ArrayList<GlAnimation> animations = new ArrayList();

    public void addAnimation(GlAnimation animation) {
        this.animations.add(animation);
    }

    public void removeAnimation(GlAnimation animation) {
        this.animations.remove(animation);
    }

    protected void setupStartValues() {
        for (int i = 0; i < this.animations.size(); i++) {
            ((GlAnimation) this.animations.get(i)).setupStartValues();
        }
    }

    protected void setupEndValues() {
        for (int i = 0; i < this.animations.size(); i++) {
            ((GlAnimation) this.animations.get(i)).setupEndValues();
        }
    }

    public void computeAnimation() {
        long ct = System.currentTimeMillis();
        if (ct > this.mStartTime) {
            if (!this.mIsRunning) {
                if (this.mAnimatiorListener != null) {
                    this.mAnimatiorListener.onAnimationStart(this);
                }
                this.mIsRunning = true;
            }
            if (ct < this.mStartTime + this.mDuration) {
                for (int i = 0; i < this.animations.size(); i++) {
                    ((GlAnimation) this.animations.get(i)).computeAnimation();
                }
                return;
            }
            end();
        }
    }

    public void start() {
        super.start();
        for (int i = 0; i < this.animations.size(); i++) {
            ((GlAnimation) this.animations.get(i)).start();
        }
    }

    public void setTarget(GameObject target) {
        super.setTarget(target);
        for (int i = 0; i < this.animations.size(); i++) {
            ((GlAnimation) this.animations.get(i)).setTarget(target);
        }
    }
}
