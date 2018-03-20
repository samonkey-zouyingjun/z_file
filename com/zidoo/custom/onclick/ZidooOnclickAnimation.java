package com.zidoo.custom.onclick;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class ZidooOnclickAnimation {
    private static final int CLICK_ANIMATION_TIME = 200;

    private ZidooOnclickAnimation() {
    }

    public static void performAnimate(View view, AnimatorListener l) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f, 0.8f, 1.0f});
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f, 0.8f, 1.0f});
        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{pvhX, pvhY});
        if (l != null) {
            oa.addListener(l);
        }
        oa.setDuration(200).start();
    }
}
