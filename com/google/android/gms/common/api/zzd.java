package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.internal.zzpz;
import com.google.android.gms.internal.zzqc.zza;
import com.google.android.gms.internal.zzqt;
import com.google.android.gms.internal.zzqu;
import com.google.android.gms.internal.zzre;
import com.google.android.gms.internal.zzro;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zzd<O extends Api.ApiOptions>
{
  private final Context mContext;
  private final int mId;
  private final Api<O> tv;
  private final AtomicBoolean vA = new AtomicBoolean(false);
  private final AtomicInteger vB = new AtomicInteger(0);
  private Api.zze vC;
  private final zzre vv;
  private final O vw;
  private final zzpz<O> vx;
  private final zzqt vy;
  private final GoogleApiClient vz;
  private final Looper zzajn;
  
  public zzd(@NonNull Context paramContext, Api<O> paramApi, O paramO) {}
  
  public zzd(@NonNull Context paramContext, Api<O> paramApi, O paramO, Looper paramLooper)
  {
    zzac.zzb(paramContext, "Null context is not permitted.");
    zzac.zzb(paramApi, "Api must not be null.");
    zzac.zzb(paramLooper, "Looper must not be null.");
    this.mContext = paramContext.getApplicationContext();
    this.tv = paramApi;
    this.vw = paramO;
    this.zzajn = paramLooper;
    this.vv = new zzre();
    this.vx = zzpz.zza(this.tv, this.vw);
    this.vz = new zzqu(this);
    paramContext = zzqt.zza(this.mContext, this);
    this.vy = ((zzqt)paramContext.first);
    this.mId = ((Integer)paramContext.second).intValue();
  }
  
  private <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zza(int paramInt, @NonNull T paramT)
  {
    paramT.zzaqt();
    this.vy.zza(this, paramInt, paramT);
    return paramT;
  }
  
  private <TResult, A extends Api.zzb> Task<TResult> zza(int paramInt, @NonNull zzro<A, TResult> paramzzro)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    this.vy.zza(this, paramInt, paramzzro, localTaskCompletionSource);
    return localTaskCompletionSource.getTask();
  }
  
  public int getInstanceId()
  {
    return this.mId;
  }
  
  public Looper getLooper()
  {
    return this.zzajn;
  }
  
  public void release()
  {
    boolean bool = true;
    if (this.vA.getAndSet(true)) {
      return;
    }
    this.vv.release();
    zzqt localzzqt = this.vy;
    int i = this.mId;
    if (this.vB.get() > 0) {}
    for (;;)
    {
      localzzqt.zzd(i, bool);
      return;
      bool = false;
    }
  }
  
  @WorkerThread
  public Api.zze zza(Looper paramLooper, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Api.zzh localzzh;
    if (!zzapw())
    {
      if (!this.tv.zzapq()) {
        break label70;
      }
      localzzh = this.tv.zzapo();
    }
    label70:
    for (this.vC = new zzai(this.mContext, paramLooper, localzzh.zzapt(), paramConnectionCallbacks, paramOnConnectionFailedListener, zzh.zzcd(this.mContext), localzzh.zzr(this.vw));; this.vC = this.tv.zzapn().zza(this.mContext, paramLooper, zzh.zzcd(this.mContext), this.vw, paramConnectionCallbacks, paramOnConnectionFailedListener)) {
      return this.vC;
    }
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zza(@NonNull T paramT)
  {
    return zza(0, paramT);
  }
  
  public <TResult, A extends Api.zzb> Task<TResult> zza(zzro<A, TResult> paramzzro)
  {
    return zza(0, paramzzro);
  }
  
  public void zzapu()
  {
    this.vB.incrementAndGet();
  }
  
  public void zzapv()
  {
    if ((this.vB.decrementAndGet() == 0) && (this.vA.get())) {
      this.vy.zzd(this.mId, false);
    }
  }
  
  public boolean zzapw()
  {
    return this.vC != null;
  }
  
  public zzpz<O> zzapx()
  {
    return this.vx;
  }
  
  public GoogleApiClient zzapy()
  {
    return this.vz;
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    return zza(1, paramT);
  }
  
  public <TResult, A extends Api.zzb> Task<TResult> zzb(zzro<A, TResult> paramzzro)
  {
    return zza(1, paramzzro);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */