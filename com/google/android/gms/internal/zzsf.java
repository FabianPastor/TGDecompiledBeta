package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
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
import com.google.android.gms.common.internal.zzaa;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public class zzsf<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final zza BA;
  private boolean BB = false;
  private ResultTransform<? super R, ? extends Result> Bv = null;
  private zzsf<? extends Result> Bw = null;
  private volatile ResultCallbacks<? super R> Bx = null;
  private PendingResult<R> By = null;
  private Status Bz = null;
  private final Object yH = new Object();
  private final WeakReference<GoogleApiClient> yJ;
  
  public zzsf(WeakReference<GoogleApiClient> paramWeakReference)
  {
    zzaa.zzb(paramWeakReference, "GoogleApiClient reference must not be null");
    this.yJ = paramWeakReference;
    paramWeakReference = (GoogleApiClient)this.yJ.get();
    if (paramWeakReference != null) {}
    for (paramWeakReference = paramWeakReference.getLooper();; paramWeakReference = Looper.getMainLooper())
    {
      this.BA = new zza(paramWeakReference);
      return;
    }
  }
  
  private void zzad(Status paramStatus)
  {
    synchronized (this.yH)
    {
      this.Bz = paramStatus;
      zzae(this.Bz);
      return;
    }
  }
  
  private void zzae(Status paramStatus)
  {
    synchronized (this.yH)
    {
      if (this.Bv != null)
      {
        paramStatus = this.Bv.onFailure(paramStatus);
        zzaa.zzb(paramStatus, "onFailure must not return null");
        this.Bw.zzad(paramStatus);
      }
      while (!zzaue()) {
        return;
      }
      this.Bx.onFailure(paramStatus);
    }
  }
  
  private void zzauc()
  {
    if ((this.Bv == null) && (this.Bx == null)) {}
    do
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.yJ.get();
      if ((!this.BB) && (this.Bv != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.BB = true;
      }
      if (this.Bz != null)
      {
        zzae(this.Bz);
        return;
      }
    } while (this.By == null);
    this.By.setResultCallback(this);
  }
  
  private boolean zzaue()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.yJ.get();
    return (this.Bx != null) && (localGoogleApiClient != null);
  }
  
  private void zze(Result paramResult)
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
  
  public void andFinally(@NonNull ResultCallbacks<? super R> paramResultCallbacks)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.yH)
      {
        if (this.Bx == null)
        {
          bool1 = true;
          zzaa.zza(bool1, "Cannot call andFinally() twice.");
          if (this.Bv != null) {
            break label65;
          }
          bool1 = bool2;
          zzaa.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.Bx = paramResultCallbacks;
          zzauc();
          return;
        }
      }
      boolean bool1 = false;
      continue;
      label65:
      bool1 = false;
    }
  }
  
  public void onResult(final R paramR)
  {
    for (;;)
    {
      synchronized (this.yH)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.Bv != null)
          {
            zzry.zzatf().submit(new Runnable()
            {
              @WorkerThread
              public void run()
              {
                try
                {
                  zzqq.yG.set(Boolean.valueOf(true));
                  Object localObject1 = zzsf.zzc(zzsf.this).onSuccess(paramR);
                  zzsf.zzd(zzsf.this).sendMessage(zzsf.zzd(zzsf.this).obtainMessage(0, localObject1));
                  zzqq.yG.set(Boolean.valueOf(false));
                  zzsf.zza(zzsf.this, paramR);
                  localObject1 = (GoogleApiClient)zzsf.zze(zzsf.this).get();
                  if (localObject1 != null) {
                    ((GoogleApiClient)localObject1).zzb(zzsf.this);
                  }
                  return;
                }
                catch (RuntimeException localRuntimeException)
                {
                  zzsf.zzd(zzsf.this).sendMessage(zzsf.zzd(zzsf.this).obtainMessage(1, localRuntimeException));
                  GoogleApiClient localGoogleApiClient1;
                  return;
                }
                finally
                {
                  zzqq.yG.set(Boolean.valueOf(false));
                  zzsf.zza(zzsf.this, paramR);
                  GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzsf.zze(zzsf.this).get();
                  if (localGoogleApiClient2 != null) {
                    localGoogleApiClient2.zzb(zzsf.this);
                  }
                }
              }
            });
            return;
          }
          if (!zzaue()) {
            continue;
          }
          this.Bx.onSuccess(paramR);
        }
      }
      zzad(paramR.getStatus());
      zze(paramR);
    }
  }
  
  @NonNull
  public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.yH)
      {
        if (this.Bv == null)
        {
          bool1 = true;
          zzaa.zza(bool1, "Cannot call then() twice.");
          if (this.Bx != null) {
            break label83;
          }
          bool1 = bool2;
          zzaa.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.Bv = paramResultTransform;
          paramResultTransform = new zzsf(this.yJ);
          this.Bw = paramResultTransform;
          zzauc();
          return paramResultTransform;
        }
      }
      boolean bool1 = false;
      continue;
      label83:
      bool1 = false;
    }
  }
  
  public void zza(PendingResult<?> paramPendingResult)
  {
    synchronized (this.yH)
    {
      this.By = paramPendingResult;
      zzauc();
      return;
    }
  }
  
  void zzaud()
  {
    this.Bx = null;
  }
  
  private final class zza
    extends Handler
  {
    public zza(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        int i = paramMessage.what;
        Log.e("TransformedResultImpl", 70 + "TransformationResultHandler received unknown message type: " + i);
        return;
      case 0: 
        PendingResult localPendingResult1 = (PendingResult)paramMessage.obj;
        paramMessage = zzsf.zzf(zzsf.this);
        if (localPendingResult1 == null) {}
        for (;;)
        {
          try
          {
            zzsf.zza(zzsf.zzg(zzsf.this), new Status(13, "Transform returned null"));
            return;
          }
          finally {}
          if ((localPendingResult2 instanceof zzrz)) {
            zzsf.zza(zzsf.zzg(zzsf.this), ((zzrz)localPendingResult2).getStatus());
          } else {
            zzsf.zzg(zzsf.this).zza(localPendingResult2);
          }
        }
      }
      RuntimeException localRuntimeException = (RuntimeException)paramMessage.obj;
      paramMessage = String.valueOf(localRuntimeException.getMessage());
      if (paramMessage.length() != 0) {}
      for (paramMessage = "Runtime exception on the transformation worker thread: ".concat(paramMessage);; paramMessage = new String("Runtime exception on the transformation worker thread: "))
      {
        Log.e("TransformedResultImpl", paramMessage);
        throw localRuntimeException;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */