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
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.zze;
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

final class zzbbk
  implements zzbdp
{
  private final Context mContext;
  private final zzbcp zzaCl;
  private final zzbcx zzaCm;
  private final zzbcx zzaCn;
  private final Map<Api.zzc<?>, zzbcx> zzaCo;
  private final Set<zzbei> zzaCp = Collections.newSetFromMap(new WeakHashMap());
  private final Api.zze zzaCq;
  private Bundle zzaCr;
  private ConnectionResult zzaCs = null;
  private ConnectionResult zzaCt = null;
  private boolean zzaCu = false;
  private final Lock zzaCv;
  private int zzaCw = 0;
  private final Looper zzrM;
  
  private zzbbk(Context paramContext, zzbcp paramzzbcp, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap1, Map<Api.zzc<?>, Api.zze> paramMap2, zzq paramzzq, Api.zza<? extends zzctk, zzctl> paramzza, Api.zze paramzze1, ArrayList<zzbbi> paramArrayList1, ArrayList<zzbbi> paramArrayList2, Map<Api<?>, Boolean> paramMap3, Map<Api<?>, Boolean> paramMap4)
  {
    this.mContext = paramContext;
    this.zzaCl = paramzzbcp;
    this.zzaCv = paramLock;
    this.zzrM = paramLooper;
    this.zzaCq = paramzze1;
    this.zzaCm = new zzbcx(paramContext, this.zzaCl, paramLock, paramLooper, paramzze, paramMap2, null, paramMap4, null, paramArrayList2, new zzbbm(this, null));
    this.zzaCn = new zzbcx(paramContext, this.zzaCl, paramLock, paramLooper, paramzze, paramMap1, paramzzq, paramMap3, paramzza, paramArrayList1, new zzbbn(this, null));
    paramContext = new ArrayMap();
    paramzzbcp = paramMap2.keySet().iterator();
    while (paramzzbcp.hasNext()) {
      paramContext.put((Api.zzc)paramzzbcp.next(), this.zzaCm);
    }
    paramzzbcp = paramMap1.keySet().iterator();
    while (paramzzbcp.hasNext()) {
      paramContext.put((Api.zzc)paramzzbcp.next(), this.zzaCn);
    }
    this.zzaCo = Collections.unmodifiableMap(paramContext);
  }
  
  public static zzbbk zza(Context paramContext, zzbcp paramzzbcp, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap, zzq paramzzq, Map<Api<?>, Boolean> paramMap1, Api.zza<? extends zzctk, zzctl> paramzza, ArrayList<zzbbi> paramArrayList)
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
      if (((Api.zze)localObject1).zzmG()) {
        paramMap = (Map<Api.zzc<?>, Api.zze>)localObject1;
      }
      if (((Api.zze)localObject1).zzmv()) {
        localArrayMap1.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      } else {
        localArrayMap2.put((Api.zzc)((Map.Entry)localObject3).getKey(), localObject1);
      }
    }
    boolean bool;
    if (!localArrayMap1.isEmpty())
    {
      bool = true;
      zzbo.zza(bool, "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
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
        Api.zzc localzzc = ((Api)localObject4).zzpd();
        if (localArrayMap1.containsKey(localzzc))
        {
          ((Map)localObject1).put(localObject4, (Boolean)paramMap1.get(localObject4));
          continue;
          bool = false;
          break;
        }
        if (localArrayMap2.containsKey(localzzc)) {
          ((Map)localObject2).put(localObject4, (Boolean)paramMap1.get(localObject4));
        } else {
          throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
        }
      }
    }
    paramMap1 = new ArrayList();
    Object localObject3 = new ArrayList();
    paramArrayList = (ArrayList)paramArrayList;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      localObject4 = paramArrayList.get(i);
      i += 1;
      localObject4 = (zzbbi)localObject4;
      if (((Map)localObject1).containsKey(((zzbbi)localObject4).zzayW)) {
        paramMap1.add(localObject4);
      } else if (((Map)localObject2).containsKey(((zzbbi)localObject4).zzayW)) {
        ((ArrayList)localObject3).add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
      }
    }
    return new zzbbk(paramContext, paramzzbcp, paramLock, paramLooper, paramzze, localArrayMap1, localArrayMap2, paramzzq, paramzza, paramMap, paramMap1, (ArrayList)localObject3, (Map)localObject1, (Map)localObject2);
  }
  
  private final void zza(ConnectionResult paramConnectionResult)
  {
    switch (this.zzaCw)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
    }
    for (;;)
    {
      this.zzaCw = 0;
      return;
      this.zzaCl.zzc(paramConnectionResult);
      zzpG();
    }
  }
  
  private static boolean zzb(ConnectionResult paramConnectionResult)
  {
    return (paramConnectionResult != null) && (paramConnectionResult.isSuccess());
  }
  
  private final void zzd(int paramInt, boolean paramBoolean)
  {
    this.zzaCl.zze(paramInt, paramBoolean);
    this.zzaCt = null;
    this.zzaCs = null;
  }
  
  private final boolean zzf(zzbay<? extends Result, ? extends Api.zzb> paramzzbay)
  {
    paramzzbay = paramzzbay.zzpd();
    zzbo.zzb(this.zzaCo.containsKey(paramzzbay), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zzbcx)this.zzaCo.get(paramzzbay)).equals(this.zzaCn);
  }
  
  private final void zzl(Bundle paramBundle)
  {
    if (this.zzaCr == null) {
      this.zzaCr = paramBundle;
    }
    while (paramBundle == null) {
      return;
    }
    this.zzaCr.putAll(paramBundle);
  }
  
  private final void zzpF()
  {
    if (zzb(this.zzaCs)) {
      if ((zzb(this.zzaCt)) || (zzpH())) {
        switch (this.zzaCw)
        {
        default: 
          Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
          this.zzaCw = 0;
        }
      }
    }
    do
    {
      do
      {
        return;
        this.zzaCl.zzm(this.zzaCr);
        zzpG();
        break;
      } while (this.zzaCt == null);
      if (this.zzaCw == 1)
      {
        zzpG();
        return;
      }
      zza(this.zzaCt);
      this.zzaCm.disconnect();
      return;
      if ((this.zzaCs != null) && (zzb(this.zzaCt)))
      {
        this.zzaCn.disconnect();
        zza(this.zzaCs);
        return;
      }
    } while ((this.zzaCs == null) || (this.zzaCt == null));
    ConnectionResult localConnectionResult = this.zzaCs;
    if (this.zzaCn.zzaDX < this.zzaCm.zzaDX) {
      localConnectionResult = this.zzaCt;
    }
    zza(localConnectionResult);
  }
  
  private final void zzpG()
  {
    Iterator localIterator = this.zzaCp.iterator();
    while (localIterator.hasNext()) {
      ((zzbei)localIterator.next()).zzmF();
    }
    this.zzaCp.clear();
  }
  
  private final boolean zzpH()
  {
    return (this.zzaCt != null) && (this.zzaCt.getErrorCode() == 4);
  }
  
  @Nullable
  private final PendingIntent zzpI()
  {
    if (this.zzaCq == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, System.identityHashCode(this.zzaCl), this.zzaCq.zzmH(), 134217728);
  }
  
  public final ConnectionResult blockingConnect()
  {
    throw new UnsupportedOperationException();
  }
  
  public final ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public final void connect()
  {
    this.zzaCw = 2;
    this.zzaCu = false;
    this.zzaCt = null;
    this.zzaCs = null;
    this.zzaCm.connect();
    this.zzaCn.connect();
  }
  
  public final void disconnect()
  {
    this.zzaCt = null;
    this.zzaCs = null;
    this.zzaCw = 0;
    this.zzaCm.disconnect();
    this.zzaCn.disconnect();
    zzpG();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    this.zzaCn.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    this.zzaCm.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  @Nullable
  public final ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    if (((zzbcx)this.zzaCo.get(paramApi.zzpd())).equals(this.zzaCn))
    {
      if (zzpH()) {
        return new ConnectionResult(4, zzpI());
      }
      return this.zzaCn.getConnectionResult(paramApi);
    }
    return this.zzaCm.getConnectionResult(paramApi);
  }
  
  /* Error */
  public final boolean isConnected()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 387 1 0
    //   11: aload_0
    //   12: getfield 80	com/google/android/gms/internal/zzbbk:zzaCm	Lcom/google/android/gms/internal/zzbcx;
    //   15: invokevirtual 389	com/google/android/gms/internal/zzbcx:isConnected	()Z
    //   18: ifeq +47 -> 65
    //   21: iload_3
    //   22: istore_2
    //   23: aload_0
    //   24: getfield 85	com/google/android/gms/internal/zzbbk:zzaCn	Lcom/google/android/gms/internal/zzbcx;
    //   27: invokevirtual 389	com/google/android/gms/internal/zzbcx:isConnected	()Z
    //   30: ifne +24 -> 54
    //   33: iload_3
    //   34: istore_2
    //   35: aload_0
    //   36: invokespecial 281	com/google/android/gms/internal/zzbbk:zzpH	()Z
    //   39: ifne +15 -> 54
    //   42: aload_0
    //   43: getfield 58	com/google/android/gms/internal/zzbbk:zzaCw	I
    //   46: istore_1
    //   47: iload_1
    //   48: iconst_1
    //   49: if_icmpne +16 -> 65
    //   52: iload_3
    //   53: istore_2
    //   54: aload_0
    //   55: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   58: invokeinterface 392 1 0
    //   63: iload_2
    //   64: ireturn
    //   65: iconst_0
    //   66: istore_2
    //   67: goto -13 -> 54
    //   70: astore 4
    //   72: aload_0
    //   73: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   76: invokeinterface 392 1 0
    //   81: aload 4
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	zzbbk
    //   46	4	1	i	int
    //   22	45	2	bool1	boolean
    //   1	52	3	bool2	boolean
    //   70	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	21	70	finally
    //   23	33	70	finally
    //   35	47	70	finally
  }
  
  /* Error */
  public final boolean isConnecting()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: getfield 58	com/google/android/gms/internal/zzbbk:zzaCw	I
    //   13: istore_1
    //   14: iload_1
    //   15: iconst_2
    //   16: if_icmpne +16 -> 32
    //   19: iconst_1
    //   20: istore_2
    //   21: aload_0
    //   22: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   25: invokeinterface 392 1 0
    //   30: iload_2
    //   31: ireturn
    //   32: iconst_0
    //   33: istore_2
    //   34: goto -13 -> 21
    //   37: astore_3
    //   38: aload_0
    //   39: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   42: invokeinterface 392 1 0
    //   47: aload_3
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	zzbbk
    //   13	4	1	i	int
    //   20	14	2	bool	boolean
    //   37	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	14	37	finally
  }
  
  public final boolean zza(zzbei paramzzbei)
  {
    this.zzaCv.lock();
    try
    {
      if (((isConnecting()) || (isConnected())) && (!this.zzaCn.isConnected()))
      {
        this.zzaCp.add(paramzzbei);
        if (this.zzaCw == 0) {
          this.zzaCw = 1;
        }
        this.zzaCt = null;
        this.zzaCn.connect();
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
    if (zzf(paramT))
    {
      if (zzpH())
      {
        paramT.zzr(new Status(4, null, zzpI()));
        return paramT;
      }
      return this.zzaCn.zzd(paramT);
    }
    return this.zzaCm.zzd(paramT);
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T paramT)
  {
    if (zzf(paramT))
    {
      if (zzpH())
      {
        paramT.zzr(new Status(4, null, zzpI()));
        return paramT;
      }
      return this.zzaCn.zze(paramT);
    }
    return this.zzaCm.zze(paramT);
  }
  
  public final void zzpE()
  {
    this.zzaCm.zzpE();
    this.zzaCn.zzpE();
  }
  
  /* Error */
  public final void zzpl()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 387 1 0
    //   9: aload_0
    //   10: invokevirtual 396	com/google/android/gms/internal/zzbbk:isConnecting	()Z
    //   13: istore_1
    //   14: aload_0
    //   15: getfield 85	com/google/android/gms/internal/zzbbk:zzaCn	Lcom/google/android/gms/internal/zzbcx;
    //   18: invokevirtual 294	com/google/android/gms/internal/zzbcx:disconnect	()V
    //   21: aload_0
    //   22: new 247	com/google/android/gms/common/ConnectionResult
    //   25: dup
    //   26: iconst_4
    //   27: invokespecial 423	com/google/android/gms/common/ConnectionResult:<init>	(I)V
    //   30: putfield 54	com/google/android/gms/internal/zzbbk:zzaCt	Lcom/google/android/gms/common/ConnectionResult;
    //   33: iload_1
    //   34: ifeq +36 -> 70
    //   37: new 425	android/os/Handler
    //   40: dup
    //   41: aload_0
    //   42: getfield 66	com/google/android/gms/internal/zzbbk:zzrM	Landroid/os/Looper;
    //   45: invokespecial 428	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   48: new 430	com/google/android/gms/internal/zzbbl
    //   51: dup
    //   52: aload_0
    //   53: invokespecial 432	com/google/android/gms/internal/zzbbl:<init>	(Lcom/google/android/gms/internal/zzbbk;)V
    //   56: invokevirtual 436	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   59: pop
    //   60: aload_0
    //   61: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   64: invokeinterface 392 1 0
    //   69: return
    //   70: aload_0
    //   71: invokespecial 228	com/google/android/gms/internal/zzbbk:zzpG	()V
    //   74: goto -14 -> 60
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 64	com/google/android/gms/internal/zzbbk:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   82: invokeinterface 392 1 0
    //   87: aload_2
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	zzbbk
    //   13	21	1	bool	boolean
    //   77	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	33	77	finally
    //   37	60	77	finally
    //   70	74	77	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */