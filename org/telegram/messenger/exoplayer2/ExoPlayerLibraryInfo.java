package org.telegram.messenger.exoplayer2;

import java.util.HashSet;

public final class ExoPlayerLibraryInfo
{
  public static final boolean ASSERTIONS_ENABLED = true;
  public static final String TAG = "ExoPlayer";
  public static final boolean TRACE_ENABLED = true;
  public static final String VERSION = "2.6.1";
  public static final int VERSION_INT = 2006001;
  public static final String VERSION_SLASHY = "ExoPlayerLib/2.6.1";
  private static final HashSet<String> registeredModules = new HashSet();
  private static String registeredModulesString = "goog.exo.core";
  
  public static void registerModule(String paramString)
  {
    try
    {
      if (registeredModules.add(paramString))
      {
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        registeredModulesString = registeredModulesString + ", " + paramString;
      }
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public static String registeredModules()
  {
    try
    {
      String str = registeredModulesString;
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ExoPlayerLibraryInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */