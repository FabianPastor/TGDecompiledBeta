package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zzd;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzqu
  implements zzrm
{
  private final zzrh xy;
  private final Lock zg;
  private final Map<Api.zzc<?>, com.google.android.gms.common.api.zzc<?>> zj = new HashMap();
  private final Map<Api<?>, Integer> zk;
  private final zzrd zl;
  private final com.google.android.gms.common.zzc zm;
  private final Condition zn;
  private boolean zo;
  private Map<zzql<?>, ConnectionResult> zp;
  private ConnectionResult zq;
  private final Looper zzajy;
  
  public zzqu(Context paramContext, Lock paramLock, Looper paramLooper, com.google.android.gms.common.zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap, Map<Api<?>, Integer> paramMap1, ArrayList<zzqr> paramArrayList, zzrd paramzzrd)
  {
    this.zg = paramLock;
    this.zzajy = paramLooper;
    this.zn = paramLock.newCondition();
    this.zm = paramzzc;
    this.zl = paramzzrd;
    this.zk = paramMap1;
    paramLock = new HashMap();
    paramzzc = paramMap1.keySet().iterator();
    while (paramzzc.hasNext())
    {
      paramMap1 = (Api)paramzzc.next();
      paramLock.put(paramMap1.zzaqv(), paramMap1);
    }
    paramzzc = new HashMap();
    paramMap1 = paramArrayList.iterator();
    while (paramMap1.hasNext())
    {
      paramArrayList = (zzqr)paramMap1.next();
      paramzzc.put(paramArrayList.vS, paramArrayList);
    }
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      paramMap1 = (Map.Entry)paramMap.next();
      paramArrayList = (Api)paramLock.get(paramMap1.getKey());
      paramArrayList = new com.google.android.gms.common.api.zzc(paramContext, paramArrayList, paramLooper, (Api.zze)paramMap1.getValue(), (zzqr)paramzzc.get(paramArrayList)) {};
      this.zj.put((Api.zzc)paramMap1.getKey(), paramArrayList);
    }
    this.xy = zzrh.zzatg();
  }
  
  public ConnectionResult blockingConnect()
  {
    connect();
    while (isConnecting()) {
      try
      {
        this.zn.await();
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
    if (this.zq != null) {
      return this.zq;
    }
    return new ConnectionResult(13, null);
  }
  
  public ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong); isConnecting(); paramLong = this.zn.awaitNanos(paramLong))
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
    if (this.zq != null) {
      return this.zq;
    }
    return new ConnectionResult(13, null);
  }
  
  public void connect()
  {
    this.zg.lock();
    try
    {
      boolean bool = this.zo;
      if (bool) {
        return;
      }
      this.zo = true;
      this.zp = null;
      this.zq = null;
      zza localzza = new zza(null);
      zzsv localzzsv = new zzsv(this.zzajy);
      this.xy.zza(this.zj.values()).addOnSuccessListener(localzzsv, localzza).addOnFailureListener(localzzsv, localzza);
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public void disconnect()
  {
    this.zg.lock();
    try
    {
      this.zo = false;
      this.zp = null;
      this.zq = null;
      this.zn.signalAll();
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    this.zg.lock();
    try
    {
      if (((com.google.android.gms.common.api.zzc)this.zj.get(paramApi.zzaqv())).getClient().isConnected())
      {
        paramApi = ConnectionResult.wO;
        return paramApi;
      }
      if (this.zp != null)
      {
        paramApi = (ConnectionResult)this.zp.get(((com.google.android.gms.common.api.zzc)this.zj.get(paramApi.zzaqv())).getApiKey());
        return paramApi;
      }
      return null;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 209 1 0
    //   9: aload_0
    //   10: getfield 142	com/google/android/gms/internal/zzqu:zp	Ljava/util/Map;
    //   13: ifnull +25 -> 38
    //   16: aload_0
    //   17: getfield 139	com/google/android/gms/internal/zzqu:zq	Lcom/google/android/gms/common/ConnectionResult;
    //   20: astore_2
    //   21: aload_2
    //   22: ifnonnull +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 212 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 212 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzqu
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
    //   1: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 209 1 0
    //   9: aload_0
    //   10: getfield 142	com/google/android/gms/internal/zzqu:zp	Ljava/util/Map;
    //   13: ifnonnull +25 -> 38
    //   16: aload_0
    //   17: getfield 146	com/google/android/gms/internal/zzqu:zo	Z
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 212 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 46	com/google/android/gms/internal/zzqu:zg	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 212 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzqu
    //   20	20	1	bool	boolean
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T paramT)
  {
    this.zl.Ap.zzb(paramT);
    return ((com.google.android.gms.common.api.zzc)this.zj.get(paramT.zzaqv())).doRead(paramT);
  }
  
  public boolean zza(zzsa paramzzsa)
  {
    throw new UnsupportedOperationException();
  }
  
  public void zzard()
  {
    throw new UnsupportedOperationException();
  }
  
  public void zzarz() {}
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    this.zl.Ap.zzb(paramT);
    return ((com.google.android.gms.common.api.zzc)this.zj.get(paramT.zzaqv())).doWrite(paramT);
  }
  
  private class zza
    implements OnFailureListener, OnSuccessListener<Void>
  {
    private zza() {}
    
    @Nullable
    private ConnectionResult zzash()
    {
      Object localObject1 = null;
      int i = 0;
      Iterator localIterator = zzqu.zzg(zzqu.this).keySet().iterator();
      Object localObject2;
      int j;
      for (;;)
      {
        if (localIterator.hasNext())
        {
          localObject2 = (Api)localIterator.next();
          ConnectionResult localConnectionResult = (ConnectionResult)zzqu.zzc(zzqu.this).get(((com.google.android.gms.common.api.zzc)zzqu.zzb(zzqu.this).get(((Api)localObject2).zzaqv())).getApiKey());
          if (!localConnectionResult.isSuccess())
          {
            j = ((Integer)zzqu.zzg(zzqu.this).get(localObject2)).intValue();
            if ((j != 2) && ((j != 1) || (localConnectionResult.hasResolution()) || (zzqu.zzh(zzqu.this).isUserResolvableError(localConnectionResult.getErrorCode()))))
            {
              int k = ((Api)localObject2).zzaqs().getPriority();
              j = k;
              localObject2 = localConnectionResult;
              if (localObject1 != null)
              {
                if (i <= k) {
                  break label194;
                }
                localObject2 = localConnectionResult;
                j = k;
              }
            }
          }
        }
      }
      for (;;)
      {
        i = j;
        localObject1 = localObject2;
        break;
        return (ConnectionResult)localObject1;
        label194:
        j = i;
        localObject2 = localObject1;
      }
    }
    
    /* Error */
    public void onFailure(@NonNull Exception paramException)
    {
      // Byte code:
      //   0: aload_1
      //   1: checkcast 118	com/google/android/gms/common/api/zzb
      //   4: astore_1
      //   5: aload_0
      //   6: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   9: invokestatic 121	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;)Ljava/util/concurrent/locks/Lock;
      //   12: invokeinterface 126 1 0
      //   17: aload_0
      //   18: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   21: aload_1
      //   22: invokevirtual 130	com/google/android/gms/common/api/zzb:zzara	()Landroid/support/v4/util/ArrayMap;
      //   25: invokestatic 133	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;Ljava/util/Map;)Ljava/util/Map;
      //   28: pop
      //   29: aload_0
      //   30: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   33: aload_0
      //   34: invokespecial 135	com/google/android/gms/internal/zzqu$zza:zzash	()Lcom/google/android/gms/common/ConnectionResult;
      //   37: invokestatic 138	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;Lcom/google/android/gms/common/ConnectionResult;)Lcom/google/android/gms/common/ConnectionResult;
      //   40: pop
      //   41: aload_0
      //   42: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   45: invokestatic 142	com/google/android/gms/internal/zzqu:zzf	(Lcom/google/android/gms/internal/zzqu;)Lcom/google/android/gms/common/ConnectionResult;
      //   48: ifnonnull +39 -> 87
      //   51: aload_0
      //   52: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   55: invokestatic 146	com/google/android/gms/internal/zzqu:zzd	(Lcom/google/android/gms/internal/zzqu;)Lcom/google/android/gms/internal/zzrd;
      //   58: aconst_null
      //   59: invokevirtual 152	com/google/android/gms/internal/zzrd:zzn	(Landroid/os/Bundle;)V
      //   62: aload_0
      //   63: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   66: invokestatic 156	com/google/android/gms/internal/zzqu:zze	(Lcom/google/android/gms/internal/zzqu;)Ljava/util/concurrent/locks/Condition;
      //   69: invokeinterface 161 1 0
      //   74: aload_0
      //   75: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   78: invokestatic 121	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;)Ljava/util/concurrent/locks/Lock;
      //   81: invokeinterface 164 1 0
      //   86: return
      //   87: aload_0
      //   88: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   91: iconst_0
      //   92: invokestatic 167	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;Z)Z
      //   95: pop
      //   96: aload_0
      //   97: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   100: invokestatic 146	com/google/android/gms/internal/zzqu:zzd	(Lcom/google/android/gms/internal/zzqu;)Lcom/google/android/gms/internal/zzrd;
      //   103: aload_0
      //   104: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   107: invokestatic 142	com/google/android/gms/internal/zzqu:zzf	(Lcom/google/android/gms/internal/zzqu;)Lcom/google/android/gms/common/ConnectionResult;
      //   110: invokevirtual 170	com/google/android/gms/internal/zzrd:zzc	(Lcom/google/android/gms/common/ConnectionResult;)V
      //   113: goto -51 -> 62
      //   116: astore_1
      //   117: aload_0
      //   118: getfield 18	com/google/android/gms/internal/zzqu$zza:zr	Lcom/google/android/gms/internal/zzqu;
      //   121: invokestatic 121	com/google/android/gms/internal/zzqu:zza	(Lcom/google/android/gms/internal/zzqu;)Ljava/util/concurrent/locks/Lock;
      //   124: invokeinterface 164 1 0
      //   129: aload_1
      //   130: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	131	0	this	zza
      //   0	131	1	paramException	Exception
      // Exception table:
      //   from	to	target	type
      //   17	62	116	finally
      //   62	74	116	finally
      //   87	113	116	finally
    }
    
    public void zza(Void paramVoid)
    {
      zzqu.zza(zzqu.this).lock();
      try
      {
        zzqu.zza(zzqu.this, new ArrayMap(zzqu.zzb(zzqu.this).size()));
        paramVoid = zzqu.zzb(zzqu.this).keySet().iterator();
        while (paramVoid.hasNext())
        {
          Api.zzc localzzc = (Api.zzc)paramVoid.next();
          zzqu.zzc(zzqu.this).put(((com.google.android.gms.common.api.zzc)zzqu.zzb(zzqu.this).get(localzzc)).getApiKey(), ConnectionResult.wO);
        }
        zzqu.zzd(zzqu.this).zzn(null);
      }
      finally
      {
        zzqu.zza(zzqu.this).unlock();
      }
      zzqu.zze(zzqu.this).signalAll();
      zzqu.zza(zzqu.this).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */