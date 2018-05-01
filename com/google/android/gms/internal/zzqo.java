package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;

public class zzqo
{
  public static abstract class zza<R extends Result, A extends Api.zzb>
    extends zzqq<R>
    implements zzqo.zzb<R>
  {
    private final Api<?> vS;
    private final Api.zzc<A> yy;
    
    @Deprecated
    protected zza(Api.zzc<A> paramzzc, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.yy = ((Api.zzc)zzaa.zzy(paramzzc));
      this.vS = null;
    }
    
    protected zza(Api<?> paramApi, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.yy = paramApi.zzaqv();
      this.vS = paramApi;
    }
    
    private void zza(RemoteException paramRemoteException)
    {
      zzaa(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    public final Api<?> getApi()
    {
      return this.vS;
    }
    
    protected abstract void zza(A paramA)
      throws RemoteException;
    
    public final void zzaa(Status paramStatus)
    {
      if (!paramStatus.isSuccess()) {}
      for (boolean bool = true;; bool = false)
      {
        zzaa.zzb(bool, "Failed result must not be success");
        paramStatus = zzc(paramStatus);
        zzc(paramStatus);
        zzb(paramStatus);
        return;
      }
    }
    
    public final Api.zzc<A> zzaqv()
    {
      return this.yy;
    }
    
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
        zza(paramA);
        throw paramA;
      }
      catch (RemoteException paramA)
      {
        zza(paramA);
      }
    }
    
    protected void zzb(R paramR) {}
  }
  
  public static abstract interface zzb<R>
  {
    public abstract void setResult(R paramR);
    
    public abstract void zzaa(Status paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */