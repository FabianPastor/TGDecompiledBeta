package org.webrtc;

import android.content.Context;

public class ContextUtils {
    private static final String TAG = "ContextUtils";
    private static Context applicationContext;

    public static void initialize(Context applicationContext2) {
        if (applicationContext2 != null) {
            applicationContext = applicationContext2;
            return;
        }
        throw new IllegalArgumentException("Application context cannot be null for ContextUtils.initialize.");
    }

    @Deprecated
    public static Context getApplicationContext() {
        return applicationContext;
    }
}
