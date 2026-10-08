package assignment2.benchmark;

public enum Workload {
    RANDOM_ACCESS("W1"), SEARCH("W2"), INSERT_REMOVE("W3"), PRIORITY_PROCESSING("W4");

    private final String csvName;
    Workload(String csvName) { this.csvName = csvName; }
    public String csvName() { return csvName; }
}
