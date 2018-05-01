package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClientEventManager;
import com.google.android.gms.common.internal.GmsClientEventManager.GmsClientEventState;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
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
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

public final class zzav
  extends GoogleApiClient
  implements zzbq
{
  private final Context mContext;
  private final Looper zzcn;
  private final int zzde;
  private final GoogleApiAvailability zzdg;
  private final Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzdh;
  private boolean zzdk;
  private final Lock zzga;
  private final ClientSettings zzgf;
  private final Map<Api<?>, Boolean> zzgi;
  final Queue<BaseImplementation.ApiMethodImpl<?, ?>> zzgo = new LinkedList();
  private final GmsClientEventManager zzie;
  private zzbp zzif = null;
  private volatile boolean zzig;
  private long zzih = 120000L;
  private long zzii = 5000L;
  private final zzba zzij;
  private GooglePlayServicesUpdatedReceiver zzik;
  final Map<Api.AnyClientKey<?>, Api.Client> zzil;
  Set<Scope> zzim = new HashSet();
  private final ListenerHolders zzin = new ListenerHolders();
  private final ArrayList<zzp> zzio;
  private Integer zzip = null;
  Set<zzch> zziq = null;
  final zzck zzir;
  private final GmsClientEventManager.GmsClientEventState zzis = new zzaw(this);
  
  public zzav(Context paramContext, Lock paramLock, Looper paramLooper, ClientSettings paramClientSettings, GoogleApiAvailability paramGoogleApiAvailability, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, Map<Api<?>, Boolean> paramMap, List<GoogleApiClient.ConnectionCallbacks> paramList, List<GoogleApiClient.OnConnectionFailedListener> paramList1, Map<Api.AnyClientKey<?>, Api.Client> paramMap1, int paramInt1, int paramInt2, ArrayList<zzp> paramArrayList, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.zzga = paramLock;
    this.zzdk = false;
    this.zzie = new GmsClientEventManager(paramLooper, this.zzis);
    this.zzcn = paramLooper;
    this.zzij = new zzba(this, paramLooper);
    this.zzdg = paramGoogleApiAvailability;
    this.zzde = paramInt1;
    if (this.zzde >= 0) {
      this.zzip = Integer.valueOf(paramInt2);
    }
    this.zzgi = paramMap;
    this.zzil = paramMap1;
    this.zzio = paramArrayList;
    this.zzir = new zzck(this.zzil);
    paramContext = paramList.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.ConnectionCallbacks)paramContext.next();
      this.zzie.registerConnectionCallbacks(paramLock);
    }
    paramLock = paramList1.iterator();
    while (paramLock.hasNext())
    {
      paramContext = (GoogleApiClient.OnConnectionFailedListener)paramLock.next();
      this.zzie.registerConnectionFailedListener(paramContext);
    }
    this.zzgf = paramClientSettings;
    this.zzdh = paramAbstractClientBuilder;
  }
  
  private final void resume()
  {
    this.zzga.lock();
    try
    {
      if (this.zzig) {
        zzax();
      }
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  public static int zza(Iterable<Api.Client> paramIterable, boolean paramBoolean)
  {
    int i = 1;
    paramIterable = paramIterable.iterator();
    int j = 0;
    int k = 0;
    if (paramIterable.hasNext())
    {
      Api.Client localClient = (Api.Client)paramIterable.next();
      if (localClient.requiresSignIn()) {
        k = 1;
      }
      if (!localClient.providesSignIn()) {
        break label93;
      }
      j = 1;
    }
    label93:
    for (;;)
    {
      break;
      if (k != 0)
      {
        k = i;
        if (j != 0)
        {
          k = i;
          if (!paramBoolean) {}
        }
      }
      for (k = 2;; k = 3) {
        return k;
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzax()
  {
    this.zzie.enableCallbacks();
    this.zzif.connect();
  }
  
  private final void zzay()
  {
    this.zzga.lock();
    try
    {
      if (zzaz()) {
        zzax();
      }
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  private final void zzg(int paramInt)
  {
    label22:
    Object localObject1;
    if (this.zzip == null)
    {
      this.zzip = Integer.valueOf(paramInt);
      if (this.zzif == null) {}
    }
    else
    {
      if (this.zzip.intValue() == paramInt) {
        break label459;
      }
      localObject1 = zzh(paramInt);
      localObject2 = zzh(this.zzip.intValue());
      throw new IllegalStateException(String.valueOf(localObject1).length() + 51 + String.valueOf(localObject2).length() + "Cannot use sign-in mode: " + (String)localObject1 + ". Mode was already set to " + (String)localObject2);
    }
    Object localObject2 = this.zzil.values().iterator();
    paramInt = 0;
    int i = 0;
    label125:
    if (((Iterator)localObject2).hasNext())
    {
      localObject1 = (Api.Client)((Iterator)localObject2).next();
      if (((Api.Client)localObject1).requiresSignIn()) {
        i = 1;
      }
      if (!((Api.Client)localObject1).providesSignIn()) {
        break label461;
      }
      paramInt = 1;
    }
    label459:
    label461:
    for (;;)
    {
      break label125;
      switch (this.zzip.intValue())
      {
      }
      for (;;)
      {
        if ((this.zzdk) && (paramInt == 0))
        {
          this.zzif = new zzw(this.mContext, this.zzga, this.zzcn, this.zzdg, this.zzil, this.zzgf, this.zzgi, this.zzdh, this.zzio, this, false);
          break;
          if (i == 0) {
            throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
          }
          if (paramInt != 0)
          {
            throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
            if (i != 0)
            {
              if (this.zzdk)
              {
                this.zzif = new zzw(this.mContext, this.zzga, this.zzcn, this.zzdg, this.zzil, this.zzgf, this.zzgi, this.zzdh, this.zzio, this, true);
                break;
              }
              this.zzif = zzr.zza(this.mContext, this, this.zzga, this.zzcn, this.zzdg, this.zzil, this.zzgf, this.zzgi, this.zzdh, this.zzio);
              break;
            }
          }
        }
      }
      this.zzif = new zzbd(this.mContext, this, this.zzga, this.zzcn, this.zzdg, this.zzil, this.zzgf, this.zzgi, this.zzdh, this.zzio, this);
      break label22;
      break;
    }
  }
  
  private static String zzh(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "UNKNOWN";
    }
    for (;;)
    {
      return str;
      str = "SIGN_IN_MODE_NONE";
      continue;
      str = "SIGN_IN_MODE_REQUIRED";
      continue;
      str = "SIGN_IN_MODE_OPTIONAL";
    }
  }
  
  public final ConnectionResult blockingConnect()
  {
    boolean bool1 = true;
    boolean bool2;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool2 = true;
    }
    for (;;)
    {
      Preconditions.checkState(bool2, "blockingConnect must not be called on the UI thread");
      this.zzga.lock();
      try
      {
        if (this.zzde >= 0) {
          if (this.zzip != null)
          {
            bool2 = bool1;
            label45:
            Preconditions.checkState(bool2, "Sign-in mode should have been set explicitly by auto-manage.");
          }
        }
        do
        {
          for (;;)
          {
            zzg(this.zzip.intValue());
            this.zzie.enableCallbacks();
            ConnectionResult localConnectionResult = this.zzif.blockingConnect();
            return localConnectionResult;
            bool2 = false;
            break;
            bool2 = false;
            break label45;
            if (this.zzip != null) {
              break label143;
            }
            this.zzip = Integer.valueOf(zza(this.zzil.values(), false));
          }
        } while (this.zzip.intValue() != 2);
      }
      finally
      {
        this.zzga.unlock();
      }
    }
    label143:
    IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
    localIllegalStateException.<init>("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    throw localIllegalStateException;
  }
  
  public final void connect()
  {
    boolean bool = false;
    this.zzga.lock();
    try
    {
      if (this.zzde >= 0)
      {
        if (this.zzip != null) {
          bool = true;
        }
        Preconditions.checkState(bool, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      do
      {
        for (;;)
        {
          connect(this.zzip.intValue());
          return;
          if (this.zzip != null) {
            break;
          }
          this.zzip = Integer.valueOf(zza(this.zzil.values(), false));
        }
      } while (this.zzip.intValue() != 2);
    }
    finally
    {
      this.zzga.unlock();
    }
    IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
    localIllegalStateException.<init>("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    throw localIllegalStateException;
  }
  
  /* Error */
  public final void connect(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 186 1 0
    //   11: iload_2
    //   12: istore_3
    //   13: iload_1
    //   14: iconst_3
    //   15: if_icmpeq +17 -> 32
    //   18: iload_2
    //   19: istore_3
    //   20: iload_1
    //   21: iconst_1
    //   22: if_icmpeq +10 -> 32
    //   25: iload_1
    //   26: iconst_2
    //   27: if_icmpne +55 -> 82
    //   30: iload_2
    //   31: istore_3
    //   32: new 243	java/lang/StringBuilder
    //   35: astore 4
    //   37: aload 4
    //   39: bipush 33
    //   41: invokespecial 253	java/lang/StringBuilder:<init>	(I)V
    //   44: iload_3
    //   45: aload 4
    //   47: ldc_w 339
    //   50: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: iload_1
    //   54: invokevirtual 342	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   57: invokevirtual 265	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: invokestatic 345	com/google/android/gms/common/internal/Preconditions:checkArgument	(ZLjava/lang/Object;)V
    //   63: aload_0
    //   64: iload_1
    //   65: invokespecial 327	com/google/android/gms/common/api/internal/zzav:zzg	(I)V
    //   68: aload_0
    //   69: invokespecial 191	com/google/android/gms/common/api/internal/zzav:zzax	()V
    //   72: aload_0
    //   73: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   76: invokeinterface 194 1 0
    //   81: return
    //   82: iconst_0
    //   83: istore_3
    //   84: goto -52 -> 32
    //   87: astore 4
    //   89: aload_0
    //   90: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   93: invokeinterface 194 1 0
    //   98: aload 4
    //   100: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	zzav
    //   0	101	1	paramInt	int
    //   1	30	2	bool1	boolean
    //   12	72	3	bool2	boolean
    //   35	11	4	localStringBuilder	StringBuilder
    //   87	12	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	72	87	finally
  }
  
  public final void disconnect()
  {
    this.zzga.lock();
    try
    {
      this.zzir.release();
      if (this.zzif != null) {
        this.zzif.disconnect();
      }
      this.zzin.release();
      Iterator localIterator = this.zzgo.iterator();
      while (localIterator.hasNext())
      {
        BaseImplementation.ApiMethodImpl localApiMethodImpl = (BaseImplementation.ApiMethodImpl)localIterator.next();
        localApiMethodImpl.zza(null);
        localApiMethodImpl.cancel();
      }
      this.zzgo.clear();
    }
    finally
    {
      this.zzga.unlock();
    }
    zzbp localzzbp = this.zzif;
    if (localzzbp == null) {
      this.zzga.unlock();
    }
    for (;;)
    {
      return;
      zzaz();
      this.zzie.disableCallbacks();
      this.zzga.unlock();
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("mContext=").println(this.mContext);
    paramPrintWriter.append(paramString).append("mResuming=").print(this.zzig);
    paramPrintWriter.append(" mWorkQueue.size()=").print(this.zzgo.size());
    zzck localzzck = this.zzir;
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(localzzck.zzmo.size());
    if (this.zzif != null) {
      this.zzif.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  /* Error */
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 417	com/google/android/gms/common/api/internal/BaseImplementation$ApiMethodImpl:getClientKey	()Lcom/google/android/gms/common/api/Api$AnyClientKey;
    //   4: ifnull +119 -> 123
    //   7: iconst_1
    //   8: istore_2
    //   9: iload_2
    //   10: ldc_w 419
    //   13: invokestatic 345	com/google/android/gms/common/internal/Preconditions:checkArgument	(ZLjava/lang/Object;)V
    //   16: aload_0
    //   17: getfield 136	com/google/android/gms/common/api/internal/zzav:zzil	Ljava/util/Map;
    //   20: aload_1
    //   21: invokevirtual 417	com/google/android/gms/common/api/internal/BaseImplementation$ApiMethodImpl:getClientKey	()Lcom/google/android/gms/common/api/Api$AnyClientKey;
    //   24: invokeinterface 423 2 0
    //   29: istore_2
    //   30: aload_1
    //   31: invokevirtual 427	com/google/android/gms/common/api/internal/BaseImplementation$ApiMethodImpl:getApi	()Lcom/google/android/gms/common/api/Api;
    //   34: ifnull +94 -> 128
    //   37: aload_1
    //   38: invokevirtual 427	com/google/android/gms/common/api/internal/BaseImplementation$ApiMethodImpl:getApi	()Lcom/google/android/gms/common/api/Api;
    //   41: invokevirtual 432	com/google/android/gms/common/api/Api:getName	()Ljava/lang/String;
    //   44: astore_3
    //   45: iload_2
    //   46: new 243	java/lang/StringBuilder
    //   49: dup
    //   50: aload_3
    //   51: invokestatic 248	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   54: invokevirtual 251	java/lang/String:length	()I
    //   57: bipush 65
    //   59: iadd
    //   60: invokespecial 253	java/lang/StringBuilder:<init>	(I)V
    //   63: ldc_w 434
    //   66: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_3
    //   70: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: ldc_w 436
    //   76: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: invokevirtual 265	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   82: invokestatic 345	com/google/android/gms/common/internal/Preconditions:checkArgument	(ZLjava/lang/Object;)V
    //   85: aload_0
    //   86: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   89: invokeinterface 186 1 0
    //   94: aload_0
    //   95: getfield 66	com/google/android/gms/common/api/internal/zzav:zzif	Lcom/google/android/gms/common/api/internal/zzbp;
    //   98: ifnonnull +37 -> 135
    //   101: aload_0
    //   102: getfield 71	com/google/android/gms/common/api/internal/zzav:zzgo	Ljava/util/Queue;
    //   105: aload_1
    //   106: invokeinterface 439 2 0
    //   111: pop
    //   112: aload_0
    //   113: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   116: invokeinterface 194 1 0
    //   121: aload_1
    //   122: areturn
    //   123: iconst_0
    //   124: istore_2
    //   125: goto -116 -> 9
    //   128: ldc_w 441
    //   131: astore_3
    //   132: goto -87 -> 45
    //   135: aload_0
    //   136: getfield 66	com/google/android/gms/common/api/internal/zzav:zzif	Lcom/google/android/gms/common/api/internal/zzbp;
    //   139: aload_1
    //   140: invokeinterface 443 2 0
    //   145: astore_1
    //   146: aload_0
    //   147: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   150: invokeinterface 194 1 0
    //   155: goto -34 -> 121
    //   158: astore_1
    //   159: aload_0
    //   160: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   163: invokeinterface 194 1 0
    //   168: aload_1
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	zzav
    //   0	170	1	paramT	T
    //   8	117	2	bool	boolean
    //   44	88	3	str	String
    // Exception table:
    //   from	to	target	type
    //   94	112	158	finally
    //   135	146	158	finally
  }
  
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    boolean bool;
    if (paramT.getClientKey() != null)
    {
      bool = true;
      Preconditions.checkArgument(bool, "This task can not be executed (it's probably a Batch or malformed)");
      bool = this.zzil.containsKey(paramT.getClientKey());
      if (paramT.getApi() == null) {
        break label131;
      }
    }
    label131:
    for (Object localObject = paramT.getApi().getName();; localObject = "the API")
    {
      Preconditions.checkArgument(bool, String.valueOf(localObject).length() + 65 + "GoogleApiClient is not configured to use " + (String)localObject + " required for this call.");
      this.zzga.lock();
      try
      {
        if (this.zzif != null) {
          break label138;
        }
        paramT = new java/lang/IllegalStateException;
        paramT.<init>("GoogleApiClient is not connected yet.");
        throw paramT;
      }
      finally
      {
        this.zzga.unlock();
      }
      bool = false;
      break;
    }
    label138:
    if (this.zzig)
    {
      this.zzgo.add(paramT);
      while (!this.zzgo.isEmpty())
      {
        localObject = (BaseImplementation.ApiMethodImpl)this.zzgo.remove();
        this.zzir.zzb((BasePendingResult)localObject);
        ((BaseImplementation.ApiMethodImpl)localObject).setFailedResult(Status.RESULT_INTERNAL_ERROR);
      }
      this.zzga.unlock();
    }
    for (;;)
    {
      return paramT;
      paramT = this.zzif.execute(paramT);
      this.zzga.unlock();
    }
  }
  
  public final Looper getLooper()
  {
    return this.zzcn;
  }
  
  public final boolean isConnected()
  {
    if ((this.zzif != null) && (this.zzif.isConnected())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzie.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.zzie.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public final void zza(zzch paramzzch)
  {
    this.zzga.lock();
    try
    {
      if (this.zziq == null)
      {
        HashSet localHashSet = new java/util/HashSet;
        localHashSet.<init>();
        this.zziq = localHashSet;
      }
      this.zziq.add(paramzzch);
      return;
    }
    finally
    {
      this.zzga.unlock();
    }
  }
  
  @GuardedBy("mLock")
  final boolean zzaz()
  {
    boolean bool = false;
    if (!this.zzig) {}
    for (;;)
    {
      return bool;
      this.zzig = false;
      this.zzij.removeMessages(2);
      this.zzij.removeMessages(1);
      if (this.zzik != null)
      {
        this.zzik.unregister();
        this.zzik = null;
      }
      bool = true;
    }
  }
  
  @GuardedBy("mLock")
  public final void zzb(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (!paramBoolean) && (!this.zzig))
    {
      this.zzig = true;
      if (this.zzik == null) {
        this.zzik = this.zzdg.registerCallbackOnUpdate(this.mContext.getApplicationContext(), new zzbb(this));
      }
      this.zzij.sendMessageDelayed(this.zzij.obtainMessage(1), this.zzih);
      this.zzij.sendMessageDelayed(this.zzij.obtainMessage(2), this.zzii);
    }
    this.zzir.zzce();
    this.zzie.onUnintentionalDisconnection(paramInt);
    this.zzie.disableCallbacks();
    if (paramInt == 2) {
      zzax();
    }
  }
  
  @GuardedBy("mLock")
  public final void zzb(Bundle paramBundle)
  {
    while (!this.zzgo.isEmpty()) {
      execute((BaseImplementation.ApiMethodImpl)this.zzgo.remove());
    }
    this.zzie.onConnectionSuccess(paramBundle);
  }
  
  public final void zzb(zzch paramzzch)
  {
    this.zzga.lock();
    for (;;)
    {
      try
      {
        if (this.zziq == null)
        {
          paramzzch = new java/lang/Exception;
          paramzzch.<init>();
          Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", paramzzch);
          return;
        }
        if (!this.zziq.remove(paramzzch))
        {
          paramzzch = new java/lang/Exception;
          paramzzch.<init>();
          Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", paramzzch);
          continue;
        }
        if (zzba()) {
          continue;
        }
      }
      finally
      {
        this.zzga.unlock();
      }
      this.zzif.zzz();
    }
  }
  
  /* Error */
  final boolean zzba()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iconst_0
    //   3: istore_2
    //   4: aload_0
    //   5: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   8: invokeinterface 186 1 0
    //   13: aload_0
    //   14: getfield 93	com/google/android/gms/common/api/internal/zzav:zziq	Ljava/util/Set;
    //   17: astore_3
    //   18: aload_3
    //   19: ifnonnull +16 -> 35
    //   22: aload_0
    //   23: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   26: invokeinterface 194 1 0
    //   31: iload_2
    //   32: istore_1
    //   33: iload_1
    //   34: ireturn
    //   35: aload_0
    //   36: getfield 93	com/google/android/gms/common/api/internal/zzav:zziq	Ljava/util/Set;
    //   39: invokeinterface 549 1 0
    //   44: istore_2
    //   45: iload_2
    //   46: ifne +5 -> 51
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 194 1 0
    //   60: goto -27 -> 33
    //   63: astore_3
    //   64: aload_0
    //   65: getfield 104	com/google/android/gms/common/api/internal/zzav:zzga	Ljava/util/concurrent/locks/Lock;
    //   68: invokeinterface 194 1 0
    //   73: aload_3
    //   74: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	this	zzav
    //   1	50	1	bool1	boolean
    //   3	43	2	bool2	boolean
    //   17	2	3	localSet	Set
    //   63	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	18	63	finally
    //   35	45	63	finally
  }
  
  final String zzbb()
  {
    StringWriter localStringWriter = new StringWriter();
    dump("", null, new PrintWriter(localStringWriter), null);
    return localStringWriter.toString();
  }
  
  @GuardedBy("mLock")
  public final void zzc(ConnectionResult paramConnectionResult)
  {
    if (!this.zzdg.isPlayServicesPossiblyUpdating(this.mContext, paramConnectionResult.getErrorCode())) {
      zzaz();
    }
    if (!this.zzig)
    {
      this.zzie.onConnectionFailure(paramConnectionResult);
      this.zzie.disableCallbacks();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzav.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */