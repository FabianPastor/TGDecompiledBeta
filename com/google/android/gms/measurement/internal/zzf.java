package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;

abstract class zzf
{
  private static volatile Handler ef;
  private final zzx aqw;
  private boolean arv;
  private volatile long eg;
  private final Runnable zzw;
  
  zzf(zzx paramzzx)
  {
    zzaa.zzy(paramzzx);
    this.aqw = paramzzx;
    this.arv = true;
    this.zzw = new Runnable()
    {
      public void run()
      {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          zzf.zza(zzf.this).zzbwa().zzm(this);
        }
        boolean bool;
        do
        {
          return;
          bool = zzf.this.zzfy();
          zzf.zza(zzf.this, 0L);
        } while ((!bool) || (!zzf.zzb(zzf.this)));
        zzf.this.run();
      }
    };
  }
  
  private Handler getHandler()
  {
    if (ef != null) {
      return ef;
    }
    try
    {
      if (ef == null) {
        ef = new Handler(this.aqw.getContext().getMainLooper());
      }
      Handler localHandler = ef;
      return localHandler;
    }
    finally {}
  }
  
  public void cancel()
  {
    this.eg = 0L;
    getHandler().removeCallbacks(this.zzw);
  }
  
  public abstract void run();
  
  public boolean zzfy()
  {
    return this.eg != 0L;
  }
  
  public void zzx(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.eg = this.aqw.zzabz().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzw, paramLong)) {
        this.aqw.zzbwb().zzbwy().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */