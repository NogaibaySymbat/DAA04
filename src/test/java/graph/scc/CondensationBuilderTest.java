package graph.scc;

import graph.common.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CondensationBuilderTest {

    @Test
    public void testNoSelfEdgesAndWeights() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 0, 3); // one SCC of 2 nodes
        g.addEdge(1, 2, 1); // edge to separate node

        TarjanSCC.Result res = new TarjanSCC(g).run();
        CondensationBuilder.CondensationResult cr = CondensationBuilder.build(g, res);

        // SCCs: {0,1}, {2} => 2 nodes in condensation
        assertEquals(2, cr.dag.size());

        // internal weight of first scc should be 2 + 3 = 5
        // second should be 0
        assertTrue(cr.nodeWeight[0] == 5.0 || cr.nodeWeight[1] == 5.0);
    }
}
