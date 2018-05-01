package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

abstract class zzatk
{
  private static volatile Handler zzafd;
  private volatile long zzafe;
  private final zzaue zzbqb;
  private boolean zzbru;
  private final Runnable zzw;
  
  zzatk(zzaue paramzzaue)
  {
    zzac.zzw(paramzzaue);
    this.zzbqb = paramzzaue;
    this.zzbru = true;
    this.zzw = new Runnable()
    {
      public void run()
      {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          zzatk.zza(zzatk.this).zzKk().zzm(this);
        }
        boolean bool;
        do
        {
          return;
          bool = zzatk.this.zzcy();
          zzatk.zza(zzatk.this, 0L);
        } while ((!bool) || (!zzatk.zzb(zzatk.this)));
        zzatk.this.run();
      }
    };
  }
  
  private Handler getHandler()
  {
    if (zzafd != null) {
      return zzafd;
    }
    try
    {
      if (zzafd == null) {
        zzafd = new Handler(this.zzbqb.getContext().getMainLooper());
      }
      Handler localHandler = zzafd;
      return localHandler;
    }
    finally {}
  }
  
  public void cancel()
  {
    this.zzafe = 0L;
    getHandler().removeCallbacks(this.zzw);
  }
  
  public abstract void run();
  
  public boolean zzcy()
  {
    return this.zzafe != 0L;
  }
  
  public void zzy(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzafe = this.zzbqb.zznR().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzw, paramLong)) {
        this.zzbqb.zzKl().zzLZ().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */