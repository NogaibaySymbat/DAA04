package app;

import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

// A simple test dataset generator for the task
// Creates 9 JSON files (small, medium, large graphs) in the ./data folder

public class DataGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        // Create the data folder if it doesn't exist
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        // Generate 3 small, 3 medium and 3 large graphs
        gen("small", 3, 6, 10);
        gen("medium", 3, 10, 20);
        gen("large", 3, 20, 50);

        System.out.println("Generated 9 datasets into ./data");
    }

    // Method for generating a set of graphs
    private static void gen(String prefix, int count, int minN, int maxN) throws IOException {
        Random rnd = new Random(42 + prefix.hashCode()); // fix the seed for repeatability
        for (int i = 1; i <= count; i++) {
            int n = rnd.nextInt(maxN - minN + 1) + minN; // random number of vertices
            JsonObject root = new JsonObject();
            root.addProperty("directed", true);
            root.addProperty("n", n);
            root.addProperty("source", 0);
            root.addProperty("weight_model", "edge");

            JsonArray edges = new JsonArray();
            int edgesToMake = Math.min(n * 3, 150); // limit the number of edges

            // create random edges
            for (int e = 0; e < edgesToMake; e++) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u == v) continue; // avoid loops
                double w = 1 + rnd.nextInt(5); // weight from 1 to 5
                JsonObject ed = new JsonObject();
                ed.addProperty("u", u);
                ed.addProperty("v", v);
                ed.addProperty("w", w);
                edges.add(ed);
            }

            root.add("edges", edges);

            // write the file
            try (FileWriter fw = new FileWriter("data/" + prefix + "_" + i + ".json")) {
                GSON.toJson(root, fw);
            }
        }
    }
}
