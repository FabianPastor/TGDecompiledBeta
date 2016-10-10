package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

abstract class zzf
{
  private static volatile Handler bY;
  private final zzx anq;
  private boolean aol;
  private volatile long bZ;
  private final Runnable zzw;
  
  zzf(zzx paramzzx)
  {
    zzac.zzy(paramzzx);
    this.anq = paramzzx;
    this.aol = true;
    this.zzw = new Runnable()
    {
      public void run()
      {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          zzf.zza(zzf.this).zzbvf().zzm(this);
        }
        boolean bool;
        do
        {
          return;
          bool = zzf.this.zzfl();
          zzf.zza(zzf.this, 0L);
        } while ((!bool) || (!zzf.zzb(zzf.this)));
        zzf.this.run();
      }
    };
  }
  
  private Handler getHandler()
  {
    if (bY != null) {
      return bY;
    }
    try
    {
      if (bY == null) {
        bY = new Handler(this.anq.getContext().getMainLooper());
      }
      Handler localHandler = bY;
      return localHandler;
    }
    finally {}
  }
  
  public void cancel()
  {
    this.bZ = 0L;
    getHandler().removeCallbacks(this.zzw);
  }
  
  public abstract void run();
  
  public boolean zzfl()
  {
    return this.bZ != 0L;
  }
  
  public void zzx(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.bZ = this.anq.zzaan().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzw, paramLong)) {
        this.anq.zzbvg().zzbwc().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */