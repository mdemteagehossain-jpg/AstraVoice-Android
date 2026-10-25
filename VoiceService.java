package com.astra.voiceassistant;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class VoiceService extends Service {

    public static final String ACTION_STOP = "com.astra.voiceassistant.STOP_SERVICE";
    private static final int NOTIFICATION_ID = 2001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, AstraApplication.CHANNEL_ID)
                .setContentTitle("AstraVoice Wake Word Active")
                .setContentText("Listening for 'Hey Astra' or 'Hey Nova' in background")
                .setSmallIcon(R.drawable.ic_mic)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}