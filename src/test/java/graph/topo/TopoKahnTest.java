package graph.topo;

import graph.common.Graph;
import graph.common.Metrics;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

 // Unit tests for Kahn's topological sort algorithm.
 // Verifies correct order on DAGs and exception handling on cycles.

public class TopoKahnTest {

    // Test that topological sort produces a valid order on a simple DAG
    @Test
    public void testSimpleDag() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 3, 1);

        Metrics m = new Metrics();
        List<Integer> order = new TopoKahn(g).sort(m);

        // Check that all 4 vertices are included in the topological order
        assertEquals(4, order.size());

        // Verify dependency order constraints
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(3));

        // Ensure metrics captured actual operations
        assertTrue(m.queuePushes > 0);
        assertTrue(m.queuePops > 0);
    }

    // Test that algorithm correctly throws an exception on a cyclic graph
    @Test
    public void testCycleThrows() {
        Graph g = new Graph(2, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);

        // Expect failure because the graph contains a cycle
        assertThrows(IllegalStateException.class, () -> new TopoKahn(g).sort());
    }
}
