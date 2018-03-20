package com.zidoo.custom.onclick;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.View;
import com.zidoo.custom.voice.PlayVoiceTool;

public class ZidooOnclick {

    class AnonymousClass1 implements AnimatorListener {
        private final /* synthetic */ OnClickFinishListener val$onClickFinish;

        AnonymousClass1(OnClickFinishListener onClickFinishListener) {
            this.val$onClickFinish = onClickFinishListener;
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            if (this.val$onClickFinish != null) {
                this.val$onClickFinish.onClickFinish();
            }
        }

        public void onAnimationCancel(Animator animation) {
        }
    }

    public interface OnClickFinishListener {
        void onClickFinish();
    }

    public static void startOnclick(Context context, View view, String playVoice, OnClickFinishListener onClickFinish) {
        PlayVoiceTool.playVoiceUri(context, playVoice);
        if (view != null) {
            ZidooOnclickAnimation.performAnimate(view, new AnonymousClass1(onClickFinish));
        }
    }
}
