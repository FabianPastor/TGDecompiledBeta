package com.google.android.gms.internal.config;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl;

abstract class zzr<R extends Result>
  extends BaseImplementation.ApiMethodImpl<R, zzw>
{
  public zzr(GoogleApiClient paramGoogleApiClient)
  {
    super(zze.API, paramGoogleApiClient);
  }
  
  protected abstract void zza(Context paramContext, zzah paramzzah)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */