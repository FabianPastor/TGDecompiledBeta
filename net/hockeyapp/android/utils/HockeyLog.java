package net.hockeyapp.android.utils;

import android.util.Log;

public class HockeyLog {
    private static int sLogLevel = 6;

    public static void verbose(String tag, String message) {
        tag = sanitizeTag(tag);
        if (sLogLevel <= 2) {
            Log.v(tag, message);
        }
    }

    public static void debug(String message) {
        debug(null, message);
    }

    public static void debug(String tag, String message) {
        tag = sanitizeTag(tag);
        if (sLogLevel <= 3) {
            Log.d(tag, message);
        }
    }

    public static void warn(String message) {
        warn(null, message);
    }

    public static void warn(String tag, String message) {
        tag = sanitizeTag(tag);
        if (sLogLevel <= 5) {
            Log.w(tag, message);
        }
    }

    public static void error(String message) {
        error(null, message);
    }

    public static void error(String tag, String message) {
        tag = sanitizeTag(tag);
        if (sLogLevel <= 6) {
            Log.e(tag, message);
        }
    }

    public static void error(String message, Throwable throwable) {
        error(null, message, throwable);
    }

    public static void error(String tag, String message, Throwable throwable) {
        tag = sanitizeTag(tag);
        if (sLogLevel <= 6) {
            Log.e(tag, message, throwable);
        }
    }

    static String sanitizeTag(String tag) {
        if (tag == null || tag.length() == 0 || tag.length() > 23) {
            return "HockeyApp";
        }
        return tag;
    }
}
