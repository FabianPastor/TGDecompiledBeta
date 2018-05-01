package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;

final class zzbbn
  implements zzbdq
{
  private zzbbn(zzbbk paramzzbbk) {}
  
  public final void zzc(@NonNull ConnectionResult paramConnectionResult)
  {
    zzbbk.zza(this.zzaCx).lock();
    try
    {
      zzbbk.zzb(this.zzaCx, paramConnectionResult);
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
      if (zzbbk.zzc(this.zzaCx))
      {
        zzbbk.zza(this.zzaCx, false);
        zzbbk.zza(this.zzaCx, paramInt, paramBoolean);
        return;
      }
      zzbbk.zza(this.zzaCx, true);
      zzbbk.zzf(this.zzaCx).onConnectionSuspended(paramInt);
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
      zzbbk.zzb(this.zzaCx, ConnectionResult.zzazX);
      zzbbk.zzb(this.zzaCx);
      return;
    }
    finally
    {
      zzbbk.zza(this.zzaCx).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */