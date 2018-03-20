package com.zidoo.fileexplorer.view;

import com.zidoo.fileexplorer.R;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.Vector3;
import zidoo.tarot.kernel.anim.GlAlphaAnimation;
import zidoo.tarot.kernel.anim.GlAnimation;
import zidoo.tarot.kernel.anim.GlAnimation.AnimatorListener;
import zidoo.tarot.kernel.anim.GlSetAnimation;
import zidoo.tarot.kernel.anim.GlTranslateAnimation;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.WrapMarqueeTextView;

public class RemindsLayout extends Layout {
    Layout[] layouts;
    int mType = 0;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public RemindsLayout(GLContext glContext, boolean hideMenu) {
        super(glContext);
        initView(glContext, hideMenu);
    }

    private void initView(GLContext glContext, boolean hideMenu) {
        this.layouts = new Layout[2];
        this.layouts[0] = new Layout(glContext);
        this.layouts[1] = new Layout(glContext);
        WrapMarqueeTextView tvReminds = new WrapMarqueeTextView(glContext);
        tvReminds.setMaxWidth(380.0f);
        tvReminds.setHeight(25.0f);
        tvReminds.setTextSize(24.0f);
        tvReminds.setTextColor(-3355444);
        tvReminds.setText(glContext.getString(hideMenu ? R.string.cancel_select_reminds : R.string.open_menu_reminds));
        tvReminds.setX((tvReminds.getWidth() / 2.0f) - 165.0f);
        this.layouts[0].addGameObject(tvReminds);
        TImageView imgReminds = new TImageView(glContext);
        imgReminds.setWidth(50.0f);
        imgReminds.setHeight(50.0f);
        imgReminds.setX(-200.0f);
        imgReminds.setImageResource(R.drawable.img_remind_menu);
        this.layouts[0].addGameObject(imgReminds);
        WrapMarqueeTextView tvUnmount = new WrapMarqueeTextView(glContext);
        tvUnmount.setMaxWidth(380.0f);
        tvUnmount.setHeight(25.0f);
        tvUnmount.setTextSize(24.0f);
        tvUnmount.setTextColor(-3355444);
        tvUnmount.setText(glContext.getString(R.string.unmount_reminds));
        tvUnmount.setX((tvUnmount.getWidth() / 2.0f) - 165.0f);
        this.layouts[1].addGameObject(tvUnmount);
        TImageView imgUnmount = new TImageView(glContext);
        imgUnmount.setWidth(50.0f);
        imgUnmount.setHeight(50.0f);
        imgUnmount.setX(-200.0f);
        imgUnmount.setImageResource(R.drawable.img_unmount_reminds);
        this.layouts[1].addGameObject(imgUnmount);
        addGameObject(this.layouts[0]);
        addGameObject(this.layouts[1]);
        this.layouts[1].setVisibility(false);
    }

    public void setReminds(int type) {
        if (this.mType != type) {
            if (type == -1) {
                goneReminds(this.mType);
            } else if (this.mType == -1) {
                showRemids(type);
            } else {
                changeReminds(this.mType, type);
            }
            this.mType = type;
        }
    }

    private void goneReminds(final int type) {
        this.layouts[type].endAnimation();
        GlAlphaAnimation goneAnimation = new GlAlphaAnimation(1.0f, 0.0f);
        goneAnimation.setDuration(500);
        goneAnimation.setFillAfter(true);
        goneAnimation.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                RemindsLayout.this.layouts[type].setVisibility(false);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        this.layouts[type].startAnimation(goneAnimation);
    }

    private void showRemids(int type) {
        this.layouts[type].endAnimation();
        this.layouts[type].setVisibility(true);
        GlAlphaAnimation showAnimation = new GlAlphaAnimation(0.0f, 1.0f);
        showAnimation.setDuration(500);
        showAnimation.setFillAfter(true);
        this.layouts[type].startAnimation(showAnimation);
    }

    private void changeReminds(final int current, int target) {
        this.layouts[current].endAnimation();
        this.layouts[target].endAnimation();
        GlTranslateAnimation t1 = new GlTranslateAnimation(new Vector3(), new Vector3(0.0f, 60.0f * getDisplay().sHeightRatio, 0.0f));
        GlAlphaAnimation a1 = new GlAlphaAnimation(1.0f, 0.0f);
        GlSetAnimation outAnimation = new GlSetAnimation();
        outAnimation.addAnimation(t1);
        outAnimation.addAnimation(a1);
        outAnimation.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                RemindsLayout.this.layouts[current].setVisibility(false);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        GlTranslateAnimation t2 = new GlTranslateAnimation(new Vector3(0.0f, -60.0f * getDisplay().sHeightRatio, 0.0f), new Vector3());
        GlAlphaAnimation a2 = new GlAlphaAnimation(0.0f, 1.0f);
        GlSetAnimation inAnimation = new GlSetAnimation();
        inAnimation.setFillAfter(true);
        inAnimation.addAnimation(t2);
        inAnimation.addAnimation(a2);
        this.layouts[current].startAnimation(outAnimation);
        this.layouts[target].setVisibility(true);
        this.layouts[target].startAnimation(inAnimation);
    }

    public int getType() {
        return this.mType;
    }
}
