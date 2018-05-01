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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzqe<R extends Result>
  extends PendingResult<R>
{
  static final ThreadLocal<Boolean> wF = new ThreadLocal()
  {
    protected Boolean zzaqv()
    {
      return Boolean.valueOf(false);
    }
  };
  private R vU;
  private final Object wG = new Object();
  protected final zza<R> wH;
  protected final WeakReference<GoogleApiClient> wI;
  private final ArrayList<PendingResult.zza> wJ = new ArrayList();
  private ResultCallback<? super R> wK;
  private final AtomicReference<zzrq.zzb> wL = new AtomicReference();
  private zzb wM;
  private volatile boolean wN;
  private boolean wO;
  private zzs wP;
  private volatile zzrp<R> wQ;
  private boolean wR = false;
  private boolean zzak;
  private final CountDownLatch zzamx = new CountDownLatch(1);
  
  @Deprecated
  zzqe()
  {
    this.wH = new zza(Looper.getMainLooper());
    this.wI = new WeakReference(null);
  }
  
  @Deprecated
  protected zzqe(Looper paramLooper)
  {
    this.wH = new zza(paramLooper);
    this.wI = new WeakReference(null);
  }
  
  protected zzqe(GoogleApiClient paramGoogleApiClient)
  {
    if (paramGoogleApiClient != null) {}
    for (Looper localLooper = paramGoogleApiClient.getLooper();; localLooper = Looper.getMainLooper())
    {
      this.wH = new zza(localLooper);
      this.wI = new WeakReference(paramGoogleApiClient);
      return;
    }
  }
  
  private R get()
  {
    boolean bool = true;
    synchronized (this.wG)
    {
      if (!this.wN)
      {
        zzac.zza(bool, "Result has already been consumed.");
        zzac.zza(isReady(), "Result is not ready.");
        Result localResult = this.vU;
        this.vU = null;
        this.wK = null;
        this.wN = true;
        zzaqr();
        return localResult;
      }
      bool = false;
    }
  }
  
  private void zzaqr()
  {
    zzrq.zzb localzzb = (zzrq.zzb)this.wL.getAndSet(null);
    if (localzzb != null) {
      localzzb.zzc(this);
    }
  }
  
  private void zzd(R paramR)
  {
    this.vU = paramR;
    this.wP = null;
    this.zzamx.countDown();
    paramR = this.vU.getStatus();
    if (this.zzak) {
      this.wK = null;
    }
    for (;;)
    {
      Iterator localIterator = this.wJ.iterator();
      while (localIterator.hasNext()) {
        ((PendingResult.zza)localIterator.next()).zzv(paramR);
      }
      if (this.wK == null)
      {
        if ((this.vU instanceof Releasable)) {
          this.wM = new zzb(null);
        }
      }
      else
      {
        this.wH.zzaqw();
        this.wH.zza(this.wK, get());
      }
    }
    this.wJ.clear();
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
      zzac.zza(bool1, "await must not be called on the UI thread");
      if (!this.wN)
      {
        bool1 = true;
        label28:
        zzac.zza(bool1, "Result has already been consumed");
        if (this.wQ != null) {
          break label80;
        }
        bool1 = bool2;
        zzac.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        this.zzamx.await();
        zzac.zza(isReady(), "Result is not ready.");
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
          zzaa(Status.vZ);
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
      zzac.zza(bool1, "await must not be called on the UI thread when time is greater than zero.");
      if (!this.wN)
      {
        bool1 = true;
        label39:
        zzac.zza(bool1, "Result has already been consumed.");
        if (this.wQ != null) {
          break label109;
        }
        bool1 = bool2;
        zzac.zza(bool1, "Cannot await if then() has been called.");
      }
      try
      {
        if (!this.zzamx.await(paramLong, paramTimeUnit)) {
          zzaa(Status.wb);
        }
        zzac.zza(isReady(), "Result is not ready.");
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
          zzaa(Status.vZ);
        }
      }
    }
  }
  
  public void cancel()
  {
    synchronized (this.wG)
    {
      if ((this.zzak) || (this.wN)) {
        return;
      }
      zzs localzzs = this.wP;
      if (localzzs == null) {}
    }
    try
    {
      this.wP.cancel();
      zze(this.vU);
      this.zzak = true;
      zzd(zzc(Status.wc));
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
    synchronized (this.wG)
    {
      boolean bool = this.zzak;
      return bool;
    }
  }
  
  public final boolean isReady()
  {
    return this.zzamx.getCount() == 0L;
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback)
  {
    boolean bool2 = true;
    Object localObject = this.wG;
    if (paramResultCallback == null) {}
    try
    {
      this.wK = null;
      return;
    }
    finally {}
    if (!this.wN)
    {
      bool1 = true;
      zzac.zza(bool1, "Result has already been consumed.");
      if (this.wQ != null) {
        break label77;
      }
    }
    label77:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label82;
      }
      return;
      bool1 = false;
      break;
    }
    label82:
    if (isReady()) {
      this.wH.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.wK = paramResultCallback;
    }
  }
  
  public final void setResultCallback(ResultCallback<? super R> paramResultCallback, long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool2 = true;
    Object localObject = this.wG;
    if (paramResultCallback == null) {}
    try
    {
      this.wK = null;
      return;
    }
    finally {}
    if (!this.wN)
    {
      bool1 = true;
      zzac.zza(bool1, "Result has already been consumed.");
      if (this.wQ != null) {
        break label84;
      }
    }
    label84:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zza(bool1, "Cannot set callbacks if then() has been called.");
      if (!isCanceled()) {
        break label90;
      }
      return;
      bool1 = false;
      break;
    }
    label90:
    if (isReady()) {
      this.wH.zza(paramResultCallback, get());
    }
    for (;;)
    {
      return;
      this.wK = paramResultCallback;
      this.wH.zza(this, paramTimeUnit.toMillis(paramLong));
    }
  }
  
  public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> paramResultTransform)
  {
    boolean bool2 = true;
    boolean bool1;
    if (!this.wN)
    {
      bool1 = true;
      zzac.zza(bool1, "Result has already been consumed.");
    }
    for (;;)
    {
      synchronized (this.wG)
      {
        if (this.wQ != null) {
          break label136;
        }
        bool1 = true;
        zzac.zza(bool1, "Cannot call then() twice.");
        if (this.wK != null) {
          break label141;
        }
        bool1 = bool2;
        zzac.zza(bool1, "Cannot call then() if callbacks are set.");
        this.wR = true;
        this.wQ = new zzrp(this.wI);
        paramResultTransform = this.wQ.then(paramResultTransform);
        if (isReady())
        {
          this.wH.zza(this.wQ, get());
          return paramResultTransform;
        }
        this.wK = this.wQ;
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
    if (!this.wN)
    {
      bool1 = true;
      zzac.zza(bool1, "Result has already been consumed.");
      if (paramzza == null) {
        break label88;
      }
    }
    label88:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzb(bool1, "Callback cannot be null.");
      synchronized (this.wG)
      {
        if (isReady())
        {
          paramzza.zzv(this.vU.getStatus());
          return;
        }
        this.wJ.add(paramzza);
      }
      bool1 = false;
      break;
    }
  }
  
  protected final void zza(zzs paramzzs)
  {
    synchronized (this.wG)
    {
      this.wP = paramzzs;
      return;
    }
  }
  
  public void zza(zzrq.zzb paramzzb)
  {
    this.wL.set(paramzzb);
  }
  
  public final void zzaa(Status paramStatus)
  {
    synchronized (this.wG)
    {
      if (!isReady())
      {
        zzc(zzc(paramStatus));
        this.wO = true;
      }
      return;
    }
  }
  
  public Integer zzaqf()
  {
    return null;
  }
  
  public boolean zzaqq()
  {
    synchronized (this.wG)
    {
      if (((GoogleApiClient)this.wI.get() == null) || (!this.wR)) {
        cancel();
      }
      boolean bool = isCanceled();
      return bool;
    }
  }
  
  public void zzaqs()
  {
    setResultCallback(null);
  }
  
  public void zzaqt()
  {
    if ((this.wR) || (((Boolean)wF.get()).booleanValue())) {}
    for (boolean bool = true;; bool = false)
    {
      this.wR = bool;
      return;
    }
  }
  
  boolean zzaqu()
  {
    return false;
  }
  
  protected abstract R zzc(Status paramStatus);
  
  public final void zzc(R paramR)
  {
    boolean bool2 = true;
    for (;;)
    {
      synchronized (this.wG)
      {
        if ((this.wO) || (this.zzak) || ((isReady()) && (zzaqu())))
        {
          zze(paramR);
          return;
        }
        if (!isReady())
        {
          bool1 = true;
          zzac.zza(bool1, "Results have already been set");
          if (this.wN) {
            break label98;
          }
          bool1 = bool2;
          zzac.zza(bool1, "Result has already been consumed");
          zzd(paramR);
          return;
        }
      }
      boolean bool1 = false;
      continue;
      label98:
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
      ((zzqe)paramMessage.obj).zzaa(Status.wb);
    }
    
    public void zza(ResultCallback<? super R> paramResultCallback, R paramR)
    {
      sendMessage(obtainMessage(1, new Pair(paramResultCallback, paramR)));
    }
    
    public void zza(zzqe<R> paramzzqe, long paramLong)
    {
      sendMessageDelayed(obtainMessage(2, paramzzqe), paramLong);
    }
    
    public void zzaqw()
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
        zzqe.zze(paramR);
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
      zzqe.zze(zzqe.zza(zzqe.this));
      super.finalize();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */