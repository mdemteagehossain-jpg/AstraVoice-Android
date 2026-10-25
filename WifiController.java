package com.astra.voiceassistant;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

public class WifiController {

    private final Context context;
    private final WifiManager wifiManager;

    public WifiController(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public boolean setWifi(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ restricts programmatic WiFi toggling; use system settings panel
            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
            panelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(panelIntent);
            return true;
        } else {
            if (wifiManager != null) {
                try {
                    return wifiManager.setWifiEnabled(enable);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }
}