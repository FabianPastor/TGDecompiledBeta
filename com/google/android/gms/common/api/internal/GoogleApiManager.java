package com.google.android.gms.common.api.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.UnsupportedApiCallException;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.SimpleClientAdapter;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;

public class GoogleApiManager
  implements Handler.Callback
{
  private static final Object lock = new Object();
  public static final Status zzjj = new Status(4, "Sign-out occurred while this API call was in progress.");
  private static final Status zzjk = new Status(4, "The user must be signed in to make this API call.");
  @GuardedBy("lock")
  private static GoogleApiManager zzjo;
  private final Handler handler;
  private long zzjl = 5000L;
  private long zzjm = 120000L;
  private long zzjn = 10000L;
  private final Context zzjp;
  private final GoogleApiAvailability zzjq;
  private final GoogleApiAvailabilityCache zzjr;
  private final AtomicInteger zzjs = new AtomicInteger(1);
  private final AtomicInteger zzjt = new AtomicInteger(0);
  private final Map<zzh<?>, zza<?>> zzju = new ConcurrentHashMap(5, 0.75F, 1);
  @GuardedBy("lock")
  private zzad zzjv = null;
  @GuardedBy("lock")
  private final Set<zzh<?>> zzjw = new ArraySet();
  private final Set<zzh<?>> zzjx = new ArraySet();
  
  private GoogleApiManager(Context paramContext, Looper paramLooper, GoogleApiAvailability paramGoogleApiAvailability)
  {
    this.zzjp = paramContext;
    this.handler = new Handler(paramLooper, this);
    this.zzjq = paramGoogleApiAvailability;
    this.zzjr = new GoogleApiAvailabilityCache(paramGoogleApiAvailability);
    this.handler.sendMessage(this.handler.obtainMessage(6));
  }
  
  public static GoogleApiManager zzb(Context paramContext)
  {
    synchronized (lock)
    {
      if (zzjo == null)
      {
        Object localObject2 = new android/os/HandlerThread;
        ((HandlerThread)localObject2).<init>("GoogleApiHandler", 9);
        ((HandlerThread)localObject2).start();
        Looper localLooper = ((HandlerThread)localObject2).getLooper();
        localObject2 = new com/google/android/gms/common/api/internal/GoogleApiManager;
        ((GoogleApiManager)localObject2).<init>(paramContext.getApplicationContext(), localLooper, GoogleApiAvailability.getInstance());
        zzjo = (GoogleApiManager)localObject2;
      }
      paramContext = zzjo;
      return paramContext;
    }
  }
  
  private final void zzb(GoogleApi<?> paramGoogleApi)
  {
    zzh localzzh = paramGoogleApi.zzm();
    zza localzza1 = (zza)this.zzju.get(localzzh);
    zza localzza2 = localzza1;
    if (localzza1 == null)
    {
      localzza2 = new zza(paramGoogleApi);
      this.zzju.put(localzzh, localzza2);
    }
    if (localzza2.requiresSignIn()) {
      this.zzjx.add(localzzh);
    }
    localzza2.connect();
  }
  
  public static GoogleApiManager zzbf()
  {
    synchronized (lock)
    {
      Preconditions.checkNotNull(zzjo, "Must guarantee manager is non-null before using getInstance");
      GoogleApiManager localGoogleApiManager = zzjo;
      return localGoogleApiManager;
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    int i;
    boolean bool;
    Object localObject1;
    Object localObject2;
    label242:
    zza localzza;
    switch (paramMessage.what)
    {
    default: 
      i = paramMessage.what;
      Log.w("GoogleApiManager", 31 + "Unknown message id: " + i);
      bool = false;
      return bool;
    case 1: 
      if (((Boolean)paramMessage.obj).booleanValue()) {}
      for (long l = 10000L;; l = 300000L)
      {
        this.zzjn = l;
        this.handler.removeMessages(12);
        localObject1 = this.zzju.keySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          paramMessage = (zzh)((Iterator)localObject1).next();
          this.handler.sendMessageDelayed(this.handler.obtainMessage(12, paramMessage), this.zzjn);
        }
      }
    case 2: 
      paramMessage = (zzj)paramMessage.obj;
      localObject2 = paramMessage.zzs().iterator();
      if (((Iterator)localObject2).hasNext())
      {
        localObject1 = (zzh)((Iterator)localObject2).next();
        localzza = (zza)this.zzju.get(localObject1);
        if (localzza != null) {
          break label306;
        }
        paramMessage.zza((zzh)localObject1, new ConnectionResult(13), null);
      }
    case 3: 
    case 4: 
    case 8: 
    case 13: 
      for (;;)
      {
        bool = true;
        break;
        if (localzza.isConnected())
        {
          paramMessage.zza((zzh)localObject1, ConnectionResult.RESULT_SUCCESS, localzza.zzae().getEndpointPackageName());
          break label242;
        }
        if (localzza.zzbp() != null)
        {
          paramMessage.zza((zzh)localObject1, localzza.zzbp(), null);
          break label242;
        }
        localzza.zza(paramMessage);
        break label242;
        paramMessage = this.zzju.values().iterator();
        while (paramMessage.hasNext())
        {
          localObject1 = (zza)paramMessage.next();
          ((zza)localObject1).zzbo();
          ((zza)localObject1).connect();
        }
        localObject2 = (zzbu)paramMessage.obj;
        localObject1 = (zza)this.zzju.get(((zzbu)localObject2).zzlr.zzm());
        paramMessage = (Message)localObject1;
        if (localObject1 == null)
        {
          zzb(((zzbu)localObject2).zzlr);
          paramMessage = (zza)this.zzju.get(((zzbu)localObject2).zzlr.zzm());
        }
        if ((paramMessage.requiresSignIn()) && (this.zzjt.get() != ((zzbu)localObject2).zzlq))
        {
          ((zzbu)localObject2).zzlp.zza(zzjj);
          paramMessage.zzbm();
        }
        else
        {
          paramMessage.zza(((zzbu)localObject2).zzlp);
        }
      }
    case 5: 
      label306:
      i = paramMessage.arg1;
      localObject1 = (ConnectionResult)paramMessage.obj;
      localObject2 = this.zzju.values().iterator();
      do
      {
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        paramMessage = (zza)((Iterator)localObject2).next();
      } while (paramMessage.getInstanceId() != i);
    }
    for (;;)
    {
      if (paramMessage != null)
      {
        localObject2 = this.zzjq.getErrorString(((ConnectionResult)localObject1).getErrorCode());
        localObject1 = ((ConnectionResult)localObject1).getErrorMessage();
        paramMessage.zzc(new Status(17, String.valueOf(localObject2).length() + 69 + String.valueOf(localObject1).length() + "Error resolution was canceled by the user, original error message: " + (String)localObject2 + ": " + (String)localObject1));
        break;
      }
      Log.wtf("GoogleApiManager", 76 + "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
      break;
      if ((!PlatformVersion.isAtLeastIceCreamSandwich()) || (!(this.zzjp.getApplicationContext() instanceof Application))) {
        break;
      }
      BackgroundDetector.initialize((Application)this.zzjp.getApplicationContext());
      BackgroundDetector.getInstance().addListener(new zzbh(this));
      if (BackgroundDetector.getInstance().readCurrentStateIfPossible(true)) {
        break;
      }
      this.zzjn = 300000L;
      break;
      zzb((GoogleApi)paramMessage.obj);
      break;
      if (!this.zzju.containsKey(paramMessage.obj)) {
        break;
      }
      ((zza)this.zzju.get(paramMessage.obj)).resume();
      break;
      localObject1 = this.zzjx.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        paramMessage = (zzh)((Iterator)localObject1).next();
        ((zza)this.zzju.remove(paramMessage)).zzbm();
      }
      this.zzjx.clear();
      break;
      if (!this.zzju.containsKey(paramMessage.obj)) {
        break;
      }
      ((zza)this.zzju.get(paramMessage.obj)).zzay();
      break;
      if (!this.zzju.containsKey(paramMessage.obj)) {
        break;
      }
      ((zza)this.zzju.get(paramMessage.obj)).zzbs();
      break;
      localObject1 = (zzae)paramMessage.obj;
      paramMessage = ((zzae)localObject1).zzm();
      if (!this.zzju.containsKey(paramMessage))
      {
        ((zzae)localObject1).zzao().setResult(Boolean.valueOf(false));
        break;
      }
      bool = zza.zza((zza)this.zzju.get(paramMessage), false);
      ((zzae)localObject1).zzao().setResult(Boolean.valueOf(bool));
      break;
      paramMessage = (zzb)paramMessage.obj;
      if (!this.zzju.containsKey(zzb.zzc(paramMessage))) {
        break;
      }
      zza.zza((zza)this.zzju.get(zzb.zzc(paramMessage)), paramMessage);
      break;
      paramMessage = (zzb)paramMessage.obj;
      if (!this.zzju.containsKey(zzb.zzc(paramMessage))) {
        break;
      }
      zza.zzb((zza)this.zzju.get(zzb.zzc(paramMessage)), paramMessage);
      break;
      paramMessage = null;
    }
  }
  
  final PendingIntent zza(zzh<?> paramzzh, int paramInt)
  {
    paramzzh = (zza)this.zzju.get(paramzzh);
    if (paramzzh == null) {
      paramzzh = null;
    }
    for (;;)
    {
      return paramzzh;
      paramzzh = paramzzh.zzbt();
      if (paramzzh == null) {
        paramzzh = null;
      } else {
        paramzzh = PendingIntent.getActivity(this.zzjp, paramInt, paramzzh.getSignInIntent(), 134217728);
      }
    }
  }
  
  public final Task<Map<zzh<?>, String>> zza(Iterable<? extends GoogleApi<?>> paramIterable)
  {
    paramIterable = new zzj(paramIterable);
    this.handler.sendMessage(this.handler.obtainMessage(2, paramIterable));
    return paramIterable.getTask();
  }
  
  public final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!zzc(paramConnectionResult, paramInt)) {
      this.handler.sendMessage(this.handler.obtainMessage(5, paramInt, 0, paramConnectionResult));
    }
  }
  
  public final void zza(GoogleApi<?> paramGoogleApi)
  {
    this.handler.sendMessage(this.handler.obtainMessage(7, paramGoogleApi));
  }
  
  public final <O extends Api.ApiOptions> void zza(GoogleApi<O> paramGoogleApi, int paramInt, BaseImplementation.ApiMethodImpl<? extends Result, Api.AnyClient> paramApiMethodImpl)
  {
    paramApiMethodImpl = new zzd(paramInt, paramApiMethodImpl);
    this.handler.sendMessage(this.handler.obtainMessage(4, new zzbu(paramApiMethodImpl, this.zzjt.get(), paramGoogleApi)));
  }
  
  public final void zza(zzad paramzzad)
  {
    synchronized (lock)
    {
      if (this.zzjv != paramzzad)
      {
        this.zzjv = paramzzad;
        this.zzjw.clear();
      }
      this.zzjw.addAll(paramzzad.zzam());
      return;
    }
  }
  
  final void zzb(zzad paramzzad)
  {
    synchronized (lock)
    {
      if (this.zzjv == paramzzad)
      {
        this.zzjv = null;
        this.zzjw.clear();
      }
      return;
    }
  }
  
  public final int zzbg()
  {
    return this.zzjs.getAndIncrement();
  }
  
  final boolean zzc(ConnectionResult paramConnectionResult, int paramInt)
  {
    return this.zzjq.showWrappedErrorNotification(this.zzjp, paramConnectionResult, paramInt);
  }
  
  public final void zzr()
  {
    this.handler.sendMessage(this.handler.obtainMessage(3));
  }
  
  public final class zza<O extends Api.ApiOptions>
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zzq
  {
    private final zzh<O> zzhc;
    private final Queue<zzb> zzjz = new LinkedList();
    private final Api.Client zzka;
    private final Api.AnyClient zzkb;
    private final zzaa zzkc;
    private final Set<zzj> zzkd = new HashSet();
    private final Map<ListenerHolder.ListenerKey<?>, zzbv> zzke = new HashMap();
    private final int zzkf;
    private final zzby zzkg;
    private boolean zzkh;
    private final List<GoogleApiManager.zzb> zzki = new ArrayList();
    private ConnectionResult zzkj = null;
    
    public zza()
    {
      Object localObject;
      this.zzka = ((GoogleApi)localObject).zza(GoogleApiManager.zza(GoogleApiManager.this).getLooper(), this);
      if ((this.zzka instanceof SimpleClientAdapter))
      {
        this.zzkb = ((SimpleClientAdapter)this.zzka).getClient();
        this.zzhc = ((GoogleApi)localObject).zzm();
        this.zzkc = new zzaa();
        this.zzkf = ((GoogleApi)localObject).getInstanceId();
        if (!this.zzka.requiresSignIn()) {
          break label165;
        }
      }
      label165:
      for (this.zzkg = ((GoogleApi)localObject).zza(GoogleApiManager.zzb(GoogleApiManager.this), GoogleApiManager.zza(GoogleApiManager.this));; this.zzkg = null)
      {
        return;
        this.zzkb = this.zzka;
        break;
      }
    }
    
    private final void zza(GoogleApiManager.zzb paramzzb)
    {
      if (!this.zzki.contains(paramzzb)) {}
      for (;;)
      {
        return;
        if (!this.zzkh) {
          if (!this.zzka.isConnected()) {
            connect();
          } else {
            zzbl();
          }
        }
      }
    }
    
    private final void zzb(GoogleApiManager.zzb paramzzb)
    {
      if (this.zzki.remove(paramzzb))
      {
        GoogleApiManager.zza(GoogleApiManager.this).removeMessages(15, paramzzb);
        GoogleApiManager.zza(GoogleApiManager.this).removeMessages(16, paramzzb);
        paramzzb = GoogleApiManager.zzb.zzd(paramzzb);
        ArrayList localArrayList = new ArrayList(this.zzjz.size());
        Object localObject = this.zzjz.iterator();
        while (((Iterator)localObject).hasNext())
        {
          zzb localzzb = (zzb)((Iterator)localObject).next();
          if ((localzzb instanceof zzf))
          {
            Feature[] arrayOfFeature = ((zzf)localzzb).getRequiredFeatures();
            if ((arrayOfFeature != null) && (ArrayUtils.contains(arrayOfFeature, paramzzb))) {
              localArrayList.add(localzzb);
            }
          }
        }
        localArrayList = (ArrayList)localArrayList;
        int i = localArrayList.size();
        int j = 0;
        while (j < i)
        {
          localObject = localArrayList.get(j);
          j++;
          localObject = (zzb)localObject;
          this.zzjz.remove(localObject);
          ((zzb)localObject).zza(new UnsupportedApiCallException(paramzzb));
        }
      }
    }
    
    private final boolean zzb(zzb paramzzb)
    {
      boolean bool;
      if (!(paramzzb instanceof zzf))
      {
        zzc(paramzzb);
        bool = true;
      }
      for (;;)
      {
        return bool;
        zzf localzzf = (zzf)paramzzb;
        Feature[] arrayOfFeature = localzzf.getRequiredFeatures();
        if ((arrayOfFeature == null) || (arrayOfFeature.length == 0))
        {
          zzc(paramzzb);
          bool = true;
        }
        else
        {
          Object localObject1 = this.zzka.getAvailableFeatures();
          Object localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new Feature[0];
          }
          localObject1 = new ArrayMap(localObject2.length);
          int i = localObject2.length;
          for (int j = 0; j < i; j++)
          {
            Object localObject3 = localObject2[j];
            ((Map)localObject1).put(((Feature)localObject3).getName(), Long.valueOf(((Feature)localObject3).getVersion()));
          }
          i = arrayOfFeature.length;
          for (j = 0;; j++)
          {
            if (j >= i) {
              break label455;
            }
            localObject2 = arrayOfFeature[j];
            if ((!((Map)localObject1).containsKey(((Feature)localObject2).getName())) || (((Long)((Map)localObject1).get(((Feature)localObject2).getName())).longValue() < ((Feature)localObject2).getVersion()))
            {
              if (localzzf.shouldAutoResolveMissingFeatures())
              {
                paramzzb = new GoogleApiManager.zzb(this.zzhc, (Feature)localObject2, null);
                j = this.zzki.indexOf(paramzzb);
                if (j >= 0)
                {
                  paramzzb = (GoogleApiManager.zzb)this.zzki.get(j);
                  GoogleApiManager.zza(GoogleApiManager.this).removeMessages(15, paramzzb);
                  GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 15, paramzzb), GoogleApiManager.zzc(GoogleApiManager.this));
                }
              }
              for (;;)
              {
                bool = false;
                break;
                this.zzki.add(paramzzb);
                GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 15, paramzzb), GoogleApiManager.zzc(GoogleApiManager.this));
                GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 16, paramzzb), GoogleApiManager.zzd(GoogleApiManager.this));
                paramzzb = new ConnectionResult(2, null);
                if (!zzh(paramzzb))
                {
                  GoogleApiManager.this.zzc(paramzzb, this.zzkf);
                  continue;
                  localzzf.zza(new UnsupportedApiCallException((Feature)localObject2));
                }
              }
            }
            this.zzki.remove(new GoogleApiManager.zzb(this.zzhc, (Feature)localObject2, null));
          }
          label455:
          zzc(paramzzb);
          bool = true;
        }
      }
    }
    
    private final boolean zzb(boolean paramBoolean)
    {
      boolean bool1 = false;
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      boolean bool2 = bool1;
      if (this.zzka.isConnected())
      {
        bool2 = bool1;
        if (this.zzke.size() == 0)
        {
          if (!this.zzkc.zzaj()) {
            break label64;
          }
          bool2 = bool1;
          if (paramBoolean) {
            zzbr();
          }
        }
      }
      for (bool2 = bool1;; bool2 = true)
      {
        return bool2;
        label64:
        this.zzka.disconnect();
      }
    }
    
    private final void zzbj()
    {
      zzbo();
      zzi(ConnectionResult.RESULT_SUCCESS);
      zzbq();
      Iterator localIterator = this.zzke.values().iterator();
      for (;;)
      {
        Object localObject;
        if (localIterator.hasNext()) {
          localObject = (zzbv)localIterator.next();
        }
        try
        {
          localObject = ((zzbv)localObject).zzlt;
          Api.AnyClient localAnyClient = this.zzkb;
          TaskCompletionSource localTaskCompletionSource = new com/google/android/gms/tasks/TaskCompletionSource;
          localTaskCompletionSource.<init>();
          ((RegisterListenerMethod)localObject).registerListener(localAnyClient, localTaskCompletionSource);
        }
        catch (DeadObjectException localDeadObjectException)
        {
          onConnectionSuspended(1);
          this.zzka.disconnect();
          zzbl();
          zzbr();
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    }
    
    private final void zzbk()
    {
      zzbo();
      this.zzkh = true;
      this.zzkc.zzal();
      GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 9, this.zzhc), GoogleApiManager.zzc(GoogleApiManager.this));
      GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 11, this.zzhc), GoogleApiManager.zzd(GoogleApiManager.this));
      GoogleApiManager.zze(GoogleApiManager.this).flush();
    }
    
    private final void zzbl()
    {
      ArrayList localArrayList = (ArrayList)new ArrayList(this.zzjz);
      int i = localArrayList.size();
      int j = 0;
      while (j < i)
      {
        Object localObject = localArrayList.get(j);
        int k = j + 1;
        localObject = (zzb)localObject;
        if (!this.zzka.isConnected()) {
          break;
        }
        j = k;
        if (zzb((zzb)localObject))
        {
          this.zzjz.remove(localObject);
          j = k;
        }
      }
    }
    
    private final void zzbq()
    {
      if (this.zzkh)
      {
        GoogleApiManager.zza(GoogleApiManager.this).removeMessages(11, this.zzhc);
        GoogleApiManager.zza(GoogleApiManager.this).removeMessages(9, this.zzhc);
        this.zzkh = false;
      }
    }
    
    private final void zzbr()
    {
      GoogleApiManager.zza(GoogleApiManager.this).removeMessages(12, this.zzhc);
      GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(GoogleApiManager.zza(GoogleApiManager.this).obtainMessage(12, this.zzhc), GoogleApiManager.zzi(GoogleApiManager.this));
    }
    
    private final void zzc(zzb paramzzb)
    {
      paramzzb.zza(this.zzkc, requiresSignIn());
      try
      {
        paramzzb.zza(this);
        return;
      }
      catch (DeadObjectException paramzzb)
      {
        for (;;)
        {
          onConnectionSuspended(1);
          this.zzka.disconnect();
        }
      }
    }
    
    private final boolean zzh(ConnectionResult paramConnectionResult)
    {
      synchronized ()
      {
        if ((GoogleApiManager.zzf(GoogleApiManager.this) != null) && (GoogleApiManager.zzg(GoogleApiManager.this).contains(this.zzhc)))
        {
          GoogleApiManager.zzf(GoogleApiManager.this).zzb(paramConnectionResult, this.zzkf);
          bool = true;
          return bool;
        }
        boolean bool = false;
      }
    }
    
    private final void zzi(ConnectionResult paramConnectionResult)
    {
      Iterator localIterator = this.zzkd.iterator();
      while (localIterator.hasNext())
      {
        zzj localzzj = (zzj)localIterator.next();
        String str = null;
        if (Objects.equal(paramConnectionResult, ConnectionResult.RESULT_SUCCESS)) {
          str = this.zzka.getEndpointPackageName();
        }
        localzzj.zza(this.zzhc, paramConnectionResult, str);
      }
      this.zzkd.clear();
    }
    
    public final void connect()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      if ((this.zzka.isConnected()) || (this.zzka.isConnecting())) {}
      for (;;)
      {
        return;
        int i = GoogleApiManager.zze(GoogleApiManager.this).getClientAvailability(GoogleApiManager.zzb(GoogleApiManager.this), this.zzka);
        if (i != 0)
        {
          onConnectionFailed(new ConnectionResult(i, null));
        }
        else
        {
          GoogleApiManager.zzc localzzc = new GoogleApiManager.zzc(GoogleApiManager.this, this.zzka, this.zzhc);
          if (this.zzka.requiresSignIn()) {
            this.zzkg.zza(localzzc);
          }
          this.zzka.connect(localzzc);
        }
      }
    }
    
    public final int getInstanceId()
    {
      return this.zzkf;
    }
    
    final boolean isConnected()
    {
      return this.zzka.isConnected();
    }
    
    public final void onConnected(Bundle paramBundle)
    {
      if (Looper.myLooper() == GoogleApiManager.zza(GoogleApiManager.this).getLooper()) {
        zzbj();
      }
      for (;;)
      {
        return;
        GoogleApiManager.zza(GoogleApiManager.this).post(new zzbi(this));
      }
    }
    
    public final void onConnectionFailed(ConnectionResult paramConnectionResult)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      if (this.zzkg != null) {
        this.zzkg.zzbz();
      }
      zzbo();
      GoogleApiManager.zze(GoogleApiManager.this).flush();
      zzi(paramConnectionResult);
      if (paramConnectionResult.getErrorCode() == 4) {
        zzc(GoogleApiManager.zzbi());
      }
      for (;;)
      {
        return;
        if (this.zzjz.isEmpty())
        {
          this.zzkj = paramConnectionResult;
        }
        else if ((!zzh(paramConnectionResult)) && (!GoogleApiManager.this.zzc(paramConnectionResult, this.zzkf)))
        {
          if (paramConnectionResult.getErrorCode() == 18) {
            this.zzkh = true;
          }
          if (this.zzkh)
          {
            GoogleApiManager.zza(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.zza(GoogleApiManager.this), 9, this.zzhc), GoogleApiManager.zzc(GoogleApiManager.this));
          }
          else
          {
            paramConnectionResult = this.zzhc.zzq();
            zzc(new Status(17, String.valueOf(paramConnectionResult).length() + 38 + "API: " + paramConnectionResult + " is not available on this device."));
          }
        }
      }
    }
    
    public final void onConnectionSuspended(int paramInt)
    {
      if (Looper.myLooper() == GoogleApiManager.zza(GoogleApiManager.this).getLooper()) {
        zzbk();
      }
      for (;;)
      {
        return;
        GoogleApiManager.zza(GoogleApiManager.this).post(new zzbj(this));
      }
    }
    
    public final boolean requiresSignIn()
    {
      return this.zzka.requiresSignIn();
    }
    
    public final void resume()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      if (this.zzkh) {
        connect();
      }
    }
    
    public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
    {
      if (Looper.myLooper() == GoogleApiManager.zza(GoogleApiManager.this).getLooper()) {
        onConnectionFailed(paramConnectionResult);
      }
      for (;;)
      {
        return;
        GoogleApiManager.zza(GoogleApiManager.this).post(new zzbk(this, paramConnectionResult));
      }
    }
    
    public final void zza(zzb paramzzb)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      if (this.zzka.isConnected()) {
        if (zzb(paramzzb)) {
          zzbr();
        }
      }
      for (;;)
      {
        return;
        this.zzjz.add(paramzzb);
        continue;
        this.zzjz.add(paramzzb);
        if ((this.zzkj != null) && (this.zzkj.hasResolution())) {
          onConnectionFailed(this.zzkj);
        } else {
          connect();
        }
      }
    }
    
    public final void zza(zzj paramzzj)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      this.zzkd.add(paramzzj);
    }
    
    public final Api.Client zzae()
    {
      return this.zzka;
    }
    
    public final void zzay()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      if (this.zzkh)
      {
        zzbq();
        if (GoogleApiManager.zzh(GoogleApiManager.this).isGooglePlayServicesAvailable(GoogleApiManager.zzb(GoogleApiManager.this)) != 18) {
          break label71;
        }
      }
      label71:
      for (Status localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");; localStatus = new Status(8, "API failed to connect while resuming due to an unknown error."))
      {
        zzc(localStatus);
        this.zzka.disconnect();
        return;
      }
    }
    
    public final void zzbm()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      zzc(GoogleApiManager.zzjj);
      this.zzkc.zzak();
      ListenerHolder.ListenerKey[] arrayOfListenerKey = (ListenerHolder.ListenerKey[])this.zzke.keySet().toArray(new ListenerHolder.ListenerKey[this.zzke.size()]);
      int i = arrayOfListenerKey.length;
      for (int j = 0; j < i; j++) {
        zza(new zzg(arrayOfListenerKey[j], new TaskCompletionSource()));
      }
      zzi(new ConnectionResult(4));
      if (this.zzka.isConnected()) {
        this.zzka.onUserSignOut(new zzbl(this));
      }
    }
    
    public final Map<ListenerHolder.ListenerKey<?>, zzbv> zzbn()
    {
      return this.zzke;
    }
    
    public final void zzbo()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      this.zzkj = null;
    }
    
    public final ConnectionResult zzbp()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      return this.zzkj;
    }
    
    public final boolean zzbs()
    {
      return zzb(true);
    }
    
    final SignInClient zzbt()
    {
      if (this.zzkg == null) {}
      for (SignInClient localSignInClient = null;; localSignInClient = this.zzkg.zzbt()) {
        return localSignInClient;
      }
    }
    
    public final void zzc(Status paramStatus)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      Iterator localIterator = this.zzjz.iterator();
      while (localIterator.hasNext()) {
        ((zzb)localIterator.next()).zza(paramStatus);
      }
      this.zzjz.clear();
    }
    
    public final void zzg(ConnectionResult paramConnectionResult)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.zza(GoogleApiManager.this));
      this.zzka.disconnect();
      onConnectionFailed(paramConnectionResult);
    }
  }
  
  private static final class zzb
  {
    private final Feature zzdr;
    private final zzh<?> zzkn;
    
    private zzb(zzh<?> paramzzh, Feature paramFeature)
    {
      this.zzkn = paramzzh;
      this.zzdr = paramFeature;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool1 = false;
      boolean bool2 = bool1;
      if (paramObject != null)
      {
        bool2 = bool1;
        if ((paramObject instanceof zzb))
        {
          paramObject = (zzb)paramObject;
          bool2 = bool1;
          if (Objects.equal(this.zzkn, ((zzb)paramObject).zzkn))
          {
            bool2 = bool1;
            if (Objects.equal(this.zzdr, ((zzb)paramObject).zzdr)) {
              bool2 = true;
            }
          }
        }
      }
      return bool2;
    }
    
    public final int hashCode()
    {
      return Objects.hashCode(new Object[] { this.zzkn, this.zzdr });
    }
    
    public final String toString()
    {
      return Objects.toStringHelper(this).add("key", this.zzkn).add("feature", this.zzdr).toString();
    }
  }
  
  private final class zzc
    implements zzcb, BaseGmsClient.ConnectionProgressReportCallbacks
  {
    private final zzh<?> zzhc;
    private final Api.Client zzka;
    private IAccountAccessor zzko = null;
    private Set<Scope> zzkp = null;
    private boolean zzkq = false;
    
    public zzc(zzh<?> paramzzh)
    {
      this.zzka = paramzzh;
      zzh localzzh;
      this.zzhc = localzzh;
    }
    
    private final void zzbu()
    {
      if ((this.zzkq) && (this.zzko != null)) {
        this.zzka.getRemoteService(this.zzko, this.zzkp);
      }
    }
    
    public final void onReportServiceBinding(ConnectionResult paramConnectionResult)
    {
      GoogleApiManager.zza(GoogleApiManager.this).post(new zzbn(this, paramConnectionResult));
    }
    
    public final void zza(IAccountAccessor paramIAccountAccessor, Set<Scope> paramSet)
    {
      if ((paramIAccountAccessor == null) || (paramSet == null))
      {
        Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
        zzg(new ConnectionResult(4));
      }
      for (;;)
      {
        return;
        this.zzko = paramIAccountAccessor;
        this.zzkp = paramSet;
        zzbu();
      }
    }
    
    public final void zzg(ConnectionResult paramConnectionResult)
    {
      ((GoogleApiManager.zza)GoogleApiManager.zzj(GoogleApiManager.this).get(this.zzhc)).zzg(paramConnectionResult);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/GoogleApiManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */