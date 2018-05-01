package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzcjj
  implements Runnable
{
  zzcjj(zzcir paramzzcir, String paramString1, String paramString2, String paramString3, long paramLong) {}
  
  public final void run()
  {
    if (this.zzjgu == null)
    {
      zzcir.zza(this.zzjgo).zzawq().zza(this.zzimf, null);
      return;
    }
    AppMeasurement.zzb localzzb = new AppMeasurement.zzb();
    localzzb.zziwk = this.zzjgv;
    localzzb.zziwl = this.zzjgu;
    localzzb.zziwm = this.zzjgw;
    zzcir.zza(this.zzjgo).zzawq().zza(this.zzimf, localzzb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcjj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */