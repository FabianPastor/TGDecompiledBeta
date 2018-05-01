package org.telegram.messenger.voip;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class VoIPServerConfig
{
  private static JSONObject config;
  
  public static boolean getBoolean(String paramString, boolean paramBoolean)
  {
    return config.optBoolean(paramString, paramBoolean);
  }
  
  public static double getDouble(String paramString, double paramDouble)
  {
    return config.optDouble(paramString, paramDouble);
  }
  
  public static int getInt(String paramString, int paramInt)
  {
    return config.optInt(paramString, paramInt);
  }
  
  public static String getString(String paramString1, String paramString2)
  {
    return config.optString(paramString1, paramString2);
  }
  
  private static native void nativeSetConfig(String[] paramArrayOfString1, String[] paramArrayOfString2);
  
  public static void setConfig(String paramString)
  {
    try
    {
      JSONObject localJSONObject = new org/json/JSONObject;
      localJSONObject.<init>(paramString);
      config = localJSONObject;
      String[] arrayOfString = new String[localJSONObject.length()];
      paramString = new String[localJSONObject.length()];
      Iterator localIterator = localJSONObject.keys();
      for (int i = 0; localIterator.hasNext(); i++)
      {
        arrayOfString[i] = ((String)localIterator.next());
        paramString[i] = localJSONObject.getString(arrayOfString[i]);
      }
      nativeSetConfig(arrayOfString, paramString);
      return;
    }
    catch (JSONException paramString)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("Error parsing VoIP config", paramString);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/VoIPServerConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */