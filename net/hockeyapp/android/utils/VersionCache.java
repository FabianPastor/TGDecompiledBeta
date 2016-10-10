package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class VersionCache
{
  private static String PREF_VERSION_INFO_KEY = "versionInfo";
  
  public static String getVersionInfo(Context paramContext)
  {
    if (paramContext != null) {
      return paramContext.getSharedPreferences("HockeyApp", 0).getString(PREF_VERSION_INFO_KEY, "[]");
    }
    return "[]";
  }
  
  public static void setVersionInfo(Context paramContext, String paramString)
  {
    if (paramContext != null)
    {
      paramContext = paramContext.getSharedPreferences("HockeyApp", 0).edit();
      paramContext.putString(PREF_VERSION_INFO_KEY, paramString);
      paramContext.apply();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/VersionCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */