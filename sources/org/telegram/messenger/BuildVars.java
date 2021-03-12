package org.telegram.messenger;

public class BuildVars {
    public static String APPCENTER_HASH = "a5b5c4f5-51da-dedc-9918-d9766a22ca7c";
    public static String APPCENTER_HASH_DEBUG = "fb258b90-CLASSNAMEa-4b33-ab15-3d142ab6d137";
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
    public static int APP_ID = 4;
    public static int BUILD_VERSION = 2247;
    public static String BUILD_VERSION_STRING = "7.5.0";
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = true;
    public static boolean DEBUG_VERSION = true;
    public static boolean LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    public static String SMS_HASH = "O2P2z+/jBpJ";
    public static boolean USE_CLOUD_STRINGS = true;

    static {
        if (ApplicationLoader.applicationContext != null) {
        }
    }
}
