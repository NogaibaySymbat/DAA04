package graph.scc;

import graph.common.Graph;
import java.util.*;

 // Simple builder for the condensation graph (a DAG of SCCs).
 // Each node in the new graph = one SCC from the original graph.
 // Also calculates internal weights for each SCC.

public class CondensationBuilder {
    // Just a small helper class to return the result
    public static class CondensationResult {
        public Graph dag; // DAG after compression
        public double[] nodeWeight; // sum of edge weights inside each SCC

        public CondensationResult(Graph dag, double[] nodeWeight) {
            this.dag = dag;
            this.nodeWeight = nodeWeight;
        }
    }

     // Builds a DAG of components using Tarjan's SCC result.
     // It also computes the total internal weight (sum of edges) for each SCC

    public static CondensationResult build(Graph g, TarjanSCC.Result sccRes) {
        List<List<Integer>> comps = sccRes.components;
        int compCount = comps.size();
        Graph dag = new Graph(compCount, true);
        double[] nodeWeight = new double[compCount];

        // Step 1: sum internal weights inside each SCC
        for (int u = 0; u < g.size(); u++) {
            int cu = sccRes.compId[u];
            for (Graph.Edge e : g.adj.get(u)) {
                int cv = sccRes.compId[e.to];
                if (cu == cv) {
                    nodeWeight[cu] += e.w; // all inner edges in same SCC
                }
            }
        }

        // Step 2: add edges between different SCCs (avoid duplicates)
        Set<String> seen = new HashSet<>();
        for (int u = 0; u < g.size(); u++) {
            int cu = sccRes.compId[u];
            for (Graph.Edge e : g.adj.get(u)) {
                int cv = sccRes.compId[e.to];
                if (cu != cv) {
                    String key = cu + "->" + cv; // simple string key instead of bit trick
                    if (!seen.contains(key)) {
                        dag.addEdge(cu, cv, e.w);
                        seen.add(key);
                    }
                }
            }
        }

        // Debug print (to make sure everything looks right)
        System.out.println("Condensation graph built: " + compCount + " components");
        for (int i = 0; i < compCount; i++) {
            System.out.println("Component " + i + " weight = " + nodeWeight[i]);
        }
        return new CondensationResult(dag, nodeWeight);
    }
}
