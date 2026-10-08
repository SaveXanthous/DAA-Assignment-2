package assignment2;

import assignment2.benchmark.BenchmarkRunner;
import assignment2.benchmark.CsvWriter;

import java.nio.file.Path;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        Path output = args.length == 0 ? Path.of("results", "results.csv") : Path.of(args[0]);
        try (CsvWriter writer = new CsvWriter(output)) {
            new BenchmarkRunner().run(writer);
        }
        System.out.println("Results saved to " + output.toAbsolutePath());
    }
}
