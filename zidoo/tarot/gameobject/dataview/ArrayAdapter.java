package zidoo.tarot.gameobject.dataview;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Texture;
import zidoo.tarot.widget.Layout;

public abstract class ArrayAdapter<T> extends Adapter {
    private final Object mLock = new Object();
    private List<T> mObjects = null;

    public void setDataSet(T[] dataset) {
        setDataSet(Arrays.asList(dataset));
    }

    public void setDataSet(List<T> dataset) {
        this.mObjects = dataset;
    }

    public void add(T object) {
        synchronized (this.mLock) {
            if (this.mObjects == null) {
                this.mObjects = new ArrayList();
            }
            this.mObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(T object) {
        synchronized (this.mLock) {
            if (this.mObjects != null) {
                this.mObjects.remove(object);
            }
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        synchronized (this.mLock) {
            if (this.mObjects != null) {
                this.mObjects.remove(position);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (this.mLock) {
            if (this.mObjects != null) {
                this.mObjects.clear();
            }
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        if (this.mObjects != null) {
            return this.mObjects.size();
        }
        return 0;
    }

    public int getPosition(T item) {
        if (this.mObjects != null) {
            return this.mObjects.indexOf(item);
        }
        return -1;
    }

    public T getItem(int position) {
        return this.mObjects.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        return convertView != null ? convertView : createViewFromContent(position, parent);
    }

    private GameObject createViewFromContent(int position, Layout parent) {
        T dataItem = this.mObjects.get(position);
        if (!((dataItem instanceof String) || (dataItem instanceof Texture))) {
            boolean z = dataItem instanceof Bitmap;
        }
        return null;
    }
}
