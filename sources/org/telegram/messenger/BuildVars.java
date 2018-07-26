package org.telegram.messenger;

public class BuildVars {
    public static String APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e";
    public static int APP_ID = 6;
    public static int BUILD_VERSION = BuildConfig.VERSION_CODE;
    public static String BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String HOCKEY_APP_HASH = "a5b5c4f551dadedc9918d9766a22ca7c";
    public static String HOCKEY_APP_HASH_DEBUG = "f972660267c948d2b5d04761f1c1a8f3";
    public static boolean LOGS_ENABLED;
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";

    static {
        LOGS_ENABLED = false;
        if (ApplicationLoader.applicationContext != null) {
            LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }
}
