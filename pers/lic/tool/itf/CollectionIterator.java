package pers.lic.tool.itf;

import android.support.annotation.NonNull;
import java.util.Iterator;

public final class CollectionIterator<K, V> implements Iterator<V>, Iterable<V> {
    private Iterator<K> iterator;
    private NameGetter<K, V> nameGetter;

    public CollectionIterator(Iterator<K> iterator, NameGetter<K, V> nameGetter) {
        this.iterator = iterator;
        this.nameGetter = nameGetter;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public V next() {
        return this.nameGetter.getValue(this.iterator.next());
    }

    @NonNull
    public Iterator<V> iterator() {
        return this;
    }
}
