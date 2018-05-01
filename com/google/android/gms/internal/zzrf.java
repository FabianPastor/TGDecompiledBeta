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
import com.google.android.gms.common.internal.zzf;
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

public class zzrf
  implements zzqs, zzrm
{
  private ConnectionResult AA = null;
  int AB;
  final zzrm.zza AC;
  final Map<Api.zzc<?>, Api.zze> Aj;
  private final Condition Aw;
  private final zzb Ax;
  final Map<Api.zzc<?>, ConnectionResult> Ay = new HashMap();
  private volatile zzre Az;
  private final Context mContext;
  final Api.zza<? extends zzxp, zzxq> xQ;
  final zzrd yW;
  final zzf zP;
  private final Lock zg;
  final Map<Api<?>, Integer> zk;
  private final zzc zm;
  
  public zzrf(Context paramContext, zzrd paramzzrd, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap, zzf paramzzf, Map<Api<?>, Integer> paramMap1, Api.zza<? extends zzxp, zzxq> paramzza, ArrayList<zzqr> paramArrayList, zzrm.zza paramzza1)
  {
    this.mContext = paramContext;
    this.zg = paramLock;
    this.zm = paramzzc;
    this.Aj = paramMap;
    this.zP = paramzzf;
    this.zk = paramMap1;
    this.xQ = paramzza;
    this.yW = paramzzrd;
    this.AC = paramzza1;
    paramContext = paramArrayList.iterator();
    while (paramContext.hasNext()) {
      ((zzqr)paramContext.next()).zza(this);
    }
    this.Ax = new zzb(paramLooper);
    this.Aw = paramLock.newCondition();
    this.Az = new zzrc(this);
  }
  
  public ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.Aw.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        return new ConnectionResult(15, null);
      }
    }
    if (isConnected()) {
      return ConnectionResult.wO;
    }
    if (this.AA != null) {
      return this.AA;
    }
    return new ConnectionResult(13, null);
  }
  
  public ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.Aw.awaitNanos(paramLong))
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
      return ConnectionResult.wO;
    }
    if (this.AA != null) {
      return this.AA;
    }
    return new ConnectionResult(13, null);
  }
  
  public void connect()
  {
    this.Az.connect();
  }
  
  public void disconnect()
  {
    if (this.Az.disconnect()) {
      this.Ay.clear();
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = String.valueOf(paramString).concat("  ");
    paramPrintWriter.append(paramString).append("mState=").println(this.Az);
    Iterator localIterator = this.zk.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      paramPrintWriter.append(paramString).append(localApi.getName()).println(":");
      ((Api.zze)this.Aj.get(localApi.zzaqv())).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    paramApi = paramApi.zzaqv();
    if (this.Aj.containsKey(paramApi))
    {
      if (((Api.zze)this.Aj.get(paramApi)).isConnected()) {
        return ConnectionResult.wO;
      }
      if (this.Ay.containsKey(paramApi)) {
        return (ConnectionResult)this.Ay.get(paramApi);
      }
    }
    return null;
  }
  
  public boolean isConnected()
  {
    return this.Az instanceof zzra;
  }
  
  public boolean isConnecting()
  {
    return this.Az instanceof zzrb;
  }
  
  public void onConnected(@Nullable Bundle paramBundle)
  {
    this.zg.lock();
    try
    {
      this.Az.onConnected(paramBundle);
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    this.zg.lock();
    try
    {
      this.Az.onConnectionSuspended(paramInt);
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T paramT)
  {
    paramT.zzarv();
    return this.Az.zza(paramT);
  }
  
  public void zza(@NonNull ConnectionResult paramConnectionResult, @NonNull Api<?> paramApi, int paramInt)
  {
    this.zg.lock();
    try
    {
      this.Az.zza(paramConnectionResult, paramApi, paramInt);
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  void zza(zza paramzza)
  {
    paramzza = this.Ax.obtainMessage(1, paramzza);
    this.Ax.sendMessage(paramzza);
  }
  
  void zza(RuntimeException paramRuntimeException)
  {
    paramRuntimeException = this.Ax.obtainMessage(2, paramRuntimeException);
    this.Ax.sendMessage(paramRuntimeException);
  }
  
  public boolean zza(zzsa paramzzsa)
  {
    return false;
  }
  
  public void zzard() {}
  
  public void zzarz()
  {
    if (isConnected()) {
      ((zzra)this.Az).zzasn();
    }
  }
  
  void zzatc()
  {
    this.zg.lock();
    try
    {
      this.Az = new zzrb(this, this.zP, this.zk, this.zm, this.xQ, this.zg, this.mContext);
      this.Az.begin();
      this.Aw.signalAll();
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  void zzatd()
  {
    this.zg.lock();
    try
    {
      this.yW.zzasz();
      this.Az = new zzra(this);
      this.Az.begin();
      this.Aw.signalAll();
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  void zzate()
  {
    Iterator localIterator = this.Aj.values().iterator();
    while (localIterator.hasNext()) {
      ((Api.zze)localIterator.next()).disconnect();
    }
  }
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    paramT.zzarv();
    return this.Az.zzb(paramT);
  }
  
  void zzh(ConnectionResult paramConnectionResult)
  {
    this.zg.lock();
    try
    {
      this.AA = paramConnectionResult;
      this.Az = new zzrc(this);
      this.Az.begin();
      this.Aw.signalAll();
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  static abstract class zza
  {
    private final zzre AD;
    
    protected zza(zzre paramzzre)
    {
      this.AD = paramzzre;
    }
    
    protected abstract void zzaso();
    
    public final void zzc(zzrf paramzzrf)
    {
      zzrf.zza(paramzzrf).lock();
      try
      {
        zzre localzzre1 = zzrf.zzb(paramzzrf);
        zzre localzzre2 = this.AD;
        if (localzzre1 != localzzre2) {
          return;
        }
        zzaso();
        return;
      }
      finally
      {
        zzrf.zza(paramzzrf).unlock();
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
        ((zzrf.zza)paramMessage.obj).zzc(zzrf.this);
        return;
      }
      throw ((RuntimeException)paramMessage.obj);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */