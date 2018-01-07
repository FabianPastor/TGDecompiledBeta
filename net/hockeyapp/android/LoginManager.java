package net.hockeyapp.android;

import android.os.Handler;

public class LoginManager {
    private static String identifier = null;
    static LoginManagerListener listener;
    private static String secret = null;
    private static String urlString = null;
    private static Handler validateHandler = null;
}
