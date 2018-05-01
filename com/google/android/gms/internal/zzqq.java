package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzq;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzqq<R extends Result>
  extends PendingResult<R>
{
  static final ThreadLocal<Boolean> yG = new ThreadLocal()
  {
    protected Boolean zzarw()
    {
      return Boolean.valueOf(false);
    }
  };
  private R xV;
  private final Object yH = new Object();
  protected final zza<R> yI;
  protected final WeakReference<GoogleApiClient> yJ;
  private final ArrayList<PendingResult.zza> yK = new ArrayList();
  private ResultCallback<? super R> yL;
  private final AtomicReference<zzsg.zzb> yM = new AtomicReference();
  private zzb yN;
  private volatile boolean yO;
  private boolean yP;
  private zzq yQ;
  private volatile zzsf<R> yR;
  private boolean yS = false;
  private boolean zzak;
  private final CountDownLatch zzank = new CountDownLatch(1);
  
  @Deprecated
  zzqq()
  {
    this.yI = new zza(Looper.getMainLooper());
    this.yJ = new WeakReference(null);
  }
  
  @Deprecated
  protected zzqq(Looper paramLooper)
  {
    this.yI = new zza(paramLooper);
    this.yJ = new WeakReference(null);
  }
  
  protected zzqq(GoogleApiClient paramGoogleApiClient)
  {
    if (paramGoogleApiClient != null) {}
    for (Looper localLooper = paramGoogleApiClient.getLooper();; localLooper = Looper.getMainLooper())
    {
      this.yI = new zza(localLooper);
      this.yJ = new WeakReference(paramGoogleApiClient);
      return;
    }
  }
  
  private R get()
  {
    boolean bool = true;
    synchronized (this.yH)
    {
      if (!this.yO)
      {
        zzaa.zza(bool, "Result has already been consumed.");
        zzaa.zza(isReady(), "Result is not ready.");
        Result localResult = this.xV;
        this.xV = null;
        this.yL = null;
        this.yO = true;
        zzart();
        return localResult;
      }
      bool = false;
    }
  }
  
  private void zzart()
  {
    zzsg.zzb localzzb = (zzsg.zzb)this.yM.getAndSet(null);
    if (localzzb != null) {
      localzzb.zzc(this);
    }
  }
  
  private void zzd(R paramR)
  {
    this.xV = paramR;
    this.yQ = null;
    this.zzank.countDown();
    paramR = this.xV.getStatus();
    if (this.zzak) {
      this.yL = null;
    }
    for (;;)
    {
      Iterator localIterator = this.yK.iterator();
      while (localIterator.hasNext()) {
        ((PendingResult.zza)localIterator.next()).zzx(paramR);
      }
      if (this.yL == null)
      {
        if ((this.xV instanceof Releasable)) {
          this.yN = new zzb(null);
        }
      }
      else
      {
        this.yI.zzarx();
        this.yI.zza(this.yL, get());
      }
    }
    this.yK.clear();
  }
  
  public static void zze(Result paramResult)
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
      Log.w("BasePendingResult", String.valueOf(paramResult).length() + 18 + "Unable to release " + paramResult, localRuntimeException);
    }
  }
  
  public final R await()
  {
    boolean bool2 = true;
    boolean bool1;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool1 = true;
    }
    for (;;)
    {
      zzaa.zza(bool1, "await must not be called on the UI thread");
      if (!this.yO)
      {
        bool1 = true;
        label28:
        zzaa.zza(bool1, "Result has already been consumed");
        if (this.yR != null) {
          break label80;
        }
        bool1 = bool2;
        zzaa.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        this.zzank.await();
        zzaa.zza(isReady(), "Result is not ready.");
        return get();
        bool1 = false;
        continue;
        bool1 = false;
        break label28;
        label80:
        bool1 = false;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzab(Status.ya);
        }
      }
    }
  }
  
  public final R await(long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool2 = true;
    boolean bool1;
    if ((paramLong <= 0L) || (Looper.myLooper() != Looper.getMainLooper())) {
      bool1 = true;
    }
    for (;;)
    {
      zzaa.zza(bool1, "await must not be called on the UI thread when time is greater than zero.");
      if (!this.yO)
      {
        bool1 = true;
        label39:
        zzaa.zza(bool1, "Result has already been consumed.");
        if (this.yR != null) {
          break label109;
        }
        bool1 = bool2;
        zzaa.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        if (!this.zzank.await(paramLong, paramTimeUnit)) {
          zzab(Status.yc);
        }
        zzaa.zza(isReady(), "Result is not ready.");
        return get();
        bool1 = false;
        continue;
        bool1 = false;
        break label39;
        label109:
        bool1 = false;
      }
      catch (InterruptedException paramTimeUnit)
      {
        for (;;)
        {
          zzab(Status.ya);
        }
      }
    }
  }
  
  public void cancel()
  {
    synchronized (this.yH)
    {
      if ((this.zzak) || (this.yO)) {
        return;
      }
      zzq localzzq = this.yQ;
      if (localzzq == null) {}
    }
    try
    {
      this.yQ.cancel();
      zze(this.xV);
      this.zzak = true;
      zzd(zzc(Status.yd));
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public boolean isCanceled()
  {
    synchronized (this.yH)
    {
      boolean bool = this.zzak;
      return bool;
    }
  }
  
  public final boolean isReady()
  {
    return this.zzank.getCount() == 0L;
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback)
  {
    boolean bool2 = true;
    Object localObject = this.yH;
    if (paramResultCallback == null) {}
    try
    {
      this.yL = null;
      return;
    }
    finally {}
    if (!this.yO)
    {
      bool1 = true;
      zzaa.zza(bool1, "Result has already been consumed.");
      if (this.yR != null) {
        break label77;
      }
    }
    label77:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzaa.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label82;
      }
      return;
      bool1 = false;
      break;
    }
    label82:
    if (isReady()) {
      this.yI.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.yL = paramResultCallback;
    }
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback, long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool2 = true;
    Object localObject = this.yH;
    if (paramResultCallback == null) {}
    try
    {
      this.yL = null;
      return;
    }
    finally {}
    if (!this.yO)
    {
      bool1 = true;
      zzaa.zza(bool1, "Result has already been consumed.");
      if (this.yR != null) {
        break label84;
      }
    }
    label84:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzaa.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label90;
      }
      return;
      bool1 = false;
      break;
    }
    label90:
    if (isReady()) {
      this.yI.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.yL = paramResultCallback;
      this.yI.zza(this, paramTimeUnit.toMillis(paramLong));
    }
  }
  
  public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    boolean bool1;
    if (!this.yO)
    {
      bool1 = true;
      zzaa.zza(bool1, "Result has already been consumed.");
    }
    for (;;)
    {
      synchronized (this.yH)
      {
        if (this.yR != null) {
          break label136;
        }
        bool1 = true;
        zzaa.zza(bool1, "Cannot call then() twice.");
        if (this.yL != null) {
          break label141;
        }
        bool1 = bool2;
        zzaa.zza(bool1, "Cannot call then() if callbacks are set.");
        this.yS = true;
        this.yR = new zzsf(this.yJ);
        paramResultTransform = this.yR.then(paramResultTransform);
        if (isReady())
        {
          this.yI.zza(this.yR, get());
          return paramResultTransform;
        }
        this.yL = this.yR;
      }
      bool1 = false;
      break;
      label136:
      bool1 = false;
      continue;
      label141:
      bool1 = false;
    }
  }
  
  public final void zza(PendingResult.zza paramzza)
  {
    boolean bool2 = true;
    if (!this.yO)
    {
      bool1 = true;
      zzaa.zza(bool1, "Result has already been consumed.");
      if (paramzza == null) {
        break label88;
      }
    }
    label88:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzaa.zzb(bool1, "Callback cannot be null.");
      synchronized (this.yH)
      {
        if (isReady())
        {
          paramzza.zzx(this.xV.getStatus());
          return;
        }
        this.yK.add(paramzza);
      }
      bool1 = false;
      break;
    }
  }
  
  protected final void zza(zzq paramzzq)
  {
    synchronized (this.yH)
    {
      this.yQ = paramzzq;
      return;
    }
  }
  
  public void zza(zzsg.zzb paramzzb)
  {
    this.yM.set(paramzzb);
  }
  
  public final void zzab(Status paramStatus)
  {
    synchronized (this.yH)
    {
      if (!isReady())
      {
        zzc(zzc(paramStatus));
        this.yP = true;
      }
      return;
    }
  }
  
  public Integer zzarh()
  {
    return null;
  }
  
  public boolean zzars()
  {
    synchronized (this.yH)
    {
      if (((GoogleApiClient)this.yJ.get() == null) || (!this.yS)) {
        cancel();
      }
      boolean bool = isCanceled();
      return bool;
    }
  }
  
  public void zzaru()
  {
    setResultCallback(null);
  }
  
  public void zzarv()
  {
    if ((this.yS) || (((Boolean)yG.get()).booleanValue())) {}
    for (boolean bool = true;; bool = false)
    {
      this.yS = bool;
      return;
    }
  }
  
  protected abstract R zzc(Status paramStatus);
  
  public final void zzc(R paramR)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.yH)
      {
        if ((!this.yP) && (!this.zzak))
        {
          if ((!isReady()) || (!isReady()))
          {
            bool1 = true;
            zzaa.zza(bool1, "Results have already been set");
            if (this.yO) {
              break label91;
            }
            bool1 = bool2;
            zzaa.zza(bool1, "Result has already been consumed");
            zzd(paramR);
          }
        }
        else
        {
          zze(paramR);
          return;
        }
      }
      boolean bool1 = false;
      continue;
      label91:
      bool1 = false;
    }
  }
  
  public static class zza<R extends Result>
    extends Handler
  {
    public zza()
    {
      this(Looper.getMainLooper());
    }
    
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
        Log.wtf("BasePendingResult", 45 + "Don't know how to handle message: " + i, new Exception());
        return;
      case 1: 
        paramMessage = (Pair)paramMessage.obj;
        zzb((ResultCallback)paramMessage.first, (Result)paramMessage.second);
        return;
      }
      ((zzqq)paramMessage.obj).zzab(Status.yc);
    }
    
    public void zza(ResultCallback<? super R> paramResultCallback, R paramR)
    {
      sendMessage(obtainMessage(1, new Pair(paramResultCallback, paramR)));
    }
    
    public void zza(zzqq<R> paramzzqq, long paramLong)
    {
      sendMessageDelayed(obtainMessage(2, paramzzqq), paramLong);
    }
    
    public void zzarx()
    {
      removeMessages(2);
    }
    
    protected void zzb(ResultCallback<? super R> paramResultCallback, R paramR)
    {
      try
      {
        paramResultCallback.onResult(paramR);
        return;
      }
      catch (RuntimeException paramResultCallback)
      {
        zzqq.zze(paramR);
        throw paramResultCallback;
      }
    }
  }
  
  private final class zzb
  {
    private zzb() {}
    
    protected void finalize()
      throws Throwable
    {
      zzqq.zze(zzqq.zza(zzqq.this));
      super.finalize();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */