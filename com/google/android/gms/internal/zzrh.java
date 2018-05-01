package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzag;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.common.util.zza;
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

public class zzrh
  implements Handler.Callback
{
  public static final Status AG = new Status(4, "Sign-out occurred while this API call was in progress.");
  private static final Status AH = new Status(4, "The user must be signed in to make this API call.");
  private static zzrh AJ;
  private static final Object zzaox = new Object();
  private long AI = 10000L;
  private int AK = -1;
  private final AtomicInteger AL = new AtomicInteger(1);
  private final AtomicInteger AM = new AtomicInteger(0);
  private zzqw AN = null;
  private final Set<zzql<?>> AO = new zza();
  private final Set<zzql<?>> AP = new zza();
  private long Af = 120000L;
  private long Ag = 5000L;
  private final Context mContext;
  private final Handler mHandler;
  private final GoogleApiAvailability xP;
  private final Map<zzql<?>, zza<?>> zj = new ConcurrentHashMap(5, 0.75F, 1);
  
  private zzrh(Context paramContext)
  {
    this(paramContext, GoogleApiAvailability.getInstance());
  }
  
  private zzrh(Context paramContext, GoogleApiAvailability paramGoogleApiAvailability)
  {
    this.mContext = paramContext;
    paramContext = new HandlerThread("GoogleApiHandler", 9);
    paramContext.start();
    this.mHandler = new Handler(paramContext.getLooper(), this);
    this.xP = paramGoogleApiAvailability;
  }
  
  @WorkerThread
  private void zza(int paramInt, ConnectionResult paramConnectionResult)
  {
    Object localObject = this.zj.values().iterator();
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
        localObject = String.valueOf(this.xP.getErrorString(paramConnectionResult.getErrorCode()));
        paramConnectionResult = String.valueOf(paramConnectionResult.getErrorMessage());
        zza.zza(localzza, new Status(17, String.valueOf(localObject).length() + 69 + String.valueOf(paramConnectionResult).length() + "Error resolution was canceled by the user, original error message: " + (String)localObject + ": " + paramConnectionResult));
        return;
      }
      Log.wtf("GoogleApiManager", 76 + "Could not find API instance " + paramInt + " while trying to fail enqueued calls.", new Exception());
      return;
      localzza = null;
    }
  }
  
  @WorkerThread
  private void zza(zzrv paramzzrv)
  {
    zza localzza2 = (zza)this.zj.get(paramzzrv.Bs.getApiKey());
    zza localzza1 = localzza2;
    if (localzza2 == null)
    {
      zzb(paramzzrv.Bs);
      localzza1 = (zza)this.zj.get(paramzzrv.Bs.getApiKey());
    }
    if ((localzza1.zzain()) && (this.AM.get() != paramzzrv.Br))
    {
      paramzzrv.Bq.zzy(AG);
      localzza1.signOut();
      return;
    }
    localzza1.zza(paramzzrv.Bq);
  }
  
  public static zzrh zzatg()
  {
    synchronized (zzaox)
    {
      zzaa.zzb(AJ, "Must guarantee manager is non-null before using getInstance");
      zzrh localzzrh = AJ;
      return localzzrh;
    }
  }
  
  @WorkerThread
  private void zzati()
  {
    Iterator localIterator = this.zj.values().iterator();
    while (localIterator.hasNext())
    {
      zza localzza = (zza)localIterator.next();
      localzza.zzato();
      zza.zzc(localzza);
    }
  }
  
  @WorkerThread
  private void zzb(zzc<?> paramzzc)
  {
    zzql localzzql = paramzzc.getApiKey();
    if (!this.zj.containsKey(localzzql)) {
      this.zj.put(localzzql, new zza(paramzzc));
    }
    paramzzc = (zza)this.zj.get(localzzql);
    if (paramzzc.zzain()) {
      this.AP.add(localzzql);
    }
    zza.zzc(paramzzc);
  }
  
  public static zzrh zzbx(Context paramContext)
  {
    synchronized (zzaox)
    {
      if (AJ == null) {
        AJ = new zzrh(paramContext.getApplicationContext());
      }
      paramContext = AJ;
      return paramContext;
    }
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
      zza((zzqn)paramMessage.obj);
    }
    for (;;)
    {
      return true;
      zzb((zzc)paramMessage.obj);
      continue;
      zzati();
      continue;
      zza((zzrv)paramMessage.obj);
      continue;
      zza(paramMessage.arg1, (ConnectionResult)paramMessage.obj);
      continue;
      if (this.zj.containsKey(paramMessage.obj))
      {
        zza.zzd((zza)this.zj.get(paramMessage.obj));
        continue;
        zzatj();
        continue;
        if (this.zj.containsKey(paramMessage.obj))
        {
          zza.zze((zza)this.zj.get(paramMessage.obj));
          continue;
          if (this.zj.containsKey(paramMessage.obj)) {
            zza.zzf((zza)this.zj.get(paramMessage.obj));
          }
        }
      }
    }
  }
  
  public <O extends Api.ApiOptions> Task<Void> zza(@NonNull zzc<O> paramzzc, @NonNull zzrr.zzb<?> paramzzb)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzb = new zzqj.zze(paramzzb, localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(11, new zzrv(paramzzb, this.AM.get(), paramzzc)));
    return localTaskCompletionSource.getTask();
  }
  
  public <O extends Api.ApiOptions> Task<Void> zza(@NonNull zzc<O> paramzzc, @NonNull zzrw<Api.zzb> paramzzrw, @NonNull zzsh<Api.zzb> paramzzsh)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramzzrw = new zzqj.zzc(new zzrx(paramzzrw, paramzzsh), localTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(6, new zzrv(paramzzrw, this.AM.get(), paramzzc)));
    return localTaskCompletionSource.getTask();
  }
  
  public Task<Void> zza(Iterable<zzc<?>> paramIterable)
  {
    zzqn localzzqn = new zzqn(paramIterable);
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      Object localObject = (zzc)paramIterable.next();
      localObject = (zza)this.zj.get(((zzc)localObject).getApiKey());
      if ((localObject == null) || (!((zza)localObject).isConnected()))
      {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, localzzqn));
        return localzzqn.getTask();
      }
    }
    localzzqn.zzarp();
    return localzzqn.getTask();
  }
  
  public void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!zzc(paramConnectionResult, paramInt)) {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(4, paramInt, 0, paramConnectionResult));
    }
  }
  
  public void zza(zzc<?> paramzzc)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramzzc));
  }
  
  public <O extends Api.ApiOptions> void zza(zzc<O> paramzzc, int paramInt, zzqo.zza<? extends Result, Api.zzb> paramzza)
  {
    paramzza = new zzqj.zzb(paramInt, paramzza);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzrv(paramzza, this.AM.get(), paramzzc)));
  }
  
  public <O extends Api.ApiOptions, TResult> void zza(zzc<O> paramzzc, int paramInt, zzse<Api.zzb, TResult> paramzzse, TaskCompletionSource<TResult> paramTaskCompletionSource, zzsb paramzzsb)
  {
    paramzzse = new zzqj.zzd(paramInt, paramzzse, paramTaskCompletionSource, paramzzsb);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzrv(paramzzse, this.AM.get(), paramzzc)));
  }
  
  @WorkerThread
  public void zza(zzqn paramzzqn)
  {
    Iterator localIterator = paramzzqn.zzaro().iterator();
    for (;;)
    {
      zzql localzzql;
      zza localzza;
      if (localIterator.hasNext())
      {
        localzzql = (zzql)localIterator.next();
        localzza = (zza)this.zj.get(localzzql);
        if (localzza == null) {
          paramzzqn.zza(localzzql, new ConnectionResult(13));
        }
      }
      else
      {
        return;
      }
      if (localzza.isConnected()) {
        paramzzqn.zza(localzzql, ConnectionResult.wO);
      } else if (localzza.zzatp() != null) {
        paramzzqn.zza(localzzql, localzza.zzatp());
      } else {
        localzza.zzb(paramzzqn);
      }
    }
  }
  
  public void zza(@NonNull zzqw paramzzqw)
  {
    synchronized (zzaox)
    {
      if (this.AN != paramzzqw)
      {
        this.AN = paramzzqw;
        this.AO.clear();
        this.AO.addAll(paramzzqw.zzasl());
      }
      return;
    }
  }
  
  public void zzarm()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
  }
  
  public int zzath()
  {
    return this.AL.getAndIncrement();
  }
  
  @WorkerThread
  public void zzatj()
  {
    Iterator localIterator = this.AP.iterator();
    while (localIterator.hasNext())
    {
      zzql localzzql = (zzql)localIterator.next();
      ((zza)this.zj.remove(localzzql)).signOut();
    }
    this.AP.clear();
  }
  
  void zzb(@NonNull zzqw paramzzqw)
  {
    synchronized (zzaox)
    {
      if (this.AN == paramzzqw)
      {
        this.AN = null;
        this.AO.clear();
      }
      return;
    }
  }
  
  boolean zzc(ConnectionResult paramConnectionResult, int paramInt)
  {
    if ((paramConnectionResult.hasResolution()) || (this.xP.isUserResolvableError(paramConnectionResult.getErrorCode())))
    {
      this.xP.zza(this.mContext, paramConnectionResult, paramInt);
      return true;
    }
    return false;
  }
  
  class zza<O extends Api.ApiOptions>
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zzqs
  {
    private final Queue<zzqj> AQ = new LinkedList();
    private final Api.zzb AR;
    private final zzqv AS;
    private final Set<zzqn> AT = new HashSet();
    private final Map<zzrr.zzb<?>, zzrx> AU = new HashMap();
    private final int AV;
    private ConnectionResult AW = null;
    private boolean Ae;
    private final Api.zze xB;
    private final zzql<O> xx;
    
    @WorkerThread
    public zza()
    {
      Object localObject;
      if (((zzc)localObject).isConnectionlessGoogleApiClient())
      {
        this.xB = ((zzc)localObject).getClient();
        ((zzc)localObject).getClientCallbacks().zza(this);
        if (!(this.xB instanceof zzag)) {
          break label142;
        }
      }
      label142:
      for (this.AR = ((zzag)this.xB).zzawt();; this.AR = this.xB)
      {
        this.xx = ((zzc)localObject).getApiKey();
        this.AS = new zzqv();
        this.AV = ((zzc)localObject).getInstanceId();
        return;
        this.xB = ((zzc)localObject).buildApiClient(zzrh.zza(zzrh.this).getLooper(), this, this);
        break;
      }
    }
    
    @WorkerThread
    private void connect()
    {
      if ((this.xB.isConnected()) || (this.xB.isConnecting())) {
        return;
      }
      if ((this.xB.zzaqx()) && (zzrh.zzi(zzrh.this) != 0))
      {
        zzrh.zza(zzrh.this, zzrh.zzg(zzrh.this).isGooglePlayServicesAvailable(zzrh.zzf(zzrh.this)));
        if (zzrh.zzi(zzrh.this) != 0)
        {
          onConnectionFailed(new ConnectionResult(zzrh.zzi(zzrh.this), null));
          return;
        }
      }
      if (this.xB.zzain()) {}
      zzrh.zzb localzzb = new zzrh.zzb(zzrh.this, this.xB, this.xx);
      this.xB.zza(localzzb);
    }
    
    @WorkerThread
    private void resume()
    {
      if (this.Ae) {
        connect();
      }
    }
    
    @WorkerThread
    private void zzac(Status paramStatus)
    {
      Iterator localIterator = this.AQ.iterator();
      while (localIterator.hasNext()) {
        ((zzqj)localIterator.next()).zzy(paramStatus);
      }
      this.AQ.clear();
    }
    
    @WorkerThread
    private void zzasx()
    {
      if (this.Ae)
      {
        zzatq();
        if (zzrh.zzg(zzrh.this).isGooglePlayServicesAvailable(zzrh.zzf(zzrh.this)) != 18) {
          break label60;
        }
      }
      label60:
      for (Status localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");; localStatus = new Status(8, "API failed to connect while resuming due to an unknown error."))
      {
        zzac(localStatus);
        this.xB.disconnect();
        return;
      }
    }
    
    @WorkerThread
    private void zzatq()
    {
      if (this.Ae)
      {
        zzrh.zza(zzrh.this).removeMessages(9, this.xx);
        zzrh.zza(zzrh.this).removeMessages(7, this.xx);
        this.Ae = false;
      }
    }
    
    private void zzatr()
    {
      zzrh.zza(zzrh.this).removeMessages(10, this.xx);
      zzrh.zza(zzrh.this).sendMessageDelayed(zzrh.zza(zzrh.this).obtainMessage(10, this.xx), zzrh.zzh(zzrh.this));
    }
    
    private void zzats()
    {
      if ((this.xB.isConnected()) && (this.AU.size() == 0))
      {
        if (this.AS.zzasi()) {
          zzatr();
        }
      }
      else {
        return;
      }
      this.xB.disconnect();
    }
    
    @WorkerThread
    private void zzb(zzqj paramzzqj)
    {
      paramzzqj.zza(this.AS, zzain());
      try
      {
        paramzzqj.zza(this);
        return;
      }
      catch (DeadObjectException paramzzqj)
      {
        onConnectionSuspended(1);
        this.xB.disconnect();
      }
    }
    
    @WorkerThread
    private void zzi(ConnectionResult paramConnectionResult)
    {
      Iterator localIterator = this.AT.iterator();
      while (localIterator.hasNext()) {
        ((zzqn)localIterator.next()).zza(this.xx, paramConnectionResult);
      }
      this.AT.clear();
    }
    
    public Api.zze getClient()
    {
      return this.xB;
    }
    
    public int getInstanceId()
    {
      return this.AV;
    }
    
    boolean isConnected()
    {
      return this.xB.isConnected();
    }
    
    @WorkerThread
    public void onConnected(@Nullable Bundle paramBundle)
    {
      zzato();
      zzi(ConnectionResult.wO);
      zzatq();
      paramBundle = this.AU.values().iterator();
      while (paramBundle.hasNext())
      {
        zzrx localzzrx = (zzrx)paramBundle.next();
        try
        {
          localzzrx.yi.zza(this.AR, new TaskCompletionSource());
        }
        catch (DeadObjectException localDeadObjectException)
        {
          onConnectionSuspended(1);
          this.xB.disconnect();
        }
      }
      zzatm();
      zzatr();
    }
    
    @WorkerThread
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      zzato();
      zzrh.zza(zzrh.this, -1);
      zzi(paramConnectionResult);
      if (paramConnectionResult.getErrorCode() == 4) {
        zzac(zzrh.zzatk());
      }
      do
      {
        return;
        if (this.AQ.isEmpty())
        {
          this.AW = paramConnectionResult;
          return;
        }
        synchronized (zzrh.zzatl())
        {
          if ((zzrh.zzd(zzrh.this) != null) && (zzrh.zze(zzrh.this).contains(this.xx)))
          {
            zzrh.zzd(zzrh.this).zzb(paramConnectionResult, this.AV);
            return;
          }
        }
      } while (zzrh.this.zzc(paramConnectionResult, this.AV));
      if (paramConnectionResult.getErrorCode() == 18) {
        this.Ae = true;
      }
      if (this.Ae)
      {
        zzrh.zza(zzrh.this).sendMessageDelayed(Message.obtain(zzrh.zza(zzrh.this), 7, this.xx), zzrh.zzb(zzrh.this));
        return;
      }
      paramConnectionResult = String.valueOf(this.xx.zzarl());
      zzac(new Status(17, String.valueOf(paramConnectionResult).length() + 38 + "API: " + paramConnectionResult + " is not available on this device."));
    }
    
    @WorkerThread
    public void onConnectionSuspended(int paramInt)
    {
      zzato();
      this.Ae = true;
      this.AS.zzask();
      zzrh.zza(zzrh.this).sendMessageDelayed(Message.obtain(zzrh.zza(zzrh.this), 7, this.xx), zzrh.zzb(zzrh.this));
      zzrh.zza(zzrh.this).sendMessageDelayed(Message.obtain(zzrh.zza(zzrh.this), 9, this.xx), zzrh.zzc(zzrh.this));
      zzrh.zza(zzrh.this, -1);
    }
    
    @WorkerThread
    public void signOut()
    {
      zzac(zzrh.AG);
      this.AS.zzasj();
      Iterator localIterator = this.AU.keySet().iterator();
      while (localIterator.hasNext()) {
        zza(new zzqj.zze((zzrr.zzb)localIterator.next(), new TaskCompletionSource()));
      }
      this.xB.disconnect();
    }
    
    public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt)
    {
      onConnectionFailed(paramConnectionResult);
    }
    
    @WorkerThread
    public void zza(zzqj paramzzqj)
    {
      if (this.xB.isConnected())
      {
        zzb(paramzzqj);
        zzatr();
        return;
      }
      this.AQ.add(paramzzqj);
      if ((this.AW != null) && (this.AW.hasResolution()))
      {
        onConnectionFailed(this.AW);
        return;
      }
      connect();
    }
    
    public boolean zzain()
    {
      return this.xB.zzain();
    }
    
    @WorkerThread
    public void zzatm()
    {
      while ((this.xB.isConnected()) && (!this.AQ.isEmpty())) {
        zzb((zzqj)this.AQ.remove());
      }
    }
    
    public Map<zzrr.zzb<?>, zzrx> zzatn()
    {
      return this.AU;
    }
    
    @WorkerThread
    public void zzato()
    {
      this.AW = null;
    }
    
    ConnectionResult zzatp()
    {
      return this.AW;
    }
    
    @WorkerThread
    public void zzb(zzqn paramzzqn)
    {
      this.AT.add(paramzzqn);
    }
  }
  
  private class zzb
    implements zze.zzf
  {
    private final Api.zze xB;
    private final zzql<?> xx;
    
    public zzb(zzql<?> paramzzql)
    {
      this.xB = paramzzql;
      zzql localzzql;
      this.xx = localzzql;
    }
    
    @WorkerThread
    public void zzg(@NonNull ConnectionResult paramConnectionResult)
    {
      if (paramConnectionResult.isSuccess())
      {
        if (this.xB.zzain()) {
          return;
        }
        this.xB.zza(null, Collections.emptySet());
        return;
      }
      ((zzrh.zza)zzrh.zzj(zzrh.this).get(this.xx)).onConnectionFailed(paramConnectionResult);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */