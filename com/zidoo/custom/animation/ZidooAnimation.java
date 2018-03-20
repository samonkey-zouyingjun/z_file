package com.zidoo.custom.animation;

import android.os.Handler;
import android.view.View;
import com.zidoo.custom.init.ZidooJarPermissions;

public class ZidooAnimation extends Handler {

    public interface ZidooAnimationListener {
        void animationOver();
    }

    public ZidooAnimation() {
        ZidooJarPermissions.checkZidooPermissions();
    }

    public void startAnimation(View targetView, ZidooScale scale, ZidooPosition position, ZidooAlpha alpha, float speed, ZidooAnimationListener zidooAnimationListener) {
        ZidooAnimationHolder holderScaleX = new ZidooAnimationHolder(scale.mFromScaleX, 10.0f);
        ZidooAnimationHolder holderScaleY = new ZidooAnimationHolder(scale.mFromScaleY, 10.0f);
        ZidooAnimationHolder holderPositonX = new ZidooAnimationHolder(position.mFromX, 10.0f);
        final ZidooAnimationHolder holderPositonY = new ZidooAnimationHolder(position.mFromY, 10.0f);
        final ZidooAnimationHolder holderAlpha = new ZidooAnimationHolder(alpha.mFromAlpha, 10.0f);
        holderScaleX.setDestinationIndex(scale.mToScaleX, 1, speed, true, true);
        holderScaleY.setDestinationIndex(scale.mToScaleY, 1, speed, true, true);
        holderPositonX.setDestinationIndex(position.mToX, 1, speed, true, true);
        holderPositonY.setDestinationIndex(position.mToY, 1, speed, true, true);
        holderAlpha.setDestinationIndex(alpha.mToAlpha, 1, speed, true, true);
        final ZidooAnimationHolder zidooAnimationHolder = holderScaleX;
        final View view = targetView;
        final ZidooAnimationHolder zidooAnimationHolder2 = holderScaleY;
        final ZidooAnimationHolder zidooAnimationHolder3 = holderPositonX;
        final ZidooAnimationListener zidooAnimationListener2 = zidooAnimationListener;
        post(new Runnable() {
            public void run() {
                zidooAnimationHolder.run(view);
                zidooAnimationHolder2.run(view);
                zidooAnimationHolder3.run(view);
                holderPositonY.run(view);
                holderAlpha.run(view);
                view.setScaleX(zidooAnimationHolder.getCurrentIndex());
                view.setScaleY(zidooAnimationHolder2.getCurrentIndex());
                view.setTranslationX(zidooAnimationHolder3.getCurrentIndex());
                view.setTranslationY(holderPositonY.getCurrentIndex());
                float alpha = holderAlpha.getCurrentIndex();
                if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                if (alpha > 1.0f) {
                    alpha = 1.0f;
                }
                view.setAlpha(alpha);
                if (!zidooAnimationHolder.isStatic() || !zidooAnimationHolder2.isStatic() || !zidooAnimationHolder3.isStatic() || !holderPositonY.isStatic() || !holderAlpha.isStatic()) {
                    ZidooAnimation.this.removeCallbacks(this);
                    ZidooAnimation.this.postDelayed(this, 15);
                } else if (zidooAnimationListener2 != null) {
                    zidooAnimationListener2.animationOver();
                }
            }
        });
    }
}
