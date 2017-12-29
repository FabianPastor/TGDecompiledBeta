package com.googlecode.mp4parser.util;

import android.util.Log;

public class AndroidLogger extends Logger {
    String name;

    public AndroidLogger(String name) {
        this.name = name;
    }

    public void logDebug(String message) {
        Log.d("isoparser", this.name + ":" + message);
    }

    public void logError(String message) {
        Log.e("isoparser", this.name + ":" + message);
    }
}
