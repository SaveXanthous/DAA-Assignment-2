package assignment2.benchmark;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public final class CsvWriter implements AutoCloseable {
    private final BufferedWriter writer;

    public CsvWriter(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.write("workload,variant,structure,n,time_ms,steps,moves,comparisons");
        writer.newLine();
    }

    public void write(String workload, String variant, String structure, int n, double timeMs,
                      long steps, long moves, long comparisons) {
        try {
            writer.write(String.format(Locale.ROOT, "%s,%s,%s,%d,%.6f,%d,%d,%d",
                    workload, variant, structure, n, timeMs, steps, moves, comparisons));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Could not write benchmark CSV", e);
        }
    }

    @Override public void close() throws IOException { writer.close(); }
}
