package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.StatusListener;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ICancelToken;
import com.google.android.gms.common.internal.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@KeepName
public abstract class BasePendingResult<R extends Result>
  extends PendingResult<R>
{
  static final ThreadLocal<Boolean> zzez = new zzo();
  @KeepName
  private zza mResultGuardian;
  private Status mStatus;
  private R zzdm;
  private final Object zzfa = new Object();
  private final CallbackHandler<R> zzfb;
  private final WeakReference<GoogleApiClient> zzfc;
  private final CountDownLatch zzfd = new CountDownLatch(1);
  private final ArrayList<PendingResult.StatusListener> zzfe = new ArrayList();
  private ResultCallback<? super R> zzff;
  private final AtomicReference<zzcn> zzfg = new AtomicReference();
  private volatile boolean zzfh;
  private boolean zzfi;
  private boolean zzfj;
  private ICancelToken zzfk;
  private volatile zzch<R> zzfl;
  private boolean zzfm = false;
  
  @Deprecated
  BasePendingResult()
  {
    this.zzfb = new CallbackHandler(Looper.getMainLooper());
    this.zzfc = new WeakReference(null);
  }
  
  protected BasePendingResult(GoogleApiClient paramGoogleApiClient)
  {
    if (paramGoogleApiClient != null) {}
    for (Looper localLooper = paramGoogleApiClient.getLooper();; localLooper = Looper.getMainLooper())
    {
      this.zzfb = new CallbackHandler(localLooper);
      this.zzfc = new WeakReference(paramGoogleApiClient);
      return;
    }
  }
  
  private final R get()
  {
    boolean bool = true;
    synchronized (this.zzfa)
    {
      if (!this.zzfh)
      {
        Preconditions.checkState(bool, "Result has already been consumed.");
        Preconditions.checkState(isReady(), "Result is not ready.");
        Result localResult = this.zzdm;
        this.zzdm = null;
        this.zzff = null;
        this.zzfh = true;
        ??? = (zzcn)this.zzfg.getAndSet(null);
        if (??? != null) {
          ((zzcn)???).zzc(this);
        }
        return localResult;
      }
      bool = false;
    }
  }
  
  private final void zza(R paramR)
  {
    this.zzdm = paramR;
    this.zzfk = null;
    this.zzfd.countDown();
    this.mStatus = this.zzdm.getStatus();
    if (this.zzfi) {
      this.zzff = null;
    }
    for (;;)
    {
      ArrayList localArrayList = (ArrayList)this.zzfe;
      int i = localArrayList.size();
      int j = 0;
      while (j < i)
      {
        paramR = localArrayList.get(j);
        j++;
        ((PendingResult.StatusListener)paramR).onComplete(this.mStatus);
      }
      if (this.zzff == null)
      {
        if ((this.zzdm instanceof Releasable)) {
          this.mResultGuardian = new zza(null);
        }
      }
      else
      {
        this.zzfb.removeMessages(2);
        this.zzfb.zza(this.zzff, get());
      }
    }
    this.zzfe.clear();
  }
  
  public static void zzb(Result paramResult)
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
        Log.w("BasePendingResult", String.valueOf(paramResult).length() + 18 + "Unable to release " + paramResult, localRuntimeException);
      }
    }
  }
  
  public final void addStatusListener(PendingResult.StatusListener paramStatusListener)
  {
    boolean bool;
    if (paramStatusListener != null) {
      bool = true;
    }
    for (;;)
    {
      Preconditions.checkArgument(bool, "Callback cannot be null.");
      synchronized (this.zzfa)
      {
        if (isReady())
        {
          paramStatusListener.onComplete(this.mStatus);
          return;
          bool = false;
          continue;
        }
        this.zzfe.add(paramStatusListener);
      }
    }
  }
  
  public final R await()
  {
    boolean bool1 = true;
    Preconditions.checkNotMainThread("await must not be called on the UI thread");
    boolean bool2;
    if (!this.zzfh) {
      bool2 = true;
    }
    for (;;)
    {
      Preconditions.checkState(bool2, "Result has already been consumed");
      if (this.zzfl == null)
      {
        bool2 = bool1;
        Preconditions.checkState(bool2, "Cannot await if then() has been called.");
      }
      try
      {
        this.zzfd.await();
        Preconditions.checkState(isReady(), "Result is not ready.");
        return get();
        bool2 = false;
        continue;
        bool2 = false;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzb(Status.RESULT_INTERRUPTED);
        }
      }
    }
  }
  
  public final R await(long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool1 = true;
    if (paramLong > 0L) {
      Preconditions.checkNotMainThread("await must not be called on the UI thread when time is greater than zero.");
    }
    boolean bool2;
    if (!this.zzfh) {
      bool2 = true;
    }
    for (;;)
    {
      Preconditions.checkState(bool2, "Result has already been consumed.");
      if (this.zzfl == null)
      {
        bool2 = bool1;
        Preconditions.checkState(bool2, "Cannot await if then() has been called.");
      }
      try
      {
        if (!this.zzfd.await(paramLong, paramTimeUnit)) {
          zzb(Status.RESULT_TIMEOUT);
        }
        Preconditions.checkState(isReady(), "Result is not ready.");
        return get();
        bool2 = false;
        continue;
        bool2 = false;
      }
      catch (InterruptedException paramTimeUnit)
      {
        for (;;)
        {
          zzb(Status.RESULT_INTERRUPTED);
        }
      }
    }
  }
  
  public void cancel()
  {
    for (;;)
    {
      synchronized (this.zzfa)
      {
        if ((this.zzfi) || (this.zzfh)) {
          return;
        }
        ICancelToken localICancelToken = this.zzfk;
        if (localICancelToken == null) {}
      }
      try
      {
        this.zzfk.cancel();
        zzb(this.zzdm);
        this.zzfi = true;
        zza(createFailedResult(Status.RESULT_CANCELED));
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  protected abstract R createFailedResult(Status paramStatus);
  
  public boolean isCanceled()
  {
    synchronized (this.zzfa)
    {
      boolean bool = this.zzfi;
      return bool;
    }
  }
  
  public final boolean isReady()
  {
    if (this.zzfd.getCount() == 0L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void setResult(R paramR)
  {
    boolean bool1 = true;
    for (;;)
    {
      synchronized (this.zzfa)
      {
        if ((!this.zzfj) && (!this.zzfi))
        {
          if ((!isReady()) || (!isReady()))
          {
            bool2 = true;
            Preconditions.checkState(bool2, "Results have already been set");
            if (this.zzfh) {
              break label93;
            }
            bool2 = bool1;
            Preconditions.checkState(bool2, "Result has already been consumed");
            zza(paramR);
          }
        }
        else {
          zzb(paramR);
        }
      }
      boolean bool2 = false;
      continue;
      label93:
      bool2 = false;
    }
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback)
  {
    boolean bool1 = true;
    Object localObject = this.zzfa;
    if (paramResultCallback == null) {}
    label31:
    try
    {
      this.zzff = null;
      return;
    }
    finally {}
    if (!this.zzfh)
    {
      bool2 = true;
      Preconditions.checkState(bool2, "Result has already been consumed.");
      if (this.zzfl != null) {
        break label79;
      }
    }
    label79:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Preconditions.checkState(bool2, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label85;
      }
      break;
      bool2 = false;
      break label31;
    }
    label85:
    if (isReady()) {
      this.zzfb.zza(paramResultCallback, get());
    }
    for (;;)
    {
      break;
      this.zzff = paramResultCallback;
    }
  }
  
  public final void zza(zzcn paramzzcn)
  {
    this.zzfg.set(paramzzcn);
  }
  
  public final void zzb(Status paramStatus)
  {
    synchronized (this.zzfa)
    {
      if (!isReady())
      {
        setResult(createFailedResult(paramStatus));
        this.zzfj = true;
      }
      return;
    }
  }
  
  public final Integer zzo()
  {
    return null;
  }
  
  public final boolean zzw()
  {
    synchronized (this.zzfa)
    {
      if (((GoogleApiClient)this.zzfc.get() == null) || (!this.zzfm)) {
        cancel();
      }
      boolean bool = isCanceled();
      return bool;
    }
  }
  
  public final void zzx()
  {
    if ((this.zzfm) || (((Boolean)zzez.get()).booleanValue())) {}
    for (boolean bool = true;; bool = false)
    {
      this.zzfm = bool;
      return;
    }
  }
  
  public static class CallbackHandler<R extends Result>
    extends Handler
  {
    public CallbackHandler()
    {
      this(Looper.getMainLooper());
    }
    
    public CallbackHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        int i = paramMessage.what;
        Log.wtf("BasePendingResult", 45 + "Don't know how to handle message: " + i, new Exception());
      }
      for (;;)
      {
        return;
        paramMessage = (Pair)paramMessage.obj;
        ResultCallback localResultCallback = (ResultCallback)paramMessage.first;
        paramMessage = (Result)paramMessage.second;
        try
        {
          localResultCallback.onResult(paramMessage);
        }
        catch (RuntimeException localRuntimeException)
        {
          BasePendingResult.zzb(paramMessage);
          throw localRuntimeException;
        }
        ((BasePendingResult)paramMessage.obj).zzb(Status.RESULT_TIMEOUT);
      }
    }
    
    public final void zza(ResultCallback<? super R> paramResultCallback, R paramR)
    {
      sendMessage(obtainMessage(1, new Pair(paramResultCallback, paramR)));
    }
  }
  
  private final class zza
  {
    private zza() {}
    
    protected final void finalize()
      throws Throwable
    {
      BasePendingResult.zzb(BasePendingResult.zza(BasePendingResult.this));
      super.finalize();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/BasePendingResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */