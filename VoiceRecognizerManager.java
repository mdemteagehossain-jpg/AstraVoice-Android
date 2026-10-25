package com.astra.voiceassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognizerManager implements RecognitionListener {

    public interface VoiceListener {
        void onVoiceStart();
        void onVoiceResult(String spokenText);
        void onVoiceError(String errorMessage);
    }

    private final Context context;
    private final VoiceListener listener;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

    public VoiceRecognizerManager(Context context, VoiceListener listener) {
        this.context = context;
        this.listener = listener;
        initRecognizer();
    }

    private void initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(this);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            
            // Prefer offline recognition if available
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
    }

    public void startListening() {
        if (speechRecognizer != null && recognizerIntent != null) {
            speechRecognizer.startListening(recognizerIntent);
            if (listener != null) listener.onVoiceStart();
        } else {
            if (listener != null) listener.onVoiceError("Speech recognition not available on device");
        }
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO: message = "Audio recording error"; break;
            case SpeechRecognizer.ERROR_CLIENT: message = "Client error"; break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: message = "Insufficient permissions"; break;
            case SpeechRecognizer.ERROR_NETWORK: message = "Network error, switching offline"; break;
            case SpeechRecognizer.ERROR_NO_MATCH: message = "Didn't catch that, please retry"; break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: message = "Voice engine busy"; break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: message = "No speech detected"; break;
            default: message = "Recognition error: " + error; break;
        }
        if (listener != null) listener.onVoiceError(message);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            if (listener != null) listener.onVoiceResult(matches.get(0));
        } else {
            if (listener != null) listener.onVoiceError("No speech detected");
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {}

    @Override
    public void onEvent(int eventType, Bundle params) {}
}