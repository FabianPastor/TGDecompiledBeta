package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;

public class zzqc
{
  public static abstract class zza<R extends Result, A extends Api.zzb>
    extends zzqe<R>
    implements zzqc.zzb<R>
  {
    private final Api<?> tv;
    private final Api.zzc<A> wx;
    
    @Deprecated
    protected zza(Api.zzc<A> paramzzc, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.wx = ((Api.zzc)zzac.zzy(paramzzc));
      this.tv = null;
    }
    
    protected zza(Api<?> paramApi, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.wx = paramApi.zzapp();
      this.tv = paramApi;
    }
    
    private void zza(RemoteException paramRemoteException)
    {
      zzz(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    protected abstract void zza(A paramA)
      throws RemoteException;
    
    public final Api.zzc<A> zzapp()
    {
      return this.wx;
    }
    
    public final Api<?> zzaqn()
    {
      return this.tv;
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
    
    public final void zzz(Status paramStatus)
    {
      if (!paramStatus.isSuccess()) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzb(bool, "Failed result must not be success");
        paramStatus = zzc(paramStatus);
        zzc(paramStatus);
        zzb(paramStatus);
        return;
      }
    }
  }
  
  public static abstract interface zzb<R>
  {
    public abstract void setResult(R paramR);
    
    public abstract void zzz(Status paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */