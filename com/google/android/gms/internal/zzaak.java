package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zzd;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzg.zza;
import com.google.android.gms.common.zze;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class zzaak
  implements zzabc
{
  private final Lock zzaAG;
  private final zzg zzaAL;
  private final Map<Api.zzc<?>, zzaaj<?>> zzaAM = new HashMap();
  private final Map<Api.zzc<?>, zzaaj<?>> zzaAN = new HashMap();
  private final Map<Api<?>, Boolean> zzaAO;
  private final zzaat zzaAP;
  private final zze zzaAQ;
  private final Condition zzaAR;
  private final boolean zzaAS;
  private final boolean zzaAT;
  private final Queue<zzaad.zza<?, ?>> zzaAU = new LinkedList();
  private boolean zzaAV;
  private Map<zzzz<?>, ConnectionResult> zzaAW;
  private Map<zzzz<?>, ConnectionResult> zzaAX;
  private zzb zzaAY;
  private ConnectionResult zzaAZ;
  private final zzaax zzayX;
  private final Looper zzrs;
  
  public zzaak(Context paramContext, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap, zzg paramzzg, Map<Api<?>, Boolean> paramMap1, Api.zza<? extends zzbai, zzbaj> paramzza, ArrayList<zzaag> paramArrayList, zzaat paramzzaat, boolean paramBoolean)
  {
    this.zzaAG = paramLock;
    this.zzrs = paramLooper;
    this.zzaAR = paramLock.newCondition();
    this.zzaAQ = paramzze;
    this.zzaAP = paramzzaat;
    this.zzaAO = paramMap1;
    this.zzaAL = paramzzg;
    this.zzaAS = paramBoolean;
    paramLock = new HashMap();
    paramzze = paramMap1.keySet().iterator();
    while (paramzze.hasNext())
    {
      paramMap1 = (Api)paramzze.next();
      paramLock.put(paramMap1.zzvg(), paramMap1);
    }
    paramzze = new HashMap();
    paramMap1 = paramArrayList.iterator();
    while (paramMap1.hasNext())
    {
      paramArrayList = (zzaag)paramMap1.next();
      paramzze.put(paramArrayList.zzaxf, paramArrayList);
    }
    paramMap = paramMap.entrySet().iterator();
    int j = 1;
    int i = 0;
    int k = 0;
    if (paramMap.hasNext())
    {
      paramMap1 = (Map.Entry)paramMap.next();
      paramzzaat = (Api)paramLock.get(paramMap1.getKey());
      paramArrayList = (Api.zze)paramMap1.getValue();
      if (paramArrayList.zzvh())
      {
        k = 1;
        if (((Boolean)this.zzaAO.get(paramzzaat)).booleanValue()) {
          break label471;
        }
        i = j;
        j = 1;
      }
    }
    for (;;)
    {
      paramzzaat = new zzaaj(paramContext, paramzzaat, paramLooper, paramArrayList, (zzaag)paramzze.get(paramzzaat), paramzzg, paramzza);
      this.zzaAM.put((Api.zzc)paramMap1.getKey(), paramzzaat);
      if (paramArrayList.zzrd()) {
        this.zzaAN.put((Api.zzc)paramMap1.getKey(), paramzzaat);
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
        this.zzaAT = paramBoolean;
        this.zzayX = zzaax.zzww();
        return;
      }
      label471:
      m = i;
      i = j;
      j = m;
    }
  }
  
  private boolean zza(zzaaj<?> paramzzaaj, ConnectionResult paramConnectionResult)
  {
    return (!paramConnectionResult.isSuccess()) && (!paramConnectionResult.hasResolution()) && (((Boolean)this.zzaAO.get(paramzzaaj.getApi())).booleanValue()) && (paramzzaaj.zzvU().zzvh()) && (this.zzaAQ.isUserResolvableError(paramConnectionResult.getErrorCode()));
  }
  
  @Nullable
  private ConnectionResult zzb(@NonNull Api.zzc<?> paramzzc)
  {
    this.zzaAG.lock();
    try
    {
      paramzzc = (zzaaj)this.zzaAM.get(paramzzc);
      if ((this.zzaAW != null) && (paramzzc != null))
      {
        paramzzc = (ConnectionResult)this.zzaAW.get(paramzzc.getApiKey());
        return paramzzc;
      }
      return null;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  private <T extends zzaad.zza<? extends Result, ? extends Api.zzb>> boolean zzd(@NonNull T paramT)
  {
    Api.zzc localzzc = paramT.zzvg();
    ConnectionResult localConnectionResult = zzb(localzzc);
    if ((localConnectionResult != null) && (localConnectionResult.getErrorCode() == 4))
    {
      paramT.zzB(new Status(4, null, this.zzayX.zza(((zzaaj)this.zzaAM.get(localzzc)).getApiKey(), this.zzaAP.getSessionId())));
      return true;
    }
    return false;
  }
  
  private void zzvV()
  {
    if (this.zzaAL == null)
    {
      this.zzaAP.zzaBR = Collections.emptySet();
      return;
    }
    HashSet localHashSet = new HashSet(this.zzaAL.zzxL());
    Map localMap = this.zzaAL.zzxN();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      ConnectionResult localConnectionResult = getConnectionResult(localApi);
      if ((localConnectionResult != null) && (localConnectionResult.isSuccess())) {
        localHashSet.addAll(((zzg.zza)localMap.get(localApi)).zzakq);
      }
    }
    this.zzaAP.zzaBR = localHashSet;
  }
  
  private void zzvW()
  {
    while (!this.zzaAU.isEmpty()) {
      zzb((zzaad.zza)this.zzaAU.remove());
    }
    this.zzaAP.zzo(null);
  }
  
  @Nullable
  private ConnectionResult zzvX()
  {
    Iterator localIterator = this.zzaAM.values().iterator();
    int j = 0;
    Object localObject2 = null;
    int i = 0;
    Object localObject1 = null;
    while (localIterator.hasNext())
    {
      Object localObject3 = (zzaaj)localIterator.next();
      Api localApi = ((zzaaj)localObject3).getApi();
      localObject3 = ((zzaaj)localObject3).getApiKey();
      localObject3 = (ConnectionResult)this.zzaAW.get(localObject3);
      if ((!((ConnectionResult)localObject3).isSuccess()) && ((!((Boolean)this.zzaAO.get(localApi)).booleanValue()) || (((ConnectionResult)localObject3).hasResolution()) || (this.zzaAQ.isUserResolvableError(((ConnectionResult)localObject3).getErrorCode()))))
      {
        int k;
        if ((((ConnectionResult)localObject3).getErrorCode() == 4) && (this.zzaAS))
        {
          k = localApi.zzve().getPriority();
          if ((localObject2 == null) || (j > k))
          {
            j = k;
            localObject2 = localObject3;
          }
        }
        else
        {
          k = localApi.zzve().getPriority();
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
  
  public ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.zzaAR.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        return new ConnectionResult(15, null);
      }
    }
    if (isConnected()) {
      return ConnectionResult.zzayj;
    }
    if (this.zzaAZ != null) {
      return this.zzaAZ;
    }
    return new ConnectionResult(13, null);
  }
  
  public ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.zzaAR.awaitNanos(paramLong))
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
      return ConnectionResult.zzayj;
    }
    if (this.zzaAZ != null) {
      return this.zzaAZ;
    }
    return new ConnectionResult(13, null);
  }
  
  public void connect()
  {
    this.zzaAG.lock();
    try
    {
      boolean bool = this.zzaAV;
      if (bool) {
        return;
      }
      this.zzaAV = true;
      this.zzaAW = null;
      this.zzaAX = null;
      this.zzaAY = null;
      this.zzaAZ = null;
      this.zzayX.zzvx();
      this.zzayX.zza(this.zzaAM.values()).addOnCompleteListener(new zzadb(this.zzrs), new zza(null));
      return;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public void disconnect()
  {
    this.zzaAG.lock();
    try
    {
      this.zzaAV = false;
      this.zzaAW = null;
      this.zzaAX = null;
      if (this.zzaAY != null)
      {
        this.zzaAY.cancel();
        this.zzaAY = null;
      }
      this.zzaAZ = null;
      while (!this.zzaAU.isEmpty())
      {
        zzaad.zza localzza = (zzaad.zza)this.zzaAU.remove();
        localzza.zza(null);
        localzza.cancel();
      }
      this.zzaAR.signalAll();
    }
    finally
    {
      this.zzaAG.unlock();
    }
    this.zzaAG.unlock();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    return zzb(paramApi.zzvg());
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 223 1 0
    //   9: aload_0
    //   10: getfield 182	com/google/android/gms/internal/zzaak:zzaAW	Ljava/util/Map;
    //   13: ifnull +25 -> 38
    //   16: aload_0
    //   17: getfield 179	com/google/android/gms/internal/zzaak:zzaAZ	Lcom/google/android/gms/common/ConnectionResult;
    //   20: astore_2
    //   21: aload_2
    //   22: ifnonnull +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 230 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 230 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzaak
    //   26	14	1	bool	boolean
    //   20	2	2	localConnectionResult	ConnectionResult
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  /* Error */
  public boolean isConnecting()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 223 1 0
    //   9: aload_0
    //   10: getfield 182	com/google/android/gms/internal/zzaak:zzaAW	Ljava/util/Map;
    //   13: ifnonnull +25 -> 38
    //   16: aload_0
    //   17: getfield 217	com/google/android/gms/internal/zzaak:zzaAV	Z
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 230 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 67	com/google/android/gms/internal/zzaak:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 230 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzaak
    //   20	20	1	bool	boolean
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(@NonNull T paramT)
  {
    if ((this.zzaAS) && (zzd(paramT))) {
      return paramT;
    }
    if (!isConnected())
    {
      this.zzaAU.add(paramT);
      return paramT;
    }
    this.zzaAP.zzaBW.zzb(paramT);
    return ((zzaaj)this.zzaAM.get(paramT.zzvg())).doRead(paramT);
  }
  
  public boolean zza(zzabq paramzzabq)
  {
    this.zzaAG.lock();
    try
    {
      if ((this.zzaAV) && (!zzvN()))
      {
        this.zzayX.zzvx();
        this.zzaAY = new zzb(paramzzabq);
        this.zzayX.zza(this.zzaAN.values()).addOnCompleteListener(new zzadb(this.zzrs), this.zzaAY);
        return true;
      }
      return false;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public <A extends Api.zzb, T extends zzaad.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    Api.zzc localzzc = paramT.zzvg();
    if ((this.zzaAS) && (zzd(paramT))) {
      return paramT;
    }
    this.zzaAP.zzaBW.zzb(paramT);
    return ((zzaaj)this.zzaAM.get(localzzc)).doWrite(paramT);
  }
  
  public void zzvM() {}
  
  public boolean zzvN()
  {
    this.zzaAG.lock();
    try
    {
      boolean bool;
      if (this.zzaAV)
      {
        bool = this.zzaAS;
        if (bool) {}
      }
      else
      {
        return false;
      }
      Iterator localIterator = this.zzaAN.keySet().iterator();
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
      this.zzaAG.unlock();
    }
  }
  
  public void zzvn()
  {
    this.zzaAG.lock();
    try
    {
      this.zzayX.zzvn();
      if (this.zzaAY != null)
      {
        this.zzaAY.cancel();
        this.zzaAY = null;
      }
      if (this.zzaAX == null) {
        this.zzaAX = new ArrayMap(this.zzaAN.size());
      }
      ConnectionResult localConnectionResult = new ConnectionResult(4);
      Iterator localIterator = this.zzaAN.values().iterator();
      while (localIterator.hasNext())
      {
        zzaaj localzzaaj = (zzaaj)localIterator.next();
        this.zzaAX.put(localzzaaj.getApiKey(), localConnectionResult);
      }
      if (this.zzaAW == null) {
        break label155;
      }
    }
    finally
    {
      this.zzaAG.unlock();
    }
    this.zzaAW.putAll(this.zzaAX);
    label155:
    this.zzaAG.unlock();
  }
  
  private class zza
    implements OnCompleteListener<Void>
  {
    private zza() {}
    
    public void onComplete(@NonNull Task<Void> paramTask)
    {
      zzaak.zza(zzaak.this).lock();
      Object localObject;
      try
      {
        boolean bool = zzaak.zzb(zzaak.this);
        if (!bool) {
          return;
        }
        if (paramTask.isSuccessful())
        {
          zzaak.zza(zzaak.this, new ArrayMap(zzaak.zzc(zzaak.this).size()));
          paramTask = zzaak.zzc(zzaak.this).values().iterator();
          while (paramTask.hasNext())
          {
            localObject = (zzaaj)paramTask.next();
            zzaak.zzd(zzaak.this).put(((zzaaj)localObject).getApiKey(), ConnectionResult.zzayj);
          }
        }
        if (!(paramTask.getException() instanceof zzb)) {
          break label435;
        }
      }
      finally
      {
        zzaak.zza(zzaak.this).unlock();
      }
      paramTask = (zzb)paramTask.getException();
      if (zzaak.zze(zzaak.this))
      {
        zzaak.zza(zzaak.this, new ArrayMap(zzaak.zzc(zzaak.this).size()));
        localObject = zzaak.zzc(zzaak.this).values().iterator();
        while (((Iterator)localObject).hasNext())
        {
          zzaaj localzzaaj = (zzaaj)((Iterator)localObject).next();
          zzzz localzzzz = localzzaaj.getApiKey();
          ConnectionResult localConnectionResult = paramTask.zza(localzzaaj);
          if (zzaak.zza(zzaak.this, localzzaaj, localConnectionResult)) {
            zzaak.zzd(zzaak.this).put(localzzzz, new ConnectionResult(16));
          } else {
            zzaak.zzd(zzaak.this).put(localzzzz, localConnectionResult);
          }
        }
      }
      zzaak.zza(zzaak.this, paramTask.zzvj());
      zzaak.zza(zzaak.this, zzaak.zzf(zzaak.this));
      if (zzaak.zzg(zzaak.this) != null)
      {
        zzaak.zzd(zzaak.this).putAll(zzaak.zzg(zzaak.this));
        zzaak.zza(zzaak.this, zzaak.zzf(zzaak.this));
      }
      if (zzaak.zzh(zzaak.this) == null)
      {
        zzaak.zzi(zzaak.this);
        zzaak.zzj(zzaak.this);
      }
      for (;;)
      {
        zzaak.zzl(zzaak.this).signalAll();
        zzaak.zza(zzaak.this).unlock();
        return;
        label435:
        Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
        zzaak.zza(zzaak.this, Collections.emptyMap());
        zzaak.zza(zzaak.this, new ConnectionResult(8));
        break;
        zzaak.zza(zzaak.this, false);
        zzaak.zzk(zzaak.this).zzc(zzaak.zzh(zzaak.this));
      }
    }
  }
  
  private class zzb
    implements OnCompleteListener<Void>
  {
    private zzabq zzaBb;
    
    zzb(zzabq paramzzabq)
    {
      this.zzaBb = paramzzabq;
    }
    
    void cancel()
    {
      this.zzaBb.zzrq();
    }
    
    public void onComplete(@NonNull Task<Void> paramTask)
    {
      zzaak.zza(zzaak.this).lock();
      Object localObject;
      try
      {
        if (!zzaak.zzb(zzaak.this))
        {
          this.zzaBb.zzrq();
          return;
        }
        if (paramTask.isSuccessful())
        {
          zzaak.zzb(zzaak.this, new ArrayMap(zzaak.zzm(zzaak.this).size()));
          paramTask = zzaak.zzm(zzaak.this).values().iterator();
          while (paramTask.hasNext())
          {
            localObject = (zzaaj)paramTask.next();
            zzaak.zzg(zzaak.this).put(((zzaaj)localObject).getApiKey(), ConnectionResult.zzayj);
          }
        }
        if (!(paramTask.getException() instanceof zzb)) {
          break label417;
        }
      }
      finally
      {
        zzaak.zza(zzaak.this).unlock();
      }
      paramTask = (zzb)paramTask.getException();
      if (zzaak.zze(zzaak.this))
      {
        zzaak.zzb(zzaak.this, new ArrayMap(zzaak.zzm(zzaak.this).size()));
        localObject = zzaak.zzm(zzaak.this).values().iterator();
        while (((Iterator)localObject).hasNext())
        {
          zzaaj localzzaaj = (zzaaj)((Iterator)localObject).next();
          zzzz localzzzz = localzzaaj.getApiKey();
          ConnectionResult localConnectionResult = paramTask.zza(localzzaaj);
          if (zzaak.zza(zzaak.this, localzzaaj, localConnectionResult)) {
            zzaak.zzg(zzaak.this).put(localzzzz, new ConnectionResult(16));
          } else {
            zzaak.zzg(zzaak.this).put(localzzzz, localConnectionResult);
          }
        }
      }
      zzaak.zzb(zzaak.this, paramTask.zzvj());
      for (;;)
      {
        if (zzaak.this.isConnected())
        {
          zzaak.zzd(zzaak.this).putAll(zzaak.zzg(zzaak.this));
          if (zzaak.zzf(zzaak.this) == null)
          {
            zzaak.zzi(zzaak.this);
            zzaak.zzj(zzaak.this);
            zzaak.zzl(zzaak.this).signalAll();
          }
        }
        this.zzaBb.zzrq();
        zzaak.zza(zzaak.this).unlock();
        return;
        label417:
        Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
        zzaak.zzb(zzaak.this, Collections.emptyMap());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */