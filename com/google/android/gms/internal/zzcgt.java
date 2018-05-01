package com.google.android.gms.internal;

import android.os.Looper;

final class zzcgt
  implements Runnable
{
  zzcgt(zzcgs paramzzcgs) {}
  
  public final void run()
  {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      zzcgs.zza(this.zzize).zzawx().zzg(this);
    }
    boolean bool;
    do
    {
      return;
      bool = this.zzize.zzdx();
      zzcgs.zza(this.zzize, 0L);
    } while ((!bool) || (!zzcgs.zzb(this.zzize)));
    this.zzize.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */