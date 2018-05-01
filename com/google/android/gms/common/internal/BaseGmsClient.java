package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;

public abstract class BaseGmsClient<T extends IInterface>
{
  public static final String[] GOOGLE_PLUS_REQUIRED_FEATURES = { "service_esmobile", "service_googleme" };
  private static final Feature[] zzqz = new Feature[0];
  protected ConnectionProgressReportCallbacks mConnectionProgressReportCallbacks;
  private final Context mContext;
  protected AtomicInteger mDisconnectCount = new AtomicInteger(0);
  final Handler mHandler;
  private final Object mLock = new Object();
  private final Looper zzcn;
  private final GoogleApiAvailabilityLight zzgk;
  private int zzra;
  private long zzrb;
  private long zzrc;
  private int zzrd;
  private long zzre;
  private GmsServiceEndpoint zzrf;
  private final GmsClientSupervisor zzrg;
  private final Object zzrh = new Object();
  @GuardedBy("mServiceBrokerLock")
  private IGmsServiceBroker zzri;
  @GuardedBy("mLock")
  private T zzrj;
  private final ArrayList<BaseGmsClient<T>.CallbackProxy<?>> zzrk = new ArrayList();
  @GuardedBy("mLock")
  private BaseGmsClient<T>.GmsServiceConnection zzrl;
  @GuardedBy("mLock")
  private int zzrm = 1;
  private final BaseConnectionCallbacks zzrn;
  private final BaseOnConnectionFailedListener zzro;
  private final int zzrp;
  private final String zzrq;
  private ConnectionResult zzrr = null;
  private boolean zzrs = false;
  private volatile ConnectionInfo zzrt = null;
  
  protected BaseGmsClient(Context paramContext, Looper paramLooper, int paramInt, BaseConnectionCallbacks paramBaseConnectionCallbacks, BaseOnConnectionFailedListener paramBaseOnConnectionFailedListener, String paramString)
  {
    this(paramContext, paramLooper, GmsClientSupervisor.getInstance(paramContext), GoogleApiAvailabilityLight.getInstance(), paramInt, (BaseConnectionCallbacks)Preconditions.checkNotNull(paramBaseConnectionCallbacks), (BaseOnConnectionFailedListener)Preconditions.checkNotNull(paramBaseOnConnectionFailedListener), paramString);
  }
  
  protected BaseGmsClient(Context paramContext, Looper paramLooper, GmsClientSupervisor paramGmsClientSupervisor, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, int paramInt, BaseConnectionCallbacks paramBaseConnectionCallbacks, BaseOnConnectionFailedListener paramBaseOnConnectionFailedListener, String paramString)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "Context must not be null"));
    this.zzcn = ((Looper)Preconditions.checkNotNull(paramLooper, "Looper must not be null"));
    this.zzrg = ((GmsClientSupervisor)Preconditions.checkNotNull(paramGmsClientSupervisor, "Supervisor must not be null"));
    this.zzgk = ((GoogleApiAvailabilityLight)Preconditions.checkNotNull(paramGoogleApiAvailabilityLight, "API availability must not be null"));
    this.mHandler = new zzb(paramLooper);
    this.zzrp = paramInt;
    this.zzrn = paramBaseConnectionCallbacks;
    this.zzro = paramBaseOnConnectionFailedListener;
    this.zzrq = paramString;
  }
  
  private final void zza(int paramInt, T paramT)
  {
    boolean bool = true;
    int i;
    int j;
    if (paramInt == 4)
    {
      i = 1;
      if (paramT == null) {
        break label94;
      }
      j = 1;
      label17:
      if (i != j) {
        break label100;
      }
      Preconditions.checkArgument(bool);
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.zzrm = paramInt;
        this.zzrj = paramT;
        onSetConnectState(paramInt, paramT);
        switch (paramInt)
        {
        default: 
          return;
          i = 0;
          break;
          j = 0;
          break label17;
          bool = false;
          break;
        case 2: 
        case 3: 
          label94:
          label100:
          Object localObject2;
          Object localObject3;
          if ((this.zzrl != null) && (this.zzrf != null))
          {
            localObject2 = this.zzrf.zzcw();
            paramT = this.zzrf.getPackageName();
            i = String.valueOf(localObject2).length();
            paramInt = String.valueOf(paramT).length();
            localObject3 = new java/lang/StringBuilder;
            ((StringBuilder)localObject3).<init>(i + 70 + paramInt);
            Log.e("GmsClient", "Calling connect() while still connected, missing disconnect() for " + (String)localObject2 + " on " + paramT);
            this.zzrg.unbindService(this.zzrf.zzcw(), this.zzrf.getPackageName(), this.zzrf.getBindFlags(), this.zzrl, getRealClientName());
            this.mDisconnectCount.incrementAndGet();
          }
          paramT = new com/google/android/gms/common/internal/BaseGmsClient$GmsServiceConnection;
          paramT.<init>(this, this.mDisconnectCount.get());
          this.zzrl = paramT;
          if ((this.zzrm == 3) && (getLocalStartServiceAction() != null))
          {
            paramT = new com/google/android/gms/common/internal/GmsServiceEndpoint;
            paramT.<init>(getContext().getPackageName(), getLocalStartServiceAction(), true, getServiceBindFlags());
            this.zzrf = paramT;
            if (this.zzrg.bindService(this.zzrf.zzcw(), this.zzrf.getPackageName(), this.zzrf.getBindFlags(), this.zzrl, getRealClientName())) {
              continue;
            }
            localObject3 = this.zzrf.zzcw();
            paramT = this.zzrf.getPackageName();
            i = String.valueOf(localObject3).length();
            paramInt = String.valueOf(paramT).length();
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>(i + 34 + paramInt);
            Log.e("GmsClient", "unable to connect to service: " + (String)localObject3 + " on " + paramT);
            onPostServiceBindingHandler(16, null, this.mDisconnectCount.get());
          }
          break;
        }
      }
      paramT = new GmsServiceEndpoint(getStartServicePackage(), getStartServiceAction(), false, getServiceBindFlags());
      continue;
      onConnectedLocked(paramT);
      continue;
      if (this.zzrl != null)
      {
        this.zzrg.unbindService(getStartServiceAction(), getStartServicePackage(), getServiceBindFlags(), this.zzrl, getRealClientName());
        this.zzrl = null;
      }
    }
  }
  
  private final void zza(ConnectionInfo paramConnectionInfo)
  {
    this.zzrt = paramConnectionInfo;
  }
  
  private final boolean zza(int paramInt1, int paramInt2, T paramT)
  {
    synchronized (this.mLock)
    {
      if (this.zzrm != paramInt1)
      {
        bool = false;
        return bool;
      }
      zza(paramInt2, paramT);
      boolean bool = true;
    }
  }
  
  private final boolean zzcq()
  {
    synchronized (this.mLock)
    {
      if (this.zzrm == 3)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  private final boolean zzcr()
  {
    boolean bool1 = false;
    boolean bool2;
    if (this.zzrs) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (!TextUtils.isEmpty(getServiceDescriptor()))
      {
        bool2 = bool1;
        if (!TextUtils.isEmpty(getLocalStartServiceAction())) {
          try
          {
            Class.forName(getServiceDescriptor());
            bool2 = true;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            bool2 = bool1;
          }
        }
      }
    }
  }
  
  private final void zzj(int paramInt)
  {
    if (zzcq())
    {
      paramInt = 5;
      this.zzrs = true;
    }
    for (;;)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(paramInt, this.mDisconnectCount.get(), 16));
      return;
      paramInt = 4;
    }
  }
  
  public void checkAvailabilityAndConnect()
  {
    int i = this.zzgk.isGooglePlayServicesAvailable(this.mContext, getMinApkVersion());
    if (i != 0)
    {
      zza(1, null);
      triggerNotAvailable(new LegacyClientCallbackAdapter(), i, null);
    }
    for (;;)
    {
      return;
      connect(new LegacyClientCallbackAdapter());
    }
  }
  
  protected final void checkConnected()
  {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
    }
  }
  
  public void connect(ConnectionProgressReportCallbacks paramConnectionProgressReportCallbacks)
  {
    this.mConnectionProgressReportCallbacks = ((ConnectionProgressReportCallbacks)Preconditions.checkNotNull(paramConnectionProgressReportCallbacks, "Connection progress callbacks cannot be null."));
    zza(2, null);
  }
  
  protected abstract T createServiceInterface(IBinder paramIBinder);
  
  public void disconnect()
  {
    this.mDisconnectCount.incrementAndGet();
    synchronized (this.zzrk)
    {
      int i = this.zzrk.size();
      for (int j = 0; j < i; j++) {
        ((CallbackProxy)this.zzrk.get(j)).removeListener();
      }
      this.zzrk.clear();
    }
    synchronized (this.zzrh)
    {
      this.zzri = null;
      zza(1, null);
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] arg4)
  {
    int i;
    synchronized (this.mLock)
    {
      i = this.zzrm;
      paramFileDescriptor = this.zzrj;
    }
    for (;;)
    {
      Object localObject;
      synchronized (this.zzrh)
      {
        localObject = this.zzri;
        paramPrintWriter.append(paramString).append("mConnectState=");
        switch (i)
        {
        default: 
          paramPrintWriter.print("UNKNOWN");
          paramPrintWriter.append(" mService=");
          if (paramFileDescriptor != null) {
            break label533;
          }
          paramPrintWriter.append("null");
          paramPrintWriter.append(" mServiceBroker=");
          if (localObject != null) {
            break label566;
          }
          paramPrintWriter.println("null");
          paramFileDescriptor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
          long l;
          if (this.zzrc > 0L)
          {
            localObject = paramPrintWriter.append(paramString).append("lastConnectedTime=");
            l = this.zzrc;
            ??? = paramFileDescriptor.format(new Date(this.zzrc));
            ((PrintWriter)localObject).println(String.valueOf(???).length() + 21 + l + " " + ???);
          }
          if (this.zzrb > 0L) {
            paramPrintWriter.append(paramString).append("lastSuspendedCause=");
          }
          switch (this.zzra)
          {
          default: 
            paramPrintWriter.append(String.valueOf(this.zzra));
            ??? = paramPrintWriter.append(" lastSuspendedTime=");
            l = this.zzrb;
            localObject = paramFileDescriptor.format(new Date(this.zzrb));
            ???.println(String.valueOf(localObject).length() + 21 + l + " " + (String)localObject);
            if (this.zzre > 0L)
            {
              paramPrintWriter.append(paramString).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.zzrd));
              paramString = paramPrintWriter.append(" lastFailedTime=");
              l = this.zzre;
              paramFileDescriptor = paramFileDescriptor.format(new Date(this.zzre));
              paramString.println(String.valueOf(paramFileDescriptor).length() + 21 + l + " " + paramFileDescriptor);
            }
            return;
            paramString = finally;
            throw paramString;
          }
          break;
        }
      }
      paramPrintWriter.print("REMOTE_CONNECTING");
      continue;
      paramPrintWriter.print("LOCAL_CONNECTING");
      continue;
      paramPrintWriter.print("CONNECTED");
      continue;
      paramPrintWriter.print("DISCONNECTING");
      continue;
      paramPrintWriter.print("DISCONNECTED");
      continue;
      label533:
      paramPrintWriter.append(getServiceDescriptor()).append("@").append(Integer.toHexString(System.identityHashCode(paramFileDescriptor.asBinder())));
      continue;
      label566:
      paramPrintWriter.append("IGmsServiceBroker@").println(Integer.toHexString(System.identityHashCode(((IGmsServiceBroker)localObject).asBinder())));
      continue;
      paramPrintWriter.append("CAUSE_SERVICE_DISCONNECTED");
      continue;
      paramPrintWriter.append("CAUSE_NETWORK_LOST");
    }
  }
  
  public Account getAccount()
  {
    return null;
  }
  
  public final Account getAccountOrDefault()
  {
    if (getAccount() != null) {}
    for (Account localAccount = getAccount();; localAccount = new Account("<<default account>>", "com.google")) {
      return localAccount;
    }
  }
  
  public Feature[] getApiFeatures()
  {
    return zzqz;
  }
  
  public final Feature[] getAvailableFeatures()
  {
    Object localObject = this.zzrt;
    if (localObject == null) {}
    for (localObject = null;; localObject = ((ConnectionInfo)localObject).getAvailableFeatures()) {
      return (Feature[])localObject;
    }
  }
  
  public Bundle getConnectionHint()
  {
    return null;
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public String getEndpointPackageName()
  {
    if ((isConnected()) && (this.zzrf != null)) {
      return this.zzrf.getPackageName();
    }
    throw new RuntimeException("Failed to connect when checking package");
  }
  
  protected Bundle getGetServiceRequestExtraArgs()
  {
    return new Bundle();
  }
  
  protected String getLocalStartServiceAction()
  {
    return null;
  }
  
  public int getMinApkVersion()
  {
    return GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  }
  
  protected final String getRealClientName()
  {
    if (this.zzrq == null) {}
    for (String str = this.mContext.getClass().getName();; str = this.zzrq) {
      return str;
    }
  }
  
  /* Error */
  public void getRemoteService(IAccountAccessor arg1, Set<Scope> paramSet)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 588	com/google/android/gms/common/internal/BaseGmsClient:getGetServiceRequestExtraArgs	()Landroid/os/Bundle;
    //   4: astore_3
    //   5: new 590	com/google/android/gms/common/internal/GetServiceRequest
    //   8: dup
    //   9: aload_0
    //   10: getfield 192	com/google/android/gms/common/internal/BaseGmsClient:zzrp	I
    //   13: invokespecial 591	com/google/android/gms/common/internal/GetServiceRequest:<init>	(I)V
    //   16: aload_0
    //   17: getfield 171	com/google/android/gms/common/internal/BaseGmsClient:mContext	Landroid/content/Context;
    //   20: invokevirtual 283	android/content/Context:getPackageName	()Ljava/lang/String;
    //   23: invokevirtual 595	com/google/android/gms/common/internal/GetServiceRequest:setCallingPackage	(Ljava/lang/String;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   26: aload_3
    //   27: invokevirtual 599	com/google/android/gms/common/internal/GetServiceRequest:setExtraArgs	(Landroid/os/Bundle;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   30: astore_3
    //   31: aload_2
    //   32: ifnull +9 -> 41
    //   35: aload_3
    //   36: aload_2
    //   37: invokevirtual 603	com/google/android/gms/common/internal/GetServiceRequest:setScopes	(Ljava/util/Collection;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 606	com/google/android/gms/common/internal/BaseGmsClient:requiresSignIn	()Z
    //   45: ifeq +83 -> 128
    //   48: aload_3
    //   49: aload_0
    //   50: invokevirtual 608	com/google/android/gms/common/internal/BaseGmsClient:getAccountOrDefault	()Landroid/accounts/Account;
    //   53: invokevirtual 612	com/google/android/gms/common/internal/GetServiceRequest:setClientRequestedAccount	(Landroid/accounts/Account;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   56: aload_1
    //   57: invokevirtual 616	com/google/android/gms/common/internal/GetServiceRequest:setAuthenticatedAccount	(Lcom/google/android/gms/common/internal/IAccountAccessor;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   60: pop
    //   61: aload_3
    //   62: aload_0
    //   63: invokevirtual 619	com/google/android/gms/common/internal/BaseGmsClient:getRequiredFeatures	()[Lcom/google/android/gms/common/Feature;
    //   66: invokevirtual 623	com/google/android/gms/common/internal/GetServiceRequest:setClientRequiredFeatures	([Lcom/google/android/gms/common/Feature;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   69: pop
    //   70: aload_3
    //   71: aload_0
    //   72: invokevirtual 625	com/google/android/gms/common/internal/BaseGmsClient:getApiFeatures	()[Lcom/google/android/gms/common/Feature;
    //   75: invokevirtual 628	com/google/android/gms/common/internal/GetServiceRequest:setClientApiFeatures	([Lcom/google/android/gms/common/Feature;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   78: pop
    //   79: aload_0
    //   80: getfield 142	com/google/android/gms/common/internal/BaseGmsClient:zzrh	Ljava/lang/Object;
    //   83: astore_1
    //   84: aload_1
    //   85: monitorenter
    //   86: aload_0
    //   87: getfield 202	com/google/android/gms/common/internal/BaseGmsClient:zzri	Lcom/google/android/gms/common/internal/IGmsServiceBroker;
    //   90: ifnull +57 -> 147
    //   93: aload_0
    //   94: getfield 202	com/google/android/gms/common/internal/BaseGmsClient:zzri	Lcom/google/android/gms/common/internal/IGmsServiceBroker;
    //   97: astore_2
    //   98: new 19	com/google/android/gms/common/internal/BaseGmsClient$GmsCallbacks
    //   101: astore 4
    //   103: aload 4
    //   105: aload_0
    //   106: aload_0
    //   107: getfield 162	com/google/android/gms/common/internal/BaseGmsClient:mDisconnectCount	Ljava/util/concurrent/atomic/AtomicInteger;
    //   110: invokevirtual 272	java/util/concurrent/atomic/AtomicInteger:get	()I
    //   113: invokespecial 629	com/google/android/gms/common/internal/BaseGmsClient$GmsCallbacks:<init>	(Lcom/google/android/gms/common/internal/BaseGmsClient;I)V
    //   116: aload_2
    //   117: aload 4
    //   119: aload_3
    //   120: invokeinterface 633 3 0
    //   125: aload_1
    //   126: monitorexit
    //   127: return
    //   128: aload_0
    //   129: invokevirtual 636	com/google/android/gms/common/internal/BaseGmsClient:requiresAccount	()Z
    //   132: ifeq -71 -> 61
    //   135: aload_3
    //   136: aload_0
    //   137: invokevirtual 540	com/google/android/gms/common/internal/BaseGmsClient:getAccount	()Landroid/accounts/Account;
    //   140: invokevirtual 612	com/google/android/gms/common/internal/GetServiceRequest:setClientRequestedAccount	(Landroid/accounts/Account;)Lcom/google/android/gms/common/internal/GetServiceRequest;
    //   143: pop
    //   144: goto -83 -> 61
    //   147: ldc -17
    //   149: ldc_w 638
    //   152: invokestatic 641	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   155: pop
    //   156: goto -31 -> 125
    //   159: astore_2
    //   160: aload_1
    //   161: monitorexit
    //   162: aload_2
    //   163: athrow
    //   164: astore_1
    //   165: ldc -17
    //   167: ldc_w 643
    //   170: aload_1
    //   171: invokestatic 646	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   174: pop
    //   175: aload_0
    //   176: iconst_1
    //   177: invokevirtual 649	com/google/android/gms/common/internal/BaseGmsClient:triggerConnectionSuspended	(I)V
    //   180: goto -53 -> 127
    //   183: astore_1
    //   184: aload_1
    //   185: athrow
    //   186: astore_1
    //   187: ldc -17
    //   189: ldc_w 643
    //   192: aload_1
    //   193: invokestatic 646	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   196: pop
    //   197: aload_0
    //   198: bipush 8
    //   200: aconst_null
    //   201: aconst_null
    //   202: aload_0
    //   203: getfield 162	com/google/android/gms/common/internal/BaseGmsClient:mDisconnectCount	Ljava/util/concurrent/atomic/AtomicInteger;
    //   206: invokevirtual 272	java/util/concurrent/atomic/AtomicInteger:get	()I
    //   209: invokevirtual 653	com/google/android/gms/common/internal/BaseGmsClient:onPostInitHandler	(ILandroid/os/IBinder;Landroid/os/Bundle;I)V
    //   212: goto -85 -> 127
    //   215: astore_1
    //   216: goto -29 -> 187
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	this	BaseGmsClient
    //   0	219	2	paramSet	Set<Scope>
    //   4	132	3	localObject	Object
    //   101	17	4	localGmsCallbacks	GmsCallbacks
    // Exception table:
    //   from	to	target	type
    //   86	125	159	finally
    //   125	127	159	finally
    //   147	156	159	finally
    //   160	162	159	finally
    //   79	86	164	android/os/DeadObjectException
    //   162	164	164	android/os/DeadObjectException
    //   79	86	183	java/lang/SecurityException
    //   162	164	183	java/lang/SecurityException
    //   79	86	186	android/os/RemoteException
    //   162	164	186	android/os/RemoteException
    //   79	86	215	java/lang/RuntimeException
    //   162	164	215	java/lang/RuntimeException
  }
  
  public Feature[] getRequiredFeatures()
  {
    return zzqz;
  }
  
  protected Set<Scope> getScopes()
  {
    return Collections.EMPTY_SET;
  }
  
  public final T getService()
    throws DeadObjectException
  {
    synchronized (this.mLock)
    {
      if (this.zzrm == 5)
      {
        DeadObjectException localDeadObjectException = new android/os/DeadObjectException;
        localDeadObjectException.<init>();
        throw localDeadObjectException;
      }
    }
    checkConnected();
    if (this.zzrj != null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "Client is connected but service is null");
      IInterface localIInterface = this.zzrj;
      return localIInterface;
    }
  }
  
  protected int getServiceBindFlags()
  {
    return 129;
  }
  
  public IBinder getServiceBrokerBinder()
  {
    synchronized (this.zzrh)
    {
      if (this.zzri == null)
      {
        localIBinder = null;
        return localIBinder;
      }
      IBinder localIBinder = this.zzri.asBinder();
    }
  }
  
  protected abstract String getServiceDescriptor();
  
  public Intent getSignInIntent()
  {
    throw new UnsupportedOperationException("Not a sign in API");
  }
  
  protected abstract String getStartServiceAction();
  
  protected String getStartServicePackage()
  {
    return "com.google.android.gms";
  }
  
  public boolean isConnected()
  {
    synchronized (this.mLock)
    {
      if (this.zzrm == 4)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public boolean isConnecting()
  {
    synchronized (this.mLock)
    {
      if ((this.zzrm == 2) || (this.zzrm == 3))
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  protected void onConnectedLocked(T paramT)
  {
    this.zzrc = System.currentTimeMillis();
  }
  
  protected void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    this.zzrd = paramConnectionResult.getErrorCode();
    this.zzre = System.currentTimeMillis();
  }
  
  protected void onConnectionSuspended(int paramInt)
  {
    this.zzra = paramInt;
    this.zzrb = System.currentTimeMillis();
  }
  
  protected void onPostInitHandler(int paramInt1, IBinder paramIBinder, Bundle paramBundle, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramInt2, -1, new PostInitCallback(paramInt1, paramIBinder, paramBundle)));
  }
  
  protected void onPostServiceBindingHandler(int paramInt1, Bundle paramBundle, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(7, paramInt2, -1, new PostServiceBindingCallback(paramInt1, paramBundle)));
  }
  
  void onSetConnectState(int paramInt, T paramT) {}
  
  public void onUserSignOut(SignOutCallbacks paramSignOutCallbacks)
  {
    paramSignOutCallbacks.onSignOutComplete();
  }
  
  public boolean providesSignIn()
  {
    return false;
  }
  
  public boolean requiresAccount()
  {
    return false;
  }
  
  public boolean requiresGooglePlayServices()
  {
    return true;
  }
  
  public boolean requiresSignIn()
  {
    return false;
  }
  
  public void triggerConnectionSuspended(int paramInt)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(6, this.mDisconnectCount.get(), paramInt));
  }
  
  protected void triggerNotAvailable(ConnectionProgressReportCallbacks paramConnectionProgressReportCallbacks, int paramInt, PendingIntent paramPendingIntent)
  {
    this.mConnectionProgressReportCallbacks = ((ConnectionProgressReportCallbacks)Preconditions.checkNotNull(paramConnectionProgressReportCallbacks, "Connection progress callbacks cannot be null."));
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.mDisconnectCount.get(), paramInt, paramPendingIntent));
  }
  
  public static abstract interface BaseConnectionCallbacks
  {
    public abstract void onConnected(Bundle paramBundle);
    
    public abstract void onConnectionSuspended(int paramInt);
  }
  
  public static abstract interface BaseOnConnectionFailedListener
  {
    public abstract void onConnectionFailed(ConnectionResult paramConnectionResult);
  }
  
  protected abstract class CallbackProxy<TListener>
  {
    private TListener zzli;
    private boolean zzrv;
    
    public CallbackProxy()
    {
      Object localObject;
      this.zzli = localObject;
      this.zzrv = false;
    }
    
    public void deliverCallback()
    {
      for (;;)
      {
        try
        {
          Object localObject1 = this.zzli;
          if (this.zzrv)
          {
            String str = String.valueOf(this);
            int i = String.valueOf(str).length();
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>(i + 47);
            Log.w("GmsClient", "Callback proxy " + str + " being reused. This is not safe.");
          }
          if (localObject1 != null) {}
          onDeliverCallbackFailed();
        }
        finally
        {
          try
          {
            deliverCallback(localObject1);
          }
          catch (RuntimeException localRuntimeException)
          {
            onDeliverCallbackFailed();
            throw localRuntimeException;
          }
          try
          {
            this.zzrv = true;
            unregister();
            return;
          }
          finally {}
          localObject2 = finally;
        }
      }
    }
    
    protected abstract void deliverCallback(TListener paramTListener);
    
    protected abstract void onDeliverCallbackFailed();
    
    public void removeListener()
    {
      try
      {
        this.zzli = null;
        return;
      }
      finally {}
    }
    
    public void unregister()
    {
      removeListener();
      synchronized (BaseGmsClient.zzf(BaseGmsClient.this))
      {
        BaseGmsClient.zzf(BaseGmsClient.this).remove(this);
        return;
      }
    }
  }
  
  public static abstract interface ConnectionProgressReportCallbacks
  {
    public abstract void onReportServiceBinding(ConnectionResult paramConnectionResult);
  }
  
  public static final class GmsCallbacks
    extends IGmsCallbacks.Stub
  {
    private BaseGmsClient zzrw;
    private final int zzrx;
    
    public GmsCallbacks(BaseGmsClient paramBaseGmsClient, int paramInt)
    {
      this.zzrw = paramBaseGmsClient;
      this.zzrx = paramInt;
    }
    
    public final void onAccountValidationComplete(int paramInt, Bundle paramBundle)
    {
      Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
    }
    
    public final void onPostInitComplete(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    {
      Preconditions.checkNotNull(this.zzrw, "onPostInitComplete can be called only once per call to getRemoteService");
      this.zzrw.onPostInitHandler(paramInt, paramIBinder, paramBundle, this.zzrx);
      this.zzrw = null;
    }
    
    public final void onPostInitCompleteWithConnectionInfo(int paramInt, IBinder paramIBinder, ConnectionInfo paramConnectionInfo)
    {
      Preconditions.checkNotNull(this.zzrw, "onPostInitCompleteWithConnectionInfo can be called only once per call togetRemoteService");
      Preconditions.checkNotNull(paramConnectionInfo);
      BaseGmsClient.zza(this.zzrw, paramConnectionInfo);
      onPostInitComplete(paramInt, paramIBinder, paramConnectionInfo.getResolutionBundle());
    }
  }
  
  public final class GmsServiceConnection
    implements ServiceConnection
  {
    private final int zzrx;
    
    public GmsServiceConnection(int paramInt)
    {
      this.zzrx = paramInt;
    }
    
    public final void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        BaseGmsClient.zza(BaseGmsClient.this, 16);
      }
      for (;;)
      {
        return;
        synchronized (BaseGmsClient.zza(BaseGmsClient.this))
        {
          BaseGmsClient.zza(BaseGmsClient.this, IGmsServiceBroker.Stub.asInterface(paramIBinder));
          BaseGmsClient.this.onPostServiceBindingHandler(0, null, this.zzrx);
        }
      }
    }
    
    public final void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (BaseGmsClient.zza(BaseGmsClient.this))
      {
        BaseGmsClient.zza(BaseGmsClient.this, null);
        BaseGmsClient.this.mHandler.sendMessage(BaseGmsClient.this.mHandler.obtainMessage(6, this.zzrx, 1));
        return;
      }
    }
  }
  
  protected class LegacyClientCallbackAdapter
    implements BaseGmsClient.ConnectionProgressReportCallbacks
  {
    public LegacyClientCallbackAdapter() {}
    
    public void onReportServiceBinding(ConnectionResult paramConnectionResult)
    {
      if (paramConnectionResult.isSuccess()) {
        BaseGmsClient.this.getRemoteService(null, BaseGmsClient.this.getScopes());
      }
      for (;;)
      {
        return;
        if (BaseGmsClient.zzg(BaseGmsClient.this) != null) {
          BaseGmsClient.zzg(BaseGmsClient.this).onConnectionFailed(paramConnectionResult);
        }
      }
    }
  }
  
  protected final class PostInitCallback
    extends BaseGmsClient.zza
  {
    public final IBinder service;
    
    public PostInitCallback(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    {
      super(paramInt, paramBundle);
      this.service = paramIBinder;
    }
    
    protected final void handleServiceFailure(ConnectionResult paramConnectionResult)
    {
      if (BaseGmsClient.zzg(BaseGmsClient.this) != null) {
        BaseGmsClient.zzg(BaseGmsClient.this).onConnectionFailed(paramConnectionResult);
      }
      BaseGmsClient.this.onConnectionFailed(paramConnectionResult);
    }
    
    protected final boolean handleServiceSuccess()
    {
      bool1 = false;
      try
      {
        String str1 = this.service.getInterfaceDescriptor();
        if (BaseGmsClient.this.getServiceDescriptor().equals(str1)) {
          break label107;
        }
        String str2 = BaseGmsClient.this.getServiceDescriptor();
        Log.e("GmsClient", String.valueOf(str2).length() + 34 + String.valueOf(str1).length() + "service descriptor mismatch: " + str2 + " vs. " + str1);
        bool2 = bool1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("GmsClient", "service probably died");
          boolean bool2 = bool1;
          continue;
          Object localObject = BaseGmsClient.this.createServiceInterface(this.service);
          bool2 = bool1;
          if (localObject != null) {
            if (!BaseGmsClient.zza(BaseGmsClient.this, 2, 4, (IInterface)localObject))
            {
              bool2 = bool1;
              if (!BaseGmsClient.zza(BaseGmsClient.this, 3, 4, (IInterface)localObject)) {}
            }
            else
            {
              BaseGmsClient.zza(BaseGmsClient.this, null);
              localObject = BaseGmsClient.this.getConnectionHint();
              if (BaseGmsClient.zze(BaseGmsClient.this) != null) {
                BaseGmsClient.zze(BaseGmsClient.this).onConnected((Bundle)localObject);
              }
              bool2 = true;
            }
          }
        }
      }
      return bool2;
    }
  }
  
  protected final class PostServiceBindingCallback
    extends BaseGmsClient.zza
  {
    public PostServiceBindingCallback(int paramInt, Bundle paramBundle)
    {
      super(paramInt, paramBundle);
    }
    
    protected final void handleServiceFailure(ConnectionResult paramConnectionResult)
    {
      BaseGmsClient.this.mConnectionProgressReportCallbacks.onReportServiceBinding(paramConnectionResult);
      BaseGmsClient.this.onConnectionFailed(paramConnectionResult);
    }
    
    protected final boolean handleServiceSuccess()
    {
      BaseGmsClient.this.mConnectionProgressReportCallbacks.onReportServiceBinding(ConnectionResult.RESULT_SUCCESS);
      return true;
    }
  }
  
  public static abstract interface SignOutCallbacks
  {
    public abstract void onSignOutComplete();
  }
  
  private abstract class zza
    extends BaseGmsClient<T>.CallbackProxy<Boolean>
  {
    public final Bundle resolution;
    public final int statusCode;
    
    protected zza(int paramInt, Bundle paramBundle)
    {
      super(Boolean.valueOf(true));
      this.statusCode = paramInt;
      this.resolution = paramBundle;
    }
    
    protected void deliverCallback(Boolean paramBoolean)
    {
      Object localObject = null;
      if (paramBoolean == null) {
        BaseGmsClient.zza(BaseGmsClient.this, 1, null);
      }
      for (;;)
      {
        return;
        switch (this.statusCode)
        {
        default: 
          BaseGmsClient.zza(BaseGmsClient.this, 1, null);
          paramBoolean = (Boolean)localObject;
          if (this.resolution != null) {
            paramBoolean = (PendingIntent)this.resolution.getParcelable("pendingIntent");
          }
          handleServiceFailure(new ConnectionResult(this.statusCode, paramBoolean));
          break;
        case 0: 
          if (!handleServiceSuccess())
          {
            BaseGmsClient.zza(BaseGmsClient.this, 1, null);
            handleServiceFailure(new ConnectionResult(8, null));
          }
          break;
        }
      }
      BaseGmsClient.zza(BaseGmsClient.this, 1, null);
      throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
    }
    
    protected abstract void handleServiceFailure(ConnectionResult paramConnectionResult);
    
    protected abstract boolean handleServiceSuccess();
    
    protected void onDeliverCallbackFailed() {}
  }
  
  final class zzb
    extends Handler
  {
    public zzb(Looper paramLooper)
    {
      super();
    }
    
    private static void zza(Message paramMessage)
    {
      paramMessage = (BaseGmsClient.CallbackProxy)paramMessage.obj;
      paramMessage.onDeliverCallbackFailed();
      paramMessage.unregister();
    }
    
    private static boolean zzb(Message paramMessage)
    {
      boolean bool1 = true;
      boolean bool2 = bool1;
      if (paramMessage.what != 2)
      {
        bool2 = bool1;
        if (paramMessage.what != 1) {
          if (paramMessage.what != 7) {
            break label35;
          }
        }
      }
      label35:
      for (bool2 = bool1;; bool2 = false) {
        return bool2;
      }
    }
    
    public final void handleMessage(Message paramMessage)
    {
      PendingIntent localPendingIntent = null;
      if (BaseGmsClient.this.mDisconnectCount.get() != paramMessage.arg1) {
        if (zzb(paramMessage)) {
          zza(paramMessage);
        }
      }
      for (;;)
      {
        return;
        if (((paramMessage.what == 1) || (paramMessage.what == 7) || (paramMessage.what == 4) || (paramMessage.what == 5)) && (!BaseGmsClient.this.isConnecting()))
        {
          zza(paramMessage);
        }
        else if (paramMessage.what == 4)
        {
          BaseGmsClient.zza(BaseGmsClient.this, new ConnectionResult(paramMessage.arg2));
          if ((BaseGmsClient.zzb(BaseGmsClient.this)) && (!BaseGmsClient.zzc(BaseGmsClient.this)))
          {
            BaseGmsClient.zza(BaseGmsClient.this, 3, null);
          }
          else
          {
            if (BaseGmsClient.zzd(BaseGmsClient.this) != null) {}
            for (paramMessage = BaseGmsClient.zzd(BaseGmsClient.this);; paramMessage = new ConnectionResult(8))
            {
              BaseGmsClient.this.mConnectionProgressReportCallbacks.onReportServiceBinding(paramMessage);
              BaseGmsClient.this.onConnectionFailed(paramMessage);
              break;
            }
          }
        }
        else
        {
          if (paramMessage.what == 5)
          {
            if (BaseGmsClient.zzd(BaseGmsClient.this) != null) {}
            for (paramMessage = BaseGmsClient.zzd(BaseGmsClient.this);; paramMessage = new ConnectionResult(8))
            {
              BaseGmsClient.this.mConnectionProgressReportCallbacks.onReportServiceBinding(paramMessage);
              BaseGmsClient.this.onConnectionFailed(paramMessage);
              break;
            }
          }
          if (paramMessage.what == 3)
          {
            if ((paramMessage.obj instanceof PendingIntent)) {
              localPendingIntent = (PendingIntent)paramMessage.obj;
            }
            paramMessage = new ConnectionResult(paramMessage.arg2, localPendingIntent);
            BaseGmsClient.this.mConnectionProgressReportCallbacks.onReportServiceBinding(paramMessage);
            BaseGmsClient.this.onConnectionFailed(paramMessage);
          }
          else if (paramMessage.what == 6)
          {
            BaseGmsClient.zza(BaseGmsClient.this, 5, null);
            if (BaseGmsClient.zze(BaseGmsClient.this) != null) {
              BaseGmsClient.zze(BaseGmsClient.this).onConnectionSuspended(paramMessage.arg2);
            }
            BaseGmsClient.this.onConnectionSuspended(paramMessage.arg2);
            BaseGmsClient.zza(BaseGmsClient.this, 5, 1, null);
          }
          else if ((paramMessage.what == 2) && (!BaseGmsClient.this.isConnected()))
          {
            zza(paramMessage);
          }
          else if (zzb(paramMessage))
          {
            ((BaseGmsClient.CallbackProxy)paramMessage.obj).deliverCallback();
          }
          else
          {
            int i = paramMessage.what;
            Log.wtf("GmsClient", 45 + "Don't know how to handle message: " + i, new Exception());
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/BaseGmsClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */