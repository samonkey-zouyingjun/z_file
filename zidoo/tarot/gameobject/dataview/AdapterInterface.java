package zidoo.tarot.gameobject.dataview;

import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;

public interface AdapterInterface {
    int getCount();

    Object getItem(int i);

    long getItemId(int i);

    GameObject getView(int i, GameObject gameObject, Layout layout);

    boolean isEmpty();

    void registerDataSetObserver(DataSetObserver dataSetObserver);

    void unregisterDataSetObserver(DataSetObserver dataSetObserver);
}
