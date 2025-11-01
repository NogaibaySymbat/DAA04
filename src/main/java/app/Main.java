package app;

import com.google.gson.*;
import graph.common.Graph;
import graph.common.Metrics;
import graph.scc.TarjanSCC;
import graph.scc.CondensationBuilder;
import graph.topo.TopoKahn;
import graph.dagsp.DagShortestPath;
import graph.dagsp.DagLongestPath;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Check if the data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("data/ directory not found. Put your JSONs there.");
            return;
        }

        // Load all JSON files from the data folder
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.err.println("No JSON files in data/");
            return;
        }

        // Process each dataset file
        for (File f : files) {
            runForFile(f);
        }
    }

    private static void runForFile(File f) {
        System.out.println("-------------------------------------");
        System.out.println("Dataset: " + f.getName());
        System.out.println("--------------------------------------");
        try {
            // Parse the JSON file
            JsonObject root = JsonParser.parseReader(new FileReader(f)).getAsJsonObject();

            boolean directed = root.get("directed").getAsBoolean();
            int n = root.get("n").getAsInt();
            int source = root.get("source").getAsInt();
            JsonArray edges = root.getAsJsonArray("edges");

            // Build the graph
            Graph g = new Graph(n, directed);
            for (JsonElement el : edges) {
                JsonObject e = el.getAsJsonObject();
                int u = e.get("u").getAsInt();
                int v = e.get("v").getAsInt();
                double w = e.get("w").getAsDouble();
                g.addEdge(u, v, w);
            }

            System.out.println("Loaded graph: n = " + n + ", edges = " + edges.size());

            // --- 1) Strongly Connected Components (SCC)
            Metrics mScc = new Metrics();
            TarjanSCC tarjan = new TarjanSCC(g);
            TarjanSCC.Result sccRes = tarjan.run(mScc);
            mScc.print("Tarjan SCC");

            System.out.println("\n--- SCC components ---");
            for (int i = 0; i < sccRes.components.size(); i++) {
                List<Integer> comp = sccRes.components.get(i);
                System.out.println("C" + i + " = " + comp + " (size = " + comp.size() + ")");
            }

            // --- 2) Build the condensation DAG
            CondensationBuilder.CondensationResult cRes = CondensationBuilder.build(g, sccRes);
            Graph dag = cRes.dag;

            System.out.println("\nCondensation DAG nodes = " + dag.size());
            System.out.println("\n--- SCC internal weights ---");
            for (int i = 0; i < cRes.nodeWeight.length; i++) {
                System.out.printf("Comp %d: weight=%.2f%n", i, cRes.nodeWeight[i]);
            }

            // --- 3) Topological sorting of the condensation DAG
            Metrics mTopo = new Metrics();
            TopoKahn topo = new TopoKahn(dag);
            List<Integer> order = topo.sort(mTopo);
            mTopo.print("Topo (condensation)");
            System.out.println("\n--- Topological order of components ---");
            System.out.println(order);

            // Derived order of original vertices
            System.out.println("\n--- Derived order of original tasks ---");
            int[] compId = sccRes.compId;
            for (int comp : order) {
                for (int v = 0; v < n; v++) {
                    if (compId[v] == comp) System.out.print(v + " ");
                }
            }
            System.out.println();

            // Identify which component the source vertex belongs to
            int srcComp = compId[source];
            System.out.println("\nSource vertex " + source + " is in component " + srcComp);

            // --- 4) Shortest paths in DAG
            Metrics mSp = new Metrics();
            DagShortestPath sp = new DagShortestPath(dag);
            DagShortestPath.Result spRes = sp.shortestPaths(srcComp, mSp);
            mSp.print("DAG shortest");

            System.out.println("\n--- Shortest distances from component " + srcComp + " ---");
            System.out.println(spRes.dist);
            int lastNode = dag.size() - 1;
            System.out.println("Shortest path " + srcComp + " -> " + lastNode + ": " + spRes.buildPath(lastNode));

            // --- 5) Longest (critical) path in DAG
            Metrics mLp = new Metrics();
            DagLongestPath lp = new DagLongestPath(dag, cRes.nodeWeight);
            DagLongestPath.Result lpRes = lp.longestPaths(srcComp, mLp);
            mLp.print("DAG longest");

            double best = Double.NEGATIVE_INFINITY;
            int bestNode = srcComp;
            for (Map.Entry<Integer, Double> e : lpRes.dist.entrySet()) {
                if (e.getValue() > best) {
                    best = e.getValue();
                    bestNode = e.getKey();
                }
            }

            System.out.println("\n--- Longest / critical distances from component " + srcComp + " ---");
            System.out.println(lpRes.dist);
            System.out.println("Critical length = " + best);
            System.out.println("Critical path   = " + lpRes.buildPath(bestNode));

            System.out.println("\nDone: " + f.getName());

        } catch (Exception e) {
            System.out.println("Error on " + f.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
