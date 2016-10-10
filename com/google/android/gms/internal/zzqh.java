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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzh;
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

final class zzqh
  implements zzqy
{
  private final Context mContext;
  private final zzqp wV;
  private final zzqr wW;
  private final zzqr wX;
  private final Map<Api.zzc<?>, zzqr> wY;
  private final Set<zzrl> wZ = Collections.newSetFromMap(new WeakHashMap());
  private final Api.zze xa;
  private Bundle xb;
  private ConnectionResult xc = null;
  private ConnectionResult xd = null;
  private boolean xe = false;
  private final Lock xf;
  private int xg = 0;
  private final Looper zzajn;
  
  private zzqh(Context paramContext, zzqp paramzzqp, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap1, Map<Api.zzc<?>, Api.zze> paramMap2, zzh paramzzh, Api.zza<? extends zzwz, zzxa> paramzza, Api.zze paramzze, ArrayList<zzqf> paramArrayList1, ArrayList<zzqf> paramArrayList2, Map<Api<?>, Integer> paramMap3, Map<Api<?>, Integer> paramMap4)
  {
    this.mContext = paramContext;
    this.wV = paramzzqp;
    this.xf = paramLock;
    this.zzajn = paramLooper;
    this.xa = paramzze;
    this.wW = new zzqr(paramContext, this.wV, paramLock, paramLooper, paramzzc, paramMap2, null, paramMap4, null, paramArrayList2, new zza(null));
    this.wX = new zzqr(paramContext, this.wV, paramLock, paramLooper, paramzzc, paramMap1, paramzzh, paramMap3, paramzza, paramArrayList1, new zzb(null));
    paramContext = new ArrayMap();
    paramzzqp = paramMap2.keySet().iterator();
    while (paramzzqp.hasNext()) {
      paramContext.put((Api.zzc)paramzzqp.next(), this.wW);
    }
    paramzzqp = paramMap1.keySet().iterator();
    while (paramzzqp.hasNext()) {
      paramContext.put((Api.zzc)paramzzqp.next(), this.wX);
    }
    this.wY = Collections.unmodifiableMap(paramContext);
  }
  
  public static zzqh zza(Context paramContext, zzqp paramzzqp, Lock paramLock, Looper paramLooper, zzc paramzzc, Map<Api.zzc<?>, Api.zze> paramMap, zzh paramzzh, Map<Api<?>, Integer> paramMap1, Api.zza<? extends zzwz, zzxa> paramzza, ArrayList<zzqf> paramArrayList)
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
      if (((Api.zze)localObject1).zzahs()) {
        paramMap = (Map<Api.zzc<?>, Api.zze>)localObject1;
      }
      if (((Api.zze)localObject1).zzahd()) {
        localArrayMap1.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      } else {
        localArrayMap2.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      }
    }
    boolean bool;
    if (!localArrayMap1.isEmpty())
    {
      bool = true;
      zzac.zza(bool, "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
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
        Api.zzc localzzc = ((Api)localObject4).zzapp();
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
      localObject4 = (zzqf)paramArrayList.next();
      if (((Map)localObject1).containsKey(((zzqf)localObject4).tv)) {
        paramMap1.add(localObject4);
      } else if (((Map)localObject2).containsKey(((zzqf)localObject4).tv)) {
        ((ArrayList)localObject3).add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
      }
    }
    return new zzqh(paramContext, paramzzqp, paramLock, paramLooper, paramzzc, localArrayMap1, localArrayMap2, paramzzh, paramzza, paramMap, paramMap1, (ArrayList)localObject3, (Map)localObject1, (Map)localObject2);
  }
  
  private void zzara()
  {
    this.xd = null;
    this.xc = null;
    this.wW.connect();
    this.wX.connect();
  }
  
  private void zzarb()
  {
    if (zzc(this.xc)) {
      if ((zzc(this.xd)) || (zzare())) {
        zzarc();
      }
    }
    do
    {
      do
      {
        return;
      } while (this.xd == null);
      if (this.xg == 1)
      {
        zzard();
        return;
      }
      zzb(this.xd);
      this.wW.disconnect();
      return;
      if ((this.xc != null) && (zzc(this.xd)))
      {
        this.wX.disconnect();
        zzb(this.xc);
        return;
      }
    } while ((this.xc == null) || (this.xd == null));
    ConnectionResult localConnectionResult = this.xc;
    if (this.wX.yo < this.wW.yo) {
      localConnectionResult = this.xd;
    }
    zzb(localConnectionResult);
  }
  
  private void zzarc()
  {
    switch (this.xg)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
    }
    for (;;)
    {
      this.xg = 0;
      return;
      this.wV.zzn(this.xb);
      zzard();
    }
  }
  
  private void zzard()
  {
    Iterator localIterator = this.wZ.iterator();
    while (localIterator.hasNext()) {
      ((zzrl)localIterator.next()).zzahr();
    }
    this.wZ.clear();
  }
  
  private boolean zzare()
  {
    return (this.xd != null) && (this.xd.getErrorCode() == 4);
  }
  
  @Nullable
  private PendingIntent zzarf()
  {
    if (this.xa == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, this.wV.getSessionId(), this.xa.zzaht(), 134217728);
  }
  
  private void zzb(int paramInt, boolean paramBoolean)
  {
    this.wV.zzc(paramInt, paramBoolean);
    this.xd = null;
    this.xc = null;
  }
  
  private void zzb(ConnectionResult paramConnectionResult)
  {
    switch (this.xg)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
    }
    for (;;)
    {
      this.xg = 0;
      return;
      this.wV.zzd(paramConnectionResult);
      zzard();
    }
  }
  
  private static boolean zzc(ConnectionResult paramConnectionResult)
  {
    return (paramConnectionResult != null) && (paramConnectionResult.isSuccess());
  }
  
  private boolean zze(zzqc.zza<? extends Result, ? extends Api.zzb> paramzza)
  {
    paramzza = paramzza.zzapp();
    zzac.zzb(this.wY.containsKey(paramzza), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zzqr)this.wY.get(paramzza)).equals(this.wX);
  }
  
  private void zzm(Bundle paramBundle)
  {
    if (this.xb == null) {
      this.xb = paramBundle;
    }
    while (paramBundle == null) {
      return;
    }
    this.xb.putAll(paramBundle);
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
    this.xg = 2;
    this.xe = false;
    zzara();
  }
  
  public void disconnect()
  {
    this.xd = null;
    this.xc = null;
    this.xg = 0;
    this.wW.disconnect();
    this.wX.disconnect();
    zzard();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    this.wX.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    this.wW.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    if (((zzqr)this.wY.get(paramApi.zzapp())).equals(this.wX))
    {
      if (zzare()) {
        return new ConnectionResult(4, zzarf());
      }
      return this.wX.getConnectionResult(paramApi);
    }
    return this.wW.getConnectionResult(paramApi);
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 387 1 0
    //   11: aload_0
    //   12: getfield 86	com/google/android/gms/internal/zzqh:wW	Lcom/google/android/gms/internal/zzqr;
    //   15: invokevirtual 389	com/google/android/gms/internal/zzqr:isConnected	()Z
    //   18: ifeq +44 -> 62
    //   21: iload_3
    //   22: istore_2
    //   23: aload_0
    //   24: invokevirtual 392	com/google/android/gms/internal/zzqh:zzaqz	()Z
    //   27: ifne +24 -> 51
    //   30: iload_3
    //   31: istore_2
    //   32: aload_0
    //   33: invokespecial 225	com/google/android/gms/internal/zzqh:zzare	()Z
    //   36: ifne +15 -> 51
    //   39: aload_0
    //   40: getfield 66	com/google/android/gms/internal/zzqh:xg	I
    //   43: istore_1
    //   44: iload_1
    //   45: iconst_1
    //   46: if_icmpne +16 -> 62
    //   49: iload_3
    //   50: istore_2
    //   51: aload_0
    //   52: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 395 1 0
    //   60: iload_2
    //   61: ireturn
    //   62: iconst_0
    //   63: istore_2
    //   64: goto -13 -> 51
    //   67: astore 4
    //   69: aload_0
    //   70: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   73: invokeinterface 395 1 0
    //   78: aload 4
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	zzqh
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
    //   1: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: getfield 66	com/google/android/gms/internal/zzqh:xg	I
    //   13: istore_1
    //   14: iload_1
    //   15: iconst_2
    //   16: if_icmpne +16 -> 32
    //   19: iconst_1
    //   20: istore_2
    //   21: aload_0
    //   22: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   25: invokeinterface 395 1 0
    //   30: iload_2
    //   31: ireturn
    //   32: iconst_0
    //   33: istore_2
    //   34: goto -13 -> 21
    //   37: astore_3
    //   38: aload_0
    //   39: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   42: invokeinterface 395 1 0
    //   47: aload_3
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	zzqh
    //   13	4	1	i	int
    //   20	14	2	bool	boolean
    //   37	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	14	37	finally
  }
  
  public boolean zza(zzrl paramzzrl)
  {
    this.xf.lock();
    try
    {
      if (((isConnecting()) || (isConnected())) && (!zzaqz()))
      {
        this.wZ.add(paramzzrl);
        if (this.xg == 0) {
          this.xg = 1;
        }
        this.xd = null;
        this.wX.connect();
        return true;
      }
      return false;
    }
    finally
    {
      this.xf.unlock();
    }
  }
  
  /* Error */
  public void zzaqb()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: invokevirtual 399	com/google/android/gms/internal/zzqh:isConnecting	()Z
    //   13: istore_1
    //   14: aload_0
    //   15: getfield 89	com/google/android/gms/internal/zzqh:wX	Lcom/google/android/gms/internal/zzqr;
    //   18: invokevirtual 237	com/google/android/gms/internal/zzqr:disconnect	()V
    //   21: aload_0
    //   22: new 270	com/google/android/gms/common/ConnectionResult
    //   25: dup
    //   26: iconst_4
    //   27: invokespecial 405	com/google/android/gms/common/ConnectionResult:<init>	(I)V
    //   30: putfield 62	com/google/android/gms/internal/zzqh:xd	Lcom/google/android/gms/common/ConnectionResult;
    //   33: iload_1
    //   34: ifeq +36 -> 70
    //   37: new 407	android/os/Handler
    //   40: dup
    //   41: aload_0
    //   42: getfield 74	com/google/android/gms/internal/zzqh:zzajn	Landroid/os/Looper;
    //   45: invokespecial 410	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   48: new 8	com/google/android/gms/internal/zzqh$1
    //   51: dup
    //   52: aload_0
    //   53: invokespecial 412	com/google/android/gms/internal/zzqh$1:<init>	(Lcom/google/android/gms/internal/zzqh;)V
    //   56: invokevirtual 416	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   59: pop
    //   60: aload_0
    //   61: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   64: invokeinterface 395 1 0
    //   69: return
    //   70: aload_0
    //   71: invokespecial 231	com/google/android/gms/internal/zzqh:zzard	()V
    //   74: goto -14 -> 60
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 72	com/google/android/gms/internal/zzqh:xf	Ljava/util/concurrent/locks/Lock;
    //   82: invokeinterface 395 1 0
    //   87: aload_2
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	zzqh
    //   13	21	1	bool	boolean
    //   77	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	33	77	finally
    //   37	60	77	finally
    //   70	74	77	finally
  }
  
  public void zzaqy()
  {
    this.wW.zzaqy();
    this.wX.zzaqy();
  }
  
  public boolean zzaqz()
  {
    return this.wX.isConnected();
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(@NonNull T paramT)
  {
    if (zze(paramT))
    {
      if (zzare())
      {
        paramT.zzz(new Status(4, null, zzarf()));
        return paramT;
      }
      return this.wX.zzc(paramT);
    }
    return this.wW.zzc(paramT);
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(@NonNull T paramT)
  {
    if (zze(paramT))
    {
      if (zzare())
      {
        paramT.zzz(new Status(4, null, zzarf()));
        return paramT;
      }
      return this.wX.zzd(paramT);
    }
    return this.wW.zzd(paramT);
  }
  
  private class zza
    implements zzqy.zza
  {
    private zza() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        if ((zzqh.zzc(zzqh.this)) || (zzqh.zzd(zzqh.this) == null) || (!zzqh.zzd(zzqh.this).isSuccess()))
        {
          zzqh.zza(zzqh.this, false);
          zzqh.zza(zzqh.this, paramInt, paramBoolean);
          return;
        }
        zzqh.zza(zzqh.this, true);
        zzqh.zze(zzqh.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
    
    public void zzd(@NonNull ConnectionResult paramConnectionResult)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        zzqh.zza(zzqh.this, paramConnectionResult);
        zzqh.zzb(zzqh.this);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
    
    public void zzn(@Nullable Bundle paramBundle)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        zzqh.zza(zzqh.this, paramBundle);
        zzqh.zza(zzqh.this, ConnectionResult.uJ);
        zzqh.zzb(zzqh.this);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
  }
  
  private class zzb
    implements zzqy.zza
  {
    private zzb() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        if (zzqh.zzc(zzqh.this))
        {
          zzqh.zza(zzqh.this, false);
          zzqh.zza(zzqh.this, paramInt, paramBoolean);
          return;
        }
        zzqh.zza(zzqh.this, true);
        zzqh.zzf(zzqh.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
    
    public void zzd(@NonNull ConnectionResult paramConnectionResult)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        zzqh.zzb(zzqh.this, paramConnectionResult);
        zzqh.zzb(zzqh.this);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
    
    public void zzn(@Nullable Bundle paramBundle)
    {
      zzqh.zza(zzqh.this).lock();
      try
      {
        zzqh.zzb(zzqh.this, ConnectionResult.uJ);
        zzqh.zzb(zzqh.this);
        return;
      }
      finally
      {
        zzqh.zza(zzqh.this).unlock();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */