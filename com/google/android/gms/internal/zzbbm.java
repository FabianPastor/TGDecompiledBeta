package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;

final class zzbbm
  implements zzbdq
{
  private zzbbm(zzbbk paramzzbbk) {}
  
  public final void zzc(@NonNull ConnectionResult paramConnectionResult)
  {
    zzbbk.zza(this.zzaCx).lock();
    try
    {
      zzbbk.zza(this.zzaCx, paramConnectionResult);
      zzbbk.zzb(this.zzaCx);
      return;
    }
    finally
    {
      zzbbk.zza(this.zzaCx).unlock();
    }
  }
  
  public final void zze(int paramInt, boolean paramBoolean)
  {
    zzbbk.zza(this.zzaCx).lock();
    try
    {
      if ((zzbbk.zzc(this.zzaCx)) || (zzbbk.zzd(this.zzaCx) == null) || (!zzbbk.zzd(this.zzaCx).isSuccess()))
      {
        zzbbk.zza(this.zzaCx, false);
        zzbbk.zza(this.zzaCx, paramInt, paramBoolean);
        return;
      }
      zzbbk.zza(this.zzaCx, true);
      zzbbk.zze(this.zzaCx).onConnectionSuspended(paramInt);
      return;
    }
    finally
    {
      zzbbk.zza(this.zzaCx).unlock();
    }
  }
  
  public final void zzm(@Nullable Bundle paramBundle)
  {
    zzbbk.zza(this.zzaCx).lock();
    try
    {
      zzbbk.zza(this.zzaCx, paramBundle);
      zzbbk.zza(this.zzaCx, ConnectionResult.zzazX);
      zzbbk.zzb(this.zzaCx);
      return;
    }
    finally
    {
      zzbbk.zza(this.zzaCx).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */