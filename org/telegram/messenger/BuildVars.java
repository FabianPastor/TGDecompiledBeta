package org.telegram.messenger;

public class BuildVars {
    public static String APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e";
    public static int APP_ID = 6;
    public static String BING_SEARCH_KEY = "300f7735cfd04393a38d7838a0bf246b";
    public static int BUILD_VERSION = 1184;
    public static String BUILD_VERSION_STRING = "4.7";
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String FOURSQUARE_API_ID = "WQFF4AJFCITFSTBAU2H2UPLCBNPP3TMAZH5XLR1AHZ0AN3EH";
    public static String FOURSQUARE_API_KEY = "KNMWIDXUJGWOHEBRHPLX5VVNGU0HQOLQGTD4GG53ST5JOUBD";
    public static String FOURSQUARE_API_VERSION = "20150326";
    public static String GOOGLE_API_KEY = "AIzaSyA-t0jLPjUt2FxrA8VPK2EiYHcYcboIR6k";
    public static String HOCKEY_APP_HASH = "a5b5c4f551dadedc9918d9766a22ca7c";
    public static String HOCKEY_APP_HASH_DEBUG = "dc3b3c6317af4a3caa5269a58697e088";
    public static boolean LOGS_ENABLED;

    static {
        LOGS_ENABLED = false;
        LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
    }
}
