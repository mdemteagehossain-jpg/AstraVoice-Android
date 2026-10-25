package com.astra.voiceassistant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;

public class ActionExecutor {

    private final Context context;
    private final FlashlightController flashlightController;
    private final BluetoothController bluetoothController;
    private final WifiController wifiController;
    private final ContactHelper contactHelper;

    public ActionExecutor(Context context) {
        this.context = context;
        this.flashlightController = new FlashlightController(context);
        this.bluetoothController = new BluetoothController(context);
        this.wifiController = new WifiController(context);
        this.contactHelper = new ContactHelper(context);
    }

    public void openCamera(CommandProcessor.CommandCallback callback) {
        try {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            callback.onExecuted("Opening Camera...");
        } catch (Exception e) {
            callback.onExecuted("Unable to launch camera app.");
        }
    }

    public void openGallery(CommandProcessor.CommandCallback callback) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            callback.onExecuted("Opening Gallery...");
        } catch (Exception e) {
            callback.onExecuted("Unable to open photo gallery.");
        }
    }

    public void openApp(String packageName, String appName, CommandProcessor.CommandCallback callback) {
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
            callback.onExecuted("Opening " + appName + "...");
        } else {
            callback.onExecuted(appName + " is not installed on this device.");
        }
    }

    public void setFlashlight(boolean enable, CommandProcessor.CommandCallback callback) {
        boolean success = flashlightController.setTorchMode(enable);
        if (success) {
            callback.onExecuted(enable ? "Flashlight turned on." : "Flashlight turned off.");
        } else {
            callback.onExecuted("Flashlight is unavailable on this device.");
        }
    }

    public void setBluetooth(boolean enable, CommandProcessor.CommandCallback callback) {
        boolean success = bluetoothController.setBluetooth(enable);
        if (success) {
            callback.onExecuted(enable ? "Bluetooth enabled." : "Bluetooth disabled.");
        } else {
            callback.onExecuted("Unable to toggle Bluetooth. Opening Bluetooth settings...");
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void setWifi(boolean enable, CommandProcessor.CommandCallback callback) {
        boolean success = wifiController.setWifi(enable);
        if (success) {
            callback.onExecuted(enable ? "Wi-Fi turned on." : "Wi-Fi turned off.");
        } else {
            callback.onExecuted("Opening Wi-Fi settings for manual toggle.");
        }
    }

    public void adjustBrightness(boolean increase, CommandProcessor.CommandCallback callback) {
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onExecuted((increase ? "Increasing" : "Decreasing") + " brightness via display settings.");
    }

    public void setMute(boolean mute, CommandProcessor.CommandCallback callback) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (mute) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    callback.onExecuted("Phone is now muted.");
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    callback.onExecuted("Phone unmuted.");
                }
            }
        } catch (Exception e) {
            callback.onExecuted("Unable to modify ringer mode.");
        }
    }

    public void controlMusic(boolean play, CommandProcessor.CommandCallback callback) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                int keyCode = play ? KeyEvent.KEYCODE_MEDIA_PLAY : KeyEvent.KEYCODE_MEDIA_PAUSE;
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                audioManager.dispatchMediaKeyEvent(event);
                KeyEvent eventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                audioManager.dispatchMediaKeyEvent(eventUp);
                callback.onExecuted(play ? "Resuming music playback." : "Pausing music playback.");
            }
        } catch (Exception e) {
            callback.onExecuted("No active music player found.");
        }
    }

    public void makePhoneCall(String contactName, CommandProcessor.CommandCallback callback) {
        String phoneNumber = contactHelper.getPhoneNumberByName(contactName);
        if (phoneNumber != null) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
                callback.onExecuted("Calling " + contactName + " (" + phoneNumber + ")...");
            } catch (SecurityException e) {
                // Fallback to dialer if CALL_PHONE permission was denied
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialIntent);
                callback.onExecuted("Opening dialer for " + contactName);
            }
        } else {
            callback.onExecuted("Could not find contact '" + contactName + "'.");
        }
    }

    public void sendSms(String contactName, CommandProcessor.CommandCallback callback) {
        String phoneNumber = contactHelper.getPhoneNumberByName(contactName);
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            if (phoneNumber != null) {
                smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            } else {
                smsIntent.setData(Uri.parse("sms:"));
            }
            smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(smsIntent);
            callback.onExecuted("Composing message to " + contactName);
        } catch (Exception e) {
            callback.onExecuted("Unable to open messaging app.");
        }
    }

    public void openContacts(CommandProcessor.CommandCallback callback) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            callback.onExecuted("Opening Contacts...");
        } catch (Exception e) {
            callback.onExecuted("Unable to open contacts app.");
        }
    }
}