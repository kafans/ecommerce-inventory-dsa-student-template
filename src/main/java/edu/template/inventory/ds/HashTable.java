package edu.template.inventory.ds;

import java.util.function.BiConsumer;

// Hash Table implementation using Separate Chaining
public class HashTable<K,V> {
    private int size;
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    // Buckets are implemented as an array of SinglyLinkedLists to handle collisions
    private SinglyLinkedList<Entry<K,V>>[] buckets;

    private static final class Entry<K,V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // Doubles the table capacity and rehashes all existing entries
    private void resize() {
        int newCapacity = buckets.length * 2;
        SinglyLinkedList<Entry<K,V>>[] newBuckets =
                (SinglyLinkedList<Entry<K,V>>[]) new SinglyLinkedList[newCapacity];

        for (int i = 0; i < newCapacity; i++) {
            newBuckets[i] = new SinglyLinkedList<>();
        }

        // Re-distribute all entries from old buckets to the new bucket array
        for (SinglyLinkedList<Entry<K,V>> bucket : buckets) {
            for (Entry<K,V> entry : bucket) {
                // Re-calculate hash index based on new capacity
                int newIndex = ((entry.key.hashCode() & 0xfffffff) % newCapacity);
                newBuckets[newIndex].addFirst(new Entry<>(entry.key, entry.value));
            }
        }

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

    // Hash function to map a key to a valid array index
    private int hash(K key) {
        // Bitwise AND ensures the result is always non-negative
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

        // Check if key already exists in the bucket (Update operation)
        for (Entry<K,V> entry : bucket) {
            if (keysEqual(entry.key, key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }

        // Key not found, insert new entry into the bucket
        bucket.addFirst(new Entry<>(key, value));
        size++;

        // Resize the table if the load factor threshold is exceeded
        if (size > buckets.length * LOAD_FACTOR) {
            resize();
        }

        return null;
    }

    public V get(K key) {
        int index = hash(key);
        SinglyLinkedList<Entry<K,V>> bucket = buckets[index];

        // Search the linked list in the specific bucket
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
        // Iterate to find the entry and remove it by index
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
        // Iterate through all buckets and all entries within them
        for (SinglyLinkedList<Entry<K,V>> bucket : buckets) {
            for (Entry<K,V> entry : bucket) {
                consumer.accept(entry.key, entry.value);
            }
        }
    }
}