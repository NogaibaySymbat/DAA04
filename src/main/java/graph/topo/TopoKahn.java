package graph.topo;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

 // Implements Kahn's algorithm for topological sorting of a DAG.
 // Runs in O(V + E) time.

public class TopoKahn {
    private final Graph g;

    public TopoKahn(Graph g) {
        this.g = g;
    }

    // Wrapper without metrics
    public List<Integer> sort() {
        return sort(new Metrics());
    }

    // Main topological sorting method
    public List<Integer> sort(Metrics m) {
        int n = g.size();
        int[] indeg = new int[n];

        // Step 1: compute in-degrees for all vertices
        for (int u = 0; u < n; u++) {
            for (Graph.Edge e : g.adj.get(u)) {
                indeg[e.to]++;
                m.edgesSeen++;
            }
        }

        // Step 2: enqueue all vertices with indegree 0
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                q.add(i);
                m.queuePushes++;
            }
        }

        // Step 3: process queue (BFS-like traversal)
        m.start();
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            m.queuePops++;
            order.add(u);

            for (Graph.Edge e : g.adj.get(u)) {
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    q.add(e.to);
                    m.queuePushes++;
                }
            }
        }
        m.stop();

        // Check for cycles - topological sort only valid for DAGs
        if (order.size() != n) {
            throw new IllegalStateException("Graph is not a DAG, topo size = " + order.size() + " < " + n);
        }

        return order;
    }
}
