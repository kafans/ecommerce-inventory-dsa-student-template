
package edu.template.inventory.ds;

import java.util.function.BiConsumer;

public class HashTable<K,V> {
    private int size;
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private SinglyLinkedList<Entry<K,V>>[] buckets;

    private static final class Entry<K,V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private void resize() {
        int newCapacity = buckets.length * 2;
        SinglyLinkedList<Entry<K,V>>[] newBuckets =
                (SinglyLinkedList<Entry<K,V>>[]) new SinglyLinkedList[newCapacity];

        for (int i = 0; i < newCapacity; i++) {
            newBuckets[i] = new SinglyLinkedList<>();
        }

        // 把旧表中的所有元素重新分配到新表中
        for (SinglyLinkedList<Entry<K,V>> bucket : buckets) {
            for (Entry<K,V> entry : bucket) {
                int newIndex = ((entry.key.hashCode() & 0xfffffff) % newCapacity);
                newBuckets[newIndex].addFirst(new Entry<>(entry.key, entry.value));
            }
        }

        // 替换 buckets 引用，size 不变
        buckets = newBuckets;
    }

    /*
    private void todo(String what) { throw new UnsupportedOperationException("students/you need to code this part: " + what); }
     */

    public HashTable() {
        buckets = (SinglyLinkedList<Entry<K,V>>[]) new SinglyLinkedList[INITIAL_CAPACITY];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new SinglyLinkedList<>();
        }
        size = 0;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % buckets.length;
    }

    private boolean keysEqual(K key1, K key2) {
        return key1.equals(key2);
    }

    public int size() {return size; }

    public boolean isEmpty() { return size == 0; }

    public V put(K key, V value) {
        int index = hash(key);
        SinglyLinkedList<Entry<K,V>> bucket = buckets[index];

        for (Entry<K,V> entry : bucket) {
            if (keysEqual(entry.key, key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }

        bucket.addFirst(new Entry<>(key, value));
        size++;

        if (size > buckets.length * LOAD_FACTOR) {
            resize();
        }

        return null;
    }

    public V get(K key) {
        int index = hash(key);
        SinglyLinkedList<Entry<K,V>> bucket = buckets[index];

        for (Entry<K,V> entry : bucket) {
            if (keysEqual(entry.key, key)) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        SinglyLinkedList<Entry<K,V>> bucket = buckets[index];

        for (Entry<K,V> entry : bucket) {
            if (keysEqual(entry.key, key)) {
                return true;
            }
        }
        return false;
    }

    public V remove(K key) {
        int index = hash(key);
        SinglyLinkedList<Entry<K,V>> bucket = buckets[index];

        int i = 0;
        for (Entry<K,V> entry : bucket) {
            if (keysEqual(entry.key, key)) {
                Entry<K,V> removed = bucket.removeAt(i);
                size--;
                return removed.value;
            }
            i++;
        }

        return null;
    }

    public void forEach(BiConsumer<K,V> consumer) {
        for (SinglyLinkedList<Entry<K,V>> bucket : buckets) {
            for (Entry<K,V> entry : bucket) {
                consumer.accept(entry.key, entry.value);
            }
        }
    }
}
