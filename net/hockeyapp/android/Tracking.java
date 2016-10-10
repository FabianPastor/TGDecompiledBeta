package net.hockeyapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Tracking
{
  protected static final String START_TIME_KEY = "startTime";
  protected static final String USAGE_TIME_KEY = "usageTime";
  
  private static boolean checkVersion(Context paramContext)
  {
    if (Constants.APP_VERSION == null)
    {
      Constants.loadFromContext(paramContext);
      if (Constants.APP_VERSION == null) {
        return false;
      }
    }
    return true;
  }
  
  protected static SharedPreferences getPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences("HockeyApp", 0);
  }
  
  public static long getUsageTime(Context paramContext)
  {
    if (!checkVersion(paramContext)) {
      return 0L;
    }
    paramContext = getPreferences(paramContext);
    long l = paramContext.getLong("usageTime" + Constants.APP_VERSION, 0L);
    if (l < 0L)
    {
      paramContext.edit().remove("usageTime" + Constants.APP_VERSION).apply();
      return 0L;
    }
    return l / 1000L;
  }
  
  public static void startUsage(Activity paramActivity)
  {
    long l = System.currentTimeMillis();
    if (paramActivity == null) {
      return;
    }
    SharedPreferences.Editor localEditor = getPreferences(paramActivity).edit();
    localEditor.putLong("startTime" + paramActivity.hashCode(), l);
    localEditor.apply();
  }
  
  public static void stopUsage(Activity paramActivity)
  {
    long l2 = System.currentTimeMillis();
    if (paramActivity == null) {}
    SharedPreferences localSharedPreferences;
    long l1;
    do
    {
      long l3;
      do
      {
        do
        {
          return;
        } while (!checkVersion(paramActivity));
        localSharedPreferences = getPreferences(paramActivity);
        l3 = localSharedPreferences.getLong("startTime" + paramActivity.hashCode(), 0L);
        l1 = localSharedPreferences.getLong("usageTime" + Constants.APP_VERSION, 0L);
      } while (l3 <= 0L);
      l2 -= l3;
      l1 += l2;
    } while ((l2 <= 0L) || (l1 < 0L));
    paramActivity = localSharedPreferences.edit();
    paramActivity.putLong("usageTime" + Constants.APP_VERSION, l1);
    paramActivity.apply();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/Tracking.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */