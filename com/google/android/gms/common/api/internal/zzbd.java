package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

public final class zzbd
  implements zzbp, zzq
{
  private final Context mContext;
  private final Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzdh;
  final zzav zzfq;
  private final Lock zzga;
  private final ClientSettings zzgf;
  private final Map<Api<?>, Boolean> zzgi;
  private final GoogleApiAvailabilityLight zzgk;
  final Map<Api.AnyClientKey<?>, Api.Client> zzil;
  private final Condition zziz;
  private final zzbf zzja;
  final Map<Api.AnyClientKey<?>, ConnectionResult> zzjb = new HashMap();
  private volatile zzbc zzjc;
  private ConnectionResult zzjd = null;
  int zzje;
  final zzbq zzjf;
  
  public zzbd(Context paramContext, zzav paramzzav, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map<Api.AnyClientKey<?>, Api.Client> paramMap, ClientSettings paramClientSettings, Map<Api<?>, Boolean> paramMap1, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, ArrayList<zzp> paramArrayList, zzbq paramzzbq)
  {
    this.mContext = paramContext;
    this.zzga = paramLock;
    this.zzgk = paramGoogleApiAvailabilityLight;
    this.zzil = paramMap;
    this.zzgf = paramClientSettings;
    this.zzgi = paramMap1;
    this.zzdh = paramAbstractClientBuilder;
    this.zzfq = paramzzav;
    this.zzjf = paramzzbq;
    paramContext = (ArrayList)paramArrayList;
    int i = paramContext.size();
    int j = 0;
    while (j < i)
    {
      paramzzav = paramContext.get(j);
      j++;
      ((zzp)paramzzav).zza(this);
    }
    this.zzja = new zzbf(this, paramLooper);
    this.zziz = paramLock.newCondition();
    this.zzjc = new zzau(this);
  }
  
  @GuardedBy("mLock")
  public final ConnectionResult blockingConnect()
  {
    connect();
    ConnectionResult localConnectionResult;
    for (;;)
    {
      if (isConnecting()) {
        try
        {
          this.zziz.await();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
          localConnectionResult = new ConnectionResult(15, null);
        }
      }
    }
    for (;;)
    {
      return localConnectionResult;
      if (isConnected()) {
        localConnectionResult = ConnectionResult.RESULT_SUCCESS;
      } else if (this.zzjd != null) {
        localConnectionResult = this.zzjd;
      } else {
        localConnectionResult = new ConnectionResult(13, null);
      }
    }
  }
  
  @GuardedBy("mLock")
  public final void connect()
  {
    this.zzjc.connect();
  }
  
  @GuardedBy("mLock")
  public final void disconnect()
  {
    if (this.zzjc.disconnect()) {
      this.zzjb.clear();
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = String.valueOf(paramString).concat("  ");
    paramPrintWriter.append(paramString).append("mState=").println(this.zzjc);
    Iterator localIterator = this.zzgi.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      paramPrintWriter.append(paramString).append(localApi.getName()).println(":");
      ((Api.Client)this.zzil.get(localApi.getClientKey())).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @GuardedBy("mLock")
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    paramT.zzx();
    return this.zzjc.enqueue(paramT);
  }
  
  @GuardedBy("mLock")
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    paramT.zzx();
    return this.zzjc.execute(paramT);
  }
  
  public final boolean isConnected()
  {
    return this.zzjc instanceof zzag;
  }
  
  public final boolean isConnecting()
  {
    return this.zzjc instanceof zzaj;
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    this.zzga.lock();
    try
    {
      this.zzjc.onConnected(paramBundle);
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzga.lock();
    try
    {
      this.zzjc.onConnectionSuspended(paramInt);
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    this.zzga.lock();
    try
    {
      this.zzjc.zza(paramConnectionResult, paramApi, paramBoolean);
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  final void zza(zzbe paramzzbe)
  {
    paramzzbe = this.zzja.obtainMessage(1, paramzzbe);
    this.zzja.sendMessage(paramzzbe);
  }
  
  final void zzb(RuntimeException paramRuntimeException)
  {
    paramRuntimeException = this.zzja.obtainMessage(2, paramRuntimeException);
    this.zzja.sendMessage(paramRuntimeException);
  }
  
  final void zzbc()
  {
    this.zzga.lock();
    try
    {
      zzaj localzzaj = new com/google/android/gms/common/api/internal/zzaj;
      localzzaj.<init>(this, this.zzgf, this.zzgi, this.zzgk, this.zzdh, this.zzga, this.mContext);
      this.zzjc = localzzaj;
      this.zzjc.begin();
      this.zziz.signalAll();
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  final void zzbd()
  {
    this.zzga.lock();
    try
    {
      this.zzfq.zzaz();
      zzag localzzag = new com/google/android/gms/common/api/internal/zzag;
      localzzag.<init>(this);
      this.zzjc = localzzag;
      this.zzjc.begin();
      this.zziz.signalAll();
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  final void zzf(ConnectionResult paramConnectionResult)
  {
    this.zzga.lock();
    try
    {
      this.zzjd = paramConnectionResult;
      paramConnectionResult = new com/google/android/gms/common/api/internal/zzau;
      paramConnectionResult.<init>(this);
      this.zzjc = paramConnectionResult;
      this.zzjc.begin();
      this.zziz.signalAll();
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  @GuardedBy("mLock")
  public final void zzz()
  {
    if (isConnected()) {
      ((zzag)this.zzjc).zzap();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */