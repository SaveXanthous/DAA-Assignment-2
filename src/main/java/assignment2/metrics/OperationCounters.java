package assignment2.metrics;

public final class OperationCounters {
    private long steps;
    private long moves;
    private long comparisons;

    public void step() { steps++; }
    public void move() { moves++; }
    public void comparison() { comparisons++; }

    public long steps() { return steps; }
    public long moves() { return moves; }
    public long comparisons() { return comparisons; }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}
