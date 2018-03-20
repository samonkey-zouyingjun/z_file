package pers.lic.tool.itf;

public interface NameGetter<K, V> {
    V getValue(K k);
}
