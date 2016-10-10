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
import com.google.android.gms.common.internal.zzac;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public class zzrp<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object wG = new Object();
  private final WeakReference<GoogleApiClient> wI;
  private ResultTransform<? super R, ? extends Result> zk = null;
  private zzrp<? extends Result> zl = null;
  private volatile ResultCallbacks<? super R> zm = null;
  private PendingResult<R> zn = null;
  private Status zo = null;
  private final zza zp;
  private boolean zq = false;
  
  public zzrp(WeakReference<GoogleApiClient> paramWeakReference)
  {
    zzac.zzb(paramWeakReference, "GoogleApiClient reference must not be null");
    this.wI = paramWeakReference;
    paramWeakReference = (GoogleApiClient)this.wI.get();
    if (paramWeakReference != null) {}
    for (paramWeakReference = paramWeakReference.getLooper();; paramWeakReference = Looper.getMainLooper())
    {
      this.zp = new zza(paramWeakReference);
      return;
    }
  }
  
  private void zzac(Status paramStatus)
  {
    synchronized (this.wG)
    {
      this.zo = paramStatus;
      zzad(this.zo);
      return;
    }
  }
  
  private void zzad(Status paramStatus)
  {
    synchronized (this.wG)
    {
      if (this.zk != null)
      {
        paramStatus = this.zk.onFailure(paramStatus);
        zzac.zzb(paramStatus, "onFailure must not return null");
        this.zl.zzac(paramStatus);
      }
      while (!zzasv()) {
        return;
      }
      this.zm.onFailure(paramStatus);
    }
  }
  
  private void zzast()
  {
    if ((this.zk == null) && (this.zm == null)) {}
    do
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.wI.get();
      if ((!this.zq) && (this.zk != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.zq = true;
      }
      if (this.zo != null)
      {
        zzad(this.zo);
        return;
      }
    } while (this.zn == null);
    this.zn.setResultCallback(this);
  }
  
  private boolean zzasv()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.wI.get();
    return (this.zm != null) && (localGoogleApiClient != null);
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
      synchronized (this.wG)
      {
        if (this.zm == null)
        {
          bool1 = true;
          zzac.zza(bool1, "Cannot call andFinally() twice.");
          if (this.zk != null) {
            break label65;
          }
          bool1 = bool2;
          zzac.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zm = paramResultCallbacks;
          zzast();
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
      synchronized (this.wG)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.zk != null)
          {
            zzrj.zzarz().submit(new Runnable()
            {
              @WorkerThread
              public void run()
              {
                try
                {
                  zzqe.wF.set(Boolean.valueOf(true));
                  Object localObject1 = zzrp.zzc(zzrp.this).onSuccess(paramR);
                  zzrp.zzd(zzrp.this).sendMessage(zzrp.zzd(zzrp.this).obtainMessage(0, localObject1));
                  zzqe.wF.set(Boolean.valueOf(false));
                  zzrp.zza(zzrp.this, paramR);
                  localObject1 = (GoogleApiClient)zzrp.zze(zzrp.this).get();
                  if (localObject1 != null) {
                    ((GoogleApiClient)localObject1).zzb(zzrp.this);
                  }
                  return;
                }
                catch (RuntimeException localRuntimeException)
                {
                  zzrp.zzd(zzrp.this).sendMessage(zzrp.zzd(zzrp.this).obtainMessage(1, localRuntimeException));
                  GoogleApiClient localGoogleApiClient1;
                  return;
                }
                finally
                {
                  zzqe.wF.set(Boolean.valueOf(false));
                  zzrp.zza(zzrp.this, paramR);
                  GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzrp.zze(zzrp.this).get();
                  if (localGoogleApiClient2 != null) {
                    localGoogleApiClient2.zzb(zzrp.this);
                  }
                }
              }
            });
            return;
          }
          if (!zzasv()) {
            continue;
          }
          this.zm.onSuccess(paramR);
        }
      }
      zzac(paramR.getStatus());
      zze(paramR);
    }
  }
  
  @NonNull
  public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.wG)
      {
        if (this.zk == null)
        {
          bool1 = true;
          zzac.zza(bool1, "Cannot call then() twice.");
          if (this.zm != null) {
            break label83;
          }
          bool1 = bool2;
          zzac.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zk = paramResultTransform;
          paramResultTransform = new zzrp(this.wI);
          this.zl = paramResultTransform;
          zzast();
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
    synchronized (this.wG)
    {
      this.zn = paramPendingResult;
      zzast();
      return;
    }
  }
  
  void zzasu()
  {
    this.zm = null;
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
        paramMessage = zzrp.zzf(zzrp.this);
        if (localPendingResult1 == null) {}
        for (;;)
        {
          try
          {
            zzrp.zza(zzrp.zzg(zzrp.this), new Status(13, "Transform returned null"));
            return;
          }
          finally {}
          if ((localPendingResult2 instanceof zzrk)) {
            zzrp.zza(zzrp.zzg(zzrp.this), ((zzrk)localPendingResult2).getStatus());
          } else {
            zzrp.zzg(zzrp.this).zza(localPendingResult2);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */