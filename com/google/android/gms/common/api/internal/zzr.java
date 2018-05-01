package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

final class zzr
  implements zzbp
{
  private final Context mContext;
  private final Looper zzcn;
  private final zzav zzfq;
  private final zzbd zzfr;
  private final zzbd zzfs;
  private final Map<Api.AnyClientKey<?>, zzbd> zzft;
  private final Set<SignInConnectionListener> zzfu = Collections.newSetFromMap(new WeakHashMap());
  private final Api.Client zzfv;
  private Bundle zzfw;
  private ConnectionResult zzfx = null;
  private ConnectionResult zzfy = null;
  private boolean zzfz = false;
  private final Lock zzga;
  @GuardedBy("mLock")
  private int zzgb = 0;
  
  private zzr(Context paramContext, zzav paramzzav, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map<Api.AnyClientKey<?>, Api.Client> paramMap1, Map<Api.AnyClientKey<?>, Api.Client> paramMap2, ClientSettings paramClientSettings, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, Api.Client paramClient, ArrayList<zzp> paramArrayList1, ArrayList<zzp> paramArrayList2, Map<Api<?>, Boolean> paramMap3, Map<Api<?>, Boolean> paramMap4)
  {
    this.mContext = paramContext;
    this.zzfq = paramzzav;
    this.zzga = paramLock;
    this.zzcn = paramLooper;
    this.zzfv = paramClient;
    this.zzfr = new zzbd(paramContext, this.zzfq, paramLock, paramLooper, paramGoogleApiAvailabilityLight, paramMap2, null, paramMap4, null, paramArrayList2, new zzt(this, null));
    this.zzfs = new zzbd(paramContext, this.zzfq, paramLock, paramLooper, paramGoogleApiAvailabilityLight, paramMap1, paramClientSettings, paramMap3, paramAbstractClientBuilder, paramArrayList1, new zzu(this, null));
    paramContext = new ArrayMap();
    paramzzav = paramMap2.keySet().iterator();
    while (paramzzav.hasNext()) {
      paramContext.put((Api.AnyClientKey)paramzzav.next(), this.zzfr);
    }
    paramzzav = paramMap1.keySet().iterator();
    while (paramzzav.hasNext()) {
      paramContext.put((Api.AnyClientKey)paramzzav.next(), this.zzfs);
    }
    this.zzft = Collections.unmodifiableMap(paramContext);
  }
  
  public static zzr zza(Context paramContext, zzav paramzzav, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map<Api.AnyClientKey<?>, Api.Client> paramMap, ClientSettings paramClientSettings, Map<Api<?>, Boolean> paramMap1, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, ArrayList<zzp> paramArrayList)
  {
    Object localObject1 = null;
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    Object localObject2 = paramMap.entrySet().iterator();
    paramMap = (Map<Api.AnyClientKey<?>, Api.Client>)localObject1;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Map.Entry)((Iterator)localObject2).next();
      localObject1 = (Api.Client)((Map.Entry)localObject3).getValue();
      if (((Api.Client)localObject1).providesSignIn()) {
        paramMap = (Map<Api.AnyClientKey<?>, Api.Client>)localObject1;
      }
      if (((Api.Client)localObject1).requiresSignIn()) {
        localArrayMap1.put((Api.AnyClientKey)((Map.Entry)localObject3).getKey(), localObject1);
      } else {
        localArrayMap2.put((Api.AnyClientKey)((Map.Entry)localObject3).getKey(), localObject1);
      }
    }
    boolean bool;
    Object localObject4;
    if (!localArrayMap1.isEmpty())
    {
      bool = true;
      Preconditions.checkState(bool, "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
      localObject1 = new ArrayMap();
      localObject2 = new ArrayMap();
      localObject4 = paramMap1.keySet().iterator();
    }
    for (;;)
    {
      if (((Iterator)localObject4).hasNext())
      {
        Api localApi = (Api)((Iterator)localObject4).next();
        localObject3 = localApi.getClientKey();
        if (localArrayMap1.containsKey(localObject3))
        {
          ((Map)localObject1).put(localApi, (Boolean)paramMap1.get(localApi));
          continue;
          bool = false;
          break;
        }
        if (localArrayMap2.containsKey(localObject3)) {
          ((Map)localObject2).put(localApi, (Boolean)paramMap1.get(localApi));
        } else {
          throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
        }
      }
    }
    Object localObject3 = new ArrayList();
    paramMap1 = new ArrayList();
    paramArrayList = (ArrayList)paramArrayList;
    int i = paramArrayList.size();
    int j = 0;
    while (j < i)
    {
      localObject4 = paramArrayList.get(j);
      j++;
      localObject4 = (zzp)localObject4;
      if (((Map)localObject1).containsKey(((zzp)localObject4).mApi)) {
        ((ArrayList)localObject3).add(localObject4);
      } else if (((Map)localObject2).containsKey(((zzp)localObject4).mApi)) {
        paramMap1.add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
      }
    }
    return new zzr(paramContext, paramzzav, paramLock, paramLooper, paramGoogleApiAvailabilityLight, localArrayMap1, localArrayMap2, paramClientSettings, paramAbstractClientBuilder, paramMap, (ArrayList)localObject3, paramMap1, (Map)localObject1, (Map)localObject2);
  }
  
  @GuardedBy("mLock")
  private final void zza(int paramInt, boolean paramBoolean)
  {
    this.zzfq.zzb(paramInt, paramBoolean);
    this.zzfy = null;
    this.zzfx = null;
  }
  
  private final void zza(Bundle paramBundle)
  {
    if (this.zzfw == null) {
      this.zzfw = paramBundle;
    }
    for (;;)
    {
      return;
      if (paramBundle != null) {
        this.zzfw.putAll(paramBundle);
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zza(ConnectionResult paramConnectionResult)
  {
    switch (this.zzgb)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
    }
    for (;;)
    {
      this.zzgb = 0;
      return;
      this.zzfq.zzc(paramConnectionResult);
      zzab();
    }
  }
  
  private final boolean zza(BaseImplementation.ApiMethodImpl<? extends Result, ? extends Api.AnyClient> paramApiMethodImpl)
  {
    paramApiMethodImpl = paramApiMethodImpl.getClientKey();
    Preconditions.checkArgument(this.zzft.containsKey(paramApiMethodImpl), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zzbd)this.zzft.get(paramApiMethodImpl)).equals(this.zzfs);
  }
  
  @GuardedBy("mLock")
  private final void zzaa()
  {
    if (zzb(this.zzfx)) {
      if ((zzb(this.zzfy)) || (zzac())) {
        switch (this.zzgb)
        {
        default: 
          Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
          this.zzgb = 0;
        }
      }
    }
    for (;;)
    {
      return;
      this.zzfq.zzb(this.zzfw);
      zzab();
      break;
      if (this.zzfy != null) {
        if (this.zzgb == 1)
        {
          zzab();
        }
        else
        {
          zza(this.zzfy);
          this.zzfr.disconnect();
          continue;
          if ((this.zzfx != null) && (zzb(this.zzfy)))
          {
            this.zzfs.disconnect();
            zza(this.zzfx);
          }
          else if ((this.zzfx != null) && (this.zzfy != null))
          {
            ConnectionResult localConnectionResult = this.zzfx;
            if (this.zzfs.zzje < this.zzfr.zzje) {
              localConnectionResult = this.zzfy;
            }
            zza(localConnectionResult);
          }
        }
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzab()
  {
    Iterator localIterator = this.zzfu.iterator();
    while (localIterator.hasNext()) {
      ((SignInConnectionListener)localIterator.next()).onComplete();
    }
    this.zzfu.clear();
  }
  
  @GuardedBy("mLock")
  private final boolean zzac()
  {
    if ((this.zzfy != null) && (this.zzfy.getErrorCode() == 4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final PendingIntent zzad()
  {
    if (this.zzfv == null) {}
    for (PendingIntent localPendingIntent = null;; localPendingIntent = PendingIntent.getActivity(this.mContext, System.identityHashCode(this.zzfq), this.zzfv.getSignInIntent(), 134217728)) {
      return localPendingIntent;
    }
  }
  
  private static boolean zzb(ConnectionResult paramConnectionResult)
  {
    if ((paramConnectionResult != null) && (paramConnectionResult.isSuccess())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @GuardedBy("mLock")
  public final ConnectionResult blockingConnect()
  {
    throw new UnsupportedOperationException();
  }
  
  @GuardedBy("mLock")
  public final void connect()
  {
    this.zzgb = 2;
    this.zzfz = false;
    this.zzfy = null;
    this.zzfx = null;
    this.zzfr.connect();
    this.zzfs.connect();
  }
  
  @GuardedBy("mLock")
  public final void disconnect()
  {
    this.zzfy = null;
    this.zzfx = null;
    this.zzgb = 0;
    this.zzfr.disconnect();
    this.zzfs.disconnect();
    zzab();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    this.zzfs.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    this.zzfr.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  @GuardedBy("mLock")
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    if (zza(paramT)) {
      if (zzac()) {
        paramT.setFailedResult(new Status(4, null, zzad()));
      }
    }
    for (;;)
    {
      return paramT;
      paramT = this.zzfs.enqueue(paramT);
      continue;
      paramT = this.zzfr.enqueue(paramT);
    }
  }
  
  @GuardedBy("mLock")
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    if (zza(paramT)) {
      if (zzac()) {
        paramT.setFailedResult(new Status(4, null, zzad()));
      }
    }
    for (;;)
    {
      return paramT;
      paramT = this.zzfs.execute(paramT);
      continue;
      paramT = this.zzfr.execute(paramT);
    }
  }
  
  /* Error */
  public final boolean isConnected()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 67	com/google/android/gms/common/api/internal/zzr:zzga	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 398 1 0
    //   11: aload_0
    //   12: getfield 83	com/google/android/gms/common/api/internal/zzr:zzfr	Lcom/google/android/gms/common/api/internal/zzbd;
    //   15: invokevirtual 400	com/google/android/gms/common/api/internal/zzbd:isConnected	()Z
    //   18: ifeq +47 -> 65
    //   21: iload_1
    //   22: istore_2
    //   23: aload_0
    //   24: getfield 88	com/google/android/gms/common/api/internal/zzr:zzfs	Lcom/google/android/gms/common/api/internal/zzbd;
    //   27: invokevirtual 400	com/google/android/gms/common/api/internal/zzbd:isConnected	()Z
    //   30: ifne +24 -> 54
    //   33: iload_1
    //   34: istore_2
    //   35: aload_0
    //   36: invokespecial 272	com/google/android/gms/common/api/internal/zzr:zzac	()Z
    //   39: ifne +15 -> 54
    //   42: aload_0
    //   43: getfield 61	com/google/android/gms/common/api/internal/zzr:zzgb	I
    //   46: istore_3
    //   47: iload_3
    //   48: iconst_1
    //   49: if_icmpne +16 -> 65
    //   52: iload_1
    //   53: istore_2
    //   54: aload_0
    //   55: getfield 67	com/google/android/gms/common/api/internal/zzr:zzga	Ljava/util/concurrent/locks/Lock;
    //   58: invokeinterface 403 1 0
    //   63: iload_2
    //   64: ireturn
    //   65: iconst_0
    //   66: istore_2
    //   67: goto -13 -> 54
    //   70: astore 4
    //   72: aload_0
    //   73: getfield 67	com/google/android/gms/common/api/internal/zzr:zzga	Ljava/util/concurrent/locks/Lock;
    //   76: invokeinterface 403 1 0
    //   81: aload 4
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	zzr
    //   1	52	1	bool1	boolean
    //   22	45	2	bool2	boolean
    //   46	4	3	i	int
    //   70	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	21	70	finally
    //   23	33	70	finally
    //   35	47	70	finally
  }
  
  @GuardedBy("mLock")
  public final void zzz()
  {
    this.zzfr.zzz();
    this.zzfs.zzz();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */