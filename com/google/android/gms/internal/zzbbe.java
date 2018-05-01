package com.google.android.gms.internal;

import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzao;
import com.google.android.gms.common.internal.zzbo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzbbe<R extends Result>
  extends PendingResult<R>
{
  static final ThreadLocal<Boolean> zzaBV = new zzbbf();
  private Status mStatus;
  private boolean zzJ;
  private final Object zzaBW = new Object();
  private zzbbg<R> zzaBX;
  private WeakReference<GoogleApiClient> zzaBY;
  private final ArrayList<PendingResult.zza> zzaBZ = new ArrayList();
  private R zzaBj;
  private ResultCallback<? super R> zzaCa;
  private final AtomicReference<zzbex> zzaCb = new AtomicReference();
  private zzbbh zzaCc;
  private volatile boolean zzaCd;
  private boolean zzaCe;
  private zzao zzaCf;
  private volatile zzbes<R> zzaCg;
  private boolean zzaCh = false;
  private final CountDownLatch zztJ = new CountDownLatch(1);
  
  @Deprecated
  zzbbe()
  {
    this.zzaBX = new zzbbg(Looper.getMainLooper());
    this.zzaBY = new WeakReference(null);
  }
  
  @Deprecated
  protected zzbbe(Looper paramLooper)
  {
    this.zzaBX = new zzbbg(paramLooper);
    this.zzaBY = new WeakReference(null);
  }
  
  protected zzbbe(GoogleApiClient paramGoogleApiClient)
  {
    if (paramGoogleApiClient != null) {}
    for (Looper localLooper = paramGoogleApiClient.getLooper();; localLooper = Looper.getMainLooper())
    {
      this.zzaBX = new zzbbg(localLooper);
      this.zzaBY = new WeakReference(paramGoogleApiClient);
      return;
    }
  }
  
  private final R get()
  {
    boolean bool = true;
    synchronized (this.zzaBW)
    {
      if (!this.zzaCd)
      {
        zzbo.zza(bool, "Result has already been consumed.");
        zzbo.zza(isReady(), "Result is not ready.");
        Result localResult = this.zzaBj;
        this.zzaBj = null;
        this.zzaCa = null;
        this.zzaCd = true;
        ??? = (zzbex)this.zzaCb.getAndSet(null);
        if (??? != null) {
          ((zzbex)???).zzc(this);
        }
        return localResult;
      }
      bool = false;
    }
  }
  
  private final void zzb(R paramR)
  {
    this.zzaBj = paramR;
    this.zzaCf = null;
    this.zztJ.countDown();
    this.mStatus = this.zzaBj.getStatus();
    if (this.zzJ) {
      this.zzaCa = null;
    }
    for (;;)
    {
      paramR = (ArrayList)this.zzaBZ;
      int j = paramR.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = paramR.get(i);
        i += 1;
        ((PendingResult.zza)localObject).zzo(this.mStatus);
      }
      if (this.zzaCa == null)
      {
        if ((this.zzaBj instanceof Releasable)) {
          this.zzaCc = new zzbbh(this, null);
        }
      }
      else
      {
        this.zzaBX.removeMessages(2);
        this.zzaBX.zza(this.zzaCa, get());
      }
    }
    this.zzaBZ.clear();
  }
  
  public static void zzc(Result paramResult)
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
      zzbo.zza(bool1, "await must not be called on the UI thread");
      if (!this.zzaCd)
      {
        bool1 = true;
        label28:
        zzbo.zza(bool1, "Result has already been consumed");
        if (this.zzaCg != null) {
          break label80;
        }
        bool1 = bool2;
        zzbo.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        this.zztJ.await();
        zzbo.zza(isReady(), "Result is not ready.");
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
          zzs(Status.zzaBn);
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
      zzbo.zza(bool1, "await must not be called on the UI thread when time is greater than zero.");
      if (!this.zzaCd)
      {
        bool1 = true;
        label38:
        zzbo.zza(bool1, "Result has already been consumed.");
        if (this.zzaCg != null) {
          break label108;
        }
        bool1 = bool2;
        zzbo.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        if (!this.zztJ.await(paramLong, paramTimeUnit)) {
          zzs(Status.zzaBp);
        }
        zzbo.zza(isReady(), "Result is not ready.");
        return get();
        bool1 = false;
        continue;
        bool1 = false;
        break label38;
        label108:
        bool1 = false;
      }
      catch (InterruptedException paramTimeUnit)
      {
        for (;;)
        {
          zzs(Status.zzaBn);
        }
      }
    }
  }
  
  public void cancel()
  {
    synchronized (this.zzaBW)
    {
      if ((this.zzJ) || (this.zzaCd)) {
        return;
      }
      zzao localzzao = this.zzaCf;
      if (localzzao == null) {}
    }
    try
    {
      this.zzaCf.cancel();
      zzc(this.zzaBj);
      this.zzJ = true;
      zzb(zzb(Status.zzaBq));
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
    synchronized (this.zzaBW)
    {
      boolean bool = this.zzJ;
      return bool;
    }
  }
  
  public final boolean isReady()
  {
    return this.zztJ.getCount() == 0L;
  }
  
  public final void setResult(R paramR)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.zzaBW)
      {
        if ((!this.zzaCe) && (!this.zzJ))
        {
          if ((!isReady()) || (!isReady()))
          {
            bool1 = true;
            zzbo.zza(bool1, "Results have already been set");
            if (this.zzaCd) {
              break label91;
            }
            bool1 = bool2;
            zzbo.zza(bool1, "Result has already been consumed");
            zzb(paramR);
          }
        }
        else
        {
          zzc(paramR);
          return;
        }
      }
      boolean bool1 = false;
      continue;
      label91:
      bool1 = false;
    }
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback)
  {
    boolean bool2 = true;
    Object localObject = this.zzaBW;
    if (paramResultCallback == null) {}
    try
    {
      this.zzaCa = null;
      return;
    }
    finally {}
    if (!this.zzaCd)
    {
      bool1 = true;
      zzbo.zza(bool1, "Result has already been consumed.");
      if (this.zzaCg != null) {
        break label77;
      }
    }
    label77:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label82;
      }
      return;
      bool1 = false;
      break;
    }
    label82:
    if (isReady()) {
      this.zzaBX.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.zzaCa = paramResultCallback;
    }
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback, long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool2 = true;
    Object localObject = this.zzaBW;
    if (paramResultCallback == null) {}
    try
    {
      this.zzaCa = null;
      return;
    }
    finally {}
    if (!this.zzaCd)
    {
      bool1 = true;
      zzbo.zza(bool1, "Result has already been consumed.");
      if (this.zzaCg != null) {
        break label84;
      }
    }
    label84:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label90;
      }
      return;
      bool1 = false;
      break;
    }
    label90:
    if (isReady()) {
      this.zzaBX.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.zzaCa = paramResultCallback;
      paramResultCallback = this.zzaBX;
      paramLong = paramTimeUnit.toMillis(paramLong);
      paramResultCallback.sendMessageDelayed(paramResultCallback.obtainMessage(2, this), paramLong);
    }
  }
  
  public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    boolean bool1;
    if (!this.zzaCd)
    {
      bool1 = true;
      zzbo.zza(bool1, "Result has already been consumed.");
    }
    for (;;)
    {
      synchronized (this.zzaBW)
      {
        if (this.zzaCg != null) {
          break label152;
        }
        bool1 = true;
        zzbo.zza(bool1, "Cannot call then() twice.");
        if (this.zzaCa != null) {
          break label157;
        }
        bool1 = true;
        zzbo.zza(bool1, "Cannot call then() if callbacks are set.");
        if (this.zzJ) {
          break label162;
        }
        bool1 = bool2;
        zzbo.zza(bool1, "Cannot call then() if result was canceled.");
        this.zzaCh = true;
        this.zzaCg = new zzbes(this.zzaBY);
        paramResultTransform = this.zzaCg.then(paramResultTransform);
        if (isReady())
        {
          this.zzaBX.zza(this.zzaCg, get());
          return paramResultTransform;
        }
        this.zzaCa = this.zzaCg;
      }
      bool1 = false;
      break;
      label152:
      bool1 = false;
      continue;
      label157:
      bool1 = false;
      continue;
      label162:
      bool1 = false;
    }
  }
  
  public final void zza(PendingResult.zza paramzza)
  {
    if (paramzza != null) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Callback cannot be null.");
      synchronized (this.zzaBW)
      {
        if (isReady())
        {
          paramzza.zzo(this.mStatus);
          return;
        }
        this.zzaBZ.add(paramzza);
      }
    }
  }
  
  protected final void zza(zzao paramzzao)
  {
    synchronized (this.zzaBW)
    {
      this.zzaCf = paramzzao;
      return;
    }
  }
  
  public final void zza(zzbex paramzzbex)
  {
    this.zzaCb.set(paramzzbex);
  }
  
  @NonNull
  protected abstract R zzb(Status paramStatus);
  
  public final boolean zzpB()
  {
    synchronized (this.zzaBW)
    {
      if (((GoogleApiClient)this.zzaBY.get() == null) || (!this.zzaCh)) {
        cancel();
      }
      boolean bool = isCanceled();
      return bool;
    }
  }
  
  public final void zzpC()
  {
    if ((this.zzaCh) || (((Boolean)zzaBV.get()).booleanValue())) {}
    for (boolean bool = true;; bool = false)
    {
      this.zzaCh = bool;
      return;
    }
  }
  
  public final Integer zzpo()
  {
    return null;
  }
  
  public final void zzs(Status paramStatus)
  {
    synchronized (this.zzaBW)
    {
      if (!isReady())
      {
        setResult(zzb(paramStatus));
        this.zzaCe = true;
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */