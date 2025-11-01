package graph.scc;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

 // Simple implementation of Tarjan's algorithm.
 // It finds strongly connected components in a directed graph.

public class TarjanSCC {

    // To return all found components and their ids
    public static class Result {
        public List<List<Integer>> components;
        public int[] compId;

        public Result(List<List<Integer>> comps, int[] ids) {
            this.components = comps;
            this.compId = ids;
        }
    }

    private Graph g;
    private int n;
    private int time = 0;

    // discovery and low-link arrays
    private int[] disc;
    private int[] low;
    private boolean[] onStack;
    private Deque<Integer> stack = new ArrayDeque<>();

    private List<List<Integer>> comps = new ArrayList<>();
    private int[] compId;

    public TarjanSCC(Graph g) {
        this.g = g;
        this.n = g.size();
        this.disc = new int[n];
        this.low = new int[n];
        this.onStack = new boolean[n];
        this.compId = new int[n];
        Arrays.fill(disc, -1); // -1 means not visited
    }

    // Just runs Tarjan with metrics tracking
    public Result run(Metrics m) {
        m.start();
        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) {
                dfs(v, m);
            }
        }
        m.stop();
        return new Result(comps, compId);
    }

    private void dfs(int u, Metrics m) {
        disc[u] = low[u] = ++time;
        stack.push(u);
        onStack[u] = true;
        m.dfsVisits++;

        for (Graph.Edge e : g.adj.get(u)) {
            m.edgesSeen++;
            int v = e.to;

            if (disc[v] == -1) { // if not visited yet
                dfs(v, m);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) { // back edge to something in current SCC
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // if u is the start of an SCC
        if (low[u] == disc[u]) {
            List<Integer> comp = new ArrayList<>();
            int x;
            do {
                x = stack.pop();
                onStack[x] = false;
                compId[x] = comps.size();
                comp.add(x);
            } while (x != u);
            comps.add(comp);

            // Small debug print — left for clarity
            System.out.println("SCC found: " + comp);
        }
    }

    // Quick helper if we want to run without metrics
    public Result run() {
        return run(new Metrics());
    }
}
