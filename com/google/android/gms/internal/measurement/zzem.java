package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.os.Handler;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;

abstract class zzem
{
  private static volatile Handler handler;
  private boolean enabled;
  private final zzgl zzacr;
  private final Runnable zzxy;
  private volatile long zzxz;
  
  zzem(zzgl paramzzgl)
  {
    Preconditions.checkNotNull(paramzzgl);
    this.zzacr = paramzzgl;
    this.enabled = true;
    this.zzxy = new zzen(this, paramzzgl);
  }
  
  private final Handler getHandler()
  {
    Handler localHandler;
    if (handler != null) {
      localHandler = handler;
    }
    for (;;)
    {
      return localHandler;
      try
      {
        if (handler == null)
        {
          localHandler = new android/os/Handler;
          localHandler.<init>(this.zzacr.getContext().getMainLooper());
          handler = localHandler;
        }
        localHandler = handler;
      }
      finally {}
    }
  }
  
  public final void cancel()
  {
    this.zzxz = 0L;
    getHandler().removeCallbacks(this.zzxy);
  }
  
  public abstract void run();
  
  public final boolean zzef()
  {
    if (this.zzxz != 0L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void zzh(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzxz = this.zzacr.zzbt().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzxy, paramLong)) {
        this.zzacr.zzgg().zzil().zzg("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */