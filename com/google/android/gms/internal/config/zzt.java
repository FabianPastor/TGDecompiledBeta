package com.google.android.gms.internal.config;

import com.google.android.gms.common.api.Status;

final class zzt
  extends zzq
{
  zzt(zzs paramzzs) {}
  
  public final void zza(Status paramStatus, zzad paramzzad)
  {
    if ((paramzzad.getStatusCode() == 6502) || (paramzzad.getStatusCode() == 6507)) {
      this.zzp.setResult(new zzu(zzo.zze(paramzzad.getStatusCode()), zzo.zzc(paramzzad), paramzzad.getThrottleEndTimeMillis(), zzo.zzb(paramzzad)));
    }
    for (;;)
    {
      return;
      this.zzp.setResult(new zzu(zzo.zze(paramzzad.getStatusCode()), zzo.zzc(paramzzad), zzo.zzb(paramzzad)));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */