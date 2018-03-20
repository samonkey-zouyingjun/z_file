package com.zidoo.custom.animation;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"NewApi"})
public class ZAnimation {
    private List<PropertyValuesHolder> mHolders = new ArrayList();

    public ZAnimation rotationX(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("rotationX", values));
        return this;
    }

    public ZAnimation rotationY(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("rotationY", values));
        return this;
    }

    public ZAnimation rotationZ(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("rotation", values));
        return this;
    }

    public ZAnimation translationX(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("translationX", values));
        return this;
    }

    public ZAnimation translationY(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("translationY", values));
        return this;
    }

    public ZAnimation scaleX(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("scaleX", values));
        return this;
    }

    public ZAnimation scale(float... values) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", values);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", values);
        this.mHolders.add(pvhX);
        this.mHolders.add(pvhY);
        return this;
    }

    public ZAnimation scaleY(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("scaleY", values));
        return this;
    }

    public ZAnimation alpha(float... values) {
        this.mHolders.add(PropertyValuesHolder.ofFloat("alpha", values));
        return this;
    }

    public void perform(View targetView, AnimatorListener l, long duration) {
        PropertyValuesHolder[] mPropertyValuesHolders = new PropertyValuesHolder[this.mHolders.size()];
        for (int i = 0; i < this.mHolders.size(); i++) {
            mPropertyValuesHolders[i] = (PropertyValuesHolder) this.mHolders.get(i);
        }
        this.mHolders.clear();
        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(targetView, mPropertyValuesHolders);
        if (l != null) {
            oa.addListener(l);
        }
        oa.setDuration(duration);
        oa.start();
    }
}
