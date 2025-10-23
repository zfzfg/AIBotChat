package de.zfzfg.aichat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TextGenAPI {

    private final AIBotPlugin plugin;
    private final HttpClient client;
    private final String apiUrl;
    private final String character;
    private final int maxTokens;
    private final double temperature;
    private final List<String> stopSequences;
    private final boolean isChatMode;

    public TextGenAPI(AIBotPlugin plugin) {
        this.plugin = plugin;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        // Config laden
        this.apiUrl = plugin.getConfig().getString("api.url");
        this.character = plugin.getConfig().getString("api.character");
        this.maxTokens = plugin.getConfig().getInt("api.max-tokens");
        this.temperature = plugin.getConfig().getDouble("api.temperature");
        this.stopSequences = plugin.getConfig().getStringList("api.stop-sequences");
        
        // Erkenne API-Format
        this.isChatMode = apiUrl.contains("/chat/completions");
        
        plugin.getLogger().info("═══════════════════════════════════════");
        plugin.getLogger().info("  API Mode: " + (isChatMode ? "Chat" : "Completion"));
        plugin.getLogger().info("  URL: " + apiUrl);
        plugin.getLogger().info("  Character: " + character);
        plugin.getLogger().info("  Max Tokens: " + maxTokens);
        plugin.getLogger().info("  Stop Sequences: " + stopSequences.size());
        plugin.getLogger().info("═══════════════════════════════════════");
    }

    public CompletableFuture<String> generateResponse(String userInput) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject json = new JsonObject();
                
                if (isChatMode) {
                    // Chat Completions Format
                    JsonArray messages = new JsonArray();
                    
                    // User Message
                    JsonObject userMsg = new JsonObject();
                    userMsg.addProperty("role", "user");
                    userMsg.addProperty("content", userInput);
                    messages.add(userMsg);
                    
                    json.add("messages", messages);
                    json.addProperty("max_tokens", maxTokens);
                    json.addProperty("temperature", temperature);
                    json.addProperty("mode", "chat");
                    json.addProperty("character", character);
                    
                    addStopSequencesToJson(json, "stop");  // Helper-Methode
                    
                } else if (apiUrl.contains("/v1/completions")) {
                    // Standard Completions Format
                    json.addProperty("prompt", userInput);
                    json.addProperty("max_tokens", maxTokens);
                    json.addProperty("temperature", temperature);
                    
                    addStopSequencesToJson(json, "stop");  // Helper-Methode
                    
                } else {
                    // text-generation-webui Format
                    json.addProperty("prompt", userInput);
                    json.addProperty("max_new_tokens", maxTokens);
                    json.addProperty("do_sample", true);
                    json.addProperty("temperature", temperature);
                    json.addProperty("top_p", 0.9);
                    json.addProperty("repetition_penalty", 1.1);
                    
                    addStopSequencesToJson(json, "stopping_strings");  // Helper-Methode
                }

                // HTTP Request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(30))
                        .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                        .build();

                // Request senden
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    String errorMsg = "API Error " + response.statusCode();
                    if (response.statusCode() == 404) {
                        errorMsg += " - Endpoint not found. Check URL in config.yml";
                    } else if (response.statusCode() == 500) {
                        errorMsg += " - Server error. Is a model loaded?";
                    }
                    throw new RuntimeException(errorMsg);
                }

                // Response parsen
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                String aiText;

                if (root.has("choices")) {
                    // OpenAI-Format
                    JsonArray choices = root.getAsJsonArray("choices");
                    if (choices == null || choices.size() == 0) {
                        throw new RuntimeException("No choices in response!");
                    }
                    
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();
                    
                    if (firstChoice.has("message")) {
                        aiText = firstChoice.getAsJsonObject("message")
                                .get("content").getAsString();
                    } else if (firstChoice.has("text")) {
                        aiText = firstChoice.get("text").getAsString();
                    } else {
                        throw new RuntimeException("Choice has neither 'message' nor 'text'!");
                    }
                    
                } else if (root.has("results")) {
                    // text-generation-webui Format
                    JsonArray results = root.getAsJsonArray("results");
                    if (results == null || results.size() == 0) {
                        throw new RuntimeException("No results in response!");
                    }
                    aiText = results.get(0).getAsJsonObject()
                            .get("text").getAsString();
                            
                } else {
                    throw new RuntimeException("Invalid response format. Keys: " + root.keySet());
                }

                // Cleanup
                aiText = cleanupResponse(aiText);
                return aiText;

            } catch (Exception e) {
                plugin.getLogger().severe("API Error: " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }
    
    // Neue Helper-Methode für Stop-Sequences
    private void addStopSequencesToJson(JsonObject json, String key) {
        if (!stopSequences.isEmpty()) {
            JsonArray stopArray = new JsonArray();
            for (String stop : stopSequences) {
                stopArray.add(stop);
            }
            json.add(key, stopArray);
        }
    }
    
    private String cleanupResponse(String text) {
        text = text.trim();
        
        // Entferne Stop-Sequences falls sie im Text gelandet sind
        for (String stopSeq : stopSequences) {
            int index = text.indexOf(stopSeq);
            if (index > 0) {
                text = text.substring(0, index).trim();
                plugin.getLogger().info("Removed stop sequence at position " + index);
            }
        }
        
        // Schneide bei unvollständigem Satz ab
        if (shouldTruncate(text)) {
            text = truncateToLastSentence(text);
        }
        
        return text;
    }
    
    private boolean shouldTruncate(String text) {
        if (text.isEmpty()) return false;
        char lastChar = text.charAt(text.length() - 1);
        return !(lastChar == '.' || lastChar == '!' || lastChar == '?' || lastChar == '…');
    }
    
    private String truncateToLastSentence(String text) {
        int lastPeriod = Math.max(
            Math.max(text.lastIndexOf('.'), text.lastIndexOf('!')),
            Math.max(text.lastIndexOf('?'), text.lastIndexOf('…'))
        );
        
        if (lastPeriod > 0 && lastPeriod > text.length() / 2) {
            return text.substring(0, lastPeriod + 1).trim();
        }
        
        return text;
    }
}