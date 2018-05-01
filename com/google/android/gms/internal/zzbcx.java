package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbcx
  implements zzbbj, zzbdp
{
  private final Context mContext;
  private Api.zza<? extends zzctk, zzctl> zzaBe;
  private zzq zzaCA;
  private Map<Api<?>, Boolean> zzaCD;
  private final zze zzaCF;
  final zzbcp zzaCl;
  private final Lock zzaCv;
  final Map<Api.zzc<?>, Api.zze> zzaDF;
  private final Condition zzaDS;
  private final zzbcz zzaDT;
  final Map<Api.zzc<?>, ConnectionResult> zzaDU = new HashMap();
  private volatile zzbcw zzaDV;
  private ConnectionResult zzaDW = null;
  int zzaDX;
  final zzbdq zzaDY;
  
  public zzbcx(Context paramContext, zzbcp paramzzbcp, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap, zzq paramzzq, Map<Api<?>, Boolean> paramMap1, Api.zza<? extends zzctk, zzctl> paramzza, ArrayList<zzbbi> paramArrayList, zzbdq paramzzbdq)
  {
    this.mContext = paramContext;
    this.zzaCv = paramLock;
    this.zzaCF = paramzze;
    this.zzaDF = paramMap;
    this.zzaCA = paramzzq;
    this.zzaCD = paramMap1;
    this.zzaBe = paramzza;
    this.zzaCl = paramzzbcp;
    this.zzaDY = paramzzbdq;
    paramContext = (ArrayList)paramArrayList;
    int j = paramContext.size();
    int i = 0;
    while (i < j)
    {
      paramzzbcp = paramContext.get(i);
      i += 1;
      ((zzbbi)paramzzbcp).zza(this);
    }
    this.zzaDT = new zzbcz(this, paramLooper);
    this.zzaDS = paramLock.newCondition();
    this.zzaDV = new zzbco(this);
  }
  
  public final ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.zzaDS.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        return new ConnectionResult(15, null);
      }
    }
    if (isConnected()) {
      return ConnectionResult.zzazX;
    }
    if (this.zzaDW != null) {
      return this.zzaDW;
    }
    return new ConnectionResult(13, null);
  }
  
  public final ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.zzaDS.awaitNanos(paramLong))
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
      return ConnectionResult.zzazX;
    }
    if (this.zzaDW != null) {
      return this.zzaDW;
    }
    return new ConnectionResult(13, null);
  }
  
  public final void connect()
  {
    this.zzaDV.connect();
  }
  
  public final void disconnect()
  {
    if (this.zzaDV.disconnect()) {
      this.zzaDU.clear();
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = String.valueOf(paramString).concat("  ");
    paramPrintWriter.append(paramString).append("mState=").println(this.zzaDV);
    Iterator localIterator = this.zzaCD.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      paramPrintWriter.append(paramString).append(localApi.getName()).println(":");
      ((Api.zze)this.zzaDF.get(localApi.zzpd())).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @Nullable
  public final ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    paramApi = paramApi.zzpd();
    if (this.zzaDF.containsKey(paramApi))
    {
      if (((Api.zze)this.zzaDF.get(paramApi)).isConnected()) {
        return ConnectionResult.zzazX;
      }
      if (this.zzaDU.containsKey(paramApi)) {
        return (ConnectionResult)this.zzaDU.get(paramApi);
      }
    }
    return null;
  }
  
  public final boolean isConnected()
  {
    return this.zzaDV instanceof zzbca;
  }
  
  public final boolean isConnecting()
  {
    return this.zzaDV instanceof zzbcd;
  }
  
  public final void onConnected(@Nullable Bundle paramBundle)
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDV.onConnected(paramBundle);
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDV.onConnectionSuspended(paramInt);
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final void zza(@NonNull ConnectionResult paramConnectionResult, @NonNull Api<?> paramApi, boolean paramBoolean)
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDV.zza(paramConnectionResult, paramApi, paramBoolean);
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  final void zza(zzbcy paramzzbcy)
  {
    paramzzbcy = this.zzaDT.obtainMessage(1, paramzzbcy);
    this.zzaDT.sendMessage(paramzzbcy);
  }
  
  final void zza(RuntimeException paramRuntimeException)
  {
    paramRuntimeException = this.zzaDT.obtainMessage(2, paramRuntimeException);
    this.zzaDT.sendMessage(paramRuntimeException);
  }
  
  public final boolean zza(zzbei paramzzbei)
  {
    return false;
  }
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T paramT)
  {
    paramT.zzpC();
    return this.zzaDV.zzd(paramT);
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T paramT)
  {
    paramT.zzpC();
    return this.zzaDV.zze(paramT);
  }
  
  final void zzg(ConnectionResult paramConnectionResult)
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDW = paramConnectionResult;
      this.zzaDV = new zzbco(this);
      this.zzaDV.begin();
      this.zzaDS.signalAll();
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final void zzpE()
  {
    if (isConnected()) {
      ((zzbca)this.zzaDV).zzpU();
    }
  }
  
  public final void zzpl() {}
  
  final void zzqh()
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDV = new zzbcd(this, this.zzaCA, this.zzaCD, this.zzaCF, this.zzaBe, this.zzaCv, this.mContext);
      this.zzaDV.begin();
      this.zzaDS.signalAll();
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  final void zzqi()
  {
    this.zzaCv.lock();
    try
    {
      this.zzaCl.zzqe();
      this.zzaDV = new zzbca(this);
      this.zzaDV.begin();
      this.zzaDS.signalAll();
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */