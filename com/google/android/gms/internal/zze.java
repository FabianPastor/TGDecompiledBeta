package com.google.android.gms.internal;

import java.util.concurrent.BlockingQueue;

final class zze
  implements Runnable
{
  zze(zzd paramzzd, zzp paramzzp) {}
  
  public final void run()
  {
    try
    {
      zzd.zza(this.zzm).put(this.zzl);
      return;
    }
    catch (InterruptedException localInterruptedException) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */