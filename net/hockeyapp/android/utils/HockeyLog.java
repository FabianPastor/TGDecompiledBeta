package net.hockeyapp.android.utils;

import android.util.Log;

public class HockeyLog
{
  public static final String HOCKEY_TAG = "HockeyApp";
  private static int sLogLevel = 6;
  
  public static void debug(String paramString)
  {
    debug(null, paramString);
  }
  
  public static void debug(String paramString1, String paramString2)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 3) {
      Log.d(paramString1, paramString2);
    }
  }
  
  public static void debug(String paramString1, String paramString2, Throwable paramThrowable)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 3) {
      Log.d(paramString1, paramString2, paramThrowable);
    }
  }
  
  public static void debug(String paramString, Throwable paramThrowable)
  {
    debug(null, paramString, paramThrowable);
  }
  
  public static void error(String paramString)
  {
    error(null, paramString);
  }
  
  public static void error(String paramString1, String paramString2)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 6) {
      Log.e(paramString1, paramString2);
    }
  }
  
  public static void error(String paramString1, String paramString2, Throwable paramThrowable)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 6) {
      Log.e(paramString1, paramString2, paramThrowable);
    }
  }
  
  public static void error(String paramString, Throwable paramThrowable)
  {
    error(null, paramString, paramThrowable);
  }
  
  public static int getLogLevel()
  {
    return sLogLevel;
  }
  
  public static void info(String paramString)
  {
    info(null, paramString);
  }
  
  public static void info(String paramString1, String paramString2)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 4) {
      Log.i(paramString1, paramString2);
    }
  }
  
  public static void info(String paramString1, String paramString2, Throwable paramThrowable)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 4) {
      Log.i(paramString1, paramString2, paramThrowable);
    }
  }
  
  public static void info(String paramString, Throwable paramThrowable)
  {
    info(paramString, paramThrowable);
  }
  
  static String sanitizeTag(String paramString)
  {
    String str;
    if ((paramString != null) && (paramString.length() != 0))
    {
      str = paramString;
      if (paramString.length() <= 23) {}
    }
    else
    {
      str = "HockeyApp";
    }
    return str;
  }
  
  public static void setLogLevel(int paramInt)
  {
    sLogLevel = paramInt;
  }
  
  public static void verbose(String paramString)
  {
    verbose(null, paramString);
  }
  
  public static void verbose(String paramString1, String paramString2)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 2) {
      Log.v(paramString1, paramString2);
    }
  }
  
  public static void verbose(String paramString1, String paramString2, Throwable paramThrowable)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 2) {
      Log.v(paramString1, paramString2, paramThrowable);
    }
  }
  
  public static void verbose(String paramString, Throwable paramThrowable)
  {
    verbose(null, paramString, paramThrowable);
  }
  
  public static void warn(String paramString)
  {
    warn(null, paramString);
  }
  
  public static void warn(String paramString1, String paramString2)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 5) {
      Log.w(paramString1, paramString2);
    }
  }
  
  public static void warn(String paramString1, String paramString2, Throwable paramThrowable)
  {
    paramString1 = sanitizeTag(paramString1);
    if (sLogLevel <= 5) {
      Log.w(paramString1, paramString2, paramThrowable);
    }
  }
  
  public static void warn(String paramString, Throwable paramThrowable)
  {
    warn(null, paramString, paramThrowable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/HockeyLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */