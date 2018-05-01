package com.google.android.gms.internal;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzf.zzf;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.util.zza;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class zzaax
  implements Handler.Callback
{
  public static final Status zzaCn = new Status(4, "Sign-out occurred while this API call was in progress.");
  private static final Status zzaCo = new Status(4, "The user must be signed in to make this API call.");
  private static zzaax zzaCq;
  private static final Object zztX = new Object();
  private final Context mContext;
  private final Handler mHandler;
  private final Map<zzzz<?>, zza<?>> zzaAM = new ConcurrentHashMap(5, 0.75F, 1);
  private long zzaBM = 120000L;
  private long zzaBN = 5000L;
  private long zzaCp = 10000L;
  private int zzaCr = -1;
  private final AtomicInteger zzaCs = new AtomicInteger(1);
  private final AtomicInteger zzaCt = new AtomicInteger(0);
  private zzaam zzaCu = null;
  private final Set<zzzz<?>> zzaCv = new zza();
  private final Set<zzzz<?>> zzaCw = new zza();
  private final GoogleApiAvailability zzazn;
  
  private zzaax(Context paramContext, Looper paramLooper, GoogleApiAvailability paramGoogleApiAvailability)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramLooper, this);
    this.zzazn = paramGoogleApiAvailability;
    this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
  }
  
  @WorkerThread
  private void zza(int paramInt, ConnectionResult paramConnectionResult)
  {
    Object localObject = this.zzaAM.values().iterator();
    zza localzza;
    do
    {
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
      localzza = (zza)((Iterator)localObject).next();
    } while (localzza.getInstanceId() != paramInt);
    for (;;)
    {
      if (localzza != null)
      {
        localObject = String.valueOf(this.zzazn.getErrorString(paramConnectionResult.getErrorCode()));
        paramConnectionResult = String.valueOf(paramConnectionResult.getErrorMessage());
        localzza.zzD(new Status(17, String.valueOf(localObject).length() + 69 + String.valueOf(paramConnectionResult).length() + "Error resolution was canceled by the user, original error message: " + (String)localObject + ": " + paramConnectionResult));
        return;
      }
      Log.wtf("GoogleApiManager", 76 + "Could not find API instance " + paramInt + " while trying to fail enqueued calls.", new Exception());
      return;
      localzza = null;
    }
  }
  
  @WorkerThread
  private void zza(zzaab paramzzaab)
  {
    Iterator localIterator = paramzzaab.zzvz().iterator();
    for (;;)
    {
      zzzz localzzzz;
      zza localzza;
      if (localIterator.hasNext())
      {
        localzzzz = (zzzz)localIterator.next();
        localzza = (zza)this.zzaAM.get(localzzzz);
        if (localzza == null) {
          paramzzaab.zza(localzzzz, new ConnectionResult(13));
        }
      }
      else
      {
        return;
      }
      if (localzza.isConnected()) {
        paramzzaab.zza(localzzzz, ConnectionResult.zzayj);
      } else if (localzza.zzwK() != null) {
        paramzzaab.zza(localzzzz, localzza.zzwK());
      } else {
        localzza.zzb(paramzzaab);
      }
    }
  }
  
  @WorkerThread
  private void zza(zzabl paramzzabl)
  {
    zza localzza2 = (zza)this.zzaAM.get(paramzzabl.zzaDe.getApiKey());
    zza localzza1 = localzza2;
    if (localzza2 == null)
    {
      zzc(paramzzabl.zzaDe);
      localzza1 = (zza)this.zzaAM.get(paramzzabl.zzaDe.getApiKey());
    }
    if ((localzza1.zzrd()) && (this.zzaCt.get() != paramzzabl.zzaDd))
    {
      paramzzabl.zzaDc.zzz(zzaCn);
      localzza1.signOut();
      return;
    }
    localzza1.zza(paramzzabl.zzaDc);
  }
  
  public static zzaax zzaP(Context paramContext)
  {
    synchronized (zztX)
    {
      if (zzaCq == null)
      {
        Looper localLooper = zzwy();
        zzaCq = new zzaax(paramContext.getApplicationContext(), localLooper, GoogleApiAvailability.getInstance());
      }
      paramContext = zzaCq;
      return paramContext;
    }
  }
  
  @WorkerThread
  private void zzau(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (long l = 10000L;; l = 300000L)
    {
      this.zzaCp = l;
      this.mHandler.removeMessages(12);
      Iterator localIterator = this.zzaAM.keySet().iterator();
      while (localIterator.hasNext())
      {
        zzzz localzzzz = (zzzz)localIterator.next();
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, localzzzz), this.zzaCp);
      }
    }
  }
  
  @WorkerThread
  private void zzc(zzc<?> paramzzc)
  {
    zzzz localzzzz = paramzzc.getApiKey();
    zza localzza2 = (zza)this.zzaAM.get(localzzzz);
    zza localzza1 = localzza2;
    if (localzza2 == null)
    {
      localzza1 = new zza(paramzzc);
      this.zzaAM.put(localzzzz, localzza1);
    }
    if (localzza1.zzrd()) {
      this.zzaCw.add(localzzzz);
    }
    localzza1.connect();
  }
  
  @WorkerThread
  private void zzwA()
  {
    zzt.zzzg();
    if ((this.mContext.getApplicationContext() instanceof Application))
    {
      zzaac.zza((Application)this.mContext.getApplicationContext());
      zzaac.zzvB().zza(new zzaac.zza()
      {
        public void zzas(boolean paramAnonymousBoolean)
        {
          zzaax.zza(zzaax.this).sendMessage(zzaax.zza(zzaax.this).obtainMessage(1, Boolean.valueOf(paramAnonymousBoolean)));
        }
      });
      if (!zzaac.zzvB().zzar(true)) {
        this.zzaCp = 300000L;
      }
    }
  }
  
  @WorkerThread
  private void zzwB()
  {
    Iterator localIterator = this.zzaAM.values().iterator();
    while (localIterator.hasNext())
    {
      zza localzza = (zza)localIterator.next();
      localzza.zzwJ();
      localzza.connect();
    }
  }
  
  @WorkerThread
  private void zzwC()
  {
    Iterator localIterator = this.zzaCw.iterator();
    while (localIterator.hasNext())
    {
      zzzz localzzzz = (zzzz)localIterator.next();
      ((zza)this.zzaAM.remove(localzzzz)).signOut();
    }
    this.zzaCw.clear();
  }
  
  public static zzaax zzww()
  {
    synchronized (zztX)
    {
      zzac.zzb(zzaCq, "Must guarantee manager is non-null before using getInstance");
      zzaax localzzaax = zzaCq;
      return localzzaax;
    }
  }
  
  public static void zzwx()
  {
    synchronized (zztX)
    {
      if (zzaCq != null) {
        zzaCq.signOut();
      }
      return;
    }
  }
  
  private static Looper zzwy()
  {
    HandlerThread localHandlerThread = new HandlerThread("GoogleApiHandler", 9);
    localHandlerThread.start();
    return localHandlerThread.getLooper();
  }
  
  @WorkerThread
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      int i = paramMessage.what;
      Log.w("GoogleApiManager", 31 + "Unknown message id: " + i);
      return false;
    case 1: 
      zzau(((Boolean)paramMessage.obj).booleanValue());
    }
    for (;;)
    {
      return true;
      zza((zzaab)paramMessage.obj);
      continue;
      zzwB();
      continue;
      zza((zzabl)paramMessage.obj);
      continue;
      zza(paramMessage.arg1, (ConnectionResult)paramMessage.obj);
      continue;
      zzwA();
      continue;
      zzc((zzc)paramMessage.obj);
      continue;
      if (this.zzaAM.containsKey(paramMessage.obj))
      {
        ((zza)this.zzaAM.get(paramMessage.obj)).resume();
        continue;
        zzwC();
        continue;
        if (this.zzaAM.containsKey(paramMessage.obj))
        {
          ((zza)this.zzaAM.get(paramMessage.obj)).zzwn();
          continue;
          if (this.zzaAM.containsKey(paramMessage.obj)) {
            ((zza)this.zzaAM.get(paramMessage.obj)).zzwN();
          }
        }
      }
    }
  }
  
  public void signOut()
  {
    this.zzaCt.incrementAndGet();
    this.mHandler.sendMessageAtFrontOfQueue(this.mHandler.obtainMessage(10));
  }
  
  PendingIntent zza(zzzz<?> paramzzzz, int paramInt)
  {
    if ((zza)this.zzaAM.get(paramzzzz) == null) {
      return null;
    }
    paramzzzz = ((zza)this.zzaAM.get(paramzzzz)).zzwO();
    if (paramzzzz == null) {
      return null;
    }
    return PendingIntent.getActivity(this.mContext, paramInt, paramzzzz.zzrs(), 134217728);
  }
  
  public <O extends Api.ApiOptions> Task<Void> zza(@NonNull zzc<O> paramzzc, @NonNull zzabh.zzb<?> paramzzb)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzb = new zzzx.zze(paramzzb, localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(13, new zzabl(paramzzb, this.zzaCt.get(), paramzzc)));
    return localTaskCompletionSource.getTask();
  }
  
  public <O extends Api.ApiOptions> Task<Void> zza(@NonNull zzc<O> paramzzc, @NonNull zzabm<Api.zzb, ?> paramzzabm, @NonNull zzabz<Api.zzb, ?> paramzzabz)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzabm = new zzzx.zzc(new zzabn(paramzzabm, paramzzabz), localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new zzabl(paramzzabm, this.zzaCt.get(), paramzzc)));
    return localTaskCompletionSource.getTask();
  }
  
  public Task<Void> zza(Iterable<? extends zzc<?>> paramIterable)
  {
    zzaab localzzaab = new zzaab(paramIterable);
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      Object localObject = (zzc)paramIterable.next();
      localObject = (zza)this.zzaAM.get(((zzc)localObject).getApiKey());
      if ((localObject == null) || (!((zza)localObject).isConnected()))
      {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2, localzzaab));
        return localzzaab.getTask();
      }
    }
    localzzaab.zzvA();
    return localzzaab.getTask();
  }
  
  public void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!zzc(paramConnectionResult, paramInt)) {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramInt, 0, paramConnectionResult));
    }
  }
  
  public <O extends Api.ApiOptions> void zza(zzc<O> paramzzc, int paramInt, zzaad.zza<? extends Result, Api.zzb> paramzza)
  {
    paramzza = new zzzx.zzb(paramInt, paramzza);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzabl(paramzza, this.zzaCt.get(), paramzzc)));
  }
  
  public <O extends Api.ApiOptions, TResult> void zza(zzc<O> paramzzc, int paramInt, zzabv<Api.zzb, TResult> paramzzabv, TaskCompletionSource<TResult> paramTaskCompletionSource, zzabs paramzzabs)
  {
    paramzzabv = new zzzx.zzd(paramInt, paramzzabv, paramTaskCompletionSource, paramzzabs);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzabl(paramzzabv, this.zzaCt.get(), paramzzc)));
  }
  
  public void zza(@NonNull zzaam paramzzaam)
  {
    synchronized (zztX)
    {
      if (this.zzaCu != paramzzaam)
      {
        this.zzaCu = paramzzaam;
        this.zzaCv.clear();
        this.zzaCv.addAll(paramzzaam.zzwb());
      }
      return;
    }
  }
  
  public void zzb(zzc<?> paramzzc)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(7, paramzzc));
  }
  
  void zzb(@NonNull zzaam paramzzaam)
  {
    synchronized (zztX)
    {
      if (this.zzaCu == paramzzaam)
      {
        this.zzaCu = null;
        this.zzaCv.clear();
      }
      return;
    }
  }
  
  boolean zzc(ConnectionResult paramConnectionResult, int paramInt)
  {
    return this.zzazn.zza(this.mContext, paramConnectionResult, paramInt);
  }
  
  void zzvn()
  {
    this.zzaCt.incrementAndGet();
    this.mHandler.sendMessage(this.mHandler.obtainMessage(10));
  }
  
  public void zzvx()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
  }
  
  public int zzwz()
  {
    return this.zzaCs.getAndIncrement();
  }
  
  public class zza<O extends Api.ApiOptions>
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zzaah
  {
    private final Api.zze zzaAJ;
    private boolean zzaBL;
    private final zzaal zzaCA;
    private final Set<zzaab> zzaCB = new HashSet();
    private final Map<zzabh.zzb<?>, zzabn> zzaCC = new HashMap();
    private final int zzaCD;
    private final zzabr zzaCE;
    private ConnectionResult zzaCF = null;
    private final Queue<zzzx> zzaCy = new LinkedList();
    private final Api.zzb zzaCz;
    private final zzzz<O> zzayU;
    
    @WorkerThread
    public zza()
    {
      Object localObject;
      this.zzaAJ = ((zzc)localObject).buildApiClient(zzaax.zza(zzaax.this).getLooper(), this);
      if ((this.zzaAJ instanceof zzal)) {}
      for (this.zzaCz = ((zzal)this.zzaAJ).zzyn();; this.zzaCz = this.zzaAJ)
      {
        this.zzayU = ((zzc)localObject).getApiKey();
        this.zzaCA = new zzaal();
        this.zzaCD = ((zzc)localObject).getInstanceId();
        if (!this.zzaAJ.zzrd()) {
          break;
        }
        this.zzaCE = ((zzc)localObject).createSignInCoordinator(zzaax.zzb(zzaax.this), zzaax.zza(zzaax.this));
        return;
      }
      this.zzaCE = null;
    }
    
    @WorkerThread
    private void zzb(zzzx paramzzzx)
    {
      paramzzzx.zza(this.zzaCA, zzrd());
      try
      {
        paramzzzx.zza(this);
        return;
      }
      catch (DeadObjectException paramzzzx)
      {
        onConnectionSuspended(1);
        this.zzaAJ.disconnect();
      }
    }
    
    @WorkerThread
    private void zzj(ConnectionResult paramConnectionResult)
    {
      Iterator localIterator = this.zzaCB.iterator();
      while (localIterator.hasNext()) {
        ((zzaab)localIterator.next()).zza(this.zzayU, paramConnectionResult);
      }
      this.zzaCB.clear();
    }
    
    @WorkerThread
    private void zzwF()
    {
      zzwJ();
      zzj(ConnectionResult.zzayj);
      zzwL();
      Iterator localIterator = this.zzaCC.values().iterator();
      for (;;)
      {
        if (localIterator.hasNext()) {
          localIterator.next();
        }
        try
        {
          new TaskCompletionSource();
        }
        catch (DeadObjectException localDeadObjectException)
        {
          onConnectionSuspended(1);
          this.zzaAJ.disconnect();
          zzwH();
          zzwM();
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    }
    
    @WorkerThread
    private void zzwG()
    {
      zzwJ();
      this.zzaBL = true;
      this.zzaCA.zzwa();
      zzaax.zza(zzaax.this).sendMessageDelayed(Message.obtain(zzaax.zza(zzaax.this), 9, this.zzayU), zzaax.zzc(zzaax.this));
      zzaax.zza(zzaax.this).sendMessageDelayed(Message.obtain(zzaax.zza(zzaax.this), 11, this.zzayU), zzaax.zzd(zzaax.this));
      zzaax.zza(zzaax.this, -1);
    }
    
    @WorkerThread
    private void zzwH()
    {
      while ((this.zzaAJ.isConnected()) && (!this.zzaCy.isEmpty())) {
        zzb((zzzx)this.zzaCy.remove());
      }
    }
    
    @WorkerThread
    private void zzwL()
    {
      if (this.zzaBL)
      {
        zzaax.zza(zzaax.this).removeMessages(11, this.zzayU);
        zzaax.zza(zzaax.this).removeMessages(9, this.zzayU);
        this.zzaBL = false;
      }
    }
    
    private void zzwM()
    {
      zzaax.zza(zzaax.this).removeMessages(12, this.zzayU);
      zzaax.zza(zzaax.this).sendMessageDelayed(zzaax.zza(zzaax.this).obtainMessage(12, this.zzayU), zzaax.zzh(zzaax.this));
    }
    
    @WorkerThread
    public void connect()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if ((this.zzaAJ.isConnected()) || (this.zzaAJ.isConnecting())) {
        return;
      }
      if ((this.zzaAJ.zzvh()) && (zzaax.zzi(zzaax.this) != 0))
      {
        zzaax.zza(zzaax.this, zzaax.zzg(zzaax.this).isGooglePlayServicesAvailable(zzaax.zzb(zzaax.this)));
        if (zzaax.zzi(zzaax.this) != 0)
        {
          onConnectionFailed(new ConnectionResult(zzaax.zzi(zzaax.this), null));
          return;
        }
      }
      zzaax.zzb localzzb = new zzaax.zzb(zzaax.this, this.zzaAJ, this.zzayU);
      if (this.zzaAJ.zzrd()) {
        this.zzaCE.zza(localzzb);
      }
      this.zzaAJ.zza(localzzb);
    }
    
    public int getInstanceId()
    {
      return this.zzaCD;
    }
    
    boolean isConnected()
    {
      return this.zzaAJ.isConnected();
    }
    
    public void onConnected(@Nullable Bundle paramBundle)
    {
      if (Looper.myLooper() == zzaax.zza(zzaax.this).getLooper())
      {
        zzwF();
        return;
      }
      zzaax.zza(zzaax.this).post(new Runnable()
      {
        public void run()
        {
          zzaax.zza.zzc(zzaax.zza.this);
        }
      });
    }
    
    @WorkerThread
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if (this.zzaCE != null) {
        this.zzaCE.zzwY();
      }
      zzwJ();
      zzaax.zza(zzaax.this, -1);
      zzj(paramConnectionResult);
      if (paramConnectionResult.getErrorCode() == 4) {
        zzD(zzaax.zzwD());
      }
      do
      {
        return;
        if (this.zzaCy.isEmpty())
        {
          this.zzaCF = paramConnectionResult;
          return;
        }
        synchronized (zzaax.zzwE())
        {
          if ((zzaax.zze(zzaax.this) != null) && (zzaax.zzf(zzaax.this).contains(this.zzayU)))
          {
            zzaax.zze(zzaax.this).zzb(paramConnectionResult, this.zzaCD);
            return;
          }
        }
      } while (zzaax.this.zzc(paramConnectionResult, this.zzaCD));
      if (paramConnectionResult.getErrorCode() == 18) {
        this.zzaBL = true;
      }
      if (this.zzaBL)
      {
        zzaax.zza(zzaax.this).sendMessageDelayed(Message.obtain(zzaax.zza(zzaax.this), 9, this.zzayU), zzaax.zzc(zzaax.this));
        return;
      }
      paramConnectionResult = String.valueOf(this.zzayU.zzvw());
      zzD(new Status(17, String.valueOf(paramConnectionResult).length() + 38 + "API: " + paramConnectionResult + " is not available on this device."));
    }
    
    public void onConnectionSuspended(int paramInt)
    {
      if (Looper.myLooper() == zzaax.zza(zzaax.this).getLooper())
      {
        zzwG();
        return;
      }
      zzaax.zza(zzaax.this).post(new Runnable()
      {
        public void run()
        {
          zzaax.zza.zzd(zzaax.zza.this);
        }
      });
    }
    
    @WorkerThread
    public void resume()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if (this.zzaBL) {
        connect();
      }
    }
    
    @WorkerThread
    public void signOut()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      zzD(zzaax.zzaCn);
      this.zzaCA.zzvZ();
      Iterator localIterator = this.zzaCC.keySet().iterator();
      while (localIterator.hasNext()) {
        zza(new zzzx.zze((zzabh.zzb)localIterator.next(), new TaskCompletionSource()));
      }
      zzj(new ConnectionResult(4));
      this.zzaAJ.disconnect();
    }
    
    @WorkerThread
    public void zzD(Status paramStatus)
    {
      zzac.zza(zzaax.zza(zzaax.this));
      Iterator localIterator = this.zzaCy.iterator();
      while (localIterator.hasNext()) {
        ((zzzx)localIterator.next()).zzz(paramStatus);
      }
      this.zzaCy.clear();
    }
    
    public void zza(final ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
    {
      if (Looper.myLooper() == zzaax.zza(zzaax.this).getLooper())
      {
        onConnectionFailed(paramConnectionResult);
        return;
      }
      zzaax.zza(zzaax.this).post(new Runnable()
      {
        public void run()
        {
          zzaax.zza.this.onConnectionFailed(paramConnectionResult);
        }
      });
    }
    
    @WorkerThread
    public void zza(zzzx paramzzzx)
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if (this.zzaAJ.isConnected())
      {
        zzb(paramzzzx);
        zzwM();
        return;
      }
      this.zzaCy.add(paramzzzx);
      if ((this.zzaCF != null) && (this.zzaCF.hasResolution()))
      {
        onConnectionFailed(this.zzaCF);
        return;
      }
      connect();
    }
    
    @WorkerThread
    public void zzb(zzaab paramzzaab)
    {
      zzac.zza(zzaax.zza(zzaax.this));
      this.zzaCB.add(paramzzaab);
    }
    
    @WorkerThread
    public void zzi(@NonNull ConnectionResult paramConnectionResult)
    {
      zzac.zza(zzaax.zza(zzaax.this));
      this.zzaAJ.disconnect();
      onConnectionFailed(paramConnectionResult);
    }
    
    public boolean zzrd()
    {
      return this.zzaAJ.zzrd();
    }
    
    public Api.zze zzvU()
    {
      return this.zzaAJ;
    }
    
    public Map<zzabh.zzb<?>, zzabn> zzwI()
    {
      return this.zzaCC;
    }
    
    @WorkerThread
    public void zzwJ()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      this.zzaCF = null;
    }
    
    @WorkerThread
    public ConnectionResult zzwK()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      return this.zzaCF;
    }
    
    @WorkerThread
    public void zzwN()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if ((this.zzaAJ.isConnected()) && (this.zzaCC.size() == 0))
      {
        if (this.zzaCA.zzvY()) {
          zzwM();
        }
      }
      else {
        return;
      }
      this.zzaAJ.disconnect();
    }
    
    zzbai zzwO()
    {
      if (this.zzaCE == null) {
        return null;
      }
      return this.zzaCE.zzwO();
    }
    
    @WorkerThread
    public void zzwn()
    {
      zzac.zza(zzaax.zza(zzaax.this));
      if (this.zzaBL)
      {
        zzwL();
        if (zzaax.zzg(zzaax.this).isGooglePlayServicesAvailable(zzaax.zzb(zzaax.this)) != 18) {
          break label71;
        }
      }
      label71:
      for (Status localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");; localStatus = new Status(8, "API failed to connect while resuming due to an unknown error."))
      {
        zzD(localStatus);
        this.zzaAJ.disconnect();
        return;
      }
    }
  }
  
  private class zzb
    implements zzf.zzf, zzabr.zza
  {
    private final Api.zze zzaAJ;
    private zzr zzaBw = null;
    private boolean zzaCI = false;
    private Set<Scope> zzakq = null;
    private final zzzz<?> zzayU;
    
    public zzb(zzzz<?> paramzzzz)
    {
      this.zzaAJ = paramzzzz;
      zzzz localzzzz;
      this.zzayU = localzzzz;
    }
    
    @WorkerThread
    private void zzwP()
    {
      if ((this.zzaCI) && (this.zzaBw != null)) {
        this.zzaAJ.zza(this.zzaBw, this.zzakq);
      }
    }
    
    @WorkerThread
    public void zzb(zzr paramzzr, Set<Scope> paramSet)
    {
      if ((paramzzr == null) || (paramSet == null))
      {
        Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
        zzi(new ConnectionResult(4));
        return;
      }
      this.zzaBw = paramzzr;
      this.zzakq = paramSet;
      zzwP();
    }
    
    public void zzg(@NonNull final ConnectionResult paramConnectionResult)
    {
      zzaax.zza(zzaax.this).post(new Runnable()
      {
        public void run()
        {
          if (paramConnectionResult.isSuccess())
          {
            zzaax.zzb.zza(zzaax.zzb.this, true);
            if (zzaax.zzb.zza(zzaax.zzb.this).zzrd())
            {
              zzaax.zzb.zzb(zzaax.zzb.this);
              return;
            }
            zzaax.zzb.zza(zzaax.zzb.this).zza(null, Collections.emptySet());
            return;
          }
          ((zzaax.zza)zzaax.zzj(zzaax.this).get(zzaax.zzb.zzc(zzaax.zzb.this))).onConnectionFailed(paramConnectionResult);
        }
      });
    }
    
    @WorkerThread
    public void zzi(ConnectionResult paramConnectionResult)
    {
      ((zzaax.zza)zzaax.zzj(zzaax.this).get(this.zzayU)).zzi(paramConnectionResult);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */