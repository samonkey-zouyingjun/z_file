package pers.lic.tool.load;

import android.graphics.Bitmap;
import android.widget.ImageView;

public abstract class BatchImageLoader<Key, Param> extends BatchLoader<Key, ImageView, Param, Bitmap> {
    private final BitmapCache<Key> cache = new BitmapCache();
    private final int defaultId;

    public BatchImageLoader(int defaultId) {
        this.defaultId = defaultId;
    }

    protected void timeOut(ImageView receiver) {
        receiver.setImageResource(this.defaultId);
    }

    protected Bitmap getResult(Key key) {
        return this.cache.getBitmap(key);
    }

    protected void saveResult(Key key, Bitmap result) {
        this.cache.putBitmap(key, result);
    }

    protected void onPostResult(ImageView image, Bitmap bitmap) {
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        } else {
            image.setImageResource(this.defaultId);
        }
    }

    public void destroy() {
        this.cache.destroy();
    }
}
