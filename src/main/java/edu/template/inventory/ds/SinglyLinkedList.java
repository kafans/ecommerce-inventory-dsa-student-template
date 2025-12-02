
package edu.template.inventory.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class SinglyLinkedList<T> implements Iterable<T> {
    public static final class Node<T> {
        public T item;
        public Node<T> next;
        Node(T item, Node<T> n) {
            this.item = item;
            this.next = n;
        }
    }

    public SinglyLinkedList() {
        sentinel = new Node<>(null, null);
        size = 0;
    }

    private int size = 0;
    private Node<T> sentinel;

    /*
    private void todo(String what) { throw new UnsupportedOperationException("students/you need to code this part: " + what); }
     */

    public int size() {return size; }

    public boolean isEmpty() {return size == 0;}

    public void addFirst(T item) {
        sentinel.next = new Node<>(item, sentinel.next);
        size = size + 1;
    }

    public void addLast(T item) {
        size = size + 1;

        Node<T> p = sentinel;

        while (p.next != null) {
            p = p.next;
        }

        p.next =new Node<>(item, null);
    }

    public T removeFirst() {
        if (isEmpty()){
            throw new NoSuchElementException("It is empty.");
        }

        size = size - 1;

        T data = sentinel.next.item;
        sentinel.next = sentinel.next.next;

        return data;
    }

    public T get(int index) {
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        return p.item;
    }

    public T set(int index, T item) {
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> p = sentinel.next;

        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        T oldItem = p.item;
        p.item = item;

        return oldItem;
    }

    public T removeAt(int index) {
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> p = sentinel;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        T data = p.next.item;
        p.next = p.next.next;

        size = size - 1;

        return data;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = sentinel.next;

            public boolean hasNext() {return current != null;}

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }

                T item = current.item;
                current = current.next;

                return item;
            }

            public void forEachRemaining(Consumer<? super T> action) {
                while (hasNext()) {
                    action.accept(next());
                }
            }
        };
    }

    public <U extends Comparable<U>> void mergeSort(java.util.function.Function<T,U> keyExtractor) {
        if (size <= 1) {
            return;
        }

        sentinel.next = mergeSortRecursive(sentinel.next, size, keyExtractor);
    }

    private <U extends Comparable<U>> Node<T> mergeSortRecursive(
            Node<T> head,
            int length,
            java.util.function.Function<T,U> keyExtractor) {

        if (head == null || length <= 1) {
            return head;
        }

        int leftSize = length / 2;
        int rightSize = length - leftSize;

        Node<T> leftTail = head;
        for (int i = 0; i < leftSize - 1; i++) {
            leftTail = leftTail.next;
        }

        Node<T> rightHead = leftTail.next;
        leftTail.next = null;

        Node<T> left = mergeSortRecursive(head, leftSize, keyExtractor);
        Node<T> right = mergeSortRecursive(rightHead, rightSize, keyExtractor);

        return merge(left, right, keyExtractor);
    }

    private <U extends Comparable<U>> Node<T> merge(Node<T> left, Node<T> right, java.util.function.Function<T,U> keyExtractor) {

        Node<T> s = new Node<>(null, null);
        Node<T> current = s;

        while (left != null && right != null) {
            U leftKey = keyExtractor.apply(left.item);
            U rightKey = keyExtractor.apply(right.item);

            if (leftKey.compareTo(rightKey) <= 0) {
                current.next = left;
                left = left.next;
            } else {
                current.next = right;
                right = right.next;
            }
            current = current.next;
        }

        if (left != null) {
            current.next = left;
        }
        if (right != null) {
            current.next = right;
        }

        return s.next;
    }
}
