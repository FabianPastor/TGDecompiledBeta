package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
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
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.zzc;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

final class zzqt
  implements zzrm
{
  private final Context mContext;
  private final zzrd yW;
  private final zzrf yX;
  private final zzrf yY;
  private final Map<Api.zzc<?>, zzrf> yZ;
  private final Set<zzsa> za = Collections.newSetFromMap(new WeakHashMap());
  private final Api.zze zb;
  private Bundle zc;
  private ConnectionResult zd = null;
  private ConnectionResult ze = null;
  private boolean zf = false;
  private final Lock zg;
  private int zh = 0;
  private final Looper zzajy;
  
  private zzqt(Context paramContext, zzrd paramzzrd, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap1, Map<Api.zzc<?>, Api.zze> paramMap2, zzf paramzzf, Api.zza<? extends zzxp, zzxq> paramzza, Api.zze paramzze, ArrayList<zzqr> paramArrayList1, ArrayList<zzqr> paramArrayList2, Map<Api<?>, Integer> paramMap3, Map<Api<?>, Integer> paramMap4)
  {
    this.mContext = paramContext;
    this.yW = paramzzrd;
    this.zg = paramLock;
    this.zzajy = paramLooper;
    this.zb = paramzze;
    this.yX = new zzrf(paramContext, this.yW, paramLock, paramLooper, paramzzc, paramMap2, null, paramMap4, null, paramArrayList2, new zza(null));
    this.yY = new zzrf(paramContext, this.yW, paramLock, paramLooper, paramzzc, paramMap1, paramzzf, paramMap3, paramzza, paramArrayList1, new zzb(null));
    paramContext = new ArrayMap();
    paramzzrd = paramMap2.keySet().iterator();
    while (paramzzrd.hasNext()) {
      paramContext.put((Api.zzc)paramzzrd.next(), this.yX);
    }
    paramzzrd = paramMap1.keySet().iterator();
    while (paramzzrd.hasNext()) {
      paramContext.put((Api.zzc)paramzzrd.next(), this.yY);
    }
    this.yZ = Collections.unmodifiableMap(paramContext);
  }
  
  public static zzqt zza(Context paramContext, zzrd paramzzrd, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap, zzf paramzzf, Map<Api<?>, Integer> paramMap1, Api.zza<? extends zzxp, zzxq> paramzza, ArrayList<zzqr> paramArrayList)
  {
    Object localObject1 = null;
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    Object localObject2 = paramMap.entrySet().iterator();
    paramMap = (Map<Api.zzc<?>, Api.zze>)localObject1;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Map.Entry)((Iterator)localObject2).next();
      localObject1 = (Api.zze)((Map.Entry)localObject3).getValue();
      if (((Api.zze)localObject1).zzajc()) {
        paramMap = (Map<Api.zzc<?>, Api.zze>)localObject1;
      }
      if (((Api.zze)localObject1).zzain()) {
        localArrayMap1.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      } else {
        localArrayMap2.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      }
    }
    boolean bool;
    if (!localArrayMap1.isEmpty())
    {
      bool = true;
      zzaa.zza(bool, "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
      localObject1 = new ArrayMap();
      localObject2 = new ArrayMap();
      localObject3 = paramMap1.keySet().iterator();
    }
    Object localObject4;
    for (;;)
    {
      if (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Api)((Iterator)localObject3).next();
        Api.zzc localzzc = ((Api)localObject4).zzaqv();
        if (localArrayMap1.containsKey(localzzc))
        {
          ((Map)localObject1).put(localObject4, (Integer)paramMap1.get(localObject4));
          continue;
          bool = false;
          break;
        }
        if (localArrayMap2.containsKey(localzzc)) {
          ((Map)localObject2).put(localObject4, (Integer)paramMap1.get(localObject4));
        } else {
          throw new IllegalStateException("Each API in the apiTypeMap must have a corresponding client in the clients map.");
        }
      }
    }
    paramMap1 = new ArrayList();
    Object localObject3 = new ArrayList();
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      localObject4 = (zzqr)paramArrayList.next();
      if (((Map)localObject1).containsKey(((zzqr)localObject4).vS)) {
        paramMap1.add(localObject4);
      } else if (((Map)localObject2).containsKey(((zzqr)localObject4).vS)) {
        ((ArrayList)localObject3).add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
      }
    }
    return new zzqt(paramContext, paramzzrd, paramLock, paramLooper, paramzzc, localArrayMap1, localArrayMap2, paramzzf, paramzza, paramMap, paramMap1, (ArrayList)localObject3, (Map)localObject1, (Map)localObject2);
  }
  
  private void zza(ConnectionResult paramConnectionResult)
  {
    switch (this.zh)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
    }
    for (;;)
    {
      this.zh = 0;
      return;
      this.yW.zzc(paramConnectionResult);
      zzase();
    }
  }
  
  private void zzasb()
  {
    this.ze = null;
    this.zd = null;
    this.yX.connect();
    this.yY.connect();
  }
  
  private void zzasc()
  {
    if (zzb(this.zd)) {
      if ((zzb(this.ze)) || (zzasf())) {
        zzasd();
      }
    }
    do
    {
      do
      {
        return;
      } while (this.ze == null);
      if (this.zh == 1)
      {
        zzase();
        return;
      }
      zza(this.ze);
      this.yX.disconnect();
      return;
      if ((this.zd != null) && (zzb(this.ze)))
      {
        this.yY.disconnect();
        zza(this.zd);
        return;
      }
    } while ((this.zd == null) || (this.ze == null));
    ConnectionResult localConnectionResult = this.zd;
    if (this.yY.AB < this.yX.AB) {
      localConnectionResult = this.ze;
    }
    zza(localConnectionResult);
  }
  
  private void zzasd()
  {
    switch (this.zh)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
    }
    for (;;)
    {
      this.zh = 0;
      return;
      this.yW.zzn(this.zc);
      zzase();
    }
  }
  
  private void zzase()
  {
    Iterator localIterator = this.za.iterator();
    while (localIterator.hasNext()) {
      ((zzsa)localIterator.next()).zzajb();
    }
    this.za.clear();
  }
  
  private boolean zzasf()
  {
    return (this.ze != null) && (this.ze.getErrorCode() == 4);
  }
  
  @Nullable
  private PendingIntent zzasg()
  {
    if (this.zb == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, this.yW.getSessionId(), this.zb.zzajd(), 134217728);
  }
  
  private void zzb(int paramInt, boolean paramBoolean)
  {
    this.yW.zzc(paramInt, paramBoolean);
    this.ze = null;
    this.zd = null;
  }
  
  private static boolean zzb(ConnectionResult paramConnectionResult)
  {
    return (paramConnectionResult != null) && (paramConnectionResult.isSuccess());
  }
  
  private boolean zzc(zzqo.zza<? extends Result, ? extends Api.zzb> paramzza)
  {
    paramzza = paramzza.zzaqv();
    zzaa.zzb(this.yZ.containsKey(paramzza), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zzrf)this.yZ.get(paramzza)).equals(this.yY);
  }
  
  private void zzm(Bundle paramBundle)
  {
    if (this.zc == null) {
      this.zc = paramBundle;
    }
    while (paramBundle == null) {
      return;
    }
    this.zc.putAll(paramBundle);
  }
  
  public ConnectionResult blockingConnect()
  {
    throw new UnsupportedOperationException();
  }
  
  public ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public void connect()
  {
    this.zh = 2;
    this.zf = false;
    zzasb();
  }
  
  public void disconnect()
  {
    this.ze = null;
    this.zd = null;
    this.zh = 0;
    this.yX.disconnect();
    this.yY.disconnect();
    zzase();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    this.yY.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    this.yX.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    if (((zzrf)this.yZ.get(paramApi.zzaqv())).equals(this.yY))
    {
      if (zzasf()) {
        return new ConnectionResult(4, zzasg());
      }
      return this.yY.getConnectionResult(paramApi);
    }
    return this.yX.getConnectionResult(paramApi);
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 387 1 0
    //   11: aload_0
    //   12: getfield 86	com/google/android/gms/internal/zzqt:yX	Lcom/google/android/gms/internal/zzrf;
    //   15: invokevirtual 389	com/google/android/gms/internal/zzrf:isConnected	()Z
    //   18: ifeq +44 -> 62
    //   21: iload_3
    //   22: istore_2
    //   23: aload_0
    //   24: invokevirtual 392	com/google/android/gms/internal/zzqt:zzasa	()Z
    //   27: ifne +24 -> 51
    //   30: iload_3
    //   31: istore_2
    //   32: aload_0
    //   33: invokespecial 246	com/google/android/gms/internal/zzqt:zzasf	()Z
    //   36: ifne +15 -> 51
    //   39: aload_0
    //   40: getfield 66	com/google/android/gms/internal/zzqt:zh	I
    //   43: istore_1
    //   44: iload_1
    //   45: iconst_1
    //   46: if_icmpne +16 -> 62
    //   49: iload_3
    //   50: istore_2
    //   51: aload_0
    //   52: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 395 1 0
    //   60: iload_2
    //   61: ireturn
    //   62: iconst_0
    //   63: istore_2
    //   64: goto -13 -> 51
    //   67: astore 4
    //   69: aload_0
    //   70: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   73: invokeinterface 395 1 0
    //   78: aload 4
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	zzqt
    //   43	4	1	i	int
    //   22	42	2	bool1	boolean
    //   1	49	3	bool2	boolean
    //   67	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	21	67	finally
    //   23	30	67	finally
    //   32	44	67	finally
  }
  
  /* Error */
  public boolean isConnecting()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: getfield 66	com/google/android/gms/internal/zzqt:zh	I
    //   13: istore_1
    //   14: iload_1
    //   15: iconst_2
    //   16: if_icmpne +16 -> 32
    //   19: iconst_1
    //   20: istore_2
    //   21: aload_0
    //   22: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   25: invokeinterface 395 1 0
    //   30: iload_2
    //   31: ireturn
    //   32: iconst_0
    //   33: istore_2
    //   34: goto -13 -> 21
    //   37: astore_3
    //   38: aload_0
    //   39: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   42: invokeinterface 395 1 0
    //   47: aload_3
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	zzqt
    //   13	4	1	i	int
    //   20	14	2	bool	boolean
    //   37	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	14	37	finally
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T paramT)
  {
    if (zzc(paramT))
    {
      if (zzasf())
      {
        paramT.zzaa(new Status(4, null, zzasg()));
        return paramT;
      }
      return this.yY.zza(paramT);
    }
    return this.yX.zza(paramT);
  }
  
  public boolean zza(zzsa paramzzsa)
  {
    this.zg.lock();
    try
    {
      if (((isConnecting()) || (isConnected())) && (!zzasa()))
      {
        this.za.add(paramzzsa);
        if (this.zh == 0) {
          this.zh = 1;
        }
        this.ze = null;
        this.yY.connect();
        return true;
      }
      return false;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  /* Error */
  public void zzard()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: invokevirtual 414	com/google/android/gms/internal/zzqt:isConnecting	()Z
    //   13: istore_1
    //   14: aload_0
    //   15: getfield 89	com/google/android/gms/internal/zzqt:yY	Lcom/google/android/gms/internal/zzrf;
    //   18: invokevirtual 254	com/google/android/gms/internal/zzrf:disconnect	()V
    //   21: aload_0
    //   22: new 277	com/google/android/gms/common/ConnectionResult
    //   25: dup
    //   26: iconst_4
    //   27: invokespecial 420	com/google/android/gms/common/ConnectionResult:<init>	(I)V
    //   30: putfield 62	com/google/android/gms/internal/zzqt:ze	Lcom/google/android/gms/common/ConnectionResult;
    //   33: iload_1
    //   34: ifeq +36 -> 70
    //   37: new 422	android/os/Handler
    //   40: dup
    //   41: aload_0
    //   42: getfield 74	com/google/android/gms/internal/zzqt:zzajy	Landroid/os/Looper;
    //   45: invokespecial 425	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   48: new 8	com/google/android/gms/internal/zzqt$1
    //   51: dup
    //   52: aload_0
    //   53: invokespecial 427	com/google/android/gms/internal/zzqt$1:<init>	(Lcom/google/android/gms/internal/zzqt;)V
    //   56: invokevirtual 431	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   59: pop
    //   60: aload_0
    //   61: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   64: invokeinterface 395 1 0
    //   69: return
    //   70: aload_0
    //   71: invokespecial 225	com/google/android/gms/internal/zzqt:zzase	()V
    //   74: goto -14 -> 60
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 72	com/google/android/gms/internal/zzqt:zg	Ljava/util/concurrent/locks/Lock;
    //   82: invokeinterface 395 1 0
    //   87: aload_2
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	zzqt
    //   13	21	1	bool	boolean
    //   77	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	33	77	finally
    //   37	60	77	finally
    //   70	74	77	finally
  }
  
  public void zzarz()
  {
    this.yX.zzarz();
    this.yY.zzarz();
  }
  
  public boolean zzasa()
  {
    return this.yY.isConnected();
  }
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    if (zzc(paramT))
    {
      if (zzasf())
      {
        paramT.zzaa(new Status(4, null, zzasg()));
        return paramT;
      }
      return this.yY.zzb(paramT);
    }
    return this.yX.zzb(paramT);
  }
  
  private class zza
    implements zzrm.zza
  {
    private zza() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        if ((zzqt.zzc(zzqt.this)) || (zzqt.zzd(zzqt.this) == null) || (!zzqt.zzd(zzqt.this).isSuccess()))
        {
          zzqt.zza(zzqt.this, false);
          zzqt.zza(zzqt.this, paramInt, paramBoolean);
          return;
        }
        zzqt.zza(zzqt.this, true);
        zzqt.zze(zzqt.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
    
    public void zzc(@NonNull ConnectionResult paramConnectionResult)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        zzqt.zza(zzqt.this, paramConnectionResult);
        zzqt.zzb(zzqt.this);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
    
    public void zzn(@Nullable Bundle paramBundle)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        zzqt.zza(zzqt.this, paramBundle);
        zzqt.zza(zzqt.this, ConnectionResult.wO);
        zzqt.zzb(zzqt.this);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
  }
  
  private class zzb
    implements zzrm.zza
  {
    private zzb() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        if (zzqt.zzc(zzqt.this))
        {
          zzqt.zza(zzqt.this, false);
          zzqt.zza(zzqt.this, paramInt, paramBoolean);
          return;
        }
        zzqt.zza(zzqt.this, true);
        zzqt.zzf(zzqt.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
    
    public void zzc(@NonNull ConnectionResult paramConnectionResult)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        zzqt.zzb(zzqt.this, paramConnectionResult);
        zzqt.zzb(zzqt.this);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
    
    public void zzn(@Nullable Bundle paramBundle)
    {
      zzqt.zza(zzqt.this).lock();
      try
      {
        zzqt.zzb(zzqt.this, ConnectionResult.wO);
        zzqt.zzb(zzqt.this);
        return;
      }
      finally
      {
        zzqt.zza(zzqt.this).unlock();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */