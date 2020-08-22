package com.example.reband;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class Headset extends Application {
    private static Headset sInstance = null;

    public static Headset getInstance() {
        return sInstance;
    }

    BluetoothSocket btSocket = null;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public void setupBluetoothConnection(BluetoothSocket bttSocket) {
        btSocket = bttSocket;
    }

    public BluetoothSocket getCurrentBluetoothConnection() {
        return btSocket;
    }
}
