package assignment2.structures;

import assignment2.metrics.OperationCounters;

public final class DynamicArray implements IntSequence {
    private int[] values = new int[1];
    private int size;
    private final OperationCounters counters;

    public DynamicArray() { this(new OperationCounters()); }
    public DynamicArray(OperationCounters counters) {
        if (counters == null) throw new IllegalArgumentException("counters cannot be null");
        this.counters = counters;
    }

    @Override public void add(int value) {
        ensureCapacity(size + 1);
        values[size++] = value;
        counters.move();
    }

    @Override public void add(int index, int value) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            values[i] = values[i - 1];
            counters.step();
            counters.move();
        }
        values[index] = value;
        counters.move();
        size++;
    }

    @Override public int remove(int index) {
        checkElementIndex(index);
        int removed = values[index];
        counters.step();
        for (int i = index; i < size - 1; i++) {
            values[i] = values[i + 1];
            counters.step();
            counters.move();
        }
        size--;
        return removed;
    }

    @Override public int get(int index) {
        checkElementIndex(index);
        counters.step();
        return values[index];
    }

    @Override public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            counters.step();
            counters.comparison();
            if (values[i] == value) return true;
        }
        return false;
    }

    @Override public int size() { return size; }
    public OperationCounters counters() { return counters; }

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

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
}
