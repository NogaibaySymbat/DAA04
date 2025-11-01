package graph.dagsp;

import graph.common.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


 // Unit tests for the DagLongestPath algorithm.
 // Verifies correctness with and without node weights.

public class DagLongestPathTest {

    // Test longest path computation in a simple linear DAG
    @Test
    public void testLongestWithoutNodeWeights() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 4);

        DagLongestPath lp = new DagLongestPath(g);
        DagLongestPath.Result res = lp.longestPaths(0);

        // Expected cumulative distances: 0 -> 2 -> 5 -> 9
        assertEquals(0.0, res.dist.get(0));
        assertEquals(2.0, res.dist.get(1));
        assertEquals(5.0, res.dist.get(2));
        assertEquals(9.0, res.dist.get(3));

        // Expected critical path
        assertEquals(java.util.List.of(0, 1, 2, 3), res.buildPath(3));
    }
    // Test longest path computation when node weights are included
    @Test
    public void testLongestWithNodeWeights() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        double[] nw = {2.0, 3.0, 4.0}; // internal SCC weights
        DagLongestPath lp = new DagLongestPath(g, nw);
        DagLongestPath.Result res = lp.longestPaths(0);

        // Distances including node weights
        // dist[0] starts at 2 (own node weight)
        assertEquals(2.0, res.dist.get(0));
        // 0 -> 1: 2 + 1 + 3 = 6
        assertEquals(6.0, res.dist.get(1));
        // 0 -> 1 -> 2: 6 + 1 + 4 = 11
        assertEquals(11.0, res.dist.get(2));
    }
}
