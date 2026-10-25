package com.astra.voiceassistant;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AstraApplication extends Application {

    public static final String CHANNEL_ID = "astra_voice_channel";
    private static AstraApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createNotificationChannel();
    }

    public static AstraApplication getInstance() {
        return instance;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "AstraVoice Assistant Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Continuous voice assistant listening in background");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}