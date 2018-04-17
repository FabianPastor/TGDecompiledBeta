package org.telegram.messenger;

public class BuildVars {
    public static String APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e";
    public static int APP_ID = 6;
    public static String BING_SEARCH_KEY = "300f7735cfd04393a38d7838a0bf246b";
    public static int BUILD_VERSION = BuildConfig.VERSION_CODE;
    public static String BUILD_VERSION_STRING = "4.8.5";
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String FOURSQUARE_API_ID = "WQFF4AJFCITFSTBAU2H2UPLCBNPP3TMAZH5XLR1AHZ0AN3EH";
    public static String FOURSQUARE_API_KEY = "KNMWIDXUJGWOHEBRHPLX5VVNGU0HQOLQGTD4GG53ST5JOUBD";
    public static String FOURSQUARE_API_VERSION = "20150326";
    public static String GOOGLE_API_KEY = "AIzaSyA-t0jLPjUt2FxrA8VPK2EiYHcYcboIR6k";
    public static String HOCKEY_APP_HASH = "a5b5c4f551dadedc9918d9766a22ca7c";
    public static String HOCKEY_APP_HASH_DEBUG = "f972660267c948d2b5d04761f1c1a8f3";
    public static boolean LOGS_ENABLED;

    static {
        LOGS_ENABLED = false;
        if (ApplicationLoader.applicationContext != null) {
            LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }
}
