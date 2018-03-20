package zidoo.tarot.adapter;

import zidoo.tarot.gameobject.dataview.Adapter;

public abstract class AdjustableAdapter extends Adapter {
    public abstract void remove(int i);

    public abstract void remove(boolean[] zArr, int i);
}
