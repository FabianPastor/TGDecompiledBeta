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

public class zzaad
{
  public static abstract class zza<R extends Result, A extends Api.zzb>
    extends zzaaf<R>
    implements zzaad.zzb<R>
  {
    private final Api<?> zzaxf;
    private final Api.zzc<A> zzazY;
    
    @Deprecated
    protected zza(Api.zzc<A> paramzzc, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.zzazY = ((Api.zzc)zzac.zzw(paramzzc));
      this.zzaxf = null;
    }
    
    protected zza(Api<?> paramApi, GoogleApiClient paramGoogleApiClient)
    {
      super();
      this.zzazY = paramApi.zzvg();
      this.zzaxf = paramApi;
    }
    
    private void zzc(RemoteException paramRemoteException)
    {
      zzB(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    public final Api<?> getApi()
    {
      return this.zzaxf;
    }
    
    public final void zzB(Status paramStatus)
    {
      if (!paramStatus.isSuccess()) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzb(bool, "Failed result must not be success");
        zzb(zzc(paramStatus));
        return;
      }
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
    
    public final Api.zzc<A> zzvg()
    {
      return this.zzazY;
    }
  }
  
  public static abstract interface zzb<R>
  {
    public abstract void setResult(R paramR);
    
    public abstract void zzB(Status paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */