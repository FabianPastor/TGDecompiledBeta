package org.telegram.messenger;

public class BuildVars {
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
    public static int APP_ID = 4;
    public static int BUILD_VERSION = 1822;
    public static String BUILD_VERSION_STRING = "5.13.0";
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String HOCKEY_APP_HASH = "a5b5c4var_dadedCLASSNAMEd9766a22ca7c";
    public static String HOCKEY_APP_HASH_DEBUG = "var_CLASSNAMEd2b5d04761f1c1a8f3";
    public static boolean LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    public static String SMS_HASH = (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static boolean TON_WALLET_STANDALONE = false;
    public static boolean USE_CLOUD_STRINGS = true;

    static {
        if (ApplicationLoader.applicationContext != null) {
        }
    }
}
