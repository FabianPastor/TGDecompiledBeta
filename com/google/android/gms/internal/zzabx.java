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

public class zzabx<R extends Result>
  extends TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object zzaAh = new Object();
  private final WeakReference<GoogleApiClient> zzaAj;
  private ResultTransform<? super R, ? extends Result> zzaDl = null;
  private zzabx<? extends Result> zzaDm = null;
  private volatile ResultCallbacks<? super R> zzaDn = null;
  private PendingResult<R> zzaDo = null;
  private Status zzaDp = null;
  private final zza zzaDq;
  private boolean zzaDr = false;
  
  public zzabx(WeakReference<GoogleApiClient> paramWeakReference)
  {
    zzac.zzb(paramWeakReference, "GoogleApiClient reference must not be null");
    this.zzaAj = paramWeakReference;
    paramWeakReference = (GoogleApiClient)this.zzaAj.get();
    if (paramWeakReference != null) {}
    for (paramWeakReference = paramWeakReference.getLooper();; paramWeakReference = Looper.getMainLooper())
    {
      this.zzaDq = new zza(paramWeakReference);
      return;
    }
  }
  
  private void zzE(Status paramStatus)
  {
    synchronized (this.zzaAh)
    {
      this.zzaDp = paramStatus;
      zzF(this.zzaDp);
      return;
    }
  }
  
  private void zzF(Status paramStatus)
  {
    synchronized (this.zzaAh)
    {
      if (this.zzaDl != null)
      {
        paramStatus = this.zzaDl.onFailure(paramStatus);
        zzac.zzb(paramStatus, "onFailure must not return null");
        this.zzaDm.zzE(paramStatus);
      }
      while (!zzxc()) {
        return;
      }
      this.zzaDn.onFailure(paramStatus);
    }
  }
  
  private void zzd(Result paramResult)
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
  
  private void zzxa()
  {
    if ((this.zzaDl == null) && (this.zzaDn == null)) {}
    do
    {
      return;
      GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzaAj.get();
      if ((!this.zzaDr) && (this.zzaDl != null) && (localGoogleApiClient != null))
      {
        localGoogleApiClient.zza(this);
        this.zzaDr = true;
      }
      if (this.zzaDp != null)
      {
        zzF(this.zzaDp);
        return;
      }
    } while (this.zzaDo == null);
    this.zzaDo.setResultCallback(this);
  }
  
  private boolean zzxc()
  {
    GoogleApiClient localGoogleApiClient = (GoogleApiClient)this.zzaAj.get();
    return (this.zzaDn != null) && (localGoogleApiClient != null);
  }
  
  public void andFinally(@NonNull ResultCallbacks<? super R> paramResultCallbacks)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.zzaAh)
      {
        if (this.zzaDn == null)
        {
          bool1 = true;
          zzac.zza(bool1, "Cannot call andFinally() twice.");
          if (this.zzaDl != null) {
            break label65;
          }
          bool1 = bool2;
          zzac.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zzaDn = paramResultCallbacks;
          zzxa();
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
      synchronized (this.zzaAh)
      {
        if (paramR.getStatus().isSuccess())
        {
          if (this.zzaDl != null)
          {
            zzabo.zzwv().submit(new Runnable()
            {
              @WorkerThread
              public void run()
              {
                try
                {
                  zzaaf.zzaAg.set(Boolean.valueOf(true));
                  Object localObject1 = zzabx.zzc(zzabx.this).onSuccess(paramR);
                  zzabx.zzd(zzabx.this).sendMessage(zzabx.zzd(zzabx.this).obtainMessage(0, localObject1));
                  zzaaf.zzaAg.set(Boolean.valueOf(false));
                  zzabx.zza(zzabx.this, paramR);
                  localObject1 = (GoogleApiClient)zzabx.zze(zzabx.this).get();
                  if (localObject1 != null) {
                    ((GoogleApiClient)localObject1).zzb(zzabx.this);
                  }
                  return;
                }
                catch (RuntimeException localRuntimeException)
                {
                  zzabx.zzd(zzabx.this).sendMessage(zzabx.zzd(zzabx.this).obtainMessage(1, localRuntimeException));
                  GoogleApiClient localGoogleApiClient1;
                  return;
                }
                finally
                {
                  zzaaf.zzaAg.set(Boolean.valueOf(false));
                  zzabx.zza(zzabx.this, paramR);
                  GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzabx.zze(zzabx.this).get();
                  if (localGoogleApiClient2 != null) {
                    localGoogleApiClient2.zzb(zzabx.this);
                  }
                }
              }
            });
            return;
          }
          if (!zzxc()) {
            continue;
          }
          this.zzaDn.onSuccess(paramR);
        }
      }
      zzE(paramR.getStatus());
      zzd(paramR);
    }
  }
  
  @NonNull
  public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.zzaAh)
      {
        if (this.zzaDl == null)
        {
          bool1 = true;
          zzac.zza(bool1, "Cannot call then() twice.");
          if (this.zzaDn != null) {
            break label83;
          }
          bool1 = bool2;
          zzac.zza(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          this.zzaDl = paramResultTransform;
          paramResultTransform = new zzabx(this.zzaAj);
          this.zzaDm = paramResultTransform;
          zzxa();
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
    synchronized (this.zzaAh)
    {
      this.zzaDo = paramPendingResult;
      zzxa();
      return;
    }
  }
  
  void zzxb()
  {
    this.zzaDn = null;
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
        paramMessage = zzabx.zzf(zzabx.this);
        if (localPendingResult1 == null) {}
        for (;;)
        {
          try
          {
            zzabx.zza(zzabx.zzg(zzabx.this), new Status(13, "Transform returned null"));
            return;
          }
          finally {}
          if ((localPendingResult2 instanceof zzabp)) {
            zzabx.zza(zzabx.zzg(zzabx.this), ((zzabp)localPendingResult2).getStatus());
          } else {
            zzabx.zzg(zzabx.this).zza(localPendingResult2);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */