package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;

final class zzjp
{
  private long startTime;
  private final Clock zzrj;
  
  public zzjp(Clock paramClock)
  {
    Preconditions.checkNotNull(paramClock);
    this.zzrj = paramClock;
  }
  
  public final void clear()
  {
    this.startTime = 0L;
  }
  
  public final void start()
  {
    this.startTime = this.zzrj.elapsedRealtime();
  }
  
  public final boolean zzj(long paramLong)
  {
    boolean bool = true;
    if (this.startTime == 0L) {}
    for (;;)
    {
      return bool;
      if (this.zzrj.elapsedRealtime() - this.startTime < 3600000L) {
        bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */