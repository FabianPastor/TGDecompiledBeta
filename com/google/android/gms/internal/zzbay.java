package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;

public abstract class zzbay<R extends Result, A extends Api.zzb>
  extends zzbbe<R>
  implements zzbaz<R>
{
  private final Api.zzc<A> zzaBM;
  private final Api<?> zzayW;
  
  @Deprecated
  protected zzbay(Api.zzc<A> paramzzc, GoogleApiClient paramGoogleApiClient)
  {
    super((GoogleApiClient)zzbo.zzb(paramGoogleApiClient, "GoogleApiClient must not be null"));
    this.zzaBM = ((Api.zzc)zzbo.zzu(paramzzc));
    this.zzayW = null;
  }
  
  protected zzbay(Api<?> paramApi, GoogleApiClient paramGoogleApiClient)
  {
    super((GoogleApiClient)zzbo.zzb(paramGoogleApiClient, "GoogleApiClient must not be null"));
    this.zzaBM = paramApi.zzpd();
    this.zzayW = paramApi;
  }
  
  private final void zzc(RemoteException paramRemoteException)
  {
    zzr(new Status(8, paramRemoteException.getLocalizedMessage(), null));
  }
  
  protected abstract void zza(A paramA)
    throws RemoteException;
  
  public final void zzb(A paramA)
    throws DeadObjectException
  {
    try
    {
      zza(paramA);
      return;
    }
    catch (DeadObjectException paramA)
    {
      zzc(paramA);
      throw paramA;
    }
    catch (RemoteException paramA)
    {
      zzc(paramA);
    }
  }
  
  public final Api.zzc<A> zzpd()
  {
    return this.zzaBM;
  }
  
  public final Api<?> zzpg()
  {
    return this.zzayW;
  }
  
  public final void zzr(Status paramStatus)
  {
    if (!paramStatus.isSuccess()) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Failed result must not be success");
      setResult(zzb(paramStatus));
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */