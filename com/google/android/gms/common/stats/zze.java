package com.google.android.gms.common.stats;

import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

public class zze
{
  private final long Ev;
  private final int Ew;
  private final SimpleArrayMap<String, Long> Ex;
  
  public zze()
  {
    this.Ev = 60000L;
    this.Ew = 10;
    this.Ex = new SimpleArrayMap(10);
  }
  
  public zze(int paramInt, long paramLong)
  {
    this.Ev = paramLong;
    this.Ew = paramInt;
    this.Ex = new SimpleArrayMap();
  }
  
  private void zze(long paramLong1, long paramLong2)
  {
    int i = this.Ex.size() - 1;
    while (i >= 0)
    {
      if (paramLong2 - ((Long)this.Ex.valueAt(i)).longValue() > paramLong1) {
        this.Ex.removeAt(i);
      }
      i -= 1;
    }
  }
  
  public Long zzif(String paramString)
  {
    long l2 = SystemClock.elapsedRealtime();
    long l1 = this.Ev;
    try
    {
      while (this.Ex.size() >= this.Ew)
      {
        zze(l1, l2);
        l1 /= 2L;
        int i = this.Ew;
        Log.w("ConnectionTracker", 94 + "The max capacity " + i + " is not enough. Current durationThreshold is: " + l1);
      }
      paramString = (Long)this.Ex.put(paramString, Long.valueOf(l2));
    }
    finally {}
    return paramString;
  }
  
  public boolean zzig(String paramString)
  {
    for (;;)
    {
      try
      {
        if (this.Ex.remove(paramString) != null)
        {
          bool = true;
          return bool;
        }
      }
      finally {}
      boolean bool = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */