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
import com.google.android.gms.common.internal.Preconditions;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import javax.annotation.concurrent.GuardedBy;

public final class zzch<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object zzfa;
  private final WeakReference<GoogleApiClient> zzfc;
  private ResultTransform<? super R, ? extends Result> zzmd;
  private zzch<? extends Result> zzme;
  private volatile ResultCallbacks<? super R> zzmf;
  private PendingResult<R> zzmg;
  private Status zzmh;
  private final zzcj zzmi;
  private boolean zzmj;
  
  private static void zzb(Result paramResult)
  {
    if ((paramResult instanceof Releasable)) {}
    try
    {
      ((Releasable)paramResult).release();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        paramResult = String.valueOf(paramResult);
        Log.w("TransformedResultImpl", String.valueOf(paramResult).length() + 18 + "Unable to release " + paramResult, localRuntimeException);
      }
    }
  }
  
  @GuardedBy("mSyncToken")
  private final void zzcb()
  {
    if ((this.zzmd == null) && (this.zzmf == null)) {}
    for (;;)
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzfc.get();
      if ((!this.zzmj) && (this.zzmd != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.zzmj = true;
      }
      if (this.zzmh != null) {
        zze(this.zzmh);
      } else if (this.zzmg != null) {
        this.zzmg.setResultCallback(this);
      }
    }
  }
  
  @GuardedBy("mSyncToken")
  private final boolean zzcd()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzfc.get();
    if ((this.zzmf != null) && (localGoogleApiClient != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final void zzd(Status paramStatus)
  {
    synchronized (this.zzfa)
    {
      this.zzmh = paramStatus;
      zze(this.zzmh);
      return;
    }
  }
  
  private final void zze(Status paramStatus)
  {
    synchronized (this.zzfa)
    {
      if (this.zzmd != null)
      {
        paramStatus = this.zzmd.onFailure(paramStatus);
        Preconditions.checkNotNull(paramStatus, "onFailure must not return null");
        this.zzme.zzd(paramStatus);
      }
      while (!zzcd()) {
        return;
      }
      this.zzmf.onFailure(paramStatus);
    }
  }
  
  public final void onResult(R paramR)
  {
    for (;;)
    {
      synchronized (this.zzfa)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.zzmd != null)
          {
            ExecutorService localExecutorService = zzbw.zzbe();
            zzci localzzci = new com/google/android/gms/common/api/internal/zzci;
            localzzci.<init>(this, paramR);
            localExecutorService.submit(localzzci);
            return;
          }
          if (!zzcd()) {
            continue;
          }
          this.zzmf.onSuccess(paramR);
        }
      }
      zzd(paramR.getStatus());
      zzb(paramR);
    }
  }
  
  public final void zza(PendingResult<?> paramPendingResult)
  {
    synchronized (this.zzfa)
    {
      this.zzmg = paramPendingResult;
      zzcb();
      return;
    }
  }
  
  final void zzcc()
  {
    this.zzmf = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */