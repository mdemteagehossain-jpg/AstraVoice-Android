package com.astra.voiceassistant;

import android.content.Context;
import android.content.SharedPreferences;

public class AIChatManager {

    public interface ChatCallback {
        void onResponse(String reply);
        void onError(String error);
    }

    private final Context context;
    private final PersonalityManager personalityManager;
    private final GeminiApiService apiService;

    public AIChatManager(Context context, PersonalityManager personalityManager) {
        this.context = context;
        this.personalityManager = personalityManager;

        SharedPreferences prefs = context.getSharedPreferences("astra_settings", Context.MODE_PRIVATE);
        String apiKey = prefs.getString("gemini_api_key", BuildConfig.GEMINI_API_KEY);
        this.apiService = new GeminiApiService(apiKey);
    }

    public void updateApiKey(String newKey) {
        apiService.setApiKey(newKey);
    }

    public void sendMessage(String prompt, ChatCallback callback) {
        String systemPrompt = personalityManager.getSystemPrompt();
        apiService.generateChatResponse(prompt, systemPrompt, new GeminiApiService.ApiCallback() {
            @Override
            public void onSuccess(String responseText) {
                callback.onResponse(responseText);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}