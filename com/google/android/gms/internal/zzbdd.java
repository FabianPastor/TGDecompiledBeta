package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbx;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class zzbdd<O extends Api.ApiOptions>
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zzbbj
{
  private final zzbat<O> zzaAK;
  private final Api.zze zzaCy;
  private boolean zzaDA;
  private final Queue<zzbam> zzaEn = new LinkedList();
  private final Api.zzb zzaEo;
  private final zzbbt zzaEp;
  private final Set<zzbav> zzaEq = new HashSet();
  private final Map<zzbdy<?>, zzbef> zzaEr = new HashMap();
  private final int zzaEs;
  private final zzbej zzaEt;
  private ConnectionResult zzaEu = null;
  
  @WorkerThread
  public zzbdd(GoogleApi<O> paramGoogleApi)
  {
    Object localObject;
    this.zzaCy = ((GoogleApi)localObject).zza(zzbdb.zza(paramGoogleApi).getLooper(), this);
    if ((this.zzaCy instanceof zzbx)) {
      zzbx localzzbx = (zzbx)this.zzaCy;
    }
    for (this.zzaEo = null;; this.zzaEo = this.zzaCy)
    {
      this.zzaAK = ((GoogleApi)localObject).zzph();
      this.zzaEp = new zzbbt();
      this.zzaEs = ((GoogleApi)localObject).getInstanceId();
      if (!this.zzaCy.zzmv()) {
        break;
      }
      this.zzaEt = ((GoogleApi)localObject).zza(zzbdb.zzb(paramGoogleApi), zzbdb.zza(paramGoogleApi));
      return;
    }
    this.zzaEt = null;
  }
  
  @WorkerThread
  private final void zzb(zzbam paramzzbam)
  {
    paramzzbam.zza(this.zzaEp, zzmv());
    try
    {
      paramzzbam.zza(this);
      return;
    }
    catch (DeadObjectException paramzzbam)
    {
      onConnectionSuspended(1);
      this.zzaCy.disconnect();
    }
  }
  
  @WorkerThread
  private final void zzi(ConnectionResult paramConnectionResult)
  {
    Iterator localIterator = this.zzaEq.iterator();
    while (localIterator.hasNext()) {
      ((zzbav)localIterator.next()).zza(this.zzaAK, paramConnectionResult);
    }
    this.zzaEq.clear();
  }
  
  @WorkerThread
  private final void zzqq()
  {
    zzqt();
    zzi(ConnectionResult.zzazX);
    zzqv();
    Iterator localIterator = this.zzaEr.values().iterator();
    for (;;)
    {
      zzbef localzzbef;
      if (localIterator.hasNext()) {
        localzzbef = (zzbef)localIterator.next();
      }
      try
      {
        localzzbef.zzaBu.zzb(this.zzaEo, new TaskCompletionSource());
      }
      catch (DeadObjectException localDeadObjectException)
      {
        onConnectionSuspended(1);
        this.zzaCy.disconnect();
        while ((this.zzaCy.isConnected()) && (!this.zzaEn.isEmpty())) {
          zzb((zzbam)this.zzaEn.remove());
        }
        zzqw();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  @WorkerThread
  private final void zzqr()
  {
    zzqt();
    this.zzaDA = true;
    this.zzaEp.zzpQ();
    zzbdb.zza(this.zzaEm).sendMessageDelayed(Message.obtain(zzbdb.zza(this.zzaEm), 9, this.zzaAK), zzbdb.zzc(this.zzaEm));
    zzbdb.zza(this.zzaEm).sendMessageDelayed(Message.obtain(zzbdb.zza(this.zzaEm), 11, this.zzaAK), zzbdb.zzd(this.zzaEm));
    zzbdb.zza(this.zzaEm, -1);
  }
  
  @WorkerThread
  private final void zzqv()
  {
    if (this.zzaDA)
    {
      zzbdb.zza(this.zzaEm).removeMessages(11, this.zzaAK);
      zzbdb.zza(this.zzaEm).removeMessages(9, this.zzaAK);
      this.zzaDA = false;
    }
  }
  
  private final void zzqw()
  {
    zzbdb.zza(this.zzaEm).removeMessages(12, this.zzaAK);
    zzbdb.zza(this.zzaEm).sendMessageDelayed(zzbdb.zza(this.zzaEm).obtainMessage(12, this.zzaAK), zzbdb.zzh(this.zzaEm));
  }
  
  @WorkerThread
  public final void connect()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if ((this.zzaCy.isConnected()) || (this.zzaCy.isConnecting())) {
      return;
    }
    if ((this.zzaCy.zzpe()) && (zzbdb.zzi(this.zzaEm) != 0))
    {
      zzbdb.zza(this.zzaEm, zzbdb.zzg(this.zzaEm).isGooglePlayServicesAvailable(zzbdb.zzb(this.zzaEm)));
      if (zzbdb.zzi(this.zzaEm) != 0)
      {
        onConnectionFailed(new ConnectionResult(zzbdb.zzi(this.zzaEm), null));
        return;
      }
    }
    zzbdh localzzbdh = new zzbdh(this.zzaEm, this.zzaCy, this.zzaAK);
    if (this.zzaCy.zzmv()) {
      this.zzaEt.zza(localzzbdh);
    }
    this.zzaCy.zza(localzzbdh);
  }
  
  public final int getInstanceId()
  {
    return this.zzaEs;
  }
  
  final boolean isConnected()
  {
    return this.zzaCy.isConnected();
  }
  
  public final void onConnected(@Nullable Bundle paramBundle)
  {
    if (Looper.myLooper() == zzbdb.zza(this.zzaEm).getLooper())
    {
      zzqq();
      return;
    }
    zzbdb.zza(this.zzaEm).post(new zzbde(this));
  }
  
  @WorkerThread
  public final void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if (this.zzaEt != null) {
      this.zzaEt.zzqI();
    }
    zzqt();
    zzbdb.zza(this.zzaEm, -1);
    zzi(paramConnectionResult);
    if (paramConnectionResult.getErrorCode() == 4) {
      zzt(zzbdb.zzqo());
    }
    do
    {
      return;
      if (this.zzaEn.isEmpty())
      {
        this.zzaEu = paramConnectionResult;
        return;
      }
      synchronized (zzbdb.zzqp())
      {
        if ((zzbdb.zze(this.zzaEm) != null) && (zzbdb.zzf(this.zzaEm).contains(this.zzaAK)))
        {
          zzbdb.zze(this.zzaEm).zzb(paramConnectionResult, this.zzaEs);
          return;
        }
      }
    } while (this.zzaEm.zzc(paramConnectionResult, this.zzaEs));
    if (paramConnectionResult.getErrorCode() == 18) {
      this.zzaDA = true;
    }
    if (this.zzaDA)
    {
      zzbdb.zza(this.zzaEm).sendMessageDelayed(Message.obtain(zzbdb.zza(this.zzaEm), 9, this.zzaAK), zzbdb.zzc(this.zzaEm));
      return;
    }
    paramConnectionResult = String.valueOf(this.zzaAK.zzpr());
    zzt(new Status(17, String.valueOf(paramConnectionResult).length() + 38 + "API: " + paramConnectionResult + " is not available on this device."));
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    if (Looper.myLooper() == zzbdb.zza(this.zzaEm).getLooper())
    {
      zzqr();
      return;
    }
    zzbdb.zza(this.zzaEm).post(new zzbdf(this));
  }
  
  @WorkerThread
  public final void resume()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if (this.zzaDA) {
      connect();
    }
  }
  
  @WorkerThread
  public final void signOut()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    zzt(zzbdb.zzaEc);
    this.zzaEp.zzpP();
    Iterator localIterator = this.zzaEr.keySet().iterator();
    while (localIterator.hasNext()) {
      zza(new zzbar((zzbdy)localIterator.next(), new TaskCompletionSource()));
    }
    zzi(new ConnectionResult(4));
    this.zzaCy.disconnect();
  }
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    if (Looper.myLooper() == zzbdb.zza(this.zzaEm).getLooper())
    {
      onConnectionFailed(paramConnectionResult);
      return;
    }
    zzbdb.zza(this.zzaEm).post(new zzbdg(this, paramConnectionResult));
  }
  
  @WorkerThread
  public final void zza(zzbam paramzzbam)
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if (this.zzaCy.isConnected())
    {
      zzb(paramzzbam);
      zzqw();
      return;
    }
    this.zzaEn.add(paramzzbam);
    if ((this.zzaEu != null) && (this.zzaEu.hasResolution()))
    {
      onConnectionFailed(this.zzaEu);
      return;
    }
    connect();
  }
  
  @WorkerThread
  public final void zza(zzbav paramzzbav)
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    this.zzaEq.add(paramzzbav);
  }
  
  @WorkerThread
  public final void zzh(@NonNull ConnectionResult paramConnectionResult)
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    this.zzaCy.disconnect();
    onConnectionFailed(paramConnectionResult);
  }
  
  public final boolean zzmv()
  {
    return this.zzaCy.zzmv();
  }
  
  public final Api.zze zzpJ()
  {
    return this.zzaCy;
  }
  
  @WorkerThread
  public final void zzqd()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if (this.zzaDA)
    {
      zzqv();
      if (zzbdb.zzg(this.zzaEm).isGooglePlayServicesAvailable(zzbdb.zzb(this.zzaEm)) != 18) {
        break label71;
      }
    }
    label71:
    for (Status localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");; localStatus = new Status(8, "API failed to connect while resuming due to an unknown error."))
    {
      zzt(localStatus);
      this.zzaCy.disconnect();
      return;
    }
  }
  
  public final Map<zzbdy<?>, zzbef> zzqs()
  {
    return this.zzaEr;
  }
  
  @WorkerThread
  public final void zzqt()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    this.zzaEu = null;
  }
  
  @WorkerThread
  public final ConnectionResult zzqu()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    return this.zzaEu;
  }
  
  @WorkerThread
  public final void zzqx()
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    if ((this.zzaCy.isConnected()) && (this.zzaEr.size() == 0))
    {
      if (this.zzaEp.zzpO()) {
        zzqw();
      }
    }
    else {
      return;
    }
    this.zzaCy.disconnect();
  }
  
  final zzctk zzqy()
  {
    if (this.zzaEt == null) {
      return null;
    }
    return this.zzaEt.zzqy();
  }
  
  @WorkerThread
  public final void zzt(Status paramStatus)
  {
    zzbo.zza(zzbdb.zza(this.zzaEm));
    Iterator localIterator = this.zzaEn.iterator();
    while (localIterator.hasNext()) {
      ((zzbam)localIterator.next()).zzp(paramStatus);
    }
    this.zzaEn.clear();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */