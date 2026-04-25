package com.your_own_ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OllamaClient {
    private final String host;
    private final int port;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    
    public String embedModel = "nomic-embed-text";
    public String genModel = "llama3.2:1b";

    public OllamaClient() {
        this("127.0.0.1", 11434);
    }

    public OllamaClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isAvailable() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/api/tags"))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public float[] embed(String text) {
        try {
            java.util.Map<String, Object> reqBody = new java.util.HashMap<>();
            reqBody.put("model", embedModel);
            reqBody.put("prompt", text);
            String body = new com.google.gson.Gson().toJson(reqBody);

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/api/embeddings"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) return new float[0];
            
            JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();
            JsonArray arr = obj.getAsJsonArray("embedding");
            float[] result = new float[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                result[i] = arr.get(i).getAsFloat();
            }
            return result;
        } catch (Exception e) {
            return new float[0];
        }
    }

    public String generate(String prompt) {
        try {
            java.util.Map<String, Object> reqBody = new java.util.HashMap<>();
            reqBody.put("model", genModel);
            reqBody.put("prompt", prompt);
            reqBody.put("stream", false);
            String body = new com.google.gson.Gson().toJson(reqBody);

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/api/generate"))
                .timeout(Duration.ofSeconds(180))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                System.err.println("Ollama generate status: " + res.statusCode() + ", body: " + res.body());
                return "ERROR: Ollama unavailable. Run: ollama serve";
            }
            
            JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();
            return obj.get("response").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Ollama unavailable (Exception). Run: ollama serve";
        }
    }
}
