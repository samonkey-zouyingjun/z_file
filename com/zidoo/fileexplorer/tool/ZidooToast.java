package com.zidoo.fileexplorer.tool;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.TextView;
import android.widget.Toast;

public class ZidooToast {

    public static class Build {
        int bgdColor = -16777216;
        Context context;
        int horizontalPadding = 16;
        String msg;
        int msgColor = -1;
        int round = 6;
        int textSize = 28;
        int verticalPadding = 12;

        public Build(Context context) {
            this.context = context;
        }

        public Build setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        public Build setMessage(int resId) {
            return setMessage(this.context.getString(resId));
        }

        public Build setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Build setRound(int round) {
            this.round = round;
            return this;
        }

        public Build setBgdColor(int bgdColor) {
            this.bgdColor = bgdColor;
            return this;
        }

        public Build setMsgColor(int msgColor) {
            this.msgColor = msgColor;
            return this;
        }

        public Build setHorizontalPadding(int horizontalPadding) {
            this.horizontalPadding = horizontalPadding;
            return this;
        }

        public Build setVerticalPadding(int verticalPadding) {
            this.verticalPadding = verticalPadding;
            return this;
        }

        public void show() {
            ZidooToast.toast(this.context, this.msg, this.round, this.textSize, this.bgdColor, this.msgColor, this.horizontalPadding, this.verticalPadding);
        }
    }

    public static void toast(Context context, String msg) {
        toast(context, msg, 6, 28, Color.parseColor("#dd000000"), -1, 16, 12);
    }

    public static Build build(Context context) {
        return new Build(context);
    }

    private static void toast(Context context, String msg, int round, int textSize, int bgdColor, int msgColor, int horizontalPadding, int verticalPadding) {
        float density = context.getResources().getDisplayMetrics().density;
        ShapeDrawable sd = new ShapeDrawable();
        float r = ((float) round) * density;
        sd.setShape(new RoundRectShape(new float[]{r, r, r, r, r, r, r, r}, null, null));
        sd.getPaint().setColor(bgdColor);
        TextView text = new TextView(context);
        text.setTextSize(((float) textSize) * context.getResources().getConfiguration().fontScale);
        text.setGravity(17);
        text.setTextColor(msgColor);
        int hs = (int) (((float) horizontalPadding) * density);
        int vs = (int) (((float) verticalPadding) * density);
        text.setPadding(hs, vs, hs, vs);
        text.setBackground(sd);
        text.setTypeface(Typeface.create("sans-serif-light", 0));
        text.setText(msg);
        Toast toast = new Toast(context);
        toast.setView(text);
        toast.setDuration(0);
        toast.show();
    }
}
