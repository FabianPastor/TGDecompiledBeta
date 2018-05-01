package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

public class BuildVars
{
  public static String APP_HASH;
  public static int APP_ID;
  public static String BING_SEARCH_KEY;
  public static int BUILD_VERSION;
  public static String BUILD_VERSION_STRING;
  public static boolean DEBUG_PRIVATE_VERSION;
  public static boolean DEBUG_VERSION = true;
  public static String FOURSQUARE_API_ID;
  public static String FOURSQUARE_API_KEY;
  public static String FOURSQUARE_API_VERSION;
  public static String GOOGLE_API_KEY;
  public static String HOCKEY_APP_HASH;
  public static String HOCKEY_APP_HASH_DEBUG;
  public static boolean LOGS_ENABLED;
  
  static
  {
    DEBUG_PRIVATE_VERSION = false;
    LOGS_ENABLED = false;
    BUILD_VERSION = 1250;
    BUILD_VERSION_STRING = "4.8.5";
    APP_ID = 6;
    APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e";
    HOCKEY_APP_HASH = "a5b5c4f551dadedc9918d9766a22ca7c";
    HOCKEY_APP_HASH_DEBUG = "f972660267c948d2b5d04761f1c1a8f3";
    BING_SEARCH_KEY = "300f7735cfd04393a38d7838a0bf246b";
    FOURSQUARE_API_KEY = "KNMWIDXUJGWOHEBRHPLX5VVNGU0HQOLQGTD4GG53ST5JOUBD";
    FOURSQUARE_API_ID = "WQFF4AJFCITFSTBAU2H2UPLCBNPP3TMAZH5XLR1AHZ0AN3EH";
    GOOGLE_API_KEY = "AIzaSyA-t0jLPjUt2FxrA8VPK2EiYHcYcboIR6k";
    FOURSQUARE_API_VERSION = "20150326";
    if (ApplicationLoader.applicationContext != null) {
      LOGS_ENABLED = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).getBoolean("logsEnabled", DEBUG_VERSION);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/BuildVars.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */