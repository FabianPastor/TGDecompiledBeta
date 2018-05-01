package com.google.android.gms.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbdb
  implements Handler.Callback
{
  public static final Status zzaEc = new Status(4, "Sign-out occurred while this API call was in progress.");
  private static final Status zzaEd = new Status(4, "The user must be signed in to make this API call.");
  private static zzbdb zzaEf;
  private static final Object zzuF = new Object();
  private final Context mContext;
  private final Handler mHandler;
  private final GoogleApiAvailability zzaBd;
  private final Map<zzbat<?>, zzbdd<?>> zzaCB = new ConcurrentHashMap(5, 0.75F, 1);
  private long zzaDB = 120000L;
  private long zzaDC = 5000L;
  private long zzaEe = 10000L;
  private int zzaEg = -1;
  private final AtomicInteger zzaEh = new AtomicInteger(1);
  private final AtomicInteger zzaEi = new AtomicInteger(0);
  private zzbbw zzaEj = null;
  private final Set<zzbat<?>> zzaEk = new zza();
  private final Set<zzbat<?>> zzaEl = new zza();
  
  private zzbdb(Context paramContext, Looper paramLooper, GoogleApiAvailability paramGoogleApiAvailability)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramLooper, this);
    this.zzaBd = paramGoogleApiAvailability;
    this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
  }
  
  public static zzbdb zzay(Context paramContext)
  {
    synchronized (zzuF)
    {
      if (zzaEf == null)
      {
        Object localObject2 = new HandlerThread("GoogleApiHandler", 9);
        ((HandlerThread)localObject2).start();
        localObject2 = ((HandlerThread)localObject2).getLooper();
        zzaEf = new zzbdb(paramContext.getApplicationContext(), (Looper)localObject2, GoogleApiAvailability.getInstance());
      }
      paramContext = zzaEf;
      return paramContext;
    }
  }
  
  @WorkerThread
  private final void zzc(GoogleApi<?> paramGoogleApi)
  {
    zzbat localzzbat = paramGoogleApi.zzph();
    zzbdd localzzbdd2 = (zzbdd)this.zzaCB.get(localzzbat);
    zzbdd localzzbdd1 = localzzbdd2;
    if (localzzbdd2 == null)
    {
      localzzbdd1 = new zzbdd(this, paramGoogleApi);
      this.zzaCB.put(localzzbat, localzzbdd1);
    }
    if (localzzbdd1.zzmv()) {
      this.zzaEl.add(localzzbat);
    }
    localzzbdd1.connect();
  }
  
  public static zzbdb zzqk()
  {
    synchronized (zzuF)
    {
      zzbo.zzb(zzaEf, "Must guarantee manager is non-null before using getInstance");
      zzbdb localzzbdb = zzaEf;
      return localzzbdb;
    }
  }
  
  public static void zzql()
  {
    synchronized (zzuF)
    {
      if (zzaEf != null)
      {
        zzbdb localzzbdb = zzaEf;
        localzzbdb.zzaEi.incrementAndGet();
        localzzbdb.mHandler.sendMessageAtFrontOfQueue(localzzbdb.mHandler.obtainMessage(10));
      }
      return;
    }
  }
  
  @WorkerThread
  private final void zzqn()
  {
    Iterator localIterator = this.zzaEl.iterator();
    while (localIterator.hasNext())
    {
      zzbat localzzbat = (zzbat)localIterator.next();
      ((zzbdd)this.zzaCB.remove(localzzbat)).signOut();
    }
    this.zzaEl.clear();
  }
  
  @WorkerThread
  public final boolean handleMessage(Message paramMessage)
  {
    int i;
    Object localObject1;
    Object localObject2;
    zzbdd localzzbdd;
    switch (paramMessage.what)
    {
    default: 
      i = paramMessage.what;
      Log.w("GoogleApiManager", 31 + "Unknown message id: " + i);
      return false;
    case 1: 
      if (((Boolean)paramMessage.obj).booleanValue()) {}
      for (long l = 10000L;; l = 300000L)
      {
        this.zzaEe = l;
        this.mHandler.removeMessages(12);
        paramMessage = this.zzaCB.keySet().iterator();
        while (paramMessage.hasNext())
        {
          localObject1 = (zzbat)paramMessage.next();
          this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, localObject1), this.zzaEe);
        }
      }
    case 2: 
      paramMessage = (zzbav)paramMessage.obj;
      localObject1 = paramMessage.zzpt().iterator();
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (zzbat)((Iterator)localObject1).next();
        localzzbdd = (zzbdd)this.zzaCB.get(localObject2);
        if (localzzbdd != null) {
          break label286;
        }
        paramMessage.zza((zzbat)localObject2, new ConnectionResult(13));
      }
    case 3: 
    case 4: 
    case 8: 
    case 13: 
      for (;;)
      {
        return true;
        if (localzzbdd.isConnected())
        {
          paramMessage.zza((zzbat)localObject2, ConnectionResult.zzazX);
          break;
        }
        if (localzzbdd.zzqu() != null)
        {
          paramMessage.zza((zzbat)localObject2, localzzbdd.zzqu());
          break;
        }
        localzzbdd.zza(paramMessage);
        break;
        paramMessage = this.zzaCB.values().iterator();
        while (paramMessage.hasNext())
        {
          localObject1 = (zzbdd)paramMessage.next();
          ((zzbdd)localObject1).zzqt();
          ((zzbdd)localObject1).connect();
        }
        localObject2 = (zzbed)paramMessage.obj;
        localObject1 = (zzbdd)this.zzaCB.get(((zzbed)localObject2).zzaET.zzph());
        paramMessage = (Message)localObject1;
        if (localObject1 == null)
        {
          zzc(((zzbed)localObject2).zzaET);
          paramMessage = (zzbdd)this.zzaCB.get(((zzbed)localObject2).zzaET.zzph());
        }
        if ((paramMessage.zzmv()) && (this.zzaEi.get() != ((zzbed)localObject2).zzaES))
        {
          ((zzbed)localObject2).zzaER.zzp(zzaEc);
          paramMessage.signOut();
        }
        else
        {
          paramMessage.zza(((zzbed)localObject2).zzaER);
        }
      }
    case 5: 
      label286:
      i = paramMessage.arg1;
      localObject1 = (ConnectionResult)paramMessage.obj;
      localObject2 = this.zzaCB.values().iterator();
      do
      {
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        paramMessage = (zzbdd)((Iterator)localObject2).next();
      } while (paramMessage.getInstanceId() != i);
    }
    for (;;)
    {
      if (paramMessage != null)
      {
        localObject2 = String.valueOf(this.zzaBd.getErrorString(((ConnectionResult)localObject1).getErrorCode()));
        localObject1 = String.valueOf(((ConnectionResult)localObject1).getErrorMessage());
        paramMessage.zzt(new Status(17, String.valueOf(localObject2).length() + 69 + String.valueOf(localObject1).length() + "Error resolution was canceled by the user, original error message: " + (String)localObject2 + ": " + (String)localObject1));
        break;
      }
      Log.wtf("GoogleApiManager", 76 + "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
      break;
      if (!(this.mContext.getApplicationContext() instanceof Application)) {
        break;
      }
      zzbaw.zza((Application)this.mContext.getApplicationContext());
      zzbaw.zzpv().zza(new zzbdc(this));
      if (zzbaw.zzpv().zzab(true)) {
        break;
      }
      this.zzaEe = 300000L;
      break;
      zzc((GoogleApi)paramMessage.obj);
      break;
      if (!this.zzaCB.containsKey(paramMessage.obj)) {
        break;
      }
      ((zzbdd)this.zzaCB.get(paramMessage.obj)).resume();
      break;
      zzqn();
      break;
      if (!this.zzaCB.containsKey(paramMessage.obj)) {
        break;
      }
      ((zzbdd)this.zzaCB.get(paramMessage.obj)).zzqd();
      break;
      if (!this.zzaCB.containsKey(paramMessage.obj)) {
        break;
      }
      ((zzbdd)this.zzaCB.get(paramMessage.obj)).zzqx();
      break;
      paramMessage = null;
    }
  }
  
  final PendingIntent zza(zzbat<?> paramzzbat, int paramInt)
  {
    paramzzbat = (zzbdd)this.zzaCB.get(paramzzbat);
    if (paramzzbat == null) {
      return null;
    }
    paramzzbat = paramzzbat.zzqy();
    if (paramzzbat == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, paramInt, paramzzbat.zzmH(), 134217728);
  }
  
  public final <O extends Api.ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> paramGoogleApi, @NonNull zzbdy<?> paramzzbdy)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzbdy = new zzbar(paramzzbdy, localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(13, new zzbed(paramzzbdy, this.zzaEi.get(), paramGoogleApi)));
    return localTaskCompletionSource.getTask();
  }
  
  public final <O extends Api.ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> paramGoogleApi, @NonNull zzbee<Api.zzb, ?> paramzzbee, @NonNull zzbey<Api.zzb, ?> paramzzbey)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzbee = new zzbap(new zzbef(paramzzbee, paramzzbey), localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new zzbed(paramzzbee, this.zzaEi.get(), paramGoogleApi)));
    return localTaskCompletionSource.getTask();
  }
  
  public final Task<Void> zza(Iterable<? extends GoogleApi<?>> paramIterable)
  {
    zzbav localzzbav = new zzbav(paramIterable);
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      Object localObject = (GoogleApi)paramIterable.next();
      localObject = (zzbdd)this.zzaCB.get(((GoogleApi)localObject).zzph());
      if ((localObject == null) || (!((zzbdd)localObject).isConnected()))
      {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2, localzzbav));
        return localzzbav.getTask();
      }
    }
    localzzbav.zzpu();
    return localzzbav.getTask();
  }
  
  public final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!zzc(paramConnectionResult, paramInt)) {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramInt, 0, paramConnectionResult));
    }
  }
  
  public final <O extends Api.ApiOptions> void zza(GoogleApi<O> paramGoogleApi, int paramInt, zzbay<? extends Result, Api.zzb> paramzzbay)
  {
    paramzzbay = new zzbao(paramInt, paramzzbay);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbed(paramzzbay, this.zzaEi.get(), paramGoogleApi)));
  }
  
  public final <O extends Api.ApiOptions, TResult> void zza(GoogleApi<O> paramGoogleApi, int paramInt, zzbeq<Api.zzb, TResult> paramzzbeq, TaskCompletionSource<TResult> paramTaskCompletionSource, zzbem paramzzbem)
  {
    paramzzbeq = new zzbaq(paramInt, paramzzbeq, paramTaskCompletionSource, paramzzbem);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbed(paramzzbeq, this.zzaEi.get(), paramGoogleApi)));
  }
  
  public final void zza(@NonNull zzbbw paramzzbbw)
  {
    synchronized (zzuF)
    {
      if (this.zzaEj != paramzzbbw)
      {
        this.zzaEj = paramzzbbw;
        this.zzaEk.clear();
        this.zzaEk.addAll(paramzzbbw.zzpR());
      }
      return;
    }
  }
  
  public final void zzb(GoogleApi<?> paramGoogleApi)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(7, paramGoogleApi));
  }
  
  final void zzb(@NonNull zzbbw paramzzbbw)
  {
    synchronized (zzuF)
    {
      if (this.zzaEj == paramzzbbw)
      {
        this.zzaEj = null;
        this.zzaEk.clear();
      }
      return;
    }
  }
  
  final boolean zzc(ConnectionResult paramConnectionResult, int paramInt)
  {
    return this.zzaBd.zza(this.mContext, paramConnectionResult, paramInt);
  }
  
  final void zzpl()
  {
    this.zzaEi.incrementAndGet();
    this.mHandler.sendMessage(this.mHandler.obtainMessage(10));
  }
  
  public final void zzps()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
  }
  
  public final int zzqm()
  {
    return this.zzaEh.getAndIncrement();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */