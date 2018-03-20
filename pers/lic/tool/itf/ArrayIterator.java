package pers.lic.tool.itf;

import android.support.annotation.NonNull;
import java.util.Iterator;

public final class ArrayIterator<K, V> implements Iterator<V>, Iterable<V> {
    private int index = 0;
    private NameGetter<K, V> nameGetter;
    private K[] ts;

    public ArrayIterator(K[] ts, NameGetter<K, V> nameGetter) {
        this.ts = ts;
        this.nameGetter = nameGetter;
    }

    public boolean hasNext() {
        return this.index < this.ts.length;
    }

    public V next() {
        NameGetter nameGetter = this.nameGetter;
        Object[] objArr = this.ts;
        int i = this.index;
        this.index = i + 1;
        return nameGetter.getValue(objArr[i]);
    }

    @NonNull
    public Iterator<V> iterator() {
        return this;
    }
}
