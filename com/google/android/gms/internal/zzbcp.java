package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzad;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public final class zzbcp
  extends GoogleApiClient
  implements zzbdq
{
  private final Context mContext;
  private final int zzaBb;
  private final GoogleApiAvailability zzaBd;
  private Api.zza<? extends zzctk, zzctl> zzaBe;
  private boolean zzaBh;
  private zzq zzaCA;
  private Map<Api<?>, Boolean> zzaCD;
  final Queue<zzbay<?, ?>> zzaCJ = new LinkedList();
  private final Lock zzaCv;
  private volatile boolean zzaDA;
  private long zzaDB = 120000L;
  private long zzaDC = 5000L;
  private final zzbcu zzaDD;
  private zzbdk zzaDE;
  final Map<Api.zzc<?>, Api.zze> zzaDF;
  Set<Scope> zzaDG = new HashSet();
  private final zzbea zzaDH = new zzbea();
  private final ArrayList<zzbbi> zzaDI;
  private Integer zzaDJ = null;
  Set<zzbes> zzaDK = null;
  final zzbev zzaDL;
  private final zzad zzaDM = new zzbcq(this);
  private final zzac zzaDy;
  private zzbdp zzaDz = null;
  private final Looper zzrM;
  
  public zzbcp(Context paramContext, Lock paramLock, Looper paramLooper, zzq paramzzq, GoogleApiAvailability paramGoogleApiAvailability, Api.zza<? extends zzctk, zzctl> paramzza, Map<Api<?>, Boolean> paramMap, List<GoogleApiClient.ConnectionCallbacks> paramList, List<GoogleApiClient.OnConnectionFailedListener> paramList1, Map<Api.zzc<?>, Api.zze> paramMap1, int paramInt1, int paramInt2, ArrayList<zzbbi> paramArrayList, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.zzaCv = paramLock;
    this.zzaBh = false;
    this.zzaDy = new zzac(paramLooper, this.zzaDM);
    this.zzrM = paramLooper;
    this.zzaDD = new zzbcu(this, paramLooper);
    this.zzaBd = paramGoogleApiAvailability;
    this.zzaBb = paramInt1;
    if (this.zzaBb >= 0) {
      this.zzaDJ = Integer.valueOf(paramInt2);
    }
    this.zzaCD = paramMap;
    this.zzaDF = paramMap1;
    this.zzaDI = paramArrayList;
    this.zzaDL = new zzbev(this.zzaDF);
    paramContext = paramList.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.ConnectionCallbacks)paramContext.next();
      this.zzaDy.registerConnectionCallbacks(paramLock);
    }
    paramContext = paramList1.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.OnConnectionFailedListener)paramContext.next();
      this.zzaDy.registerConnectionFailedListener(paramLock);
    }
    this.zzaCA = paramzzq;
    this.zzaBe = paramzza;
  }
  
  private final void resume()
  {
    this.zzaCv.lock();
    try
    {
      if (this.zzaDA) {
        zzqc();
      }
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public static int zza(Iterable<Api.zze> paramIterable, boolean paramBoolean)
  {
    int k = 1;
    paramIterable = paramIterable.iterator();
    int i = 0;
    int j = 0;
    if (paramIterable.hasNext())
    {
      Api.zze localzze = (Api.zze)paramIterable.next();
      if (localzze.zzmv()) {
        j = 1;
      }
      if (!localzze.zzmG()) {
        break label85;
      }
      i = 1;
    }
    label85:
    for (;;)
    {
      break;
      if (j != 0)
      {
        j = k;
        if (i != 0)
        {
          j = k;
          if (paramBoolean) {
            j = 2;
          }
        }
        return j;
      }
      return 3;
    }
  }
  
  private final void zza(GoogleApiClient paramGoogleApiClient, zzben paramzzben, boolean paramBoolean)
  {
    zzbfo.zzaIy.zzd(paramGoogleApiClient).setResultCallback(new zzbct(this, paramzzben, paramBoolean, paramGoogleApiClient));
  }
  
  private final void zzap(int paramInt)
  {
    if (this.zzaDJ == null) {
      this.zzaDJ = Integer.valueOf(paramInt);
    }
    Object localObject2;
    while (this.zzaDz != null)
    {
      return;
      if (this.zzaDJ.intValue() != paramInt)
      {
        localObject1 = String.valueOf(zzaq(paramInt));
        localObject2 = String.valueOf(zzaq(this.zzaDJ.intValue()));
        throw new IllegalStateException(String.valueOf(localObject1).length() + 51 + String.valueOf(localObject2).length() + "Cannot use sign-in mode: " + (String)localObject1 + ". Mode was already set to " + (String)localObject2);
      }
    }
    Object localObject1 = this.zzaDF.values().iterator();
    paramInt = 0;
    int i = 0;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Api.zze)((Iterator)localObject1).next();
      if (((Api.zze)localObject2).zzmv()) {
        i = 1;
      }
      if (!((Api.zze)localObject2).zzmG()) {
        break label463;
      }
      paramInt = 1;
    }
    label463:
    for (;;)
    {
      break;
      switch (this.zzaDJ.intValue())
      {
      }
      while ((this.zzaBh) && (paramInt == 0))
      {
        this.zzaDz = new zzbbp(this.mContext, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI, this, false);
        return;
        if (i == 0) {
          throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
        }
        if (paramInt != 0)
        {
          throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
          if (i != 0)
          {
            if (this.zzaBh)
            {
              this.zzaDz = new zzbbp(this.mContext, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI, this, true);
              return;
            }
            this.zzaDz = zzbbk.zza(this.mContext, this, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI);
            return;
          }
        }
      }
      this.zzaDz = new zzbcx(this.mContext, this, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI, this);
      return;
    }
  }
  
  private static String zzaq(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 3: 
      return "SIGN_IN_MODE_NONE";
    case 1: 
      return "SIGN_IN_MODE_REQUIRED";
    }
    return "SIGN_IN_MODE_OPTIONAL";
  }
  
  private final void zzqc()
  {
    this.zzaDy.zzrA();
    this.zzaDz.connect();
  }
  
  private final void zzqd()
  {
    this.zzaCv.lock();
    try
    {
      if (zzqe()) {
        zzqc();
      }
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final ConnectionResult blockingConnect()
  {
    boolean bool2 = true;
    boolean bool1;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool1 = true;
    }
    for (;;)
    {
      zzbo.zza(bool1, "blockingConnect must not be called on the UI thread");
      this.zzaCv.lock();
      try
      {
        if (this.zzaBb >= 0) {
          if (this.zzaDJ != null)
          {
            bool1 = bool2;
            label45:
            zzbo.zza(bool1, "Sign-in mode should have been set explicitly by auto-manage.");
          }
        }
        do
        {
          for (;;)
          {
            zzap(this.zzaDJ.intValue());
            this.zzaDy.zzrA();
            ConnectionResult localConnectionResult = this.zzaDz.blockingConnect();
            return localConnectionResult;
            bool1 = false;
            break;
            bool1 = false;
            break label45;
            if (this.zzaDJ != null) {
              break label143;
            }
            this.zzaDJ = Integer.valueOf(zza(this.zzaDF.values(), false));
          }
        } while (this.zzaDJ.intValue() != 2);
      }
      finally
      {
        this.zzaCv.unlock();
      }
    }
    label143:
    throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
  }
  
  public final ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    boolean bool = false;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool = true;
    }
    zzbo.zza(bool, "blockingConnect must not be called on the UI thread");
    zzbo.zzb(paramTimeUnit, "TimeUnit must not be null");
    this.zzaCv.lock();
    try
    {
      if (this.zzaDJ == null) {
        this.zzaDJ = Integer.valueOf(zza(this.zzaDF.values(), false));
      }
      while (this.zzaDJ.intValue() != 2)
      {
        zzap(this.zzaDJ.intValue());
        this.zzaDy.zzrA();
        paramTimeUnit = this.zzaDz.blockingConnect(paramLong, paramTimeUnit);
        return paramTimeUnit;
      }
      throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final PendingResult<Status> clearDefaultAccountAndReconnect()
  {
    zzbo.zza(isConnected(), "GoogleApiClient is not connected yet.");
    if (this.zzaDJ.intValue() != 2) {}
    zzben localzzben;
    for (boolean bool = true;; bool = false)
    {
      zzbo.zza(bool, "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
      localzzben = new zzben(this);
      if (!this.zzaDF.containsKey(zzbfo.zzajR)) {
        break;
      }
      zza(this, localzzben, false);
      return localzzben;
    }
    AtomicReference localAtomicReference = new AtomicReference();
    Object localObject = new zzbcr(this, localAtomicReference, localzzben);
    zzbcs localzzbcs = new zzbcs(this, localzzben);
    localObject = new GoogleApiClient.Builder(this.mContext).addApi(zzbfo.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)localObject).addOnConnectionFailedListener(localzzbcs).setHandler(this.zzaDD).build();
    localAtomicReference.set(localObject);
    ((GoogleApiClient)localObject).connect();
    return localzzben;
  }
  
  public final void connect()
  {
    boolean bool = false;
    this.zzaCv.lock();
    try
    {
      if (this.zzaBb >= 0)
      {
        if (this.zzaDJ != null) {
          bool = true;
        }
        zzbo.zza(bool, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      do
      {
        for (;;)
        {
          connect(this.zzaDJ.intValue());
          return;
          if (this.zzaDJ != null) {
            break;
          }
          this.zzaDJ = Integer.valueOf(zza(this.zzaDF.values(), false));
        }
      } while (this.zzaDJ.intValue() != 2);
    }
    finally
    {
      this.zzaCv.unlock();
    }
    throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
  }
  
  /* Error */
  public final void connect(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 104	com/google/android/gms/internal/zzbcp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 186 1 0
    //   11: iload_3
    //   12: istore_2
    //   13: iload_1
    //   14: iconst_3
    //   15: if_icmpeq +17 -> 32
    //   18: iload_3
    //   19: istore_2
    //   20: iload_1
    //   21: iconst_1
    //   22: if_icmpeq +10 -> 32
    //   25: iload_1
    //   26: iconst_2
    //   27: if_icmpne +50 -> 77
    //   30: iload_3
    //   31: istore_2
    //   32: iload_2
    //   33: new 256	java/lang/StringBuilder
    //   36: dup
    //   37: bipush 33
    //   39: invokespecial 261	java/lang/StringBuilder:<init>	(I)V
    //   42: ldc_w 443
    //   45: invokevirtual 267	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: iload_1
    //   49: invokevirtual 446	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   52: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 448	com/google/android/gms/common/internal/zzbo:zzb	(ZLjava/lang/Object;)V
    //   58: aload_0
    //   59: iload_1
    //   60: invokespecial 351	com/google/android/gms/internal/zzbcp:zzap	(I)V
    //   63: aload_0
    //   64: invokespecial 191	com/google/android/gms/internal/zzbcp:zzqc	()V
    //   67: aload_0
    //   68: getfield 104	com/google/android/gms/internal/zzbcp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   71: invokeinterface 194 1 0
    //   76: return
    //   77: iconst_0
    //   78: istore_2
    //   79: goto -47 -> 32
    //   82: astore 4
    //   84: aload_0
    //   85: getfield 104	com/google/android/gms/internal/zzbcp:zzaCv	Ljava/util/concurrent/locks/Lock;
    //   88: invokeinterface 194 1 0
    //   93: aload 4
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	zzbcp
    //   0	96	1	paramInt	int
    //   12	67	2	bool1	boolean
    //   1	30	3	bool2	boolean
    //   82	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	67	82	finally
  }
  
  public final void disconnect()
  {
    this.zzaCv.lock();
    try
    {
      this.zzaDL.release();
      if (this.zzaDz != null) {
        this.zzaDz.disconnect();
      }
      this.zzaDH.release();
      Iterator localIterator = this.zzaCJ.iterator();
      while (localIterator.hasNext())
      {
        zzbay localzzbay = (zzbay)localIterator.next();
        localzzbay.zza(null);
        localzzbay.cancel();
      }
      this.zzaCJ.clear();
    }
    finally
    {
      this.zzaCv.unlock();
    }
    zzbdp localzzbdp = this.zzaDz;
    if (localzzbdp == null)
    {
      this.zzaCv.unlock();
      return;
    }
    zzqe();
    this.zzaDy.zzrz();
    this.zzaCv.unlock();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("mContext=").println(this.mContext);
    paramPrintWriter.append(paramString).append("mResuming=").print(this.zzaDA);
    paramPrintWriter.append(" mWorkQueue.size()=").print(this.zzaCJ.size());
    zzbev localzzbev = this.zzaDL;
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(localzzbev.zzaFl.size());
    if (this.zzaDz != null) {
      this.zzaDz.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @NonNull
  public final ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    this.zzaCv.lock();
    try
    {
      if ((!isConnected()) && (!this.zzaDA)) {
        throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
      }
    }
    finally
    {
      this.zzaCv.unlock();
    }
    if (this.zzaDF.containsKey(paramApi.zzpd()))
    {
      ConnectionResult localConnectionResult = this.zzaDz.getConnectionResult(paramApi);
      if (localConnectionResult == null)
      {
        if (this.zzaDA)
        {
          paramApi = ConnectionResult.zzazX;
          this.zzaCv.unlock();
          return paramApi;
        }
        Log.w("GoogleApiClientImpl", zzqg());
        Log.wtf("GoogleApiClientImpl", String.valueOf(paramApi.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
        paramApi = new ConnectionResult(8, null);
        this.zzaCv.unlock();
        return paramApi;
      }
      this.zzaCv.unlock();
      return localConnectionResult;
    }
    throw new IllegalArgumentException(String.valueOf(paramApi.getName()).concat(" was never registered with GoogleApiClient"));
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public final Looper getLooper()
  {
    return this.zzrM;
  }
  
  public final boolean hasConnectedApi(@NonNull Api<?> paramApi)
  {
    if (!isConnected()) {
      return false;
    }
    paramApi = (Api.zze)this.zzaDF.get(paramApi.zzpd());
    return (paramApi != null) && (paramApi.isConnected());
  }
  
  public final boolean isConnected()
  {
    return (this.zzaDz != null) && (this.zzaDz.isConnected());
  }
  
  public final boolean isConnecting()
  {
    return (this.zzaDz != null) && (this.zzaDz.isConnecting());
  }
  
  public final boolean isConnectionCallbacksRegistered(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    return this.zzaDy.isConnectionCallbacksRegistered(paramConnectionCallbacks);
  }
  
  public final boolean isConnectionFailedListenerRegistered(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    return this.zzaDy.isConnectionFailedListenerRegistered(paramOnConnectionFailedListener);
  }
  
  public final void reconnect()
  {
    disconnect();
    connect();
  }
  
  public final void registerConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.zzaDy.registerConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public final void registerConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzaDy.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public final void stopAutoManage(@NonNull FragmentActivity paramFragmentActivity)
  {
    paramFragmentActivity = new zzbdr(paramFragmentActivity);
    if (this.zzaBb >= 0)
    {
      zzbau.zza(paramFragmentActivity).zzal(this.zzaBb);
      return;
    }
    throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
  }
  
  public final void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.zzaDy.unregisterConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public final void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzaDy.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  @NonNull
  public final <C extends Api.zze> C zza(@NonNull Api.zzc<C> paramzzc)
  {
    paramzzc = (Api.zze)this.zzaDF.get(paramzzc);
    zzbo.zzb(paramzzc, "Appropriate Api was not requested.");
    return paramzzc;
  }
  
  public final void zza(zzbes paramzzbes)
  {
    this.zzaCv.lock();
    try
    {
      if (this.zzaDK == null) {
        this.zzaDK = new HashSet();
      }
      this.zzaDK.add(paramzzbes);
      return;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final boolean zza(@NonNull Api<?> paramApi)
  {
    return this.zzaDF.containsKey(paramApi.zzpd());
  }
  
  public final boolean zza(zzbei paramzzbei)
  {
    return (this.zzaDz != null) && (this.zzaDz.zza(paramzzbei));
  }
  
  public final void zzb(zzbes paramzzbes)
  {
    this.zzaCv.lock();
    for (;;)
    {
      try
      {
        if (this.zzaDK == null)
        {
          Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
          return;
        }
        if (!this.zzaDK.remove(paramzzbes))
        {
          Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
          continue;
        }
        if (zzqf()) {
          continue;
        }
      }
      finally
      {
        this.zzaCv.unlock();
      }
      this.zzaDz.zzpE();
    }
  }
  
  public final void zzc(ConnectionResult paramConnectionResult)
  {
    if (!zze.zze(this.mContext, paramConnectionResult.getErrorCode())) {
      zzqe();
    }
    if (!this.zzaDA)
    {
      this.zzaDy.zzk(paramConnectionResult);
      this.zzaDy.zzrz();
    }
  }
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzpd() != null) {
      bool = true;
    }
    for (;;)
    {
      zzbo.zzb(bool, "This task can not be enqueued (it's probably a Batch or malformed)");
      bool = this.zzaDF.containsKey(paramT.zzpd());
      String str;
      if (paramT.zzpg() != null)
      {
        str = paramT.zzpg().getName();
        label45:
        zzbo.zzb(bool, String.valueOf(str).length() + 65 + "GoogleApiClient is not configured to use " + str + " required for this call.");
        this.zzaCv.lock();
      }
      try
      {
        if (this.zzaDz == null)
        {
          this.zzaCJ.add(paramT);
          return paramT;
          bool = false;
          continue;
          str = "the API";
          break label45;
        }
        paramT = this.zzaDz.zzd(paramT);
        return paramT;
      }
      finally
      {
        this.zzaCv.unlock();
      }
    }
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzpd() != null)
    {
      bool = true;
      zzbo.zzb(bool, "This task can not be executed (it's probably a Batch or malformed)");
      bool = this.zzaDF.containsKey(paramT.zzpd());
      if (paramT.zzpg() == null) {
        break label129;
      }
    }
    label129:
    for (Object localObject = paramT.zzpg().getName();; localObject = "the API")
    {
      zzbo.zzb(bool, String.valueOf(localObject).length() + 65 + "GoogleApiClient is not configured to use " + (String)localObject + " required for this call.");
      this.zzaCv.lock();
      try
      {
        if (this.zzaDz != null) {
          break label136;
        }
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
      }
      finally
      {
        this.zzaCv.unlock();
      }
      bool = false;
      break;
    }
    label136:
    if (this.zzaDA)
    {
      this.zzaCJ.add(paramT);
      while (!this.zzaCJ.isEmpty())
      {
        localObject = (zzbay)this.zzaCJ.remove();
        this.zzaDL.zzb((zzbbe)localObject);
        ((zzbay)localObject).zzr(Status.zzaBo);
      }
      this.zzaCv.unlock();
      return paramT;
    }
    paramT = this.zzaDz.zze(paramT);
    this.zzaCv.unlock();
    return paramT;
  }
  
  public final void zze(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (!paramBoolean) && (!this.zzaDA))
    {
      this.zzaDA = true;
      if (this.zzaDE == null) {
        this.zzaDE = GoogleApiAvailability.zza(this.mContext.getApplicationContext(), new zzbcv(this));
      }
      this.zzaDD.sendMessageDelayed(this.zzaDD.obtainMessage(1), this.zzaDB);
      this.zzaDD.sendMessageDelayed(this.zzaDD.obtainMessage(2), this.zzaDC);
    }
    this.zzaDL.zzqM();
    this.zzaDy.zzaA(paramInt);
    this.zzaDy.zzrz();
    if (paramInt == 2) {
      zzqc();
    }
  }
  
  public final void zzm(Bundle paramBundle)
  {
    while (!this.zzaCJ.isEmpty()) {
      zze((zzbay)this.zzaCJ.remove());
    }
    this.zzaDy.zzn(paramBundle);
  }
  
  public final <L> zzbdw<L> zzp(@NonNull L paramL)
  {
    this.zzaCv.lock();
    try
    {
      paramL = this.zzaDH.zza(paramL, this.zzrM, "NO_TYPE");
      return paramL;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  public final void zzpl()
  {
    if (this.zzaDz != null) {
      this.zzaDz.zzpl();
    }
  }
  
  final boolean zzqe()
  {
    if (!this.zzaDA) {
      return false;
    }
    this.zzaDA = false;
    this.zzaDD.removeMessages(2);
    this.zzaDD.removeMessages(1);
    if (this.zzaDE != null)
    {
      this.zzaDE.unregister();
      this.zzaDE = null;
    }
    return true;
  }
  
  final boolean zzqf()
  {
    boolean bool1 = false;
    this.zzaCv.lock();
    try
    {
      Set localSet = this.zzaDK;
      if (localSet == null) {
        return false;
      }
      boolean bool2 = this.zzaDK.isEmpty();
      if (!bool2) {
        bool1 = true;
      }
      return bool1;
    }
    finally
    {
      this.zzaCv.unlock();
    }
  }
  
  final String zzqg()
  {
    StringWriter localStringWriter = new StringWriter();
    dump("", null, new PrintWriter(localStringWriter), null);
    return localStringWriter.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */