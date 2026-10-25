package com.astra.voiceassistant;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

public class TTSManager implements TextToSpeech.OnInitListener {

    private final Context context;
    private final PersonalityManager personalityManager;
    private TextToSpeech tts;
    private boolean isInitialized = false;

    public TTSManager(Context context, PersonalityManager personalityManager) {
        this.context = context;
        this.personalityManager = personalityManager;
        this.tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true;
                updateVoiceSettings();
            }
        }
    }

    public void updateVoiceSettings() {
        if (!isInitialized || tts == null) return;

        int mode = personalityManager.getPersonalityMode();
        switch (mode) {
            case PersonalityManager.MODE_GIRLFRIEND:
                // Soft, feminine, higher pitch, warm tempo
                tts.setPitch(1.35f);
                tts.setSpeechRate(0.98f);
                break;
            case PersonalityManager.MODE_BOYFRIEND:
                // Deep, protective, confident lower pitch
                tts.setPitch(0.82f);
                tts.setSpeechRate(0.96f);
                break;
            case PersonalityManager.MODE_ASSISTANT:
            default:
                // Balanced, articulate, professional
                tts.setPitch(1.05f);
                tts.setSpeechRate(1.02f);
                break;
        }
    }

    public void speak(String text) {
        if (isInitialized && tts != null && text != null && !text.isEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AstraUtteranceID");
        }
    }

    public void stop() {
        if (isInitialized && tts != null) {
            tts.stop();
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}