package assignment2.benchmark;

import assignment2.metrics.OperationCounters;
import assignment2.structures.DynamicArray;
import assignment2.structures.IntSequence;
import assignment2.structures.MinHeap;
import assignment2.structures.MyLinkedList;

import java.util.Random;

public final class BenchmarkRunner {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int MEASURED_RUNS = 5;
    private static final int WARMUP_RUNS = 1;
    private static final int ACCESS_QUERIES = 10_000;
    private static final int SEARCH_QUERIES = 1_000;
    private static final int EDITS = 1_000;

    public void run(CsvWriter writer) {
        for (int n : SIZES) {
            Input input = createInput(n);
            runSequenceCase(writer, "W1", "-", "DynamicArray", n, input,
                    (sequence, counters) -> {
                        for (int index : input.accessIndices) sequence.get(index);
                    });
            runSequenceCase(writer, "W1", "-", "MyLinkedList", n, input,
                    (sequence, counters) -> {
                        for (int index : input.accessIndices) sequence.get(index);
                    });

            runSequenceCase(writer, "W2", "-", "DynamicArray", n, input,
                    (sequence, counters) -> {
                        for (int value : input.searchValues) sequence.contains(value);
                    });
            runSequenceCase(writer, "W2", "-", "MyLinkedList", n, input,
                    (sequence, counters) -> {
                        for (int value : input.searchValues) sequence.contains(value);
                    });

            for (String variant : new String[]{"head", "middle"}) {
                int index = variant.equals("head") ? 0 : n / 2;
                runSequenceCase(writer, "W3", variant, "DynamicArray", n, input,
                        (sequence, counters) -> edit(sequence, index));
                runSequenceCase(writer, "W3", variant, "MyLinkedList", n, input,
                        (sequence, counters) -> edit(sequence, index));
            }

            runHeapCase(writer, n, input.values);
        }
    }

    private void runSequenceCase(CsvWriter writer, String workload, String variant,
                                 String structureName, int n, Input input, SequenceAction action) {
        for (int i = 0; i < WARMUP_RUNS; i++) runSequence(structureName, n, input, action, false);
        long[] times = new long[MEASURED_RUNS];
        long[] steps = new long[MEASURED_RUNS];
        long[] moves = new long[MEASURED_RUNS];
        long[] comparisons = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            RunResult result = runSequence(structureName, n, input, action, true);
            times[i] = result.elapsedNanos;
            steps[i] = result.steps;
            moves[i] = result.moves;
            comparisons[i] = result.comparisons;
        }
        writer.write(workload, variant, structureName, n, median(times) / 1_000_000.0,
                median(steps), median(moves), median(comparisons));
        System.out.printf("%s %s %s n=%d complete%n", workload, variant, structureName, n);
    }

    private RunResult runSequence(String structureName, int n, Input input,
                                   SequenceAction action, boolean measure) {
        OperationCounters counters = new OperationCounters();
        IntSequence sequence = structureName.equals("DynamicArray")
                ? new DynamicArray(counters) : new MyLinkedList(counters);
        for (int value : input.values) sequence.add(value);
        counters.reset();
        long start = measure ? System.nanoTime() : 0;
        action.run(sequence, counters);
        long elapsed = measure ? System.nanoTime() - start : 0;
        return new RunResult(elapsed, counters.steps(), counters.moves(), counters.comparisons());
    }

    private void runHeapCase(CsvWriter writer, int n, int[] values) {
        for (int i = 0; i < WARMUP_RUNS; i++) runHeap(values, false);
        long[] times = new long[MEASURED_RUNS];
        long[] steps = new long[MEASURED_RUNS];
        long[] moves = new long[MEASURED_RUNS];
        long[] comparisons = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            RunResult result = runHeap(values, true);
            times[i] = result.elapsedNanos;
            steps[i] = result.steps;
            moves[i] = result.moves;
            comparisons[i] = result.comparisons;
        }
        writer.write("W4", "-", "MinHeap", n, median(times) / 1_000_000.0,
                median(steps), median(moves), median(comparisons));
        System.out.printf("W4 - MinHeap n=%d complete%n", n);
    }

    private RunResult runHeap(int[] values, boolean measure) {
        OperationCounters counters = new OperationCounters();
        MinHeap heap = new MinHeap(counters);
        long start = measure ? System.nanoTime() : 0;
        for (int value : values) heap.insert(value);
        boolean first = true;
        int previous = 0;
        while (heap.size() > 0) {
            int current = heap.extractMin();
            if (!first && current < previous) {
                throw new IllegalStateException("heap output is not non-decreasing");
            }
            previous = current;
            first = false;
        }
        long elapsed = measure ? System.nanoTime() - start : 0;
        return new RunResult(elapsed, counters.steps(), counters.moves(), counters.comparisons());
    }

    private void edit(IntSequence sequence, int index) {
        for (int i = 0; i < EDITS; i++) sequence.add(index, -i - 1);
        for (int i = 0; i < EDITS; i++) sequence.remove(index);
    }

    private Input createInput(int n) {
        Random random = new Random(42);
        int[] values = new int[n];
        for (int i = 0; i < n; i++) values[i] = random.nextInt(Math.max(1, n * 10));
        int[] accessIndices = new int[ACCESS_QUERIES];
        for (int i = 0; i < accessIndices.length; i++) accessIndices[i] = random.nextInt(n);
        int[] searchValues = new int[SEARCH_QUERIES];
        for (int i = 0; i < SEARCH_QUERIES / 2; i++) searchValues[i] = values[random.nextInt(n)];
        for (int i = SEARCH_QUERIES / 2; i < SEARCH_QUERIES; i++) searchValues[i] = n * 10 + 1 + i;
        return new Input(values, accessIndices, searchValues);
    }

    private long median(long[] values) {
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    @FunctionalInterface
    private interface SequenceAction {
        void run(IntSequence sequence, OperationCounters counters);
    }

    private record Input(int[] values, int[] accessIndices, int[] searchValues) {}
    private record RunResult(long elapsedNanos, long steps, long moves, long comparisons) {}
}
