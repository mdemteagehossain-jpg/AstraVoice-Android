package com.astra.voiceassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private EditText etApiKey;
    private RadioGroup rgPersonality;
    private SwitchCompat switchWakeWord;
    private SwitchCompat switchOfflineOnly;
    private Button btnSave;

    private PersonalityManager personalityManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        personalityManager = new PersonalityManager(this);
        prefs = getSharedPreferences("astra_settings", Context.MODE_PRIVATE);

        etApiKey = findViewById(R.id.etApiKey);
        rgPersonality = findViewById(R.id.rgPersonality);
        switchWakeWord = findViewById(R.id.switchWakeWord);
        switchOfflineOnly = findViewById(R.id.switchOfflineOnly);
        btnSave = findViewById(R.id.btnSave);

        // Load existing settings
        String savedKey = prefs.getString("gemini_api_key", BuildConfig.GEMINI_API_KEY);
        if (!"YOUR_GEMINI_API_KEY_HERE".equals(savedKey)) {
            etApiKey.setText(savedKey);
        }

        switchWakeWord.setChecked(prefs.getBoolean("wake_word_enabled", true));
        switchOfflineOnly.setChecked(prefs.getBoolean("offline_only", false));

        int mode = personalityManager.getPersonalityMode();
        if (mode == PersonalityManager.MODE_GIRLFRIEND) {
            rgPersonality.check(R.id.rbGirlfriend);
        } else if (mode == PersonalityManager.MODE_BOYFRIEND) {
            rgPersonality.check(R.id.rbBoyfriend);
        } else {
            rgPersonality.check(R.id.rbAssistant);
        }

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String apiKey = etApiKey.getText().toString().trim();
        boolean wakeWord = switchWakeWord.isChecked();
        boolean offlineOnly = switchOfflineOnly.isChecked();

        prefs.edit()
                .putString("gemini_api_key", apiKey)
                .putBoolean("wake_word_enabled", wakeWord)
                .putBoolean("offline_only", offlineOnly)
                .apply();

        int checkedId = rgPersonality.getCheckedRadioButtonId();
        if (checkedId == R.id.rbGirlfriend) {
            personalityManager.setPersonality(PersonalityManager.MODE_GIRLFRIEND);
        } else if (checkedId == R.id.rbBoyfriend) {
            personalityManager.setPersonality(PersonalityManager.MODE_BOYFRIEND);
        } else {
            personalityManager.setPersonality(PersonalityManager.MODE_ASSISTANT);
        }

        Toast.makeText(this, "Settings Saved Successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}