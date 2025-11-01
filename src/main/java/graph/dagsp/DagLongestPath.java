package graph.dagsp;

import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.TopoKahn;
import java.util.*;

 // Computes the longest (critical) path in a DAG
 // Works with edge weights and optionally includes node weights

public class DagLongestPath {
    private final Graph g;
    private final double[] nodeWeight; // optional array of node weights

    public DagLongestPath(Graph g) {
        this(g, null);
    }

    public DagLongestPath(Graph g, double[] nodeWeight) {
        this.g = g;
        this.nodeWeight = nodeWeight;
    }

    // Result container: stores distances and parent links
    public static class Result {
        public final Map<Integer, Double> dist;
        public final Map<Integer, Integer> parent;

        public Result(Map<Integer, Double> dist, Map<Integer, Integer> parent) {
            this.dist = dist;
            this.parent = parent;
        }

        // Reconstructs path from source to target using parent map
        public List<Integer> buildPath(int target) {
            if (!dist.containsKey(target) || dist.get(target) == Double.NEGATIVE_INFINITY) {
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

    // Wrapper for running without metrics
    public Result longestPaths(int src) {
        return longestPaths(src, new Metrics());
    }

    // Main DP algorithm for longest paths in DAG
    public Result longestPaths(int src, Metrics m) {
        // Topological order is required for DAG DP
        List<Integer> order = new TopoKahn(g).sort();

        m.start();
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();

        // Initialize distances
        for (int i = 0; i < g.size(); i++) {
            dist.put(i, Double.NEGATIVE_INFINITY);
            parent.put(i, null);
        }

        // Start from source node (include its own weight if available)
        double startVal = (nodeWeight != null ? nodeWeight[src] : 0.0);
        dist.put(src, startVal);

        // Relax edges in topological order
        for (int u : order) {
            double du = dist.get(u);
            if (du == Double.NEGATIVE_INFINITY) continue;

            for (Graph.Edge e : g.adj.get(u)) {
                double edgeCost = e.w;
                double nodeCost = (nodeWeight != null ? nodeWeight[e.to] : 0.0);
                double nd = du + edgeCost + nodeCost;

                // Update if longer path found
                if (nd > dist.get(e.to)) {
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
