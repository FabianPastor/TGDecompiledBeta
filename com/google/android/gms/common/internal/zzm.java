package com.google.android.gms.common.internal;

import com.google.android.gms.common.ConnectionResult;

public final class zzm
  implements zzj
{
  public zzm(zzd paramzzd) {}
  
  public final void zzf(ConnectionResult paramConnectionResult)
  {
    if (paramConnectionResult.isSuccess()) {
      this.zzfza.zza(null, this.zzfza.zzakp());
    }
    while (zzd.zzg(this.zzfza) == null) {
      return;
    }
    zzd.zzg(this.zzfza).onConnectionFailed(paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */