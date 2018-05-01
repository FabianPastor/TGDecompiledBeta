package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;

abstract class zzcgs
{
  private static volatile Handler zzdvp;
  private volatile long zzdvq;
  private final zzcim zziwf;
  private boolean zzizd;
  private final Runnable zzz;
  
  zzcgs(zzcim paramzzcim)
  {
    zzbq.checkNotNull(paramzzcim);
    this.zziwf = paramzzcim;
    this.zzizd = true;
    this.zzz = new zzcgt(this);
  }
  
  private final Handler getHandler()
  {
    if (zzdvp != null) {
      return zzdvp;
    }
    try
    {
      if (zzdvp == null) {
        zzdvp = new Handler(this.zziwf.getContext().getMainLooper());
      }
      Handler localHandler = zzdvp;
      return localHandler;
    }
    finally {}
  }
  
  public final void cancel()
  {
    this.zzdvq = 0L;
    getHandler().removeCallbacks(this.zzz);
  }
  
  public abstract void run();
  
  public final boolean zzdx()
  {
    return this.zzdvq != 0L;
  }
  
  public final void zzs(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzdvq = this.zziwf.zzws().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzz, paramLong)) {
        this.zziwf.zzawy().zzazd().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */