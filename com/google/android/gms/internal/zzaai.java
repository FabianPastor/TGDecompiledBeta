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
import com.google.android.gms.common.internal.zzg;
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

final class zzaai
  implements zzabc
{
  private final Context mContext;
  private final Set<zzabq> zzaAA = Collections.newSetFromMap(new WeakHashMap());
  private final Api.zze zzaAB;
  private Bundle zzaAC;
  private ConnectionResult zzaAD = null;
  private ConnectionResult zzaAE = null;
  private boolean zzaAF = false;
  private final Lock zzaAG;
  private int zzaAH = 0;
  private final zzaat zzaAw;
  private final zzaav zzaAx;
  private final zzaav zzaAy;
  private final Map<Api.zzc<?>, zzaav> zzaAz;
  private final Looper zzrs;
  
  private zzaai(Context paramContext, zzaat paramzzaat, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap1, Map<Api.zzc<?>, Api.zze> paramMap2, zzg paramzzg, Api.zza<? extends zzbai, zzbaj> paramzza, Api.zze paramzze1, ArrayList<zzaag> paramArrayList1, ArrayList<zzaag> paramArrayList2, Map<Api<?>, Boolean> paramMap3, Map<Api<?>, Boolean> paramMap4)
  {
    this.mContext = paramContext;
    this.zzaAw = paramzzaat;
    this.zzaAG = paramLock;
    this.zzrs = paramLooper;
    this.zzaAB = paramzze1;
    this.zzaAx = new zzaav(paramContext, this.zzaAw, paramLock, paramLooper, paramzze, paramMap2, null, paramMap4, null, paramArrayList2, new zza(null));
    this.zzaAy = new zzaav(paramContext, this.zzaAw, paramLock, paramLooper, paramzze, paramMap1, paramzzg, paramMap3, paramzza, paramArrayList1, new zzb(null));
    paramContext = new ArrayMap();
    paramzzaat = paramMap2.keySet().iterator();
    while (paramzzaat.hasNext()) {
      paramContext.put((Api.zzc)paramzzaat.next(), this.zzaAx);
    }
    paramzzaat = paramMap1.keySet().iterator();
    while (paramzzaat.hasNext()) {
      paramContext.put((Api.zzc)paramzzaat.next(), this.zzaAy);
    }
    this.zzaAz = Collections.unmodifiableMap(paramContext);
  }
  
  public static zzaai zza(Context paramContext, zzaat paramzzaat, Lock paramLock, Looper paramLooper, zze paramzze, Map<Api.zzc<?>, Api.zze> paramMap, zzg paramzzg, Map<Api<?>, Boolean> paramMap1, Api.zza<? extends zzbai, zzbaj> paramzza, ArrayList<zzaag> paramArrayList)
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
      if (((Api.zze)localObject1).zzrr()) {
        paramMap = (Map<Api.zzc<?>, Api.zze>)localObject1;
      }
      if (((Api.zze)localObject1).zzrd()) {
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
        Api.zzc localzzc = ((Api)localObject4).zzvg();
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
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      localObject4 = (zzaag)paramArrayList.next();
      if (((Map)localObject1).containsKey(((zzaag)localObject4).zzaxf)) {
        paramMap1.add(localObject4);
      } else if (((Map)localObject2).containsKey(((zzaag)localObject4).zzaxf)) {
        ((ArrayList)localObject3).add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
      }
    }
    return new zzaai(paramContext, paramzzaat, paramLock, paramLooper, paramzze, localArrayMap1, localArrayMap2, paramzzg, paramzza, paramMap, paramMap1, (ArrayList)localObject3, (Map)localObject1, (Map)localObject2);
  }
  
  private void zza(ConnectionResult paramConnectionResult)
  {
    switch (this.zzaAH)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
    }
    for (;;)
    {
      this.zzaAH = 0;
      return;
      this.zzaAw.zzc(paramConnectionResult);
      zzvR();
    }
  }
  
  private void zzb(int paramInt, boolean paramBoolean)
  {
    this.zzaAw.zzc(paramInt, paramBoolean);
    this.zzaAE = null;
    this.zzaAD = null;
  }
  
  private static boolean zzb(ConnectionResult paramConnectionResult)
  {
    return (paramConnectionResult != null) && (paramConnectionResult.isSuccess());
  }
  
  private boolean zzc(zzaad.zza<? extends Result, ? extends Api.zzb> paramzza)
  {
    paramzza = paramzza.zzvg();
    zzac.zzb(this.zzaAz.containsKey(paramzza), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zzaav)this.zzaAz.get(paramzza)).equals(this.zzaAy);
  }
  
  private void zzn(Bundle paramBundle)
  {
    if (this.zzaAC == null) {
      this.zzaAC = paramBundle;
    }
    while (paramBundle == null) {
      return;
    }
    this.zzaAC.putAll(paramBundle);
  }
  
  private void zzvO()
  {
    this.zzaAE = null;
    this.zzaAD = null;
    this.zzaAx.connect();
    this.zzaAy.connect();
  }
  
  private void zzvP()
  {
    if (zzb(this.zzaAD)) {
      if ((zzb(this.zzaAE)) || (zzvS())) {
        zzvQ();
      }
    }
    do
    {
      do
      {
        return;
      } while (this.zzaAE == null);
      if (this.zzaAH == 1)
      {
        zzvR();
        return;
      }
      zza(this.zzaAE);
      this.zzaAx.disconnect();
      return;
      if ((this.zzaAD != null) && (zzb(this.zzaAE)))
      {
        this.zzaAy.disconnect();
        zza(this.zzaAD);
        return;
      }
    } while ((this.zzaAD == null) || (this.zzaAE == null));
    ConnectionResult localConnectionResult = this.zzaAD;
    if (this.zzaAy.zzaCi < this.zzaAx.zzaCi) {
      localConnectionResult = this.zzaAE;
    }
    zza(localConnectionResult);
  }
  
  private void zzvQ()
  {
    switch (this.zzaAH)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
    }
    for (;;)
    {
      this.zzaAH = 0;
      return;
      this.zzaAw.zzo(this.zzaAC);
      zzvR();
    }
  }
  
  private void zzvR()
  {
    Iterator localIterator = this.zzaAA.iterator();
    while (localIterator.hasNext()) {
      ((zzabq)localIterator.next()).zzrq();
    }
    this.zzaAA.clear();
  }
  
  private boolean zzvS()
  {
    return (this.zzaAE != null) && (this.zzaAE.getErrorCode() == 4);
  }
  
  @Nullable
  private PendingIntent zzvT()
  {
    if (this.zzaAB == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, this.zzaAw.getSessionId(), this.zzaAB.zzrs(), 134217728);
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
    this.zzaAH = 2;
    this.zzaAF = false;
    zzvO();
  }
  
  public void disconnect()
  {
    this.zzaAE = null;
    this.zzaAD = null;
    this.zzaAH = 0;
    this.zzaAx.disconnect();
    this.zzaAy.disconnect();
    zzvR();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    this.zzaAy.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    this.zzaAx.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  @Nullable
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    if (((zzaav)this.zzaAz.get(paramApi.zzvg())).equals(this.zzaAy))
    {
      if (zzvS()) {
        return new ConnectionResult(4, zzvT());
      }
      return this.zzaAy.getConnectionResult(paramApi);
    }
    return this.zzaAx.getConnectionResult(paramApi);
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 386 1 0
    //   11: aload_0
    //   12: getfield 86	com/google/android/gms/internal/zzaai:zzaAx	Lcom/google/android/gms/internal/zzaav;
    //   15: invokevirtual 388	com/google/android/gms/internal/zzaav:isConnected	()Z
    //   18: ifeq +44 -> 62
    //   21: iload_3
    //   22: istore_2
    //   23: aload_0
    //   24: invokevirtual 391	com/google/android/gms/internal/zzaai:zzvN	()Z
    //   27: ifne +24 -> 51
    //   30: iload_3
    //   31: istore_2
    //   32: aload_0
    //   33: invokespecial 281	com/google/android/gms/internal/zzaai:zzvS	()Z
    //   36: ifne +15 -> 51
    //   39: aload_0
    //   40: getfield 66	com/google/android/gms/internal/zzaai:zzaAH	I
    //   43: istore_1
    //   44: iload_1
    //   45: iconst_1
    //   46: if_icmpne +16 -> 62
    //   49: iload_3
    //   50: istore_2
    //   51: aload_0
    //   52: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 394 1 0
    //   60: iload_2
    //   61: ireturn
    //   62: iconst_0
    //   63: istore_2
    //   64: goto -13 -> 51
    //   67: astore 4
    //   69: aload_0
    //   70: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   73: invokeinterface 394 1 0
    //   78: aload 4
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	zzaai
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
    //   1: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 386 1 0
    //   9: aload_0
    //   10: getfield 66	com/google/android/gms/internal/zzaai:zzaAH	I
    //   13: istore_1
    //   14: iload_1
    //   15: iconst_2
    //   16: if_icmpne +16 -> 32
    //   19: iconst_1
    //   20: istore_2
    //   21: aload_0
    //   22: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   25: invokeinterface 394 1 0
    //   30: iload_2
    //   31: ireturn
    //   32: iconst_0
    //   33: istore_2
    //   34: goto -13 -> 21
    //   37: astore_3
    //   38: aload_0
    //   39: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   42: invokeinterface 394 1 0
    //   47: aload_3
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	zzaai
    //   13	4	1	i	int
    //   20	14	2	bool	boolean
    //   37	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	14	37	finally
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(@NonNull T paramT)
  {
    if (zzc(paramT))
    {
      if (zzvS())
      {
        paramT.zzB(new Status(4, null, zzvT()));
        return paramT;
      }
      return this.zzaAy.zza(paramT);
    }
    return this.zzaAx.zza(paramT);
  }
  
  public boolean zza(zzabq paramzzabq)
  {
    this.zzaAG.lock();
    try
    {
      if (((isConnecting()) || (isConnected())) && (!zzvN()))
      {
        this.zzaAA.add(paramzzabq);
        if (this.zzaAH == 0) {
          this.zzaAH = 1;
        }
        this.zzaAE = null;
        this.zzaAy.connect();
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
    if (zzc(paramT))
    {
      if (zzvS())
      {
        paramT.zzB(new Status(4, null, zzvT()));
        return paramT;
      }
      return this.zzaAy.zzb(paramT);
    }
    return this.zzaAx.zzb(paramT);
  }
  
  public void zzvM()
  {
    this.zzaAx.zzvM();
    this.zzaAy.zzvM();
  }
  
  public boolean zzvN()
  {
    return this.zzaAy.isConnected();
  }
  
  /* Error */
  public void zzvn()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 386 1 0
    //   9: aload_0
    //   10: invokevirtual 413	com/google/android/gms/internal/zzaai:isConnecting	()Z
    //   13: istore_1
    //   14: aload_0
    //   15: getfield 89	com/google/android/gms/internal/zzaai:zzaAy	Lcom/google/android/gms/internal/zzaav;
    //   18: invokevirtual 289	com/google/android/gms/internal/zzaav:disconnect	()V
    //   21: aload_0
    //   22: new 244	com/google/android/gms/common/ConnectionResult
    //   25: dup
    //   26: iconst_4
    //   27: invokespecial 425	com/google/android/gms/common/ConnectionResult:<init>	(I)V
    //   30: putfield 62	com/google/android/gms/internal/zzaai:zzaAE	Lcom/google/android/gms/common/ConnectionResult;
    //   33: iload_1
    //   34: ifeq +36 -> 70
    //   37: new 427	android/os/Handler
    //   40: dup
    //   41: aload_0
    //   42: getfield 74	com/google/android/gms/internal/zzaai:zzrs	Landroid/os/Looper;
    //   45: invokespecial 430	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   48: new 8	com/google/android/gms/internal/zzaai$1
    //   51: dup
    //   52: aload_0
    //   53: invokespecial 432	com/google/android/gms/internal/zzaai$1:<init>	(Lcom/google/android/gms/internal/zzaai;)V
    //   56: invokevirtual 436	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   59: pop
    //   60: aload_0
    //   61: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   64: invokeinterface 394 1 0
    //   69: return
    //   70: aload_0
    //   71: invokespecial 225	com/google/android/gms/internal/zzaai:zzvR	()V
    //   74: goto -14 -> 60
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 72	com/google/android/gms/internal/zzaai:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   82: invokeinterface 394 1 0
    //   87: aload_2
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	zzaai
    //   13	21	1	bool	boolean
    //   77	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	33	77	finally
    //   37	60	77	finally
    //   70	74	77	finally
  }
  
  private class zza
    implements zzabc.zza
  {
    private zza() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        if ((zzaai.zzc(zzaai.this)) || (zzaai.zzd(zzaai.this) == null) || (!zzaai.zzd(zzaai.this).isSuccess()))
        {
          zzaai.zza(zzaai.this, false);
          zzaai.zza(zzaai.this, paramInt, paramBoolean);
          return;
        }
        zzaai.zza(zzaai.this, true);
        zzaai.zze(zzaai.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
    
    public void zzc(@NonNull ConnectionResult paramConnectionResult)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        zzaai.zza(zzaai.this, paramConnectionResult);
        zzaai.zzb(zzaai.this);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
    
    public void zzo(@Nullable Bundle paramBundle)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        zzaai.zza(zzaai.this, paramBundle);
        zzaai.zza(zzaai.this, ConnectionResult.zzayj);
        zzaai.zzb(zzaai.this);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
  }
  
  private class zzb
    implements zzabc.zza
  {
    private zzb() {}
    
    public void zzc(int paramInt, boolean paramBoolean)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        if (zzaai.zzc(zzaai.this))
        {
          zzaai.zza(zzaai.this, false);
          zzaai.zza(zzaai.this, paramInt, paramBoolean);
          return;
        }
        zzaai.zza(zzaai.this, true);
        zzaai.zzf(zzaai.this).onConnectionSuspended(paramInt);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
    
    public void zzc(@NonNull ConnectionResult paramConnectionResult)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        zzaai.zzb(zzaai.this, paramConnectionResult);
        zzaai.zzb(zzaai.this);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
    
    public void zzo(@Nullable Bundle paramBundle)
    {
      zzaai.zza(zzaai.this).lock();
      try
      {
        zzaai.zzb(zzaai.this, ConnectionResult.zzayj);
        zzaai.zzb(zzaai.this);
        return;
      }
      finally
      {
        zzaai.zza(zzaai.this).unlock();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */