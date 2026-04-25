package com.your_own_ai;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Server {
    private static final Gson gson = new Gson();
    
    private static void loadDemo(VectorDB db) {
        Distance.DistFn dist = Distance.getDistFn("cosine");
        db.insert("Linked List: nodes connected by pointers", "cs", new float[]{0.90f,0.85f,0.72f,0.68f,0.12f,0.08f,0.15f,0.10f,0.05f,0.08f,0.06f,0.09f,0.07f,0.11f,0.08f,0.06f}, dist);
        db.insert("Binary Search Tree: O(log n) search and insert", "cs", new float[]{0.88f,0.82f,0.78f,0.74f,0.15f,0.10f,0.08f,0.12f,0.06f,0.07f,0.08f,0.05f,0.09f,0.06f,0.07f,0.10f}, dist);
        db.insert("Dynamic Programming: memoization overlapping subproblems", "cs", new float[]{0.82f,0.76f,0.88f,0.80f,0.20f,0.18f,0.12f,0.09f,0.07f,0.06f,0.08f,0.07f,0.08f,0.09f,0.06f,0.07f}, dist);
        db.insert("Graph BFS and DFS: breadth and depth first traversal", "cs", new float[]{0.85f,0.80f,0.75f,0.82f,0.18f,0.14f,0.10f,0.08f,0.06f,0.09f,0.07f,0.06f,0.10f,0.08f,0.09f,0.07f}, dist);
        db.insert("Hash Table: O(1) lookup with collision chaining", "cs", new float[]{0.87f,0.78f,0.70f,0.76f,0.13f,0.11f,0.09f,0.14f,0.08f,0.07f,0.06f,0.08f,0.07f,0.10f,0.08f,0.09f}, dist);
        db.insert("Calculus: derivatives integrals and limits", "math", new float[]{0.12f,0.15f,0.18f,0.10f,0.91f,0.86f,0.78f,0.72f,0.08f,0.06f,0.07f,0.09f,0.07f,0.08f,0.06f,0.10f}, dist);
        db.insert("Linear Algebra: matrices eigenvalues eigenvectors", "math", new float[]{0.20f,0.18f,0.15f,0.12f,0.88f,0.90f,0.82f,0.76f,0.09f,0.07f,0.08f,0.06f,0.10f,0.07f,0.08f,0.09f}, dist);
        db.insert("Probability: distributions random variables Bayes theorem", "math", new float[]{0.15f,0.12f,0.20f,0.18f,0.84f,0.80f,0.88f,0.82f,0.07f,0.08f,0.06f,0.10f,0.09f,0.06f,0.09f,0.08f}, dist);
        db.insert("Number Theory: primes modular arithmetic RSA cryptography", "math", new float[]{0.22f,0.16f,0.14f,0.20f,0.80f,0.85f,0.76f,0.90f,0.08f,0.09f,0.07f,0.06f,0.08f,0.10f,0.07f,0.06f}, dist);
        db.insert("Combinatorics: permutations combinations generating functions", "math", new float[]{0.18f,0.20f,0.16f,0.14f,0.86f,0.78f,0.84f,0.80f,0.06f,0.07f,0.09f,0.08f,0.06f,0.09f,0.10f,0.07f}, dist);
        db.insert("Neapolitan Pizza: wood-fired dough San Marzano tomatoes", "food", new float[]{0.08f,0.06f,0.09f,0.07f,0.07f,0.08f,0.06f,0.09f,0.90f,0.86f,0.78f,0.72f,0.08f,0.06f,0.09f,0.07f}, dist);
        db.insert("Sushi: vinegared rice raw fish and nori rolls", "food", new float[]{0.06f,0.08f,0.07f,0.09f,0.09f,0.06f,0.08f,0.07f,0.86f,0.90f,0.82f,0.76f,0.07f,0.09f,0.06f,0.08f}, dist);
        db.insert("Ramen: noodle soup with chashu pork and soft-boiled eggs", "food", new float[]{0.09f,0.07f,0.06f,0.08f,0.08f,0.09f,0.07f,0.06f,0.82f,0.78f,0.90f,0.84f,0.09f,0.07f,0.08f,0.06f}, dist);
        db.insert("Tacos: corn tortillas with carnitas salsa and cilantro", "food", new float[]{0.07f,0.09f,0.08f,0.06f,0.06f,0.07f,0.09f,0.08f,0.78f,0.82f,0.86f,0.90f,0.06f,0.08f,0.07f,0.09f}, dist);
        db.insert("Croissant: laminated pastry with buttery flaky layers", "food", new float[]{0.06f,0.07f,0.10f,0.09f,0.10f,0.06f,0.07f,0.10f,0.85f,0.80f,0.76f,0.82f,0.09f,0.07f,0.10f,0.06f}, dist);
        db.insert("Basketball: fast-paced shooting dribbling slam dunks", "sports", new float[]{0.09f,0.07f,0.08f,0.10f,0.08f,0.09f,0.07f,0.06f,0.08f,0.07f,0.09f,0.06f,0.91f,0.85f,0.78f,0.72f}, dist);
        db.insert("Football: tackles touchdowns field goals and strategy", "sports", new float[]{0.07f,0.09f,0.06f,0.08f,0.09f,0.07f,0.10f,0.08f,0.07f,0.09f,0.08f,0.07f,0.87f,0.89f,0.82f,0.76f}, dist);
        db.insert("Tennis: racket volleys groundstrokes and Wimbledon serves", "sports", new float[]{0.08f,0.06f,0.09f,0.07f,0.07f,0.08f,0.06f,0.09f,0.09f,0.06f,0.07f,0.08f,0.83f,0.80f,0.88f,0.82f}, dist);
        db.insert("Chess: openings endgames tactics strategic board game", "sports", new float[]{0.25f,0.20f,0.22f,0.18f,0.22f,0.18f,0.20f,0.15f,0.06f,0.08f,0.07f,0.09f,0.80f,0.84f,0.78f,0.90f}, dist);
        db.insert("Swimming: butterfly freestyle backstroke Olympic competition", "sports", new float[]{0.06f,0.08f,0.07f,0.09f,0.08f,0.06f,0.09f,0.07f,0.10f,0.08f,0.06f,0.07f,0.85f,0.82f,0.86f,0.80f}, dist);
    }
    
    private static void sendResponse(HttpExchange ex, int code, Object body) throws IOException {
        String rsp = gson.toJson(body);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = rsp.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private static void cors(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(204, -1);
            ex.close();
        }
    }
    
    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
    
    private static float[] parseVec(String s) {
        if (s == null) return new float[0];
        String[] parts = s.split(",");
        float[] res = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            res[i] = Float.parseFloat(parts[i]);
        }
        return res;
    }
    
    private static String slurp(InputStream is) throws IOException {
        Scanner s = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    private static List<String> chunkText(String text, int chunkWords, int overlapWords) {
        String[] words = text.split("\\s+");
        if (words.length == 0) return new ArrayList<>();
        if (words.length <= chunkWords) return Collections.singletonList(text);
        
        List<String> chunks = new ArrayList<>();
        int step = chunkWords - overlapWords;
        for (int i = 0; i < words.length; i += step) {
            int end = Math.min(i + chunkWords, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int j = i; j < end; j++) {
                if (j > i) chunk.append(" ");
                chunk.append(words[j]);
            }
            chunks.add(chunk.toString());
            if (end == words.length) break;
        }
        return chunks;
    }

    public static void main(String[] args) throws IOException {
        VectorDB db = new VectorDB(16);
        DocumentDB docDB = new DocumentDB();
        OllamaClient ollama = new OllamaClient();

        loadDemo(db);

        boolean ollamaUp = ollama.isAvailable();
        System.out.println("=== VectorDB Engine (Java) ===");
        System.out.println("http://localhost:8080");
        System.out.println(db.size() + " demo vectors | " + db.dims + " dims | HNSW+KD-Tree+BruteForce");
        System.out.println("Ollama: " + (ollamaUp ? "ONLINE" : "OFFLINE (install from ollama.com)"));
        if (ollamaUp) {
            System.out.println("  embed model: " + ollama.embedModel + "  gen model: " + ollama.genModel);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) {
                Path idx = Paths.get("index.html");
                if (Files.exists(idx)) {
                    byte[] bytes = Files.readAllBytes(idx);
                    ex.getResponseHeaders().set("Content-Type", "text/html");
                    ex.sendResponseHeaders(200, bytes.length);
                    OutputStream os = ex.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }
            }
            ex.sendResponseHeaders(404, -1);
            ex.close();
        });

        server.createContext("/search", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                Map<String, String> qm = queryToMap(ex.getRequestURI().getQuery());
                float[] q = parseVec(qm.get("v"));
                if (q.length != db.dims) {
                    sendResponse(ex, 400, Map.of("error", "need 16D vector"));
                    return;
                }
                int k = 5;
                if (qm.containsKey("k")) k = Integer.parseInt(qm.get("k"));
                String metric = qm.getOrDefault("metric", "cosine");
                String algo = qm.getOrDefault("algo", "hnsw");

                VectorDB.SearchOut res = db.search(q, k, metric, algo);
                Map<String, Object> out = new HashMap<>();
                out.put("latencyUs", res.us);
                out.put("algo", res.algo);
                out.put("metric", res.metric);
                
                List<Map<String, Object>> hits = new ArrayList<>();
                for (VectorDB.Hit h : res.hits) {
                    Map<String, Object> ro = new HashMap<>();
                    ro.put("id", h.id);
                    ro.put("metadata", h.meta);
                    ro.put("category", h.cat);
                    ro.put("embedding", h.emb);
                    ro.put("distance", h.dist);
                    hits.add(ro);
                }
                out.put("results", hits);
                sendResponse(ex, 200, out);
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/insert", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                JsonObject json = JsonParser.parseString(slurp(ex.getRequestBody())).getAsJsonObject();
                String meta = json.get("metadata").getAsString();
                String cat = json.get("category").getAsString();
                JsonArray arr = json.getAsJsonArray("embedding");
                float[] emb = new float[arr.size()];
                for (int i=0; i<arr.size(); i++) emb[i] = arr.get(i).getAsFloat();
                int id = db.insert(meta, cat, emb, Distance.getDistFn("cosine"));
                sendResponse(ex, 200, Map.of("success", true, "id", id));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/delete", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                String path = ex.getRequestURI().getPath();
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                boolean ok = db.remove(id);
                if (ok) sendResponse(ex, 200, Map.of("success", true));
                else sendResponse(ex, 404, Map.of("error", "not found"));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/items", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            sendResponse(ex, 200, db.all());
        });

        server.createContext("/benchmark", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                Map<String, String> qm = queryToMap(ex.getRequestURI().getQuery());
                float[] q = parseVec(qm.get("v"));
                int k = 5;
                if (qm.containsKey("k")) k = Integer.parseInt(qm.get("k"));
                String metric = qm.getOrDefault("metric", "cosine");
                
                VectorDB.BenchOut bout = db.benchmark(q, k, metric);
                sendResponse(ex, 200, bout);
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/hnsw-info", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            sendResponse(ex, 200, db.hnswInfo());
        });

        server.createContext("/status", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            boolean up = ollama.isAvailable();
            Map<String, Object> st = new HashMap<>();
            st.put("ollamaAvailable", up);
            st.put("embedModel", ollama.embedModel);
            st.put("genModel", ollama.genModel);
            st.put("docCount", docDB.size());
            st.put("docDims", docDB.getDims());
            sendResponse(ex, 200, st);
        });

        server.createContext("/doc/insert", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                String reqBody = slurp(ex.getRequestBody());
                JsonObject json = JsonParser.parseString(reqBody).getAsJsonObject();
                String title = json.get("title").getAsString();
                String text = json.get("text").getAsString();
                
                List<String> chunks = chunkText(text, 250, 30);
                for (String chunk : chunks) {
                    float[] e = ollama.embed(title + "\n" + chunk);
                    if (e.length == 0) {
                        sendResponse(ex, 500, Map.of("error", "Ollama embed failed (is it running?)"));
                        return;
                    }
                    docDB.insert(title, chunk, e);
                }
                sendResponse(ex, 200, Map.of(
                    "success", true,
                    "chunks", chunks.size(),
                    "dims", docDB.getDims()
                ));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/doc/list", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            List<Map<String, Object>> out = new ArrayList<>();
            for (DocItem d : docDB.all()) {
                Map<String, Object> o = new HashMap<>();
                o.put("id", d.id);
                o.put("title", d.title);
                String clean = d.text.replace("\n", " ").replace("\r", " ");
                o.put("preview", clean.substring(0, Math.min(clean.length(), 100)) + "...");
                o.put("words", d.text.split("\\s+").length);
                out.add(o);
            }
            sendResponse(ex, 200, out);
        });

        server.createContext("/doc/delete", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                String path = ex.getRequestURI().getPath();
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                docDB.remove(id);
                sendResponse(ex, 200, Map.of("success", true));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });
        
        server.createContext("/doc/search", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                JsonObject json = JsonParser.parseString(slurp(ex.getRequestBody())).getAsJsonObject();
                String qStr = json.get("question").getAsString();
                int k = json.has("k") ? json.get("k").getAsInt() : 3;
                
                float[] qe = ollama.embed(qStr);
                List<DocumentDB.DocHit> hits = docDB.search(qe, k, 0.7f);
                
                List<Map<String, Object>> ctx = new ArrayList<>();
                for (DocumentDB.DocHit dh : hits) {
                    Map<String, Object> o = new HashMap<>();
                    o.put("title", dh.item.title);
                    o.put("text", dh.item.text);
                    o.put("distance", dh.dist);
                    ctx.add(o);
                }
                sendResponse(ex, 200, Map.of("contexts", ctx));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        server.createContext("/doc/ask", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            try {
                if (docDB.size() == 0) {
                    sendResponse(ex, 400, Map.of("error", "No documents inserted yet. Please insert documents first."));
                    return;
                }
                
                JsonObject json = JsonParser.parseString(slurp(ex.getRequestBody())).getAsJsonObject();
                String qStr = json.get("question").getAsString();
                int k = json.has("k") ? json.get("k").getAsInt() : 3;
                
                float[] qe = ollama.embed(qStr);
                if (qe.length == 0) {
                    sendResponse(ex, 500, Map.of("error", "Failed to embed question (is Ollama running?)"));
                    return;
                }
                
                List<DocumentDB.DocHit> hits = docDB.search(qe, k, 0.7f);
                StringBuilder prompt = new StringBuilder("Answer the question using ONLY the provided context.\n\nCONTEXT:\n");
                List<Map<String, Object>> ctxOut = new ArrayList<>();
                
                for (int i = 0; i < hits.size(); i++) {
                    DocumentDB.DocHit dh = hits.get(i);
                    prompt.append("[").append(i + 1).append("] Title: ").append(dh.item.title)
                          .append("\nText: ").append(dh.item.text).append("\n\n");
                    
                    Map<String, Object> o = new HashMap<>();
                    o.put("title", dh.item.title);
                    o.put("text", dh.item.text);
                    o.put("distance", dh.dist);
                    ctxOut.add(o);
                }
                
                prompt.append("QUESTION:\n").append(qStr).append("\n\nANSWER:");
                String ans = ollama.generate(prompt.toString());
                
                sendResponse(ex, 200, Map.of("answer", ans, "contexts", ctxOut));
            } catch (Exception e) {
                sendResponse(ex, 500, Map.of("error", e.getMessage()));
            }
        });

        // Map /stats based on C++ missing this actually, or we just mock.
        server.createContext("/stats", ex -> {
            cors(ex);
            if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) return;
            sendResponse(ex, 200, Map.of("vectorDbSize", db.size(), "documentDbSize", docDB.size()));
        });

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }
}
