package com.astra.voiceassistant;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothController {

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;

    public BluetoothController(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean setBluetooth(boolean enable) {
        if (bluetoothAdapter == null) {
            return false;
        }

        try {
            if (enable) {
                return bluetoothAdapter.enable();
            } else {
                return bluetoothAdapter.disable();
            }
        } catch (SecurityException e) {
            return false;
        }
    }

    public boolean isEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
}