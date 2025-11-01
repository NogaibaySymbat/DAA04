package graph.dagsp;

import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.TopoKahn;
import java.util.*;

// Computes single-source shortest paths in a DAG
// Runs in O(V + E) using dynamic programming over a topological order

public class DagShortestPath {
    private final Graph g;

    public DagShortestPath(Graph g) {
        this.g = g;
    }

    // Result container: holds distances and parent links
    public static class Result {
        public final Map<Integer, Double> dist;
        public final Map<Integer, Integer> parent;

        public Result(Map<Integer, Double> dist, Map<Integer, Integer> parent) {
            this.dist = dist;
            this.parent = parent;
        }

        // Reconstructs the path from source to target
        public List<Integer> buildPath(int target) {
            if (!dist.containsKey(target) || dist.get(target) == Double.POSITIVE_INFINITY) {
                return Collections.emptyList();
            }
            List<Integer> path = new ArrayList<>();
            Integer cur = target;
            while (cur != null) {
                path.add(cur);
                cur = parent.get(cur);
            }
            Collections.reverse(path);
            return path;
        }
    }

    // Wrapper without metrics
    public Result shortestPaths(int src) {
        return shortestPaths(src, new Metrics());
    }

    // Main shortest path algorithm for DAGs
    public Result shortestPaths(int src, Metrics m) {
        // Compute topological order first
        List<Integer> order = new TopoKahn(g).sort();

        m.start();
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();

        // Initialize all distances to infinity
        for (int i = 0; i < g.size(); i++) {
            dist.put(i, Double.POSITIVE_INFINITY);
            parent.put(i, null);
        }
        dist.put(src, 0.0);

        // Relax edges following topological order
        for (int u : order) {
            double du = dist.get(u);
            if (du == Double.POSITIVE_INFINITY) continue;
            for (Graph.Edge e : g.adj.get(u)) {
                double nd = du + e.w;
                if (nd < dist.get(e.to)) {
                    dist.put(e.to, nd);
                    parent.put(e.to, u);
                    m.relaxations++;
                }
            }
        }

        m.stop();
        return new Result(dist, parent);
    }
}
