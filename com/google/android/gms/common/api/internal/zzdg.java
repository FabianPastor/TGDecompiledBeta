package com.google.android.gms.common.api.internal;

import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzbq;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public final class zzdg<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object zzfou;
  private final WeakReference<GoogleApiClient> zzfow;
  private ResultTransform<? super R, ? extends Result> zzfux;
  private zzdg<? extends Result> zzfuy;
  private volatile ResultCallbacks<? super R> zzfuz;
  private PendingResult<R> zzfva;
  private Status zzfvb;
  private final zzdi zzfvc;
  private boolean zzfvd;
  
  private final void zzajr()
  {
    if ((this.zzfux == null) && (this.zzfuz == null)) {}
    do
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzfow.get();
      if ((!this.zzfvd) && (this.zzfux != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.zzfvd = true;
      }
      if (this.zzfvb != null)
      {
        zzx(this.zzfvb);
        return;
      }
    } while (this.zzfva == null);
    this.zzfva.setResultCallback(this);
  }
  
  private final boolean zzajt()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzfow.get();
    return (this.zzfuz != null) && (localGoogleApiClient != null);
  }
  
  private static void zzd(Result paramResult)
  {
    if ((paramResult instanceof Releasable)) {}
    try
    {
      ((Releasable)paramResult).release();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      paramResult = String.valueOf(paramResult);
      Log.w("TransformedResultImpl", String.valueOf(paramResult).length() + 18 + "Unable to release " + paramResult, localRuntimeException);
    }
  }
  
  private final void zzd(Status paramStatus)
  {
    synchronized (this.zzfou)
    {
      this.zzfvb = paramStatus;
      zzx(this.zzfvb);
      return;
    }
  }
  
  private final void zzx(Status paramStatus)
  {
    synchronized (this.zzfou)
    {
      if (this.zzfux != null)
      {
        paramStatus = this.zzfux.onFailure(paramStatus);
        zzbq.checkNotNull(paramStatus, "onFailure must not return null");
        this.zzfuy.zzd(paramStatus);
      }
      while (!zzajt()) {
        return;
      }
      this.zzfuz.onFailure(paramStatus);
    }
  }
  
  public final void onResult(R paramR)
  {
    for (;;)
    {
      synchronized (this.zzfou)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.zzfux != null)
          {
            zzcs.zzaip().submit(new zzdh(this, paramR));
            return;
          }
          if (!zzajt()) {
            continue;
          }
          this.zzfuz.onSuccess(paramR);
        }
      }
      zzd(paramR.getStatus());
      zzd(paramR);
    }
  }
  
  public final void zza(PendingResult<?> paramPendingResult)
  {
    synchronized (this.zzfou)
    {
      this.zzfva = paramPendingResult;
      zzajr();
      return;
    }
  }
  
  final void zzajs()
  {
    this.zzfuz = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */