package com.google.android.gms.common.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

public final class zzo
  extends zze
{
  public zzo(zzd paramzzd, int paramInt, Bundle paramBundle)
  {
    super(paramzzd, paramInt, null);
  }
  
  protected final boolean zzakr()
  {
    this.zzfza.zzfym.zzf(ConnectionResult.zzfkr);
    return true;
  }
  
  protected final void zzj(ConnectionResult paramConnectionResult)
  {
    this.zzfza.zzfym.zzf(paramConnectionResult);
    this.zzfza.onConnectionFailed(paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */