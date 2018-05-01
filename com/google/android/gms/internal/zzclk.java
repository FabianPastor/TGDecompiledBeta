package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;

final class zzclk
{
  private long mStartTime;
  private final zzd zzata;
  
  public zzclk(zzd paramzzd)
  {
    zzbq.checkNotNull(paramzzd);
    this.zzata = paramzzd;
  }
  
  public final void clear()
  {
    this.mStartTime = 0L;
  }
  
  public final void start()
  {
    this.mStartTime = this.zzata.elapsedRealtime();
  }
  
  public final boolean zzu(long paramLong)
  {
    if (this.mStartTime == 0L) {}
    while (this.zzata.elapsedRealtime() - this.mStartTime >= 3600000L) {
      return true;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */