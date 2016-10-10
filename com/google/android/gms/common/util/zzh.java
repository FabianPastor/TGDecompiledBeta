package com.google.android.gms.common.util;

import android.os.SystemClock;

public final class zzh
  implements zze
{
  private static zzh EK;
  
  public static zze zzaxj()
  {
    try
    {
      if (EK == null) {
        EK = new zzh();
      }
      zzh localzzh = EK;
      return localzzh;
    }
    finally {}
  }
  
  public long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public long elapsedRealtime()
  {
    return SystemClock.elapsedRealtime();
  }
  
  public long nanoTime()
  {
    return System.nanoTime();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */