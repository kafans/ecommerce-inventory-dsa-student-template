package edu.template.inventory.ds;

import java.util.Arrays;
import java.util.Comparator;

public final class Sorting {
    private Sorting() {}

    private static void todo(String what) { throw new UnsupportedOperationException("students/you need to code this part: " + what); }

    public static <T> void selectionSort(T[] a, Comparator<? super T> cmp) {
        int n = a.length;

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            // Find the index of the minimum element in the unsorted portion
            for (int j = i + 1; j < n; j++) {

                if (cmp.compare(a[j], a[minIndex]) < 0) {
                    minIndex = j;
                }
            }

            // Swap the found minimum element with the first unsorted element
            T temp = a[i];
            a[i] = a[minIndex];
            a[minIndex] = temp;
        }
    }

    public static <T> void mergeSort(T[] a, Comparator<? super T> cmp) {
        if (a == null || a.length <= 1) return;
        mergeSortRecursive(a, 0, a.length - 1, cmp);
    }

    private static <T> void mergeSortRecursive(T[] a, int left, int right, Comparator<? super T> cmp) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        // Recursively sort the left and right halves
        mergeSortRecursive(a, left, mid, cmp);          // [left, mid]
        mergeSortRecursive(a, mid + 1, right, cmp); // [mid+1, right]

        // Merge the two sorted halves
        merge(a, left, mid, right, cmp);
    }

    private static <T> void merge(T[] a, int left, int mid, int right, Comparator<? super T> cmp) {
        int length = right - left + 1;

        // Create a temporary array to hold merged results
        // Using Arrays.copyOf to safely create a generic array of the same type as 'a'
        T[] tmp = Arrays.copyOf(a, length);

        int i = left, j = mid + 1, k = 0;

        // Compare elements from both halves and copy the smaller one to tmp
        while (i <= mid && j <= right) {
            int compareResult = cmp.compare(a[i], a[j]);

            if (compareResult <= 0) {
                tmp[k] = a[i];
                i = i + 1;
            } else {
                tmp[k] = a[j];
                j = j + 1;
            }

            k = k + 1;
        }

        // Copy remaining elements from the left half, if any
        while (i <= mid) tmp[k++] = a[i++];
        // Copy remaining elements from the right half, if any
        while (j <= right) tmp[k++] = a[j++];

        // Copy the sorted elements from tmp back to the original array
        for (k = 0; k < length; k++) {
            a[left + k] = tmp[k];
        }
    }

    public static <T> void quickSort(T[] a, Comparator<? super T> cmp) {
        if (a == null || a.length <= 1) {
            return;
        }
        quickSortRecursive(a, 0, a.length - 1, cmp);
    }

    private static <T> void quickSortRecursive(T[] a, int low, int high, Comparator<? super T> cmp) {
        if (low >= high) {
            return;
        }

        // Select the last element as the pivot
        T pivot = a[high];

        int i = low;

        // Partition: move elements smaller than or equal to pivot to the left
        for (int j = low; j < high; j++) {
            if (cmp.compare(a[j], pivot) <= 0) {
                if (i != j) {
                    // Swap a[i] and a[j]
                    T temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
                i++;
            }
        }

        // Place the pivot in its correct sorted position
        if (i != high) {
            T temp = a[i];
            a[i] = a[high];
            a[high] = temp;
        }

        // Recursively sort the sub-arrays before and after the pivot
        quickSortRecursive(a, low, i - 1, cmp); // Left side
        quickSortRecursive(a, i + 1, high, cmp); // Right side
    }
}