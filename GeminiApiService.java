package com.astra.voiceassistant;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiService {

    private static final String GEMINI_MODEL = "gemini-2.5-flash"; // Compatible with 12-2025 release & 3.8
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";

    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;
    private String apiKey;

    public interface ApiCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }

    public GeminiApiService(String apiKey) {
        this.apiKey = apiKey;
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    public void generateChatResponse(String prompt, String systemPrompt, ApiCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            mainHandler.post(() -> callback.onError("Please configure your Gemini API Key in Settings"));
            return;
        }

        String requestUrl = BASE_URL + "?key=" + apiKey;

        // Build Gemini 2.5 payload
        JsonObject payload = new JsonObject();

        // System Instruction
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JsonObject systemInstruction = new JsonObject();
            JsonArray systemParts = new JsonArray();
            JsonObject partObj = new JsonObject();
            partObj.addProperty("text", systemPrompt);
            systemParts.add(partObj);
            systemInstruction.add("parts", systemParts);
            payload.add("systemInstruction", systemInstruction);
        }

        // Contents
        JsonArray contents = new JsonArray();
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        JsonArray parts = new JsonArray();
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);
        parts.add(textPart);
        userContent.add("parts", parts);
        contents.add(userContent);
        payload.add("contents", contents);

        // Generation Config
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.75);
        genConfig.addProperty("maxOutputTokens", 500);
        payload.add("generationConfig", genConfig);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(payload.toString(), JSON);

        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("User-Agent", "AstraVoice-Android/1.0")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Network failure: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    mainHandler.post(() -> callback.onError("API Error (" + response.code() + "): " + errorBody));
                    return;
                }

                String jsonResponse = response.body() != null ? response.body().string() : "";
                try {
                    JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray candidates = root.getAsJsonArray("candidates");
                    if (candidates != null && candidates.size() > 0) {
                        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = firstCandidate.getAsJsonObject("content");
                        JsonArray responseParts = content.getAsJsonArray("parts");
                        if (responseParts != null && responseParts.size() > 0) {
                            String replyText = responseParts.get(0).getAsJsonObject().get("text").getAsString();
                            mainHandler.post(() -> callback.onSuccess(replyText.trim()));
                            return;
                        }
                    }
                    mainHandler.post(() -> callback.onError("Empty response from AI model"));
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("JSON Parsing error: " + e.getMessage()));
                }
            }
        });
    }
}