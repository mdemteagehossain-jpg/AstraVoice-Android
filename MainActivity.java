package com.astra.voiceassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements VoiceRecognizerManager.VoiceListener {

    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;

    private FloatingActionButton btnMic;
    private ImageView imgAvatarPulse;
    private TextView tvStatus;
    private TextView tvCurrentMode;
    private ProgressBar progressBarListening;

    private VoiceRecognizerManager voiceManager;
    private TTSManager ttsManager;
    private PersonalityManager personalityManager;
    private CommandProcessor commandProcessor;
    private AIChatManager aiChatManager;
    private PermissionManager permissionManager;

    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupManagers();
        setupRecyclerView();
        setupClickListeners();

        // Greet user based on personality
        String greeting = personalityManager.getGreeting();
        addAssistantMessage(greeting);
        ttsManager.speak(greeting);
    }

    private void initializeViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        btnMic = findViewById(R.id.btnMic);
        imgAvatarPulse = findViewById(R.id.imgAvatarPulse);
        tvStatus = findViewById(R.id.tvStatus);
        tvCurrentMode = findViewById(R.id.tvCurrentMode);
        progressBarListening = findViewById(R.id.progressBarListening);
    }

    private void setupManagers() {
        personalityManager = new PersonalityManager(this);
        ttsManager = new TTSManager(this, personalityManager);
        permissionManager = new PermissionManager(this);
        commandProcessor = new CommandProcessor(this, ttsManager);
        aiChatManager = new AIChatManager(this, personalityManager);
        voiceManager = new VoiceRecognizerManager(this, this);

        updateModeUI();
    }

    private void setupRecyclerView() {
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnMic.setOnClickListener(v -> toggleListening());

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnModeSelector).setOnClickListener(v -> showModesBottomSheet());

        findViewById(R.id.btnSuggestionCall).setOnClickListener(v -> handleVoiceInput("Call Rahul"));
        findViewById(R.id.btnSuggestionTorch).setOnClickListener(v -> handleVoiceInput("Turn on flashlight"));
        findViewById(R.id.btnSuggestionChat).setOnClickListener(v -> handleVoiceInput("How are you feeling today?"));
    }

    private void toggleListening() {
        if (!permissionManager.checkAudioPermission()) {
            permissionManager.requestAudioPermission(this);
            return;
        }

        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        isListening = true;
        tvStatus.setText("Listening... speak now");
        progressBarListening.setVisibility(View.VISIBLE);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_anim);
        imgAvatarPulse.startAnimation(pulse);
        voiceManager.startListening();
    }

    private void stopListening() {
        isListening = false;
        tvStatus.setText("Tap mic to speak");
        progressBarListening.setVisibility(View.GONE);
        imgAvatarPulse.clearAnimation();
        voiceManager.stopListening();
    }

    @Override
    public void onVoiceStart() {
        tvStatus.setText("Recognizing voice...");
    }

    @Override
    public void onVoiceResult(String spokenText) {
        stopListening();
        if (spokenText != null && !spokenText.trim().isEmpty()) {
            handleVoiceInput(spokenText);
        }
    }

    @Override
    public void onVoiceError(String errorMessage) {
        stopListening();
        tvStatus.setText("Ready");
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void handleVoiceInput(String text) {
        addUserMessage(text);

        // 1. First check if it is a local hardware/phone command
        if (commandProcessor.isDeviceCommand(text)) {
            commandProcessor.executeCommand(text, resultMessage -> {
                runOnUiThread(() -> {
                    addAssistantMessage(resultMessage);
                    ttsManager.speak(resultMessage);
                });
            });
            return;
        }

        // 2. Otherwise send to Gemini AI Conversational mode
        tvStatus.setText("Astra is thinking...");
        aiChatManager.sendMessage(text, new AIChatManager.ChatCallback() {
            @Override
            public void onResponse(String reply) {
                runOnUiThread(() -> {
                    tvStatus.setText("Ready");
                    addAssistantMessage(reply);
                    ttsManager.speak(reply);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvStatus.setText("Ready");
                    String fallback = "I'm offline right now, but I can still execute your device commands!";
                    addAssistantMessage(fallback);
                    ttsManager.speak(fallback);
                });
            }
        });
    }

    private void addUserMessage(String message) {
        chatList.add(new ChatMessage(message, ChatMessage.TYPE_USER));
        chatAdapter.notifyItemInserted(chatList.size() - 1);
        recyclerViewChat.smoothScrollToPosition(chatList.size() - 1);
    }

    private void addAssistantMessage(String message) {
        chatList.add(new ChatMessage(message, ChatMessage.TYPE_ASSISTANT));
        chatAdapter.notifyItemInserted(chatList.size() - 1);
        recyclerViewChat.smoothScrollToPosition(chatList.size() - 1);
    }

    private void showModesBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_modes, null);

        view.findViewById(R.id.cardGirlfriendMode).setOnClickListener(v -> {
            personalityManager.setPersonality(PersonalityManager.MODE_GIRLFRIEND);
            updateModeUI();
            dialog.dismiss();
            ttsManager.updateVoiceSettings();
            addAssistantMessage("Girlfriend mode active ❤️! Sweetheart, I'm all yours.");
            ttsManager.speak("Girlfriend mode active! Sweetheart, I'm all yours.");
        });

        view.findViewById(R.id.cardBoyfriendMode).setOnClickListener(v -> {
            personalityManager.setPersonality(PersonalityManager.MODE_BOYFRIEND);
            updateModeUI();
            dialog.dismiss();
            ttsManager.updateVoiceSettings();
            addAssistantMessage("Boyfriend mode active 💙! Hey, I've got your back no matter what.");
            ttsManager.speak("Boyfriend mode active! Hey, I've got your back no matter what.");
        });

        view.findViewById(R.id.cardAssistantMode).setOnClickListener(v -> {
            personalityManager.setPersonality(PersonalityManager.MODE_ASSISTANT);
            updateModeUI();
            dialog.dismiss();
            ttsManager.updateVoiceSettings();
            addAssistantMessage("Assistant mode active 🤖. Ready for tasks and automation.");
            ttsManager.speak("Assistant mode active. Ready for tasks and automation.");
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void updateModeUI() {
        String modeName = personalityManager.getPersonalityDisplayName();
        tvCurrentMode.setText(modeName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateModeUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceManager != null) voiceManager.destroy();
        if (ttsManager != null) ttsManager.shutdown();
    }
}