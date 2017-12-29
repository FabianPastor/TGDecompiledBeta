package net.hockeyapp.android;

import android.content.Context;
import android.content.SharedPreferences;

public class Tracking {
    public static long getUsageTime(Context context) {
        if (!checkVersion(context)) {
            return 0;
        }
        SharedPreferences preferences = getPreferences(context);
        long sum = preferences.getLong("usageTime" + Constants.APP_VERSION, 0);
        if (sum >= 0) {
            return sum / 1000;
        }
        preferences.edit().remove("usageTime" + Constants.APP_VERSION).apply();
        return 0;
    }

    private static boolean checkVersion(Context context) {
        if (Constants.APP_VERSION == null) {
            Constants.loadFromContext(context);
            if (Constants.APP_VERSION == null) {
                return false;
            }
        }
        return true;
    }

    protected static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("HockeyApp", 0);
    }
}
