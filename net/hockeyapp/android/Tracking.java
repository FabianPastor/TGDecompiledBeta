package net.hockeyapp.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Tracking
{
  private static boolean checkVersion(Context paramContext)
  {
    if (Constants.APP_VERSION == null)
    {
      Constants.loadFromContext(paramContext);
      if (Constants.APP_VERSION != null) {}
    }
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  protected static SharedPreferences getPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences("HockeyApp", 0);
  }
  
  public static long getUsageTime(Context paramContext)
  {
    long l1 = 0L;
    if (!checkVersion(paramContext)) {}
    for (;;)
    {
      return l1;
      paramContext = getPreferences(paramContext);
      long l2 = paramContext.getLong("usageTime" + Constants.APP_VERSION, 0L);
      if (l2 < 0L) {
        paramContext.edit().remove("usageTime" + Constants.APP_VERSION).apply();
      } else {
        l1 = l2 / 1000L;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/Tracking.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */