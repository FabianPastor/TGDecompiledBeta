package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.BaseClientBuilder;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.OptionalApiSettings;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

public final class zzw
  implements zzbp
{
  private final Looper zzcn;
  private final GoogleApiManager zzcq;
  private final Lock zzga;
  private final ClientSettings zzgf;
  private final Map<Api.AnyClientKey<?>, zzv<?>> zzgg = new HashMap();
  private final Map<Api.AnyClientKey<?>, zzv<?>> zzgh = new HashMap();
  private final Map<Api<?>, Boolean> zzgi;
  private final zzav zzgj;
  private final GoogleApiAvailabilityLight zzgk;
  private final Condition zzgl;
  private final boolean zzgm;
  private final boolean zzgn;
  private final Queue<BaseImplementation.ApiMethodImpl<?, ?>> zzgo = new LinkedList();
  @GuardedBy("mLock")
  private boolean zzgp;
  @GuardedBy("mLock")
  private Map<zzh<?>, ConnectionResult> zzgq;
  @GuardedBy("mLock")
  private Map<zzh<?>, ConnectionResult> zzgr;
  @GuardedBy("mLock")
  private zzz zzgs;
  @GuardedBy("mLock")
  private ConnectionResult zzgt;
  
  public zzw(Context paramContext, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map<Api.AnyClientKey<?>, Api.Client> paramMap, ClientSettings paramClientSettings, Map<Api<?>, Boolean> paramMap1, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, ArrayList<zzp> paramArrayList, zzav paramzzav, boolean paramBoolean)
  {
    this.zzga = paramLock;
    this.zzcn = paramLooper;
    this.zzgl = paramLock.newCondition();
    this.zzgk = paramGoogleApiAvailabilityLight;
    this.zzgj = paramzzav;
    this.zzgi = paramMap1;
    this.zzgf = paramClientSettings;
    this.zzgm = paramBoolean;
    paramLock = new HashMap();
    paramMap1 = paramMap1.keySet().iterator();
    while (paramMap1.hasNext())
    {
      paramGoogleApiAvailabilityLight = (Api)paramMap1.next();
      paramLock.put(paramGoogleApiAvailabilityLight.getClientKey(), paramGoogleApiAvailabilityLight);
    }
    paramGoogleApiAvailabilityLight = new HashMap();
    paramMap1 = (ArrayList)paramArrayList;
    int i = paramMap1.size();
    int j = 0;
    while (j < i)
    {
      paramArrayList = paramMap1.get(j);
      j++;
      paramArrayList = (zzp)paramArrayList;
      paramGoogleApiAvailabilityLight.put(paramArrayList.mApi, paramArrayList);
    }
    paramMap = paramMap.entrySet().iterator();
    int k = 0;
    i = 1;
    j = 0;
    if (paramMap.hasNext())
    {
      paramArrayList = (Map.Entry)paramMap.next();
      paramzzav = (Api)paramLock.get(paramArrayList.getKey());
      paramMap1 = (Api.Client)paramArrayList.getValue();
      if (paramMap1.requiresGooglePlayServices())
      {
        k = 1;
        if (((Boolean)this.zzgi.get(paramzzav)).booleanValue()) {
          break label485;
        }
        j = i;
        i = 1;
      }
    }
    for (;;)
    {
      paramzzav = new zzv(paramContext, paramzzav, paramLooper, paramMap1, (zzp)paramGoogleApiAvailabilityLight.get(paramzzav), paramClientSettings, paramAbstractClientBuilder);
      this.zzgg.put((Api.AnyClientKey)paramArrayList.getKey(), paramzzav);
      if (paramMap1.requiresSignIn()) {
        this.zzgh.put((Api.AnyClientKey)paramArrayList.getKey(), paramzzav);
      }
      int m = i;
      i = j;
      j = m;
      break;
      m = 0;
      i = j;
      j = m;
      continue;
      if ((k != 0) && (i == 0) && (j == 0)) {}
      for (paramBoolean = true;; paramBoolean = false)
      {
        this.zzgn = paramBoolean;
        this.zzcq = GoogleApiManager.zzbf();
        return;
      }
      label485:
      m = i;
      i = j;
      j = m;
    }
  }
  
  /* Error */
  private final ConnectionResult zza(Api.AnyClientKey<?> paramAnyClientKey)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 182 1 0
    //   9: aload_0
    //   10: getfield 53	com/google/android/gms/common/api/internal/zzw:zzgg	Ljava/util/Map;
    //   13: aload_1
    //   14: invokeinterface 141 2 0
    //   19: checkcast 156	com/google/android/gms/common/api/internal/zzv
    //   22: astore_1
    //   23: aload_0
    //   24: getfield 184	com/google/android/gms/common/api/internal/zzw:zzgq	Ljava/util/Map;
    //   27: ifnull +35 -> 62
    //   30: aload_1
    //   31: ifnull +31 -> 62
    //   34: aload_0
    //   35: getfield 184	com/google/android/gms/common/api/internal/zzw:zzgq	Ljava/util/Map;
    //   38: aload_1
    //   39: invokevirtual 190	com/google/android/gms/common/api/GoogleApi:zzm	()Lcom/google/android/gms/common/api/internal/zzh;
    //   42: invokeinterface 141 2 0
    //   47: checkcast 192	com/google/android/gms/common/ConnectionResult
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 195 1 0
    //   60: aload_1
    //   61: areturn
    //   62: aload_0
    //   63: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   66: invokeinterface 195 1 0
    //   71: aconst_null
    //   72: astore_1
    //   73: goto -13 -> 60
    //   76: astore_1
    //   77: aload_0
    //   78: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   81: invokeinterface 195 1 0
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	zzw
    //   0	88	1	paramAnyClientKey	Api.AnyClientKey<?>
    // Exception table:
    //   from	to	target	type
    //   9	30	76	finally
    //   34	51	76	finally
  }
  
  private final boolean zza(zzv<?> paramzzv, ConnectionResult paramConnectionResult)
  {
    if ((!paramConnectionResult.isSuccess()) && (!paramConnectionResult.hasResolution()) && (((Boolean)this.zzgi.get(paramzzv.getApi())).booleanValue()) && (paramzzv.zzae().requiresGooglePlayServices()) && (this.zzgk.isUserResolvableError(paramConnectionResult.getErrorCode()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @GuardedBy("mLock")
  private final void zzag()
  {
    if (this.zzgf == null) {}
    HashSet localHashSet;
    for (this.zzgj.zzim = Collections.emptySet();; this.zzgj.zzim = localHashSet)
    {
      return;
      localHashSet = new HashSet(this.zzgf.getRequiredScopes());
      Map localMap = this.zzgf.getOptionalApiSettings();
      Iterator localIterator = localMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Api localApi = (Api)localIterator.next();
        ConnectionResult localConnectionResult = getConnectionResult(localApi);
        if ((localConnectionResult != null) && (localConnectionResult.isSuccess())) {
          localHashSet.addAll(((ClientSettings.OptionalApiSettings)localMap.get(localApi)).mScopes);
        }
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzah()
  {
    while (!this.zzgo.isEmpty()) {
      execute((BaseImplementation.ApiMethodImpl)this.zzgo.remove());
    }
    this.zzgj.zzb(null);
  }
  
  @GuardedBy("mLock")
  private final ConnectionResult zzai()
  {
    Iterator localIterator = this.zzgg.values().iterator();
    int i = 0;
    Object localObject1 = null;
    int j = 0;
    Object localObject2 = null;
    Object localObject4;
    int k;
    for (;;)
    {
      if (localIterator.hasNext())
      {
        Object localObject3 = (zzv)localIterator.next();
        localObject4 = ((GoogleApi)localObject3).getApi();
        localObject3 = ((GoogleApi)localObject3).zzm();
        localObject3 = (ConnectionResult)this.zzgq.get(localObject3);
        if ((!((ConnectionResult)localObject3).isSuccess()) && ((!((Boolean)this.zzgi.get(localObject4)).booleanValue()) || (((ConnectionResult)localObject3).hasResolution()) || (this.zzgk.isUserResolvableError(((ConnectionResult)localObject3).getErrorCode())))) {
          if ((((ConnectionResult)localObject3).getErrorCode() == 4) && (this.zzgm))
          {
            k = ((Api)localObject4).zzj().getPriority();
            if ((localObject1 == null) || (i > k))
            {
              i = k;
              localObject1 = localObject3;
            }
          }
          else
          {
            int m = ((Api)localObject4).zzj().getPriority();
            localObject4 = localObject3;
            k = m;
            if (localObject2 != null)
            {
              if (j <= m) {
                break label244;
              }
              k = m;
              localObject4 = localObject3;
            }
          }
        }
      }
    }
    for (;;)
    {
      j = k;
      localObject2 = localObject4;
      break;
      if ((localObject2 != null) && (localObject1 != null) && (j > i)) {
        localObject2 = localObject1;
      }
      for (;;)
      {
        return (ConnectionResult)localObject2;
      }
      label244:
      localObject4 = localObject2;
      k = j;
    }
  }
  
  private final <T extends BaseImplementation.ApiMethodImpl<? extends Result, ? extends Api.AnyClient>> boolean zzb(T paramT)
  {
    Api.AnyClientKey localAnyClientKey = paramT.getClientKey();
    ConnectionResult localConnectionResult = zza(localAnyClientKey);
    if ((localConnectionResult != null) && (localConnectionResult.getErrorCode() == 4)) {
      paramT.setFailedResult(new Status(4, null, this.zzcq.zza(((zzv)this.zzgg.get(localAnyClientKey)).zzm(), System.identityHashCode(this.zzgj))));
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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
          this.zzgl.await();
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
      } else if (this.zzgt != null) {
        localConnectionResult = this.zzgt;
      } else {
        localConnectionResult = new ConnectionResult(13, null);
      }
    }
  }
  
  /* Error */
  public final void connect()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 182 1 0
    //   9: aload_0
    //   10: getfield 232	com/google/android/gms/common/api/internal/zzw:zzgp	Z
    //   13: istore_1
    //   14: iload_1
    //   15: ifeq +13 -> 28
    //   18: aload_0
    //   19: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   22: invokeinterface 195 1 0
    //   27: return
    //   28: aload_0
    //   29: iconst_1
    //   30: putfield 232	com/google/android/gms/common/api/internal/zzw:zzgp	Z
    //   33: aload_0
    //   34: aconst_null
    //   35: putfield 184	com/google/android/gms/common/api/internal/zzw:zzgq	Ljava/util/Map;
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 311	com/google/android/gms/common/api/internal/zzw:zzgr	Ljava/util/Map;
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 389	com/google/android/gms/common/api/internal/zzw:zzgs	Lcom/google/android/gms/common/api/internal/zzz;
    //   48: aload_0
    //   49: aconst_null
    //   50: putfield 199	com/google/android/gms/common/api/internal/zzw:zzgt	Lcom/google/android/gms/common/ConnectionResult;
    //   53: aload_0
    //   54: getfield 174	com/google/android/gms/common/api/internal/zzw:zzcq	Lcom/google/android/gms/common/api/internal/GoogleApiManager;
    //   57: invokevirtual 392	com/google/android/gms/common/api/internal/GoogleApiManager:zzr	()V
    //   60: aload_0
    //   61: getfield 174	com/google/android/gms/common/api/internal/zzw:zzcq	Lcom/google/android/gms/common/api/internal/GoogleApiManager;
    //   64: aload_0
    //   65: getfield 53	com/google/android/gms/common/api/internal/zzw:zzgg	Ljava/util/Map;
    //   68: invokeinterface 297 1 0
    //   73: invokevirtual 395	com/google/android/gms/common/api/internal/GoogleApiManager:zza	(Ljava/lang/Iterable;)Lcom/google/android/gms/tasks/Task;
    //   76: astore_2
    //   77: new 397	com/google/android/gms/common/util/concurrent/HandlerExecutor
    //   80: astore_3
    //   81: aload_3
    //   82: aload_0
    //   83: getfield 64	com/google/android/gms/common/api/internal/zzw:zzcn	Landroid/os/Looper;
    //   86: invokespecial 400	com/google/android/gms/common/util/concurrent/HandlerExecutor:<init>	(Landroid/os/Looper;)V
    //   89: new 402	com/google/android/gms/common/api/internal/zzy
    //   92: astore 4
    //   94: aload 4
    //   96: aload_0
    //   97: aconst_null
    //   98: invokespecial 405	com/google/android/gms/common/api/internal/zzy:<init>	(Lcom/google/android/gms/common/api/internal/zzw;Lcom/google/android/gms/common/api/internal/zzx;)V
    //   101: aload_2
    //   102: aload_3
    //   103: aload 4
    //   105: invokevirtual 411	com/google/android/gms/tasks/Task:addOnCompleteListener	(Ljava/util/concurrent/Executor;Lcom/google/android/gms/tasks/OnCompleteListener;)Lcom/google/android/gms/tasks/Task;
    //   108: pop
    //   109: aload_0
    //   110: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   113: invokeinterface 195 1 0
    //   118: goto -91 -> 27
    //   121: astore_2
    //   122: aload_0
    //   123: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   126: invokeinterface 195 1 0
    //   131: aload_2
    //   132: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	this	zzw
    //   13	2	1	bool	boolean
    //   76	26	2	localTask	com.google.android.gms.tasks.Task
    //   121	11	2	localObject	Object
    //   80	23	3	localHandlerExecutor	com.google.android.gms.common.util.concurrent.HandlerExecutor
    //   92	12	4	localzzy	zzy
    // Exception table:
    //   from	to	target	type
    //   9	14	121	finally
    //   28	109	121	finally
  }
  
  public final void disconnect()
  {
    this.zzga.lock();
    try
    {
      this.zzgp = false;
      this.zzgq = null;
      this.zzgr = null;
      if (this.zzgs != null)
      {
        this.zzgs.cancel();
        this.zzgs = null;
      }
      this.zzgt = null;
      while (!this.zzgo.isEmpty())
      {
        BaseImplementation.ApiMethodImpl localApiMethodImpl = (BaseImplementation.ApiMethodImpl)this.zzgo.remove();
        localApiMethodImpl.zza(null);
        localApiMethodImpl.cancel();
      }
      this.zzgl.signalAll();
    }
    finally
    {
      this.zzga.unlock();
    }
    this.zzga.unlock();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    if ((this.zzgm) && (zzb(paramT))) {}
    for (;;)
    {
      return paramT;
      if (!isConnected())
      {
        this.zzgo.add(paramT);
      }
      else
      {
        this.zzgj.zzir.zzb(paramT);
        paramT = ((zzv)this.zzgg.get(paramT.getClientKey())).doRead(paramT);
      }
    }
  }
  
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    Api.AnyClientKey localAnyClientKey = paramT.getClientKey();
    if ((this.zzgm) && (zzb(paramT))) {}
    for (;;)
    {
      return paramT;
      this.zzgj.zzir.zzb(paramT);
      paramT = ((zzv)this.zzgg.get(localAnyClientKey)).doWrite(paramT);
    }
  }
  
  public final ConnectionResult getConnectionResult(Api<?> paramApi)
  {
    return zza(paramApi.getClientKey());
  }
  
  /* Error */
  public final boolean isConnected()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 182 1 0
    //   9: aload_0
    //   10: getfield 184	com/google/android/gms/common/api/internal/zzw:zzgq	Ljava/util/Map;
    //   13: ifnull +25 -> 38
    //   16: aload_0
    //   17: getfield 199	com/google/android/gms/common/api/internal/zzw:zzgt	Lcom/google/android/gms/common/ConnectionResult;
    //   20: astore_1
    //   21: aload_1
    //   22: ifnonnull +16 -> 38
    //   25: iconst_1
    //   26: istore_2
    //   27: aload_0
    //   28: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 195 1 0
    //   36: iload_2
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_2
    //   40: goto -13 -> 27
    //   43: astore_1
    //   44: aload_0
    //   45: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 195 1 0
    //   53: aload_1
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzw
    //   20	2	1	localConnectionResult	ConnectionResult
    //   43	11	1	localObject	Object
    //   26	14	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  /* Error */
  public final boolean isConnecting()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 182 1 0
    //   9: aload_0
    //   10: getfield 184	com/google/android/gms/common/api/internal/zzw:zzgq	Ljava/util/Map;
    //   13: ifnonnull +25 -> 38
    //   16: aload_0
    //   17: getfield 232	com/google/android/gms/common/api/internal/zzw:zzgp	Z
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq +16 -> 38
    //   25: iconst_1
    //   26: istore_1
    //   27: aload_0
    //   28: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   31: invokeinterface 195 1 0
    //   36: iload_1
    //   37: ireturn
    //   38: iconst_0
    //   39: istore_1
    //   40: goto -13 -> 27
    //   43: astore_2
    //   44: aload_0
    //   45: getfield 62	com/google/android/gms/common/api/internal/zzw:zzga	Ljava/util/concurrent/locks/Lock;
    //   48: invokeinterface 195 1 0
    //   53: aload_2
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	zzw
    //   20	20	1	bool	boolean
    //   43	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	43	finally
  }
  
  public final void zzz() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */