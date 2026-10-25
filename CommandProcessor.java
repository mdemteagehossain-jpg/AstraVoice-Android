package com.astra.voiceassistant;

import android.content.Context;
import java.util.Locale;

public class CommandProcessor {

    public interface CommandCallback {
        void onExecuted(String responseMessage);
    }

    private final Context context;
    private final ActionExecutor actionExecutor;
    private final TTSManager ttsManager;

    public CommandProcessor(Context context, TTSManager ttsManager) {
        this.context = context;
        this.ttsManager = ttsManager;
        this.actionExecutor = new ActionExecutor(context);
    }

    public boolean isDeviceCommand(String rawQuery) {
        if (rawQuery == null) return false;
        String query = rawQuery.toLowerCase(Locale.getDefault()).trim();

        return query.contains("open camera") ||
                query.contains("open gallery") ||
                query.contains("open whatsapp") ||
                query.contains("open youtube") ||
                query.contains("open instagram") ||
                query.contains("flashlight") ||
                query.contains("torch") ||
                query.contains("bluetooth") ||
                query.contains("wifi") || query.contains("wi-fi") ||
                query.contains("brightness") ||
                query.contains("mute") || query.contains("unmute") ||
                query.contains("play music") || query.contains("stop music") ||
                query.startsWith("call ") || query.startsWith("dial ") ||
                query.startsWith("message ") || query.startsWith("text ") ||
                query.contains("open contacts");
    }

    public void executeCommand(String rawQuery, CommandCallback callback) {
        String query = rawQuery.toLowerCase(Locale.getDefault()).trim();

        // 1. App Launchers
        if (query.contains("open camera")) {
            actionExecutor.openCamera(callback);
        } else if (query.contains("open gallery")) {
            actionExecutor.openGallery(callback);
        } else if (query.contains("open whatsapp")) {
            actionExecutor.openApp("com.whatsapp", "WhatsApp", callback);
        } else if (query.contains("open youtube")) {
            actionExecutor.openApp("com.google.android.youtube", "YouTube", callback);
        } else if (query.contains("open instagram")) {
            actionExecutor.openApp("com.instagram.android", "Instagram", callback);
        }
        // 2. Hardware Controls
        else if (query.contains("turn on flashlight") || query.contains("flashlight on") || query.contains("torch on")) {
            actionExecutor.setFlashlight(true, callback);
        } else if (query.contains("turn off flashlight") || query.contains("flashlight off") || query.contains("torch off")) {
            actionExecutor.setFlashlight(false, callback);
        } else if (query.contains("turn on bluetooth") || query.contains("bluetooth on")) {
            actionExecutor.setBluetooth(true, callback);
        } else if (query.contains("turn off bluetooth") || query.contains("bluetooth off")) {
            actionExecutor.setBluetooth(false, callback);
        } else if (query.contains("turn on wifi") || query.contains("wifi on")) {
            actionExecutor.setWifi(true, callback);
        } else if (query.contains("turn off wifi") || query.contains("wifi off")) {
            actionExecutor.setWifi(false, callback);
        }
        // 3. Audio & Brightness
        else if (query.contains("increase brightness")) {
            actionExecutor.adjustBrightness(true, callback);
        } else if (query.contains("decrease brightness")) {
            actionExecutor.adjustBrightness(false, callback);
        } else if (query.contains("mute phone") || query.contains("mute volume")) {
            actionExecutor.setMute(true, callback);
        } else if (query.contains("play music")) {
            actionExecutor.controlMusic(true, callback);
        } else if (query.contains("stop music") || query.contains("pause music")) {
            actionExecutor.controlMusic(false, callback);
        }
        // 4. Contacts & Calling
        else if (query.startsWith("call ") || query.startsWith("dial ")) {
            String contactName = query.replace("call ", "").replace("dial ", "").trim();
            actionExecutor.makePhoneCall(contactName, callback);
        } else if (query.startsWith("message ") || query.startsWith("text ")) {
            String contactName = query.replace("message ", "").replace("text ", "").trim();
            actionExecutor.sendSms(contactName, callback);
        } else if (query.contains("open contacts")) {
            actionExecutor.openContacts(callback);
        } else {
            callback.onExecuted("Command unrecognized in local mode.");
        }
    }
}