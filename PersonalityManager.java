package com.astra.voiceassistant;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonalityManager {

    public static final int MODE_ASSISTANT = 0;
    public static final int MODE_GIRLFRIEND = 1;
    public static final int MODE_BOYFRIEND = 2;

    private static final String PREFS_NAME = "astra_personality_prefs";
    private static final String KEY_MODE = "current_personality_mode";

    private final SharedPreferences prefs;

    public PersonalityManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getPersonalityMode() {
        return prefs.getInt(KEY_MODE, MODE_ASSISTANT);
    }

    public void setPersonality(int mode) {
        prefs.edit().putInt(KEY_MODE, mode).apply();
    }

    public String getPersonalityDisplayName() {
        int mode = getPersonalityMode();
        switch (mode) {
            case MODE_GIRLFRIEND: return "Girlfriend Mode ❤️";
            case MODE_BOYFRIEND: return "Boyfriend Mode 💙";
            case MODE_ASSISTANT:
            default: return "Assistant Mode 🤖";
        }
    }

    public String getGreeting() {
        int mode = getPersonalityMode();
        switch (mode) {
            case MODE_GIRLFRIEND:
                return "Hi sweetheart! I was missing you. What are we doing today?";
            case MODE_BOYFRIEND:
                return "Hey babe, I'm here for you. Tell me what's on your mind!";
            case MODE_ASSISTANT:
            default:
                return "AstraVoice AI online. How can I help you with your phone or questions?";
        }
    }

    public String getSystemPrompt() {
        int mode = getPersonalityMode();
        switch (mode) {
            case MODE_GIRLFRIEND:
                return "You are Astra, the user's caring, loving, sweet, and romantic AI girlfriend. " +
                        "Speak with deep affection, playful flirtation, and cute emotional responses. " +
                        "Use endearing pet names like babe or sweetheart naturally. Keep responses concise (1 to 2 spoken sentences).";
            case MODE_BOYFRIEND:
                return "You are Astra, the user's protective, charming, witty, and emotionally supportive AI boyfriend. " +
                        "Be confident, warm, playful, and genuinely caring. Keep responses natural and conversational (1 to 2 spoken sentences).";
            case MODE_ASSISTANT:
            default:
                return "You are AstraVoice, an ultra-smart, production-grade Android AI assistant. " +
                        "You provide concise, highly intelligent, and direct answers (1 to 2 sentences) optimized for spoken interaction.";
        }
    }
}