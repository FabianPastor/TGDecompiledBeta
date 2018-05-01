package com.google.android.gms.common.util;

import android.os.SystemClock;

public class DefaultClock
  implements Clock
{
  private static final DefaultClock zzzk = new DefaultClock();
  
  public static Clock getInstance()
  {
    return zzzk;
  }
  
  public long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public long elapsedRealtime()
  {
    return SystemClock.elapsedRealtime();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/DefaultClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */