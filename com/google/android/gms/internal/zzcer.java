package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;

abstract class zzcer
{
  private static volatile Handler zzagY;
  private volatile long zzagZ;
  private final zzcgl zzboe;
  private boolean zzbpA;
  private final Runnable zzv;
  
  zzcer(zzcgl paramzzcgl)
  {
    zzbo.zzu(paramzzcgl);
    this.zzboe = paramzzcgl;
    this.zzbpA = true;
    this.zzv = new zzces(this);
  }
  
  private final Handler getHandler()
  {
    if (zzagY != null) {
      return zzagY;
    }
    try
    {
      if (zzagY == null) {
        zzagY = new Handler(this.zzboe.getContext().getMainLooper());
      }
      Handler localHandler = zzagY;
      return localHandler;
    }
    finally {}
  }
  
  public final void cancel()
  {
    this.zzagZ = 0L;
    getHandler().removeCallbacks(this.zzv);
  }
  
  public abstract void run();
  
  public final boolean zzbo()
  {
    return this.zzagZ != 0L;
  }
  
  public final void zzs(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzagZ = this.zzboe.zzkq().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzv, paramLong)) {
        this.zzboe.zzwF().zzyx().zzj("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */