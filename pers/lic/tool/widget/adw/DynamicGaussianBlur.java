package pers.lic.tool.widget.adw;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class DynamicGaussianBlur {
    private final int[] A;
    private final int[] B;
    private final int[] G;
    private final int[] R;
    private boolean complete = false;
    private final int h;
    private final int[] outPixels;
    private final int[] pixels;
    private final int w;

    public DynamicGaussianBlur(Bitmap bitmap) {
        this.w = bitmap.getWidth();
        this.h = bitmap.getHeight();
        int length = this.w * this.h;
        this.pixels = new int[length];
        this.outPixels = new int[length];
        this.A = new int[length];
        this.R = new int[length];
        this.G = new int[length];
        this.B = new int[length];
        bitmap.getPixels(this.pixels, 0, this.w, 0, 0, this.w, this.h);
        System.arraycopy(this.pixels, 0, this.outPixels, 0, length);
    }

    public void upperLeftToLowerRight() {
        fullArgb();
        int x;
        int y;
        int i;
        int j;
        if (this.w > this.h) {
            for (x = 0; x < this.h; x++) {
                for (y = 0; y <= x; y++) {
                    blur(x - y, y);
                }
            }
            for (x = this.h; x < this.w; x++) {
                for (y = 0; y < this.h; y++) {
                    blur(x - y, y);
                }
            }
            for (i = 1; i < this.h; i++) {
                y = i;
                j = 1;
                while (y < this.h) {
                    blur(this.w - j, y);
                    y++;
                    j++;
                }
            }
        } else {
            for (y = 0; y < this.w; y++) {
                for (x = 0; x <= y; x++) {
                    blur(x, y - x);
                }
            }
            for (y = this.w; y < this.h; y++) {
                for (x = 0; x < this.w; x++) {
                    blur(x, y - x);
                }
            }
            for (i = 1; i < this.w; i++) {
                x = i;
                j = 1;
                while (x < this.w) {
                    blur(x, this.h - j);
                    x++;
                    j++;
                }
            }
        }
        this.complete = true;
    }

    public void random() {
        int count = this.w * this.h;
        for (int r : new int[]{7, 5, 3, 2, 1}) {
            for (int i = r; i < count; i += r) {
                blur(i % this.w, i / this.w);
            }
        }
        blur(0, 0);
    }

    public void center() {
        int count = this.pixels.length;
        for (int i = 0; i < count; i++) {
            blur(i % this.w, i / this.w);
        }
    }

    public boolean isComplete() {
        return this.complete;
    }

    private void fullArgb() {
        for (int i = 0; i < this.pixels.length; i++) {
            int color = this.pixels[i];
            this.A[i] = Color.alpha(color);
            this.R[i] = Color.red(color);
            this.G[i] = Color.green(color);
            this.B[i] = Color.blue(color);
        }
    }

    private void blur(int x, int y) {
        this.outPixels[(this.w * y) + x] = com(new int[]{getPixel(x - 1, y - 1, 0.0947416f), getPixel(x, y - 1, 0.118318f), getPixel(x + 1, y - 1, 0.0947416f), getPixel(x - 1, y, 0.118318f), getPixel(x, y, 0.147761f), getPixel(x + 1, y, 0.118318f), getPixel(x - 1, y + 1, 0.0947416f), getPixel(x, y + 1, 0.118318f), getPixel(x + 1, y + 1, 0.0947416f)});
    }

    private int com(int[] colors) {
        int color = 0;
        for (int c : colors) {
            color = Color.argb(Color.alpha(color) + Color.alpha(c), Color.red(color) + Color.red(c), Color.green(color) + Color.green(c), Color.blue(color) + Color.blue(c));
        }
        return color;
    }

    private int getPixel(int x, int y, float weight) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return 0;
        }
        int color = this.pixels[(this.w * y) + x];
        return Color.argb((int) (((float) Color.alpha(color)) * weight), (int) (((float) Color.red(color)) * weight), (int) (((float) Color.green(color)) * weight), (int) (((float) Color.blue(color)) * weight));
    }

    public Bitmap out() {
        return Bitmap.createBitmap(this.outPixels, 0, this.w, this.w, this.h, Config.ARGB_8888);
    }
}
