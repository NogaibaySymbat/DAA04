package graph.common;

import java.util.ArrayList;
import java.util.List;

// Basic adjacency-list representation of a directed or undirected weighted graph
public class Graph {
    private final int n; // number of vertices
    private final boolean directed; // true if the graph is directed
    public final List<List<Edge>> adj; // adjacency list

    // Inner class representing an edge
    public static class Edge {
        public final int to;
        public final double w;
        public Edge(int to, double w) {
            this.to = to;
            this.w = w;
        }
        @Override
        public String toString() {
            return "→" + to + "(" + w + ")";
        }
    }

    // Constructor: creates a graph with n nodes
    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    // Adds an edge u -> v with weight w
    // If undirected, also adds v -> u
    public void addEdge(int u, int v, double w) {
        adj.get(u).add(new Edge(v, w));
        if (!directed) {
            adj.get(v).add(new Edge(u, w));
        }
    }

    // Returns the number of vertices
    public int size() {
        return n;
    }

    // Returns whether the graph is directed
    public boolean isDirected() {
        return directed;
    }
}
