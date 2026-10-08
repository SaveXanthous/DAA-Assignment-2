package assignment2.structures;

import assignment2.metrics.OperationCounters;

public final class MinHeap {
    private int[] values = new int[1];
    private int size;
    private final OperationCounters counters;

    public MinHeap() { this(new OperationCounters()); }
    public MinHeap(OperationCounters counters) {
        if (counters == null) throw new IllegalArgumentException("counters cannot be null");
        this.counters = counters;
    }

    public void insert(int value) {
        ensureCapacity(size + 1);
        values[size] = value;
        counters.move();
        int child = size++;
        while (child > 0) {
            int parent = (child - 1) / 2;
            counters.step();
            counters.step();
            counters.comparison();
            if (values[parent] <= values[child]) break;
            swap(parent, child);
            child = parent;
        }
    }

    public int peekMin() {
        ensureNotEmpty();
        counters.step();
        return values[0];
    }

    public int extractMin() {
        ensureNotEmpty();
        int minimum = values[0];
        counters.step();
        int last = values[--size];
        counters.step();
        if (size > 0) {
            values[0] = last;
            counters.move();
            siftDown(0);
        }
        return minimum;
    }

    public int size() { return size; }
    public OperationCounters counters() { return counters; }

    private void siftDown(int parent) {
        while (true) {
            int left = parent * 2 + 1;
            if (left >= size) return;
            int right = left + 1;
            int smaller = left;
            if (right < size) {
                counters.step();
                counters.step();
                counters.comparison();
                if (values[right] < values[left]) smaller = right;
            }
            counters.step();
            counters.step();
            counters.comparison();
            if (values[parent] <= values[smaller]) return;
            swap(parent, smaller);
            parent = smaller;
        }
    }

    private void swap(int first, int second) {
        int temporary = values[first];
        counters.step();
        counters.step();
        values[first] = values[second];
        values[second] = temporary;
        counters.move();
        counters.move();
    }

    private void ensureCapacity(int required) {
        if (required <= values.length) return;
        int[] expanded = new int[values.length * 2];
        for (int i = 0; i < size; i++) {
            expanded[i] = values[i];
            counters.step();
            counters.move();
        }
        values = expanded;
    }

    private void ensureNotEmpty() {
        if (size == 0) throw new IllegalStateException("heap is empty");
    }
}
