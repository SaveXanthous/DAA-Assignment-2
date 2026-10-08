package assignment2.structures;

import assignment2.metrics.OperationCounters;

public final class MyLinkedList implements IntSequence {
    private static final class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private Node tail;
    private int size;
    private final OperationCounters counters;

    public MyLinkedList() { this(new OperationCounters()); }
    public MyLinkedList(OperationCounters counters) {
        if (counters == null) throw new IllegalArgumentException("counters cannot be null");
        this.counters = counters;
    }

    @Override public void add(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = tail = node;
            counters.move();
            counters.move();
        } else {
            tail.next = node;
            counters.move();
            tail = node;
            counters.move();
        }
        size++;
    }

    @Override public void add(int index, int value) {
        checkPositionIndex(index);
        if (index == size) { add(value); return; }
        Node node = new Node(value);
        if (index == 0) {
            node.next = head;
            counters.move();
            head = node;
            counters.move();
        } else {
            Node previous = nodeAt(index - 1);
            node.next = previous.next;
            counters.move();
            previous.next = node;
            counters.move();
        }
        size++;
    }

    @Override public int remove(int index) {
        checkElementIndex(index);
        Node removed;
        if (index == 0) {
            removed = head;
            head = head.next;
            counters.move();
            if (size == 1) {
                tail = null;
                counters.move();
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;
            previous.next = removed.next;
            counters.move();
            if (removed == tail) {
                tail = previous;
                counters.move();
            }
        }
        size--;
        return removed.value;
    }

    @Override public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    @Override public boolean contains(int value) {
        Node current = head;
        while (current != null) {
            counters.comparison();
            if (current.value == value) return true;
            current = current.next;
            if (current != null) counters.step();
        }
        return false;
    }

    @Override public int size() { return size; }
    public OperationCounters counters() { return counters; }

    private Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
            counters.step();
        }
        return current;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
}
