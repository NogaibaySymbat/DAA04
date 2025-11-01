package graph.scc;

import graph.common.Graph;
import graph.common.Metrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

 // Unit tests for Tarjan's Strongly Connected Components (SCC) algorithm.
 // Verifies correct detection of cycles and multiple components.

public class TarjanSCTest {

    // Test detection of a single strongly connected component
    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        TarjanSCC scc = new TarjanSCC(g);
        Metrics m = new Metrics();
        TarjanSCC.Result res = scc.run(m);

        // Expect one SCC containing all 3 vertices
        assertEquals(1, res.components.size());
        assertEquals(3, res.components.get(0).size());

        // Metrics sanity check: algorithm must have visited all nodes and edges
        assertTrue(m.dfsVisits >= 3);
        assertTrue(m.edgesSeen >= 3);
    }

    // Test detection of two or more separate SCCs
    @Test
    public void testTwoSccs() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(2, 3, 1);

        TarjanSCC.Result res = new TarjanSCC(g).run();

        // Expect at least two SCCs: {0,1} and {2}, {3}
        assertTrue(res.components.size() >= 2);
    }
}
