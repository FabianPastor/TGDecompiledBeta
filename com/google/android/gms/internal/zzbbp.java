package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zzd;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zze;
import com.google.android.gms.tasks.Task;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbbp
  implements zzbdp
{
  private final zzbdb zzaAN;
  private final zzq zzaCA;
  private final Map<Api.zzc<?>, zzbbo<?>> zzaCB = new HashMap();
  private final Map<Api.zzc<?>, zzbbo<?>> zzaCC = new HashMap();
  private final Map<Api<?>, Boolean> zzaCD;
  private final zzbcp zzaCE;
  private final zze zzaCF;
  private final Condition zzaCG;
  private final boolean zzaCH;
  private final boolean zzaCI;
  private final Queue<zzbay<?, ?>> zzaCJ = new LinkedList();
  private boolean zzaCK;
  private Map<zzbat<?>, ConnectionResult> zzaCL;
  private Map<zzbat<?>, ConnectionResult> zzaCM;
  private zzbbs zzaCN;
  private ConnectionResult zzaCO;
  private final Lock zzaCv;
  private final Looper zzrM;
  
  public zzbbp(Context paramContext, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap, zzq paramzzq, Map<Api<?>, Boolean> paramMap1, Api.zza<? extends zzctk, zzctl> paramzza, ArrayList<zzbbi> paramArrayList, zzbcp paramzzbcp, boolean paramBoolean)
  {
    this.zzaCv = paramLock;
    this.zzrM = paramLooper;
    this.zzaCG = paramLock.newCondition();
    this.zzaCF = paramzze;
    this.zzaCE = paramzzbcp;
    this.zzaCD = paramMap1;
    this.zzaCA = paramzzq;
    this.zzaCH = paramBoolean;
    paramLock = new HashMap();
    paramzze = paramMap1.keySet().iterator();
    while (paramzze.hasNext())
    {
      paramMap1 = (Api)paramzze.next();
      paramLock.put(paramMap1.zzpd(), paramMap1);
    }
    paramzze = new HashMap();
    paramMap1 = (ArrayList)paramArrayList;
    int j = paramMap1.size();
    int i = 0;
    while (i < j)
    {
      paramArrayList = paramMap1.get(i);
      i += 1;
      paramArrayList = (zzbbi)paramArrayList;
      paramzze.put(paramArrayList.zzayW, paramArrayList);
    }
    paramMap = paramMap.entrySet().iterator();
    j = 1;
    i = 0;
    int k = 0;
    if (paramMap.hasNext())
    {
      paramMap1 = (Map.Entry)paramMap.next();
      paramzzbcp = (Api)paramLock.get(paramMap1.getKey());
      paramArrayList = (Api.zze)paramMap1.getValue();
      if (paramArrayList.zzpe())
      {
        k = 1;
        if (((Boolean)this.zzaCD.get(paramzzbcp)).booleanValue()) {
          break label488;
        }
        i = j;
        j = 1;
      }
    }
    for (;;)
    {
      paramzzbcp = new zzbbo(paramContext, paramzzbcp, paramLooper, paramArrayList, (zzbbi)paramzze.get(paramzzbcp), paramzzq, paramzza);
      this.zzaCB.put((Api.zzc)paramMap1.getKey(), paramzzbcp);
      if (paramArrayList.zzmv()) {
        this.zzaCC.put((Api.zzc)paramMap1.getKey(), paramzzbcp);
      }
      int m = j;
      j = i;
      i = m;
      break;
      m = 0;
      j = i;
      i = m;
      continue;
      if ((k != 0) && (j == 0) && (i == 0)) {}
      for (paramBoolean = true;; paramBoolean = false)
      {
        this.zzaCI = paramBoolean;
        this.zzaAN = zzbdb.zzqk();
        return;
      }
      label488:
      m = i;
      i = j;
      j = m;
    }
  }
  
  private final boolean zza(zzbbo<?> paramzzbbo, ConnectionResult paramConnectionResult)
  {
    return (!paramConnectionResult.isSuccess()) && (!paramConnectionResult.hasResolution()) && (((Boolean)this.zzaCD.get(paramzzbbo.zzpg())).booleanValue()) && (paramzzbbo.zzpJ().zzpe()) && (this.zzaCF.isUserResolvableError(paramConnectionResult.getErrorCode()));
  }
  
  @Nullable
  private final ConnectionResult zzb(@NonNull Api.zzc<?> paramzzc)
  {
    this.zzaCv.lock();
    try
    {
      paramzzc = (zzbbo)this.zzaCB.get(paramzzc);
      if ((this.zzaCL != null) && (paramzzc != null))
      {
        paramzzc = (ConnectionResult)this.zzaCL.get(paramzzc.zzph());
        return paramzzc;
      }
      return null;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  private final <T extends zzbay<? extends Result, ? extends Api.zzb>> boolean zzg(@NonNull T paramT)
  {
    Api.zzc localzzc = paramT.zzpd();
    ConnectionResult localConnectionResult = zzb(localzzc);
    if ((localConnectionResult != null) && (localConnectionResult.getErrorCode() == 4))
    {
      paramT.zzr(new Status(4, null, this.zzaAN.zza(((zzbbo)this.zzaCB.get(localzzc)).zzph(), System.identityHashCode(this.zzaCE))));
      return true;
    }
    return false;
  }
  
  private final boolean zzpK()
  {
    this.zzaCv.lock();
    try
    {
      boolean bool;
      if (this.zzaCK)
      {
        bool = this.zzaCH;
        if (bool) {}
      }
      else
      {
        return false;
      }
      Iterator localIterator = this.zzaCC.keySet().iterator();
      while (localIterator.hasNext())
      {
        ConnectionResult localConnectionResult = zzb((Api.zzc)localIterator.next());
        if (localConnectionResult != null)
        {
          bool = localConnectionResult.isSuccess();
          if (bool) {
            break;
          }
        }
        else
        {
          return false;
        }
      }
      return true;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  private final void zzpL()
  {
    if (this.zzaCA == null)
    {
      this.zzaCE.zzaDG = Collections.emptySet();
      return;
    }
    HashSet localHashSet = new HashSet(this.zzaCA.zzrn());
    Map localMap = this.zzaCA.zzrp();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      ConnectionResult localConnectionResult = getConnectionResult(localApi);
      if ((localConnectionResult != null) && (localConnectionResult.isSuccess())) {
        localHashSet.addAll(((zzr)localMap.get(localApi)).zzame);
      }
    }
    this.zzaCE.zzaDG = localHashSet;
  }
  
  private final void zzpM()
  {
    while (!this.zzaCJ.isEmpty()) {
      zze((zzbay)this.zzaCJ.remove());
    }
    this.zzaCE.zzm(null);
  }
  
  @Nullable
  private final ConnectionResult zzpN()
  {
    Iterator localIterator = this.zzaCB.values().iterator();
    int j = 0;
    Object localObject2 = null;
    int i = 0;
    Object localObject1 = null;
    while (localIterator.hasNext())
    {
      Object localObject3 = (zzbbo)localIterator.next();
      Api localApi = ((zzbbo)localObject3).zzpg();
      localObject3 = ((zzbbo)localObject3).zzph();
      localObject3 = (ConnectionResult)this.zzaCL.get(localObject3);
      if ((!((ConnectionResult)localObject3).isSuccess()) && ((!((Boolean)this.zzaCD.get(localApi)).booleanValue()) || (((ConnectionResult)localObject3).hasResolution()) || (this.zzaCF.isUserResolvableError(((ConnectionResult)localObject3).getErrorCode()))))
      {
        int k;
        if ((((ConnectionResult)localObject3).getErrorCode() == 4) && (this.zzaCH))
        {
          k = localApi.zzpb().getPriority();
          if ((localObject2 == null) || (j > k))
          {
            j = k;
            localObject2 = localObject3;
          }
        }
        else
        {
          k = localApi.zzpb().getPriority();
          if ((localObject1 != null) && (i <= k)) {
            break label222;
          }
          localObject1 = localObject3;
          i = k;
        }
      }
    }
    label222:
    for (;;)
    {
      break;
      if ((localObject1 != null) && (localObject2 != null) && (i > j)) {
        return (ConnectionResult)localObject2;
      }
      return (ConnectionResult)localObject1;
    }
  }
  
  public final ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.zzaCG.await();
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
    if (this.zzaCO != null) {
      return this.zzaCO;
    }
    return new ConnectionResult(13, null);
  }
  
  public final ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.zzaCG.awaitNanos(paramLong))
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
    if (this.zzaCO != null) {
      return this.zzaCO;
    }
    return new ConnectionResult(13, null);
  }
  
  public final void connect()
  {
    this.zzaCv.lock();
    try
    {
      boolean bool = this.zzaCK;
      if (bool) {
        return;
      }
      this.zzaCK = true;
      this.zzaCL = null;
      this.zzaCM = null;
      this.zzaCN = null;
      this.zzaCO = null;
      this.zzaAN.zzps();
      this.zzaAN.zza(this.zzaCB.values()).addOnCompleteListener(new zzbgv(this.zzrM), new zzbbr(this, null));
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final void disconnect()
  {
    this.zzaCv.lock();
    try
    {
      this.zzaCK = false;
      this.zzaCL = null;
      this.zzaCM = null;
      if (this.zzaCN != null)
      {
        this.zzaCN.cancel();
        this.zzaCN = null;
      }
      this.zzaCO = null;
      while (!this.zzaCJ.isEmpty())
      {
        zzbay localzzbay = (zzbay)this.zzaCJ.remove();
        localzzbay.zza(null);
        localzzbay.cancel();
      }
      this.zzaCG.signalAll();
    }
    finally
    {
      this.zzaCv.unlock();
    }
    this.zzaCv.unlock();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  @Nullable
  public final ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    return zzb(paramApi.zzpd());
  }
  
  /* Error */
  public final boolean isConnected()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 222 1 0
    //   9: aload_0
    //   10: getfield 181	com/google/android/gms/internal/zzbbp:zzaCL	Ljava/util/Map;
    //   13: ifnull +25 -> 38
    //   16: aload_0
    //   17: getfield 178	com/google/android/gms/internal/zzbbp:zzaCO	Lcom/google/android/gms/common/ConnectionResult;
    //   20: astore_2
    //   21: aload_2
    //   22: ifnonnull +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 229 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 229 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzbbp
    //   26	14	1	bool	boolean
    //   20	2	2	localConnectionResult	ConnectionResult
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  /* Error */
  public final boolean isConnecting()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 222 1 0
    //   9: aload_0
    //   10: getfield 181	com/google/android/gms/internal/zzbbp:zzaCL	Ljava/util/Map;
    //   13: ifnonnull +25 -> 38
    //   16: aload_0
    //   17: getfield 215	com/google/android/gms/internal/zzbbp:zzaCK	Z
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 229 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 59	com/google/android/gms/internal/zzbbp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 229 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzbbp
    //   20	20	1	bool	boolean
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  public final boolean zza(zzbei paramzzbei)
  {
    this.zzaCv.lock();
    try
    {
      if ((this.zzaCK) && (!zzpK()))
      {
        this.zzaAN.zzps();
        this.zzaCN = new zzbbs(this, paramzzbei);
        this.zzaAN.zza(this.zzaCC.values()).addOnCompleteListener(new zzbgv(this.zzrM), this.zzaCN);
        return true;
      }
      return false;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T paramT)
  {
    if ((this.zzaCH) && (zzg(paramT))) {
      return paramT;
    }
    if (!isConnected())
    {
      this.zzaCJ.add(paramT);
      return paramT;
    }
    this.zzaCE.zzaDL.zzb(paramT);
    return ((zzbbo)this.zzaCB.get(paramT.zzpd())).zza(paramT);
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T paramT)
  {
    Api.zzc localzzc = paramT.zzpd();
    if ((this.zzaCH) && (zzg(paramT))) {
      return paramT;
    }
    this.zzaCE.zzaDL.zzb(paramT);
    return ((zzbbo)this.zzaCB.get(localzzc)).zzb(paramT);
  }
  
  public final void zzpE() {}
  
  public final void zzpl()
  {
    this.zzaCv.lock();
    try
    {
      this.zzaAN.zzpl();
      if (this.zzaCN != null)
      {
        this.zzaCN.cancel();
        this.zzaCN = null;
      }
      if (this.zzaCM == null) {
        this.zzaCM = new ArrayMap(this.zzaCC.size());
      }
      ConnectionResult localConnectionResult = new ConnectionResult(4);
      Iterator localIterator = this.zzaCC.values().iterator();
      while (localIterator.hasNext())
      {
        zzbbo localzzbbo = (zzbbo)localIterator.next();
        this.zzaCM.put(localzzbbo.zzph(), localConnectionResult);
      }
      if (this.zzaCL == null) {
        break label155;
      }
    }
    finally
    {
      this.zzaCv.unlock();
    }
    this.zzaCL.putAll(this.zzaCM);
    label155:
    this.zzaCv.unlock();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */