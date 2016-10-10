package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.zzc;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzqr
  implements zzqg, zzqy
{
  private final Context mContext;
  final Api.zza<? extends zzwz, zzxa> vQ;
  final zzqp wV;
  final zzh xB;
  final Map<Api<?>, Integer> xC;
  final Map<Api.zzc<?>, Api.zze> xW;
  private final Lock xf;
  private final zzc xn;
  private final Condition yj;
  private final zzb yk;
  final Map<Api.zzc<?>, ConnectionResult> yl = new HashMap();
  private volatile zzqq ym;
  private ConnectionResult yn = null;
  int yo;
  final zzqy.zza yp;
  
  public zzqr(Context paramContext, zzqp paramzzqp, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap, zzh paramzzh, Map<Api<?>, Integer> paramMap1, Api.zza<? extends zzwz, zzxa> paramzza, ArrayList<zzqf> paramArrayList, zzqy.zza paramzza1)
  {
    this.mContext = paramContext;
    this.xf = paramLock;
    this.xn = paramzzc;
    this.xW = paramMap;
    this.xB = paramzzh;
    this.xC = paramMap1;
    this.vQ = paramzza;
    this.wV = paramzzqp;
    this.yp = paramzza1;
    paramContext = paramArrayList.iterator();
    while (paramContext.hasNext()) {
      ((zzqf)paramContext.next()).zza(this);
    }
    this.yk = new zzb(paramLooper);
    this.yj = paramLock.newCondition();
    this.ym = new zzqo(this);
  }
  
  public ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.yj.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        return new ConnectionResult(15, null);
      }
    }
    if (isConnected()) {
      return ConnectionResult.uJ;
    }
    if (this.yn != null) {
      return this.yn;
    }
    return new ConnectionResult(13, null);
  }
  
  public ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.yj.awaitNanos(paramLong))
    {
      if (paramLong <= 0L) {}
      try
      {
        disconnect();
        return new ConnectionResult(14, null);
      }
      catch (InterruptedException paramTimeUnit)
      {
        Thread.currentThread().interrupt();
        return new ConnectionResult(15, null);
      }
    }
    if (isConnected()) {
      return ConnectionResult.uJ;
    }
    if (this.yn != null) {
      return this.yn;
    }
    return new ConnectionResult(13, null);
  }
  
  public void connect()
  {
    this.ym.connect();
  }
  
  public void disconnect()
  {
    if (this.ym.disconnect()) {
      this.yl.clear();
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = String.valueOf(paramString).concat("  ");
    paramPrintWriter.append(paramString).append("mState=").println(this.ym);
    Iterator localIterator = this.xC.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      paramPrintWriter.append(paramString).append(localApi.getName()).println(":");
      ((Api.zze)this.xW.get(localApi.zzapp())).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    paramApi = paramApi.zzapp();
    if (this.xW.containsKey(paramApi))
    {
      if (((Api.zze)this.xW.get(paramApi)).isConnected()) {
        return ConnectionResult.uJ;
      }
      if (this.yl.containsKey(paramApi)) {
        return (ConnectionResult)this.yl.get(paramApi);
      }
    }
    return null;
  }
  
  public boolean isConnected()
  {
    return this.ym instanceof zzqm;
  }
  
  public boolean isConnecting()
  {
    return this.ym instanceof zzqn;
  }
  
  public void onConnected(@Nullable Bundle paramBundle)
  {
    this.xf.lock();
    try
    {
      this.ym.onConnected(paramBundle);
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    this.xf.lock();
    try
    {
      this.ym.onConnectionSuspended(paramInt);
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  public void zza(@NonNull ConnectionResult paramConnectionResult, @NonNull Api<?> paramApi, int paramInt)
  {
    this.xf.lock();
    try
    {
      this.ym.zza(paramConnectionResult, paramApi, paramInt);
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  void zza(zza paramzza)
  {
    paramzza = this.yk.obtainMessage(1, paramzza);
    this.yk.sendMessage(paramzza);
  }
  
  void zza(RuntimeException paramRuntimeException)
  {
    paramRuntimeException = this.yk.obtainMessage(2, paramRuntimeException);
    this.yk.sendMessage(paramRuntimeException);
  }
  
  public boolean zza(zzrl paramzzrl)
  {
    return false;
  }
  
  public void zzaqb() {}
  
  public void zzaqy()
  {
    if (isConnected()) {
      ((zzqm)this.ym).zzarh();
    }
  }
  
  void zzarw()
  {
    this.xf.lock();
    try
    {
      this.ym = new zzqn(this, this.xB, this.xC, this.xn, this.vQ, this.xf, this.mContext);
      this.ym.begin();
      this.yj.signalAll();
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  void zzarx()
  {
    this.xf.lock();
    try
    {
      this.wV.zzart();
      this.ym = new zzqm(this);
      this.ym.begin();
      this.yj.signalAll();
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  void zzary()
  {
    Iterator localIterator = this.xW.values().iterator();
    while (localIterator.hasNext()) {
      ((Api.zze)localIterator.next()).disconnect();
    }
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(@NonNull T paramT)
  {
    paramT.zzaqt();
    return this.ym.zzc(paramT);
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(@NonNull T paramT)
  {
    paramT.zzaqt();
    return this.ym.zzd(paramT);
  }
  
  void zzi(ConnectionResult paramConnectionResult)
  {
    this.xf.lock();
    try
    {
      this.yn = paramConnectionResult;
      this.ym = new zzqo(this);
      this.ym.begin();
      this.yj.signalAll();
      return;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  static abstract class zza
  {
    private final zzqq yq;
    
    protected zza(zzqq paramzzqq)
    {
      this.yq = paramzzqq;
    }
    
    protected abstract void zzari();
    
    public final void zzc(zzqr paramzzqr)
    {
      zzqr.zza(paramzzqr).lock();
      try
      {
        zzqq localzzqq1 = zzqr.zzb(paramzzqr);
        zzqq localzzqq2 = this.yq;
        if (localzzqq1 != localzzqq2) {
          return;
        }
        zzari();
        return;
      }
      finally
      {
        zzqr.zza(paramzzqr).unlock();
      }
    }
  }
  
  final class zzb
    extends Handler
  {
    zzb(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        int i = paramMessage.what;
        Log.w("GACStateManager", 31 + "Unknown message id: " + i);
        return;
      case 1: 
        ((zzqr.zza)paramMessage.obj).zzc(zzqr.this);
        return;
      }
      throw ((RuntimeException)paramMessage.obj);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */