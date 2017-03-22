package lab9;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

/**
 * Created by Thuy-Du on 3/16/2017.
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private int entries;
    private int length;
    private HashSet<K> keys = new HashSet<>();
    private ArrayList<V> table;
    private double lf;

    public MyHashMap() {
        table = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            table.add(null);
        }
        lf = 0.75;
        entries = 0;
        length = 10;
    }

    public MyHashMap(int initialSize) {
        table = new ArrayList<>();
        lf = 0.75;
        entries = 0;
        length = initialSize;
        for (int x = 0; x < initialSize; x++) {
            table.add(null);
        }

    }

    public MyHashMap(int initialSize, double loadFactor) {
        table = new ArrayList<>();
        lf = loadFactor;
        entries = 0;
        length = initialSize;
        for (int x = 0; x < initialSize; x++) {
            table.add(null);
        }

    }

    public void clear() {
        table.clear();
        entries = 0;
        keys.clear();
    }

    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    public V get(K key) {
        if (containsKey(key)) {
            return table.get(hashBrown(key));

        } else {
            return null;
        }
    }

    public void put(K key, V val) {
        resize();
        keys.add(key);

        int place = hashBrown(key);
        table.set(place, val);
        entries = size();
    }


    private int hashBrown(K key) {
        int place = (key.hashCode() & 0x7fffffff) % length;
        return place;
    }

    public int size() {
        return keys.size();
    }

    public Set<K> keySet() {
        return new HashSet<K>(keys);
    }

    public V remove(K key) {
        throw new UnsupportedOperationException("Remove method is not implemented");
    }

    public V remove(K key, V val) {
        throw new UnsupportedOperationException("Remove method is not implemented");
    }

    public Iterator iterator() {
        return keys.iterator();
    }

    private void resize() {
        if (entries > length * lf) {
            MyHashMap<K, V> expand = new MyHashMap<>(length * 2, lf);
            for (int x = 0; x < length; x++) {
                expand.table.set(x, table.get(x));
            }
            table = expand.table;
            length = expand.length;
        }

    }

    public static void main(String[] args) {
        String key = "sarah";
        System.out.println(key.hashCode());
    }


}
