package com.google.android.gms.internal;

import android.os.Looper;

final class zzces
  implements Runnable
{
  zzces(zzcer paramzzcer) {}
  
  public final void run()
  {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      zzcer.zza(this.zzbpB).zzwE().zzj(this);
    }
    boolean bool;
    do
    {
      return;
      bool = this.zzbpB.zzbo();
      zzcer.zza(this.zzbpB, 0L);
    } while ((!bool) || (!zzcer.zzb(this.zzbpB)));
    this.zzbpB.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzces.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */