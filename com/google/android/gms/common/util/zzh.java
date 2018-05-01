package com.google.android.gms.common.util;

import android.os.SystemClock;

public final class zzh
  implements zzd
{
  private static zzh zzgef = new zzh();
  
  public static zzd zzamg()
  {
    return zzgef;
  }
  
  public final long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public final long elapsedRealtime()
  {
    return SystemClock.elapsedRealtime();
  }
  
  public final long nanoTime()
  {
    return System.nanoTime();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */