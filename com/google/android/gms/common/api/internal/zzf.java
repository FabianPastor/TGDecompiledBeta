package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzf<ResultT>
  extends zzb
{
  private final TaskCompletionSource<ResultT> zzdu;
  private final TaskApiCall<Api.AnyClient, ResultT> zzdy;
  private final StatusExceptionMapper zzdz;
  
  public final Feature[] getRequiredFeatures()
  {
    return this.zzdy.zzca();
  }
  
  public final boolean shouldAutoResolveMissingFeatures()
  {
    return this.zzdy.shouldAutoResolveMissingFeatures();
  }
  
  public final void zza(Status paramStatus)
  {
    this.zzdu.trySetException(this.zzdz.getException(paramStatus));
  }
  
  public final void zza(GoogleApiManager.zza<?> paramzza)
    throws DeadObjectException
  {
    try
    {
      this.zzdy.doExecute(paramzza.zzae(), this.zzdu);
      return;
    }
    catch (DeadObjectException paramzza)
    {
      throw paramzza;
    }
    catch (RemoteException paramzza)
    {
      for (;;)
      {
        zza(zzb.zzb(paramzza));
      }
    }
    catch (RuntimeException paramzza)
    {
      for (;;)
      {
        zza(paramzza);
      }
    }
  }
  
  public final void zza(zzaa paramzzaa, boolean paramBoolean)
  {
    paramzzaa.zza(this.zzdu, paramBoolean);
  }
  
  public final void zza(RuntimeException paramRuntimeException)
  {
    this.zzdu.trySetException(paramRuntimeException);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */