package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzk.zza;
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

public final class zzrd
  extends GoogleApiClient
  implements zzrm.zza
{
  private final com.google.android.gms.common.internal.zzk Ab;
  private zzrm Ac = null;
  final Queue<zzqo.zza<?, ?>> Ad = new LinkedList();
  private volatile boolean Ae;
  private long Af = 120000L;
  private long Ag = 5000L;
  private final zza Ah;
  zzrj Ai;
  final Map<Api.zzc<?>, Api.zze> Aj;
  Set<Scope> Ak = new HashSet();
  private final zzrs Al = new zzrs();
  private final ArrayList<zzqr> Am;
  private Integer An = null;
  Set<zzsf> Ao = null;
  final zzsg Ap;
  private final zzk.zza Aq = new zzk.zza()
  {
    public boolean isConnected()
    {
      return zzrd.this.isConnected();
    }
    
    public Bundle zzapn()
    {
      return null;
    }
  };
  private final Context mContext;
  private final int xN;
  private final GoogleApiAvailability xP;
  final Api.zza<? extends zzxp, zzxq> xQ;
  private boolean xT;
  final zzf zP;
  private final Lock zg;
  final Map<Api<?>, Integer> zk;
  private final Looper zzajy;
  
  public zzrd(Context paramContext, Lock paramLock, Looper paramLooper, zzf paramzzf, GoogleApiAvailability paramGoogleApiAvailability, Api.zza<? extends zzxp, zzxq> paramzza, Map<Api<?>, Integer> paramMap, List<GoogleApiClient.ConnectionCallbacks> paramList, List<GoogleApiClient.OnConnectionFailedListener> paramList1, Map<Api.zzc<?>, Api.zze> paramMap1, int paramInt1, int paramInt2, ArrayList<zzqr> paramArrayList, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.zg = paramLock;
    this.xT = paramBoolean;
    this.Ab = new com.google.android.gms.common.internal.zzk(paramLooper, this.Aq);
    this.zzajy = paramLooper;
    this.Ah = new zza(paramLooper);
    this.xP = paramGoogleApiAvailability;
    this.xN = paramInt1;
    if (this.xN >= 0) {
      this.An = Integer.valueOf(paramInt2);
    }
    this.zk = paramMap;
    this.Aj = paramMap1;
    this.Am = paramArrayList;
    this.Ap = new zzsg(this.Aj);
    paramContext = paramList.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.ConnectionCallbacks)paramContext.next();
      this.Ab.registerConnectionCallbacks(paramLock);
    }
    paramContext = paramList1.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.OnConnectionFailedListener)paramContext.next();
      this.Ab.registerConnectionFailedListener(paramLock);
    }
    this.zP = paramzzf;
    this.xQ = paramzza;
  }
  
  private void resume()
  {
    this.zg.lock();
    try
    {
      if (isResuming()) {
        zzasw();
      }
      return;
    }
    finally
    {
      this.zg.unlock();
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
      if (localzze.zzain()) {
        j = 1;
      }
      if (!localzze.zzajc()) {
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
  
  private void zza(final GoogleApiClient paramGoogleApiClient, final zzsc paramzzsc, final boolean paramBoolean)
  {
    zzsn.EU.zzg(paramGoogleApiClient).setResultCallback(new ResultCallback()
    {
      public void zzp(@NonNull Status paramAnonymousStatus)
      {
        com.google.android.gms.auth.api.signin.internal.zzk.zzba(zzrd.zzc(zzrd.this)).zzajo();
        if ((paramAnonymousStatus.isSuccess()) && (zzrd.this.isConnected())) {
          zzrd.this.reconnect();
        }
        paramzzsc.zzc(paramAnonymousStatus);
        if (paramBoolean) {
          paramGoogleApiClient.disconnect();
        }
      }
    });
  }
  
  private void zzasw()
  {
    this.Ab.zzawd();
    this.Ac.connect();
  }
  
  private void zzasx()
  {
    this.zg.lock();
    try
    {
      if (zzasz()) {
        zzasw();
      }
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  private void zzb(@NonNull zzrn paramzzrn)
  {
    if (this.xN >= 0)
    {
      zzqm.zza(paramzzrn).zzfs(this.xN);
      return;
    }
    throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
  }
  
  private void zzfv(int paramInt)
  {
    if (this.An == null) {
      this.An = Integer.valueOf(paramInt);
    }
    Object localObject2;
    while (this.Ac != null)
    {
      return;
      if (this.An.intValue() != paramInt)
      {
        localObject1 = String.valueOf(zzfw(paramInt));
        localObject2 = String.valueOf(zzfw(this.An.intValue()));
        throw new IllegalStateException(String.valueOf(localObject1).length() + 51 + String.valueOf(localObject2).length() + "Cannot use sign-in mode: " + (String)localObject1 + ". Mode was already set to " + (String)localObject2);
      }
    }
    Object localObject1 = this.Aj.values().iterator();
    paramInt = 0;
    int i = 0;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Api.zze)((Iterator)localObject1).next();
      if (((Api.zze)localObject2).zzain()) {
        i = 1;
      }
      if (!((Api.zze)localObject2).zzajc()) {
        break label401;
      }
      paramInt = 1;
    }
    label401:
    for (;;)
    {
      break;
      switch (this.An.intValue())
      {
      }
      while ((this.xT) && (i == 0) && (paramInt == 0))
      {
        this.Ac = new zzqu(this.mContext, this.zg, this.zzajy, this.xP, this.Aj, this.zk, this.Am, this);
        return;
        if (i == 0) {
          throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
        }
        if (paramInt != 0)
        {
          throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
          if (i != 0)
          {
            this.Ac = zzqt.zza(this.mContext, this, this.zg, this.zzajy, this.xP, this.Aj, this.zP, this.zk, this.xQ, this.Am);
            return;
          }
        }
      }
      this.Ac = new zzrf(this.mContext, this, this.zg, this.zzajy, this.xP, this.Aj, this.zP, this.zk, this.xQ, this.Am, this);
      return;
    }
  }
  
  static String zzfw(int paramInt)
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
  
  public ConnectionResult blockingConnect()
  {
    boolean bool2 = true;
    boolean bool1;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool1 = true;
    }
    for (;;)
    {
      zzaa.zza(bool1, "blockingConnect must not be called on the UI thread");
      this.zg.lock();
      try
      {
        if (this.xN >= 0) {
          if (this.An != null)
          {
            bool1 = bool2;
            label45:
            zzaa.zza(bool1, "Sign-in mode should have been set explicitly by auto-manage.");
          }
        }
        do
        {
          for (;;)
          {
            zzfv(this.An.intValue());
            this.Ab.zzawd();
            ConnectionResult localConnectionResult = this.Ac.blockingConnect();
            return localConnectionResult;
            bool1 = false;
            break;
            bool1 = false;
            break label45;
            if (this.An != null) {
              break label143;
            }
            this.An = Integer.valueOf(zza(this.Aj.values(), false));
          }
        } while (this.An.intValue() != 2);
      }
      finally
      {
        this.zg.unlock();
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
    zzaa.zza(bool, "blockingConnect must not be called on the UI thread");
    zzaa.zzb(paramTimeUnit, "TimeUnit must not be null");
    this.zg.lock();
    try
    {
      if (this.An == null) {
        this.An = Integer.valueOf(zza(this.Aj.values(), false));
      }
      while (this.An.intValue() != 2)
      {
        zzfv(this.An.intValue());
        this.Ab.zzawd();
        paramTimeUnit = this.Ac.blockingConnect(paramLong, paramTimeUnit);
        return paramTimeUnit;
      }
      throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public PendingResult<Status> clearDefaultAccountAndReconnect()
  {
    zzaa.zza(isConnected(), "GoogleApiClient is not connected yet.");
    if (this.An.intValue() != 2) {}
    final zzsc localzzsc;
    for (boolean bool = true;; bool = false)
    {
      zzaa.zza(bool, "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
      localzzsc = new zzsc(this);
      if (!this.Aj.containsKey(zzsn.hg)) {
        break;
      }
      zza(this, localzzsc, false);
      return localzzsc;
    }
    final AtomicReference localAtomicReference = new AtomicReference();
    Object localObject = new GoogleApiClient.ConnectionCallbacks()
    {
      public void onConnected(Bundle paramAnonymousBundle)
      {
        zzrd.zza(zzrd.this, (GoogleApiClient)localAtomicReference.get(), localzzsc, true);
      }
      
      public void onConnectionSuspended(int paramAnonymousInt) {}
    };
    GoogleApiClient.OnConnectionFailedListener local3 = new GoogleApiClient.OnConnectionFailedListener()
    {
      public void onConnectionFailed(@NonNull ConnectionResult paramAnonymousConnectionResult)
      {
        localzzsc.zzc(new Status(8));
      }
    };
    localObject = new GoogleApiClient.Builder(this.mContext).addApi(zzsn.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)localObject).addOnConnectionFailedListener(local3).setHandler(this.Ah).build();
    localAtomicReference.set(localObject);
    ((GoogleApiClient)localObject).connect();
    return localzzsc;
  }
  
  public void connect()
  {
    boolean bool = false;
    this.zg.lock();
    try
    {
      if (this.xN >= 0)
      {
        if (this.An != null) {
          bool = true;
        }
        zzaa.zza(bool, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      do
      {
        for (;;)
        {
          connect(this.An.intValue());
          return;
          if (this.An != null) {
            break;
          }
          this.An = Integer.valueOf(zza(this.Aj.values(), false));
        }
      } while (this.An.intValue() != 2);
    }
    finally
    {
      this.zg.unlock();
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
    //   3: getfield 116	com/google/android/gms/internal/zzrd:zg	Ljava/util/concurrent/locks/Lock;
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
    //   33: new 296	java/lang/StringBuilder
    //   36: dup
    //   37: bipush 33
    //   39: invokespecial 301	java/lang/StringBuilder:<init>	(I)V
    //   42: ldc_w 457
    //   45: invokevirtual 307	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: iload_1
    //   49: invokevirtual 460	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   52: invokevirtual 313	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 462	com/google/android/gms/common/internal/zzaa:zzb	(ZLjava/lang/Object;)V
    //   58: aload_0
    //   59: iload_1
    //   60: invokespecial 371	com/google/android/gms/internal/zzrd:zzfv	(I)V
    //   63: aload_0
    //   64: invokespecial 202	com/google/android/gms/internal/zzrd:zzasw	()V
    //   67: aload_0
    //   68: getfield 116	com/google/android/gms/internal/zzrd:zg	Ljava/util/concurrent/locks/Lock;
    //   71: invokeinterface 205 1 0
    //   76: return
    //   77: iconst_0
    //   78: istore_2
    //   79: goto -47 -> 32
    //   82: astore 4
    //   84: aload_0
    //   85: getfield 116	com/google/android/gms/internal/zzrd:zg	Ljava/util/concurrent/locks/Lock;
    //   88: invokeinterface 205 1 0
    //   93: aload 4
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	zzrd
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
    this.zg.lock();
    try
    {
      this.Ap.release();
      if (this.Ac != null) {
        this.Ac.disconnect();
      }
      this.Al.release();
      Iterator localIterator = this.Ad.iterator();
      while (localIterator.hasNext())
      {
        zzqo.zza localzza = (zzqo.zza)localIterator.next();
        localzza.zza(null);
        localzza.cancel();
      }
      this.Ad.clear();
    }
    finally
    {
      this.zg.unlock();
    }
    zzrm localzzrm = this.Ac;
    if (localzzrm == null)
    {
      this.zg.unlock();
      return;
    }
    zzasz();
    this.Ab.zzawc();
    this.zg.unlock();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("mContext=").println(this.mContext);
    paramPrintWriter.append(paramString).append("mResuming=").print(this.Ae);
    paramPrintWriter.append(" mWorkQueue.size()=").print(this.Ad.size());
    this.Ap.dump(paramPrintWriter);
    if (this.Ac != null) {
      this.Ac.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  @NonNull
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    this.zg.lock();
    try
    {
      if ((!isConnected()) && (!isResuming())) {
        throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
      }
    }
    finally
    {
      this.zg.unlock();
    }
    if (this.Aj.containsKey(paramApi.zzaqv()))
    {
      ConnectionResult localConnectionResult = this.Ac.getConnectionResult(paramApi);
      if (localConnectionResult == null)
      {
        if (isResuming())
        {
          paramApi = ConnectionResult.wO;
          this.zg.unlock();
          return paramApi;
        }
        Log.w("GoogleApiClientImpl", zzatb());
        Log.wtf("GoogleApiClientImpl", String.valueOf(paramApi.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
        paramApi = new ConnectionResult(8, null);
        this.zg.unlock();
        return paramApi;
      }
      this.zg.unlock();
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
    return this.zzajy;
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
    paramApi = (Api.zze)this.Aj.get(paramApi.zzaqv());
    if ((paramApi != null) && (paramApi.isConnected())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isConnected()
  {
    return (this.Ac != null) && (this.Ac.isConnected());
  }
  
  public boolean isConnecting()
  {
    return (this.Ac != null) && (this.Ac.isConnecting());
  }
  
  public boolean isConnectionCallbacksRegistered(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    return this.Ab.isConnectionCallbacksRegistered(paramConnectionCallbacks);
  }
  
  public boolean isConnectionFailedListenerRegistered(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    return this.Ab.isConnectionFailedListenerRegistered(paramOnConnectionFailedListener);
  }
  
  boolean isResuming()
  {
    return this.Ae;
  }
  
  public void reconnect()
  {
    disconnect();
    connect();
  }
  
  public void registerConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.Ab.registerConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void registerConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.Ab.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public void stopAutoManage(@NonNull FragmentActivity paramFragmentActivity)
  {
    zzb(new zzrn(paramFragmentActivity));
  }
  
  public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    this.Ab.unregisterConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.Ab.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  @NonNull
  public <C extends Api.zze> C zza(@NonNull Api.zzc<C> paramzzc)
  {
    paramzzc = (Api.zze)this.Aj.get(paramzzc);
    zzaa.zzb(paramzzc, "Appropriate Api was not requested.");
    return paramzzc;
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzaqv() != null) {
      bool = true;
    }
    for (;;)
    {
      zzaa.zzb(bool, "This task can not be enqueued (it's probably a Batch or malformed)");
      bool = this.Aj.containsKey(paramT.zzaqv());
      String str;
      if (paramT.getApi() != null)
      {
        str = paramT.getApi().getName();
        label45:
        zzaa.zzb(bool, String.valueOf(str).length() + 65 + "GoogleApiClient is not configured to use " + str + " required for this call.");
        this.zg.lock();
      }
      try
      {
        if (this.Ac == null)
        {
          this.Ad.add(paramT);
          return paramT;
          bool = false;
          continue;
          str = "the API";
          break label45;
        }
        paramT = this.Ac.zza(paramT);
        return paramT;
      }
      finally
      {
        this.zg.unlock();
      }
    }
  }
  
  public void zza(zzsf paramzzsf)
  {
    this.zg.lock();
    try
    {
      if (this.Ao == null) {
        this.Ao = new HashSet();
      }
      this.Ao.add(paramzzsf);
      return;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  public boolean zza(@NonNull Api<?> paramApi)
  {
    return this.Aj.containsKey(paramApi.zzaqv());
  }
  
  public boolean zza(zzsa paramzzsa)
  {
    return (this.Ac != null) && (this.Ac.zza(paramzzsa));
  }
  
  public void zzard()
  {
    if (this.Ac != null) {
      this.Ac.zzard();
    }
  }
  
  void zzasy()
  {
    if (isResuming()) {
      return;
    }
    this.Ae = true;
    if (this.Ai == null) {
      this.Ai = this.xP.zza(this.mContext.getApplicationContext(), new zzb(this));
    }
    this.Ah.sendMessageDelayed(this.Ah.obtainMessage(1), this.Af);
    this.Ah.sendMessageDelayed(this.Ah.obtainMessage(2), this.Ag);
  }
  
  boolean zzasz()
  {
    if (!isResuming()) {
      return false;
    }
    this.Ae = false;
    this.Ah.removeMessages(2);
    this.Ah.removeMessages(1);
    if (this.Ai != null)
    {
      this.Ai.unregister();
      this.Ai = null;
    }
    return true;
  }
  
  boolean zzata()
  {
    boolean bool1 = false;
    this.zg.lock();
    try
    {
      Set localSet = this.Ao;
      if (localSet == null) {
        return false;
      }
      boolean bool2 = this.Ao.isEmpty();
      if (!bool2) {
        bool1 = true;
      }
      return bool1;
    }
    finally
    {
      this.zg.unlock();
    }
  }
  
  String zzatb()
  {
    StringWriter localStringWriter = new StringWriter();
    dump("", null, new PrintWriter(localStringWriter), null);
    return localStringWriter.toString();
  }
  
  <C extends Api.zze> C zzb(Api.zzc<?> paramzzc)
  {
    paramzzc = (Api.zze)this.Aj.get(paramzzc);
    zzaa.zzb(paramzzc, "Appropriate Api was not requested.");
    return paramzzc;
  }
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    boolean bool;
    if (paramT.zzaqv() != null)
    {
      bool = true;
      zzaa.zzb(bool, "This task can not be executed (it's probably a Batch or malformed)");
      bool = this.Aj.containsKey(paramT.zzaqv());
      if (paramT.getApi() == null) {
        break label129;
      }
    }
    label129:
    for (Object localObject = paramT.getApi().getName();; localObject = "the API")
    {
      zzaa.zzb(bool, String.valueOf(localObject).length() + 65 + "GoogleApiClient is not configured to use " + (String)localObject + " required for this call.");
      this.zg.lock();
      try
      {
        if (this.Ac != null) {
          break label136;
        }
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
      }
      finally
      {
        this.zg.unlock();
      }
      bool = false;
      break;
    }
    label136:
    if (isResuming())
    {
      this.Ad.add(paramT);
      while (!this.Ad.isEmpty())
      {
        localObject = (zzqo.zza)this.Ad.remove();
        this.Ap.zzb((zzqq)localObject);
        ((zzqo.zza)localObject).zzaa(Status.yb);
      }
      this.zg.unlock();
      return paramT;
    }
    paramT = this.Ac.zzb(paramT);
    this.zg.unlock();
    return paramT;
  }
  
  public void zzb(zzsf paramzzsf)
  {
    this.zg.lock();
    for (;;)
    {
      try
      {
        if (this.Ao == null)
        {
          Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
          return;
        }
        if (!this.Ao.remove(paramzzsf))
        {
          Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
          continue;
        }
        if (zzata()) {
          continue;
        }
      }
      finally
      {
        this.zg.unlock();
      }
      this.Ac.zzarz();
    }
  }
  
  public void zzc(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (!paramBoolean)) {
      zzasy();
    }
    this.Ap.zzauf();
    this.Ab.zzgn(paramInt);
    this.Ab.zzawc();
    if (paramInt == 2) {
      zzasw();
    }
  }
  
  public void zzc(ConnectionResult paramConnectionResult)
  {
    if (!this.xP.zzd(this.mContext, paramConnectionResult.getErrorCode())) {
      zzasz();
    }
    if (!isResuming())
    {
      this.Ab.zzn(paramConnectionResult);
      this.Ab.zzawc();
    }
  }
  
  public void zzn(Bundle paramBundle)
  {
    while (!this.Ad.isEmpty()) {
      zzb((zzqo.zza)this.Ad.remove());
    }
    this.Ab.zzp(paramBundle);
  }
  
  public <L> zzrr<L> zzs(@NonNull L paramL)
  {
    this.zg.lock();
    try
    {
      paramL = this.Al.zzb(paramL, this.zzajy);
      return paramL;
    }
    finally
    {
      this.zg.unlock();
    }
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
        zzrd.zzb(zzrd.this);
        return;
      }
      zzrd.zza(zzrd.this);
    }
  }
  
  static class zzb
    extends zzrj.zza
  {
    private WeakReference<zzrd> Av;
    
    zzb(zzrd paramzzrd)
    {
      this.Av = new WeakReference(paramzzrd);
    }
    
    public void zzarr()
    {
      zzrd localzzrd = (zzrd)this.Av.get();
      if (localzzrd == null) {
        return;
      }
      zzrd.zza(localzzrd);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */