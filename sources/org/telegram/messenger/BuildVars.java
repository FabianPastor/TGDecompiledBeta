package org.telegram.messenger;

import android.content.SharedPreferences;

public class BuildVars {
    public static String APPCENTER_HASH = "a5b5c4f5-51da-dedc-9918-d9766a22ca7c";
    public static String APPCENTER_HASH_DEBUG = "var_-67c9-48d2-b5d0-4761f1c1a8f3";
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
    public static int APP_ID = 4;
    public static int BUILD_VERSION = 2381;
    public static String BUILD_VERSION_STRING = "7.9.0";
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static boolean LOGS_ENABLED = true;
    public static boolean NO_SCOPED_STORAGE = true;
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    public static String SMS_HASH = (AndroidUtilities.isStandaloneApp() ? "w0lkcmTZkKh" : DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static boolean USE_CLOUD_STRINGS = true;

    static {
        if (ApplicationLoader.applicationContext != null) {
            boolean z = false;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
            boolean z2 = DEBUG_VERSION;
            if (z2 || sharedPreferences.getBoolean("logsEnabled", z2)) {
                z = true;
            }
            LOGS_ENABLED = z;
        }
    }
}
