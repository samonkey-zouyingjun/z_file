package com.zidoo.fileexplorer.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import com.zidoo.fileexplorer.R;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GLResources;

public class NumberDeviceView extends StateImageView {
    int number = -1;

    public NumberDeviceView(GLContext glContext) {
        super(glContext);
    }

    public void setNumber(int num) {
        if (this.number != num) {
            this.number = num;
            this.mNeedGenTexture = true;
            invalidate();
        }
    }

    protected void onGenarateTexture() {
        this.mMaterials[0].recycle();
        this.mMaterials[1].recycle();
        this.mMaterials[0].texture = GLResources.genTextrue(drawIcon(this.number));
        this.mMaterials[1].texture = GLResources.genTextrue(drawHighlightIcon(this.number));
        this.mNeedGenTexture = false;
    }

    private Bitmap drawIcon(int number) {
        int w = (int) getWidth();
        int h = (int) getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgd = getContext().getResources().getDrawable(R.drawable.ic_device_usb_small);
        bgd.setBounds(0, 0, w, h);
        bgd.draw(canvas);
        if (number > 1) {
            int x;
            int y;
            float size;
            Paint delete = new Paint();
            delete.setAntiAlias(true);
            delete.setColor(0);
            delete.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
            canvas.drawCircle((float) (w - 10), (float) (h - 10), 10.0f, delete);
            Drawable ring = getContext().getResources().getDrawable(R.drawable.sp_ring);
            ring.setBounds(w - 20, h - 20, w, h);
            ring.draw(canvas);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.parseColor("#959595"));
            p.setFakeBoldText(true);
            if (number < 10) {
                x = w - 14;
                y = h - 4;
                size = 16.0f;
            } else {
                x = w - 18;
                y = w - 5;
                size = 12.0f;
            }
            p.setTextSize(size);
            canvas.drawText(String.valueOf(number), (float) x, (float) y, p);
        }
        return bitmap;
    }

    private Bitmap drawHighlightIcon(int number) {
        int w = (int) getWidth();
        int h = (int) getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgd = getContext().getResources().getDrawable(R.drawable.ic_device_usb_small_s);
        bgd.setBounds(0, 0, w, h);
        bgd.draw(canvas);
        if (number > 1) {
            int x;
            int y;
            float size;
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(0);
            p.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
            canvas.drawCircle((float) (w - 12), (float) (h - 12), 10.0f, p);
            p.setColor(-1);
            p.setXfermode(null);
            canvas.drawCircle((float) (w - 10), (float) (h - 10), 10.0f, p);
            p.setColor(0);
            p.setFakeBoldText(true);
            p.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
            if (number < 10) {
                x = w - 14;
                y = h - 4;
                size = 16.0f;
            } else {
                x = w - 18;
                y = w - 5;
                size = 12.0f;
            }
            p.setTextSize(size);
            canvas.drawText(String.valueOf(number), (float) x, (float) y, p);
        }
        return bitmap;
    }
}
