package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.internal.zzbo;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public final class zzbes<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object zzaBW = new Object();
  private final WeakReference<GoogleApiClient> zzaBY;
  private ResultTransform<? super R, ? extends Result> zzaFa = null;
  private zzbes<? extends Result> zzaFb = null;
  private volatile ResultCallbacks<? super R> zzaFc = null;
  private PendingResult<R> zzaFd = null;
  private Status zzaFe = null;
  private final zzbeu zzaFf;
  private boolean zzaFg = false;
  
  public zzbes(WeakReference<GoogleApiClient> paramWeakReference)
  {
    zzbo.zzb(paramWeakReference, "GoogleApiClient reference must not be null");
    this.zzaBY = paramWeakReference;
    paramWeakReference = (GoogleApiClient)this.zzaBY.get();
    if (paramWeakReference != null) {}
    for (paramWeakReference = paramWeakReference.getLooper();; paramWeakReference = Looper.getMainLooper())
    {
      this.zzaFf = new zzbeu(this, paramWeakReference);
      return;
    }
  }
  
  private static void zzc(Result paramResult)
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
  
  private final void zzqJ()
  {
    if ((this.zzaFa == null) && (this.zzaFc == null)) {}
    do
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzaBY.get();
      if ((!this.zzaFg) && (this.zzaFa != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.zzaFg = true;
      }
      if (this.zzaFe != null)
      {
        zzw(this.zzaFe);
        return;
      }
    } while (this.zzaFd == null);
    this.zzaFd.setResultCallback(this);
  }
  
  private final boolean zzqL()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzaBY.get();
    return (this.zzaFc != null) && (localGoogleApiClient != null);
  }
  
  private final void zzv(Status paramStatus)
  {
    synchronized (this.zzaBW)
    {
      this.zzaFe = paramStatus;
      zzw(this.zzaFe);
      return;
    }
  }
  
  private final void zzw(Status paramStatus)
  {
    synchronized (this.zzaBW)
    {
      if (this.zzaFa != null)
      {
        paramStatus = this.zzaFa.onFailure(paramStatus);
        zzbo.zzb(paramStatus, "onFailure must not return null");
        this.zzaFb.zzv(paramStatus);
      }
      while (!zzqL()) {
        return;
      }
      this.zzaFc.onFailure(paramStatus);
    }
  }
  
  public final void andFinally(@NonNull ResultCallbacks<? super R> paramResultCallbacks)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.zzaBW)
      {
        if (this.zzaFc == null)
        {
          bool1 = true;
          zzbo.zza(bool1, "Cannot call andFinally() twice.");
          if (this.zzaFa != null) {
            break label65;
          }
          bool1 = bool2;
          zzbo.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zzaFc = paramResultCallbacks;
          zzqJ();
          return;
        }
      }
      boolean bool1 = false;
      continue;
      label65:
      bool1 = false;
    }
  }
  
  public final void onResult(R paramR)
  {
    for (;;)
    {
      synchronized (this.zzaBW)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.zzaFa != null)
          {
            zzbeg.zzqj().submit(new zzbet(this, paramR));
            return;
          }
          if (!zzqL()) {
            continue;
          }
          this.zzaFc.onSuccess(paramR);
        }
      }
      zzv(paramR.getStatus());
      zzc(paramR);
    }
  }
  
  @NonNull
  public final <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.zzaBW)
      {
        if (this.zzaFa == null)
        {
          bool1 = true;
          zzbo.zza(bool1, "Cannot call then() twice.");
          if (this.zzaFc != null) {
            break label83;
          }
          bool1 = bool2;
          zzbo.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zzaFa = paramResultTransform;
          paramResultTransform = new zzbes(this.zzaBY);
          this.zzaFb = paramResultTransform;
          zzqJ();
          return paramResultTransform;
        }
      }
      boolean bool1 = false;
      continue;
      label83:
      bool1 = false;
    }
  }
  
  public final void zza(PendingResult<?> paramPendingResult)
  {
    synchronized (this.zzaBW)
    {
      this.zzaFd = paramPendingResult;
      zzqJ();
      return;
    }
  }
  
  final void zzqK()
  {
    this.zzaFc = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */