package graph.common;

 // Common metrics tracker for all graph algorithms
 // Measures operation counts and total execution time using System.nanoTime()

public class Metrics {
    // Operation counters
    public long dfsVisits = 0; // DFS calls or nodes processed
    public long edgesSeen = 0; // Edges traversed during execution
    public long queuePushes = 0; // Number of elements pushed to queue (Kahn)
    public long queuePops = 0; // Number of elements popped from queue (Kahn)
    public long relaxations = 0; // Relaxation operations in shortest/longest paths

    // Time measurement
    private long startNanos;
    private long endNanos;

    // Start timing
    public void start() {
        startNanos = System.nanoTime();
    }

    // Stop timing
    public void stop() {
        endNanos = System.nanoTime();
    }

    // Return total elapsed time
    public long elapsedNanos() {
        return endNanos - startNanos;
    }

    // Print formatted metrics summary for a given algorithm name
    public void print(String name) {
        double ms = (elapsedNanos()) / 1_000_000.0;
        System.out.printf(
                "%-20s | time: %7.3f ms | dfs: %-5d | edges: %-5d | push: %-5d | pop: %-5d | relax: %-5d%n",
                name, ms,
                dfsVisits, edgesSeen, queuePushes, queuePops, relaxations
        );
    }
}
