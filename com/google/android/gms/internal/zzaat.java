package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.auth.api.signin.internal.zzn;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzm;
import com.google.android.gms.common.internal.zzm.zza;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
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

public final class zzaat
  extends GoogleApiClient
  implements zzabc.zza
{
  private final Context mContext;
  private final Lock zzaAG;
  final zzg zzaAL;
  final Map<Api<?>, Boolean> zzaAO;
  final Queue<zzaad.zza<?, ?>> zzaAU = new LinkedList();
  private final zzm zzaBJ;
  private zzabc zzaBK = null;
  private volatile boolean zzaBL;
  private long zzaBM = 120000L;
  private long zzaBN = 5000L;
  private final zza zzaBO;
  zzaaz zzaBP;
  final Map<Api.zzc<?>, Api.zze> zzaBQ;
  Set<Scope> zzaBR = new HashSet();
  private final zzabi zzaBS = new zzabi();
  private final ArrayList<zzaag> zzaBT;
  private Integer zzaBU = null;
  Set<zzabx> zzaBV = null;
  final zzaby zzaBW;
  private final zzm.zza zzaBX = new zzm.zza()
  {
    public boolean isConnected()
    {
      return zzaat.this.isConnected();
    }
    
    public Bundle zzuB()
    {
      return null;
    }
  };
  private final int zzazl;
  private final GoogleApiAvailability zzazn;
  final Api.zza<? extends zzbai, zzbaj> zzazo;
  private boolean zzazr;
  private final Looper zzrs;
  
  public zzaat(Context paramContext, Lock paramLock, Looper paramLooper, zzg paramzzg, GoogleApiAvailability paramGoogleApiAvailability, Api.zza<? extends zzbai, zzbaj> paramzza, Map<Api<?>, Boolean> paramMap, List<GoogleApiClient.ConnectionCallbacks> paramList, List<GoogleApiClient.OnConnectionFailedListener> paramList1, Map<Api.zzc<?>, Api.zze> paramMap1, int paramInt1, int paramInt2, ArrayList<zzaag> paramArrayList, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.zzaAG = paramLock;
    this.zzazr = paramBoolean;
    this.zzaBJ = new zzm(paramLooper, this.zzaBX);
    this.zzrs = paramLooper;
    this.zzaBO = new zza(paramLooper);
    this.zzazn = paramGoogleApiAvailability;
    this.zzazl = paramInt1;
    if (this.zzazl >= 0) {
      this.zzaBU = Integer.valueOf(paramInt2);
    }
    this.zzaAO = paramMap;
    this.zzaBQ = paramMap1;
    this.zzaBT = paramArrayList;
    this.zzaBW = new zzaby(this.zzaBQ);
    paramContext = paramList.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.ConnectionCallbacks)paramContext.next();
      this.zzaBJ.registerConnectionCallbacks(paramLock);
    }
    paramContext = paramList1.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.OnConnectionFailedListener)paramContext.next();
      this.zzaBJ.registerConnectionFailedListener(paramLock);
    }
    this.zzaAL = paramzzg;
    this.zzazo = paramzza;
  }
  
  private void resume()
  {
    this.zzaAG.lock();
    try
    {
      if (isResuming()) {
        zzwm();
      }
      return;
    }
    finally
    {
      this.zzaAG.unlock();
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
      if (localzze.zzrd()) {
        j = 1;
      }
      if (!localzze.zzrr()) {
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
  
  private void zza(final GoogleApiClient paramGoogleApiClient, final zzabt paramzzabt, final boolean paramBoolean)
  {
    zzacf.zzaGM.zzg(paramGoogleApiClient).setResultCallback(new ResultCallback()
    {
      public void zzp(@NonNull Status paramAnonymousStatus)
      {
        zzn.zzas(zzaat.zzc(zzaat.this)).zzrD();
        if ((paramAnonymousStatus.isSuccess()) && (zzaat.this.isConnected())) {
          zzaat.this.reconnect();
        }
        paramzzabt.zzb(paramAnonymousStatus);
        if (paramBoolean) {
          paramGoogleApiClient.disconnect();
        }
      }
    });
  }
  
  private void zzb(@NonNull zzabd paramzzabd)
  {
    if (this.zzazl >= 0)
    {
      zzaaa.zza(paramzzabd).zzcA(this.zzazl);
      return;
    }
    throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
  }
  
  private void zzcD(int paramInt)
  {
    if (this.zzaBU == null) {
      this.zzaBU = Integer.valueOf(paramInt);
    }
    Object localObject2;
    while (this.zzaBK != null)
    {
      return;
      if (this.zzaBU.intValue() != paramInt)
      {
        localObject1 = String.valueOf(zzcE(paramInt));
        localObject2 = String.valueOf(zzcE(this.zzaBU.intValue()));
        throw new IllegalStateException(String.valueOf(localObject1).length() + 51 + String.valueOf(localObject2).length() + "Cannot use sign-in mode: " + (String)localObject1 + ". Mode was already set to " + (String)localObject2);
      }
    }
    Object localObject1 = this.zzaBQ.values().iterator();
    paramInt = 0;
    int i = 0;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Api.zze)((Iterator)localObject1).next();
      if (((Api.zze)localObject2).zzrd()) {
        i = 1;
      }
      if (!((Api.zze)localObject2).zzrr()) {
        break label463;
      }
      paramInt = 1;
    }
    label463:
    for (;;)
    {
      break;
      switch (this.zzaBU.intValue())
      {
      }
      while ((this.zzazr) && (paramInt == 0))
      {
        this.zzaBK = new zzaak(this.mContext, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this, false);
        return;
        if (i == 0) {
          throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
        }
        if (paramInt != 0)
        {
          throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
          if (i != 0)
          {
            if (this.zzazr)
            {
              this.zzaBK = new zzaak(this.mContext, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this, true);
              return;
            }
            this.zzaBK = zzaai.zza(this.mContext, this, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT);
            return;
          }
        }
      }
      this.zzaBK = new zzaav(this.mContext, this, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this);
      return;
    }
  }
  
  static String zzcE(int paramInt)
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
  
  private void zzwm()
  {
    this.zzaBJ.zzxY();
    this.zzaBK.connect();
  }
  
  private void zzwn()
  {
    this.zzaAG.lock();
    try
    {
      if (zzwp()) {
        zzwm();
      }
      return;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public ConnectionResult blockingConnect()
  {
    boolean bool2 = true;
    boolean bool1;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool1 = true;
    }
    for (;;)
    {
      zzac.zza(bool1, "blockingConnect must not be called on the UI thread");
      this.zzaAG.lock();
      try
      {
        if (this.zzazl >= 0) {
          if (this.zzaBU != null)
          {
            bool1 = bool2;
            label45:
            zzac.zza(bool1, "Sign-in mode should have been set explicitly by auto-manage.");
          }
        }
        do
        {
          for (;;)
          {
            zzcD(this.zzaBU.intValue());
            this.zzaBJ.zzxY();
            ConnectionResult localConnectionResult = this.zzaBK.blockingConnect();
            return localConnectionResult;
            bool1 = false;
            break;
            bool1 = false;
            break label45;
            if (this.zzaBU != null) {
              break label143;
            }
            this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
          }
        } while (this.zzaBU.intValue() != 2);
      }
      finally
      {
        this.zzaAG.unlock();
      }
    }
    label143:
    throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
  }
  
  public ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    boolean bool = false;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool = true;
    }
    zzac.zza(bool, "blockingConnect must not be called on the UI thread");
    zzac.zzb(paramTimeUnit, "TimeUnit must not be null");
    this.zzaAG.lock();
    try
    {
      if (this.zzaBU == null) {
        this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
      }
      while (this.zzaBU.intValue() != 2)
      {
        zzcD(this.zzaBU.intValue());
        this.zzaBJ.zzxY();
        paramTimeUnit = this.zzaBK.blockingConnect(paramLong, paramTimeUnit);
        return paramTimeUnit;
      }
      throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public PendingResult<Status> clearDefaultAccountAndReconnect()
  {
    zzac.zza(isConnected(), "GoogleApiClient is not connected yet.");
    if (this.zzaBU.intValue() != 2) {}
    final zzabt localzzabt;
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
      localzzabt = new zzabt(this);
      if (!this.zzaBQ.containsKey(zzacf.zzaid)) {
        break;
      }
      zza(this, localzzabt, false);
      return localzzabt;
    }
    final AtomicReference localAtomicReference = new AtomicReference();
    Object localObject = new GoogleApiClient.ConnectionCallbacks()
    {
      public void onConnected(Bundle paramAnonymousBundle)
      {
        zzaat.zza(zzaat.this, (GoogleApiClient)localAtomicReference.get(), localzzabt, true);
      }
      
      public void onConnectionSuspended(int paramAnonymousInt) {}
    };
    GoogleApiClient.OnConnectionFailedListener local3 = new GoogleApiClient.OnConnectionFailedListener()
    {
      public void onConnectionFailed(@NonNull ConnectionResult paramAnonymousConnectionResult)
      {
        localzzabt.zzb(new Status(8));
      }
    };
    localObject = new GoogleApiClient.Builder(this.mContext).addApi(zzacf.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)localObject).addOnConnectionFailedListener(local3).setHandler(this.zzaBO).build();
    localAtomicReference.set(localObject);
    ((GoogleApiClient)localObject).connect();
    return localzzabt;
  }
  
  public void connect()
  {
    boolean bool = false;
    this.zzaAG.lock();
    try
    {
      if (this.zzazl >= 0)
      {
        if (this.zzaBU != null) {
          bool = true;
        }
        zzac.zza(bool, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      do
      {
        for (;;)
        {
          connect(this.zzaBU.intValue());
          return;
          if (this.zzaBU != null) {
            break;
          }
          this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
        }
      } while (this.zzaBU.intValue() != 2);
    }
    finally
    {
      this.zzaAG.unlock();
    }
    throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
  }
  
  /* Error */
  public void connect(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 116	com/google/android/gms/internal/zzaat:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 196 1 0
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
    //   33: new 285	java/lang/StringBuilder
    //   36: dup
    //   37: bipush 33
    //   39: invokespecial 290	java/lang/StringBuilder:<init>	(I)V
    //   42: ldc_w 457
    //   45: invokevirtual 296	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: iload_1
    //   49: invokevirtual 460	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   52: invokevirtual 302	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 462	com/google/android/gms/common/internal/zzac:zzb	(ZLjava/lang/Object;)V
    //   58: aload_0
    //   59: iload_1
    //   60: invokespecial 371	com/google/android/gms/internal/zzaat:zzcD	(I)V
    //   63: aload_0
    //   64: invokespecial 202	com/google/android/gms/internal/zzaat:zzwm	()V
    //   67: aload_0
    //   68: getfield 116	com/google/android/gms/internal/zzaat:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   71: invokeinterface 205 1 0
    //   76: return
    //   77: iconst_0
    //   78: istore_2
    //   79: goto -47 -> 32
    //   82: astore 4
    //   84: aload_0
    //   85: getfield 116	com/google/android/gms/internal/zzaat:zzaAG	Ljava/util/concurrent/locks/Lock;
    //   88: invokeinterface 205 1 0
    //   93: aload 4
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	zzaat
    //   0	96	1	paramInt	int
    //   12	67	2	bool1	boolean
    //   1	30	3	bool2	boolean
    //   82	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	67	82	finally
  }
  
  public void disconnect()
  {
    this.zzaAG.lock();
    try
    {
      this.zzaBW.release();
      if (this.zzaBK != null) {
        this.zzaBK.disconnect();
      }
      this.zzaBS.release();
      Iterator localIterator = this.zzaAU.iterator();
      while (localIterator.hasNext())
      {
        zzaad.zza localzza = (zzaad.zza)localIterator.next();
        localzza.zza(null);
        localzza.cancel();
      }
      this.zzaAU.clear();
    }
    finally
    {
      this.zzaAG.unlock();
    }
    zzabc localzzabc = this.zzaBK;
    if (localzzabc == null)
    {
      this.zzaAG.unlock();
      return;
    }
    zzwp();
    this.zzaBJ.zzxX();
    this.zzaAG.unlock();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("mContext=").println(this.mContext);
    paramPrintWriter.append(paramString).append("mResuming=").print(this.zzaBL);
    paramPrintWriter.append(" mWorkQueue.size()=").print(this.zzaAU.size());
    this.zzaBW.dump(paramPrintWriter);
    if (this.zzaBK != null) {
      this.zzaBK.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @NonNull
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    this.zzaAG.lock();
    try
    {
      if ((!isConnected()) && (!isResuming())) {
        throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
      }
    }
    finally
    {
      this.zzaAG.unlock();
    }
    if (this.zzaBQ.containsKey(paramApi.zzvg()))
    {
      ConnectionResult localConnectionResult = this.zzaBK.getConnectionResult(paramApi);
      if (localConnectionResult == null)
      {
        if (isResuming())
        {
          paramApi = ConnectionResult.zzayj;
          this.zzaAG.unlock();
          return paramApi;
        }
        Log.w("GoogleApiClientImpl", zzwr());
        Log.wtf("GoogleApiClientImpl", String.valueOf(paramApi.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
        paramApi = new ConnectionResult(8, null);
        this.zzaAG.unlock();
        return paramApi;
      }
      this.zzaAG.unlock();
      return localConnectionResult;
    }
    throw new IllegalArgumentException(String.valueOf(paramApi.getName()).concat(" was never registered with GoogleApiClient"));
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public Looper getLooper()
  {
    return this.zzrs;
  }
  
  public int getSessionId()
  {
    return System.identityHashCode(this);
  }
  
  public boolean hasConnectedApi(@NonNull Api<?> paramApi)
  {
    if (!isConnected()) {
      return false;
    }
    paramApi = (Api.zze)this.zzaBQ.get(paramApi.zzvg());
    if ((paramApi != null) && (paramApi.isConnected())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isConnected()
  {
    return (this.zzaBK != null) && (this.zzaBK.isConnected());
  }
  
  public boolean isConnecting()
  {
    return (this.zzaBK != null) && (this.zzaBK.isConnecting());
  }
  
  public boolean isConnectionCallbacksRegistered(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    return this.zzaBJ.isConnectionCallbacksRegistered(paramConnectionCallbacks);
  }
  
  public boolean isConnectionFailedListenerRegistered(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    return this.zzaBJ.isConnectionFailedListenerRegistered(paramOnConnectionFailedListener);
  }
  
  boolean isResuming()
  {
    return this.zzaBL;
  }
  
  public void reconnect()
  {
    disconnect();
    connect();
  }
  
  public void registerConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.zzaBJ.registerConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void registerConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzaBJ.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public void stopAutoManage(@NonNull FragmentActivity paramFragmentActivity)
  {
    zzb(new zzabd(paramFragmentActivity));
  }
  
  public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.zzaBJ.unregisterConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzaBJ.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  @NonNull
  public <C extends Api.zze> C zza(@NonNull Api.zzc<C> paramzzc)
  {
    paramzzc = (Api.zze)this.zzaBQ.get(paramzzc);
    zzac.zzb(paramzzc, "Appropriate Api was not requested.");
    return paramzzc;
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzvg() != null) {
      bool = true;
    }
    for (;;)
    {
      zzac.zzb(bool, "This task can not be enqueued (it's probably a Batch or malformed)");
      bool = this.zzaBQ.containsKey(paramT.zzvg());
      String str;
      if (paramT.getApi() != null)
      {
        str = paramT.getApi().getName();
        label45:
        zzac.zzb(bool, String.valueOf(str).length() + 65 + "GoogleApiClient is not configured to use " + str + " required for this call.");
        this.zzaAG.lock();
      }
      try
      {
        if (this.zzaBK == null)
        {
          this.zzaAU.add(paramT);
          return paramT;
          bool = false;
          continue;
          str = "the API";
          break label45;
        }
        paramT = this.zzaBK.zza(paramT);
        return paramT;
      }
      finally
      {
        this.zzaAG.unlock();
      }
    }
  }
  
  public void zza(zzabx paramzzabx)
  {
    this.zzaAG.lock();
    try
    {
      if (this.zzaBV == null) {
        this.zzaBV = new HashSet();
      }
      this.zzaBV.add(paramzzabx);
      return;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public boolean zza(@NonNull Api<?> paramApi)
  {
    return this.zzaBQ.containsKey(paramApi.zzvg());
  }
  
  public boolean zza(zzabq paramzzabq)
  {
    return (this.zzaBK != null) && (this.zzaBK.zza(paramzzabq));
  }
  
  public <A extends Api.zzb, T extends zzaad.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzvg() != null)
    {
      bool = true;
      zzac.zzb(bool, "This task can not be executed (it's probably a Batch or malformed)");
      bool = this.zzaBQ.containsKey(paramT.zzvg());
      if (paramT.getApi() == null) {
        break label129;
      }
    }
    label129:
    for (Object localObject = paramT.getApi().getName();; localObject = "the API")
    {
      zzac.zzb(bool, String.valueOf(localObject).length() + 65 + "GoogleApiClient is not configured to use " + (String)localObject + " required for this call.");
      this.zzaAG.lock();
      try
      {
        if (this.zzaBK != null) {
          break label136;
        }
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
      }
      finally
      {
        this.zzaAG.unlock();
      }
      bool = false;
      break;
    }
    label136:
    if (isResuming())
    {
      this.zzaAU.add(paramT);
      while (!this.zzaAU.isEmpty())
      {
        localObject = (zzaad.zza)this.zzaAU.remove();
        this.zzaBW.zzb((zzaaf)localObject);
        ((zzaad.zza)localObject).zzB(Status.zzazz);
      }
      this.zzaAG.unlock();
      return paramT;
    }
    paramT = this.zzaBK.zzb(paramT);
    this.zzaAG.unlock();
    return paramT;
  }
  
  public void zzb(zzabx paramzzabx)
  {
    this.zzaAG.lock();
    for (;;)
    {
      try
      {
        if (this.zzaBV == null)
        {
          Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
          return;
        }
        if (!this.zzaBV.remove(paramzzabx))
        {
          Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
          continue;
        }
        if (zzwq()) {
          continue;
        }
      }
      finally
      {
        this.zzaAG.unlock();
      }
      this.zzaBK.zzvM();
    }
  }
  
  <C extends Api.zze> C zzc(Api.zzc<?> paramzzc)
  {
    paramzzc = (Api.zze)this.zzaBQ.get(paramzzc);
    zzac.zzb(paramzzc, "Appropriate Api was not requested.");
    return paramzzc;
  }
  
  public void zzc(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (!paramBoolean)) {
      zzwo();
    }
    this.zzaBW.zzxd();
    this.zzaBJ.zzcV(paramInt);
    this.zzaBJ.zzxX();
    if (paramInt == 2) {
      zzwm();
    }
  }
  
  public void zzc(ConnectionResult paramConnectionResult)
  {
    if (!this.zzazn.zzd(this.mContext, paramConnectionResult.getErrorCode())) {
      zzwp();
    }
    if (!isResuming())
    {
      this.zzaBJ.zzn(paramConnectionResult);
      this.zzaBJ.zzxX();
    }
  }
  
  public void zzo(Bundle paramBundle)
  {
    while (!this.zzaAU.isEmpty()) {
      zzb((zzaad.zza)this.zzaAU.remove());
    }
    this.zzaBJ.zzq(paramBundle);
  }
  
  public <L> zzabh<L> zzr(@NonNull L paramL)
  {
    this.zzaAG.lock();
    try
    {
      paramL = this.zzaBS.zzb(paramL, this.zzrs);
      return paramL;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  public void zzvn()
  {
    if (this.zzaBK != null) {
      this.zzaBK.zzvn();
    }
  }
  
  void zzwo()
  {
    if (isResuming()) {
      return;
    }
    this.zzaBL = true;
    if (this.zzaBP == null) {
      this.zzaBP = this.zzazn.zza(this.mContext.getApplicationContext(), new zzb(this));
    }
    this.zzaBO.sendMessageDelayed(this.zzaBO.obtainMessage(1), this.zzaBM);
    this.zzaBO.sendMessageDelayed(this.zzaBO.obtainMessage(2), this.zzaBN);
  }
  
  boolean zzwp()
  {
    if (!isResuming()) {
      return false;
    }
    this.zzaBL = false;
    this.zzaBO.removeMessages(2);
    this.zzaBO.removeMessages(1);
    if (this.zzaBP != null)
    {
      this.zzaBP.unregister();
      this.zzaBP = null;
    }
    return true;
  }
  
  boolean zzwq()
  {
    boolean bool1 = false;
    this.zzaAG.lock();
    try
    {
      Set localSet = this.zzaBV;
      if (localSet == null) {
        return false;
      }
      boolean bool2 = this.zzaBV.isEmpty();
      if (!bool2) {
        bool1 = true;
      }
      return bool1;
    }
    finally
    {
      this.zzaAG.unlock();
    }
  }
  
  String zzwr()
  {
    StringWriter localStringWriter = new StringWriter();
    dump("", null, new PrintWriter(localStringWriter), null);
    return localStringWriter.toString();
  }
  
  final class zza
    extends Handler
  {
    zza(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        int i = paramMessage.what;
        Log.w("GoogleApiClientImpl", 31 + "Unknown message id: " + i);
        return;
      case 1: 
        zzaat.zzb(zzaat.this);
        return;
      }
      zzaat.zza(zzaat.this);
    }
  }
  
  static class zzb
    extends zzaaz.zza
  {
    private WeakReference<zzaat> zzaCc;
    
    zzb(zzaat paramzzaat)
    {
      this.zzaCc = new WeakReference(paramzzaat);
    }
    
    public void zzvE()
    {
      zzaat localzzaat = (zzaat)this.zzaCc.get();
      if (localzzaat == null) {
        return;
      }
      zzaat.zza(localzzaat);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */