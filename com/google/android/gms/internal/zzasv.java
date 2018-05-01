package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

abstract class zzasv
{
  private static volatile Handler zzaec;
  private volatile long zzaed;
  private final zzatp zzbpw;
  private boolean zzbqB;
  private final Runnable zzv;
  
  zzasv(zzatp paramzzatp)
  {
    zzac.zzw(paramzzatp);
    this.zzbpw = paramzzatp;
    this.zzbqB = true;
    this.zzv = new Runnable()
    {
      public void run()
      {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          zzasv.zza(zzasv.this).zzJs().zzm(this);
        }
        boolean bool;
        do
        {
          return;
          bool = zzasv.this.zzcv();
          zzasv.zza(zzasv.this, 0L);
        } while ((!bool) || (!zzasv.zzb(zzasv.this)));
        zzasv.this.run();
      }
    };
  }
  
  private Handler getHandler()
  {
    if (zzaec != null) {
      return zzaec;
    }
    try
    {
      if (zzaec == null) {
        zzaec = new Handler(this.zzbpw.getContext().getMainLooper());
      }
      Handler localHandler = zzaec;
      return localHandler;
    }
    finally {}
  }
  
  public void cancel()
  {
    this.zzaed = 0L;
    getHandler().removeCallbacks(this.zzv);
  }
  
  public abstract void run();
  
  public boolean zzcv()
  {
    return this.zzaed != 0L;
  }
  
  public void zzx(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzaed = this.zzbpw.zznq().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzv, paramLong)) {
        this.zzbpw.zzJt().zzLa().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */