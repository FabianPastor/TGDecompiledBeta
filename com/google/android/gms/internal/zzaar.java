package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zzd;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzf.zzf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzg.zza;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zze;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class zzaar
  implements zzaau
{
  private final Context mContext;
  private final Lock zzaAG;
  private final zzg zzaAL;
  private final Map<Api<?>, Boolean> zzaAO;
  private final zze zzaAQ;
  private ConnectionResult zzaAZ;
  private final zzaav zzaBk;
  private int zzaBn;
  private int zzaBo = 0;
  private int zzaBp;
  private final Bundle zzaBq = new Bundle();
  private final Set<Api.zzc> zzaBr = new HashSet();
  private zzbai zzaBs;
  private boolean zzaBt;
  private boolean zzaBu;
  private boolean zzaBv;
  private zzr zzaBw;
  private boolean zzaBx;
  private boolean zzaBy;
  private ArrayList<Future<?>> zzaBz = new ArrayList();
  private final Api.zza<? extends zzbai, zzbaj> zzazo;
  
  public zzaar(zzaav paramzzaav, zzg paramzzg, Map<Api<?>, Boolean> paramMap, zze paramzze, Api.zza<? extends zzbai, zzbaj> paramzza, Lock paramLock, Context paramContext)
  {
    this.zzaBk = paramzzaav;
    this.zzaAL = paramzzg;
    this.zzaAO = paramMap;
    this.zzaAQ = paramzze;
    this.zzazo = paramzza;
    this.zzaAG = paramLock;
    this.mContext = paramContext;
  }
  
  private void zza(zzbaw paramzzbaw)
  {
    if (!zzcB(0)) {
      return;
    }
    Object localObject = paramzzbaw.zzyh();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramzzbaw.zzPW();
      paramzzbaw = ((zzaf)localObject).zzyh();
      if (!paramzzbaw.isSuccess())
      {
        localObject = String.valueOf(paramzzbaw);
        Log.wtf("GoogleApiClientConnecting", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        zzf(paramzzbaw);
        return;
      }
      this.zzaBv = true;
      this.zzaBw = ((zzaf)localObject).zzyg();
      this.zzaBx = ((zzaf)localObject).zzyi();
      this.zzaBy = ((zzaf)localObject).zzyj();
      zzwg();
      return;
    }
    if (zze((ConnectionResult)localObject))
    {
      zzwj();
      zzwg();
      return;
    }
    zzf((ConnectionResult)localObject);
  }
  
  private boolean zza(int paramInt, boolean paramBoolean, ConnectionResult paramConnectionResult)
  {
    if ((paramBoolean) && (!zzd(paramConnectionResult))) {}
    while ((this.zzaAZ != null) && (paramInt >= this.zzaBn)) {
      return false;
    }
    return true;
  }
  
  private void zzat(boolean paramBoolean)
  {
    if (this.zzaBs != null)
    {
      if ((this.zzaBs.isConnected()) && (paramBoolean)) {
        this.zzaBs.zzPL();
      }
      this.zzaBs.disconnect();
      this.zzaBw = null;
    }
  }
  
  private void zzb(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    int i = paramApi.zzve().getPriority();
    if (zza(i, paramBoolean, paramConnectionResult))
    {
      this.zzaAZ = paramConnectionResult;
      this.zzaBn = i;
    }
    this.zzaBk.zzaCf.put(paramApi.zzvg(), paramConnectionResult);
  }
  
  private boolean zzcB(int paramInt)
  {
    if (this.zzaBo != paramInt)
    {
      Log.w("GoogleApiClientConnecting", this.zzaBk.zzaAw.zzwr());
      String str1 = String.valueOf(this);
      Log.w("GoogleApiClientConnecting", String.valueOf(str1).length() + 23 + "Unexpected callback in " + str1);
      int i = this.zzaBp;
      Log.w("GoogleApiClientConnecting", 33 + "mRemainingConnections=" + i);
      str1 = String.valueOf(zzcC(this.zzaBo));
      String str2 = String.valueOf(zzcC(paramInt));
      Log.wtf("GoogleApiClientConnecting", String.valueOf(str1).length() + 70 + String.valueOf(str2).length() + "GoogleApiClient connecting is in step " + str1 + " but received callback for step " + str2, new Exception());
      zzf(new ConnectionResult(8, null));
      return false;
    }
    return true;
  }
  
  private String zzcC(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 0: 
      return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
    }
    return "STEP_GETTING_REMOTE_SERVICE";
  }
  
  private boolean zzd(ConnectionResult paramConnectionResult)
  {
    if (paramConnectionResult.hasResolution()) {}
    while (this.zzaAQ.zzcw(paramConnectionResult.getErrorCode()) != null) {
      return true;
    }
    return false;
  }
  
  private boolean zze(ConnectionResult paramConnectionResult)
  {
    return (this.zzaBt) && (!paramConnectionResult.hasResolution());
  }
  
  private void zzf(ConnectionResult paramConnectionResult)
  {
    zzwk();
    if (!paramConnectionResult.hasResolution()) {}
    for (boolean bool = true;; bool = false)
    {
      zzat(bool);
      this.zzaBk.zzh(paramConnectionResult);
      this.zzaBk.zzaCj.zzc(paramConnectionResult);
      return;
    }
  }
  
  private boolean zzwf()
  {
    this.zzaBp -= 1;
    if (this.zzaBp > 0) {
      return false;
    }
    if (this.zzaBp < 0)
    {
      Log.w("GoogleApiClientConnecting", this.zzaBk.zzaAw.zzwr());
      Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
      zzf(new ConnectionResult(8, null));
      return false;
    }
    if (this.zzaAZ != null)
    {
      this.zzaBk.zzaCi = this.zzaBn;
      zzf(this.zzaAZ);
      return false;
    }
    return true;
  }
  
  private void zzwg()
  {
    if (this.zzaBp != 0) {}
    while ((this.zzaBu) && (!this.zzaBv)) {
      return;
    }
    zzwh();
  }
  
  private void zzwh()
  {
    ArrayList localArrayList = new ArrayList();
    this.zzaBo = 1;
    this.zzaBp = this.zzaBk.zzaBQ.size();
    Iterator localIterator = this.zzaBk.zzaBQ.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api.zzc localzzc = (Api.zzc)localIterator.next();
      if (this.zzaBk.zzaCf.containsKey(localzzc))
      {
        if (zzwf()) {
          zzwi();
        }
      }
      else {
        localArrayList.add((Api.zze)this.zzaBk.zzaBQ.get(localzzc));
      }
    }
    if (!localArrayList.isEmpty()) {
      this.zzaBz.add(zzaaw.zzwv().submit(new zzc(localArrayList)));
    }
  }
  
  private void zzwi()
  {
    this.zzaBk.zzwt();
    zzaaw.zzwv().execute(new Runnable()
    {
      public void run()
      {
        zzaar.zzb(zzaar.this).zzaF(zzaar.zza(zzaar.this));
      }
    });
    if (this.zzaBs != null)
    {
      if (this.zzaBx) {
        this.zzaBs.zza(this.zzaBw, this.zzaBy);
      }
      zzat(false);
    }
    Object localObject = this.zzaBk.zzaCf.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Api.zzc localzzc = (Api.zzc)((Iterator)localObject).next();
      ((Api.zze)this.zzaBk.zzaBQ.get(localzzc)).disconnect();
    }
    if (this.zzaBq.isEmpty()) {}
    for (localObject = null;; localObject = this.zzaBq)
    {
      this.zzaBk.zzaCj.zzo((Bundle)localObject);
      return;
    }
  }
  
  private void zzwj()
  {
    this.zzaBu = false;
    this.zzaBk.zzaAw.zzaBR = Collections.emptySet();
    Iterator localIterator = this.zzaBr.iterator();
    while (localIterator.hasNext())
    {
      Api.zzc localzzc = (Api.zzc)localIterator.next();
      if (!this.zzaBk.zzaCf.containsKey(localzzc)) {
        this.zzaBk.zzaCf.put(localzzc, new ConnectionResult(17, null));
      }
    }
  }
  
  private void zzwk()
  {
    Iterator localIterator = this.zzaBz.iterator();
    while (localIterator.hasNext()) {
      ((Future)localIterator.next()).cancel(true);
    }
    this.zzaBz.clear();
  }
  
  private Set<Scope> zzwl()
  {
    if (this.zzaAL == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet(this.zzaAL.zzxL());
    Map localMap = this.zzaAL.zzxN();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      if (!this.zzaBk.zzaCf.containsKey(localApi.zzvg())) {
        localHashSet.addAll(((zzg.zza)localMap.get(localApi)).zzakq);
      }
    }
    return localHashSet;
  }
  
  public void begin()
  {
    this.zzaBk.zzaCf.clear();
    this.zzaBu = false;
    this.zzaAZ = null;
    this.zzaBo = 0;
    this.zzaBt = true;
    this.zzaBv = false;
    this.zzaBx = false;
    HashMap localHashMap = new HashMap();
    Object localObject = this.zzaAO.keySet().iterator();
    int i = 0;
    if (((Iterator)localObject).hasNext())
    {
      Api localApi = (Api)((Iterator)localObject).next();
      Api.zze localzze = (Api.zze)this.zzaBk.zzaBQ.get(localApi.zzvg());
      int j;
      label127:
      boolean bool;
      if (localApi.zzve().getPriority() == 1)
      {
        j = 1;
        bool = ((Boolean)this.zzaAO.get(localApi)).booleanValue();
        if (localzze.zzrd())
        {
          this.zzaBu = true;
          if (!bool) {
            break label212;
          }
          this.zzaBr.add(localApi.zzvg());
        }
      }
      for (;;)
      {
        localHashMap.put(localzze, new zza(this, localApi, bool));
        i = j | i;
        break;
        j = 0;
        break label127;
        label212:
        this.zzaBt = false;
      }
    }
    if (i != 0) {
      this.zzaBu = false;
    }
    if (this.zzaBu)
    {
      this.zzaAL.zzc(Integer.valueOf(this.zzaBk.zzaAw.getSessionId()));
      localObject = new zze(null);
      this.zzaBs = ((zzbai)this.zzazo.zza(this.mContext, this.zzaBk.zzaAw.getLooper(), this.zzaAL, this.zzaAL.zzxR(), (GoogleApiClient.ConnectionCallbacks)localObject, (GoogleApiClient.OnConnectionFailedListener)localObject));
    }
    this.zzaBp = this.zzaBk.zzaBQ.size();
    this.zzaBz.add(zzaaw.zzwv().submit(new zzb(localHashMap)));
  }
  
  public void connect() {}
  
  public boolean disconnect()
  {
    zzwk();
    zzat(true);
    this.zzaBk.zzh(null);
    return true;
  }
  
  public void onConnected(Bundle paramBundle)
  {
    if (!zzcB(1)) {}
    do
    {
      return;
      if (paramBundle != null) {
        this.zzaBq.putAll(paramBundle);
      }
    } while (!zzwf());
    zzwi();
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    zzf(new ConnectionResult(8, null));
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(T paramT)
  {
    this.zzaBk.zzaAw.zzaAU.add(paramT);
    return paramT;
  }
  
  public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    if (!zzcB(1)) {}
    do
    {
      return;
      zzb(paramConnectionResult, paramApi, paramBoolean);
    } while (!zzwf());
    zzwi();
  }
  
  public <A extends Api.zzb, T extends zzaad.zza<? extends Result, A>> T zzb(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
  
  private static class zza
    implements zzf.zzf
  {
    private final boolean zzaAu;
    private final WeakReference<zzaar> zzaBB;
    private final Api<?> zzaxf;
    
    public zza(zzaar paramzzaar, Api<?> paramApi, boolean paramBoolean)
    {
      this.zzaBB = new WeakReference(paramzzaar);
      this.zzaxf = paramApi;
      this.zzaAu = paramBoolean;
    }
    
    public void zzg(@NonNull ConnectionResult paramConnectionResult)
    {
      boolean bool = false;
      zzaar localzzaar = (zzaar)this.zzaBB.get();
      if (localzzaar == null) {
        return;
      }
      if (Looper.myLooper() == zzaar.zzd(localzzaar).zzaAw.getLooper()) {
        bool = true;
      }
      zzac.zza(bool, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
      zzaar.zzc(localzzaar).lock();
      try
      {
        bool = zzaar.zza(localzzaar, 0);
        if (!bool) {
          return;
        }
        if (!paramConnectionResult.isSuccess()) {
          zzaar.zza(localzzaar, paramConnectionResult, this.zzaxf, this.zzaAu);
        }
        if (zzaar.zzk(localzzaar)) {
          zzaar.zzj(localzzaar);
        }
        return;
      }
      finally
      {
        zzaar.zzc(localzzaar).unlock();
      }
    }
  }
  
  private class zzb
    extends zzaar.zzf
  {
    private final Map<Api.zze, zzaar.zza> zzaBC;
    
    public zzb()
    {
      super(null);
      Map localMap;
      this.zzaBC = localMap;
    }
    
    @WorkerThread
    public void zzwe()
    {
      int n = 1;
      int m = 0;
      final Object localObject = this.zzaBC.keySet().iterator();
      int j = 1;
      int i = 0;
      Api.zze localzze;
      int k;
      if (((Iterator)localObject).hasNext())
      {
        localzze = (Api.zze)((Iterator)localObject).next();
        if (localzze.zzvh())
        {
          if (zzaar.zza.zza((zzaar.zza)this.zzaBC.get(localzze))) {
            break label301;
          }
          i = 1;
          k = n;
        }
      }
      for (;;)
      {
        if (k != 0) {
          m = zzaar.zzb(zzaar.this).isGooglePlayServicesAvailable(zzaar.zza(zzaar.this));
        }
        if ((m != 0) && ((i != 0) || (j != 0)))
        {
          localObject = new ConnectionResult(m, null);
          zzaar.zzd(zzaar.this).zza(new zzaav.zza(zzaar.this)
          {
            public void zzwe()
            {
              zzaar.zza(zzaar.this, localObject);
            }
          });
          label155:
          return;
          k = 0;
          j = i;
          i = k;
        }
        for (;;)
        {
          k = j;
          j = i;
          i = k;
          break;
          if (zzaar.zze(zzaar.this)) {
            zzaar.zzf(zzaar.this).connect();
          }
          localObject = this.zzaBC.keySet().iterator();
          while (((Iterator)localObject).hasNext())
          {
            localzze = (Api.zze)((Iterator)localObject).next();
            final zzf.zzf localzzf = (zzf.zzf)this.zzaBC.get(localzze);
            if ((localzze.zzvh()) && (m != 0)) {
              zzaar.zzd(zzaar.this).zza(new zzaav.zza(zzaar.this)
              {
                public void zzwe()
                {
                  localzzf.zzg(new ConnectionResult(16, null));
                }
              });
            } else {
              localzze.zza(localzzf);
            }
          }
          break label155;
          label301:
          i = j;
          j = 1;
        }
        k = i;
        i = 0;
      }
    }
  }
  
  private class zzc
    extends zzaar.zzf
  {
    private final ArrayList<Api.zze> zzaBG;
    
    public zzc()
    {
      super(null);
      ArrayList localArrayList;
      this.zzaBG = localArrayList;
    }
    
    @WorkerThread
    public void zzwe()
    {
      zzaar.zzd(zzaar.this).zzaAw.zzaBR = zzaar.zzg(zzaar.this);
      Iterator localIterator = this.zzaBG.iterator();
      while (localIterator.hasNext()) {
        ((Api.zze)localIterator.next()).zza(zzaar.zzh(zzaar.this), zzaar.zzd(zzaar.this).zzaAw.zzaBR);
      }
    }
  }
  
  private static class zzd
    extends zzbam
  {
    private final WeakReference<zzaar> zzaBB;
    
    zzd(zzaar paramzzaar)
    {
      this.zzaBB = new WeakReference(paramzzaar);
    }
    
    @BinderThread
    public void zzb(final zzbaw paramzzbaw)
    {
      final zzaar localzzaar = (zzaar)this.zzaBB.get();
      if (localzzaar == null) {
        return;
      }
      zzaar.zzd(localzzaar).zza(new zzaav.zza(localzzaar)
      {
        public void zzwe()
        {
          zzaar.zza(localzzaar, paramzzbaw);
        }
      });
    }
  }
  
  private class zze
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
  {
    private zze() {}
    
    public void onConnected(Bundle paramBundle)
    {
      zzaar.zzf(zzaar.this).zza(new zzaar.zzd(zzaar.this));
    }
    
    /* Error */
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   4: invokestatic 46	com/google/android/gms/internal/zzaar:zzc	(Lcom/google/android/gms/internal/zzaar;)Ljava/util/concurrent/locks/Lock;
      //   7: invokeinterface 51 1 0
      //   12: aload_0
      //   13: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   16: aload_1
      //   17: invokestatic 55	com/google/android/gms/internal/zzaar:zzb	(Lcom/google/android/gms/internal/zzaar;Lcom/google/android/gms/common/ConnectionResult;)Z
      //   20: ifeq +30 -> 50
      //   23: aload_0
      //   24: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   27: invokestatic 58	com/google/android/gms/internal/zzaar:zzi	(Lcom/google/android/gms/internal/zzaar;)V
      //   30: aload_0
      //   31: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   34: invokestatic 61	com/google/android/gms/internal/zzaar:zzj	(Lcom/google/android/gms/internal/zzaar;)V
      //   37: aload_0
      //   38: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   41: invokestatic 46	com/google/android/gms/internal/zzaar:zzc	(Lcom/google/android/gms/internal/zzaar;)Ljava/util/concurrent/locks/Lock;
      //   44: invokeinterface 64 1 0
      //   49: return
      //   50: aload_0
      //   51: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   54: aload_1
      //   55: invokestatic 67	com/google/android/gms/internal/zzaar:zza	(Lcom/google/android/gms/internal/zzaar;Lcom/google/android/gms/common/ConnectionResult;)V
      //   58: goto -21 -> 37
      //   61: astore_1
      //   62: aload_0
      //   63: getfield 17	com/google/android/gms/internal/zzaar$zze:zzaBA	Lcom/google/android/gms/internal/zzaar;
      //   66: invokestatic 46	com/google/android/gms/internal/zzaar:zzc	(Lcom/google/android/gms/internal/zzaar;)Ljava/util/concurrent/locks/Lock;
      //   69: invokeinterface 64 1 0
      //   74: aload_1
      //   75: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	76	0	this	zze
      //   0	76	1	paramConnectionResult	ConnectionResult
      // Exception table:
      //   from	to	target	type
      //   12	37	61	finally
      //   50	58	61	finally
    }
    
    public void onConnectionSuspended(int paramInt) {}
  }
  
  private abstract class zzf
    implements Runnable
  {
    private zzf() {}
    
    @WorkerThread
    public void run()
    {
      zzaar.zzc(zzaar.this).lock();
      try
      {
        boolean bool = Thread.interrupted();
        if (bool) {
          return;
        }
        zzwe();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        zzaar.zzd(zzaar.this).zza(localRuntimeException);
        return;
      }
      finally
      {
        zzaar.zzc(zzaar.this).unlock();
      }
    }
    
    @WorkerThread
    protected abstract void zzwe();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */