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
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzh.zza;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zzc;
import com.google.android.gms.signin.internal.SignInResponse;
import com.google.android.gms.signin.internal.zzb;
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

public class zzqn
  implements zzqq
{
  private final Context mContext;
  private final Api.zza<? extends zzwz, zzxa> vQ;
  private boolean xA;
  private final zzh xB;
  private final Map<Api<?>, Integer> xC;
  private ArrayList<Future<?>> xD = new ArrayList();
  private final Lock xf;
  private final zzqr xk;
  private final zzc xn;
  private ConnectionResult xo;
  private int xp;
  private int xq = 0;
  private int xr;
  private final Bundle xs = new Bundle();
  private final Set<Api.zzc> xt = new HashSet();
  private zzwz xu;
  private int xv;
  private boolean xw;
  private boolean xx;
  private zzr xy;
  private boolean xz;
  
  public zzqn(zzqr paramzzqr, zzh paramzzh, Map<Api<?>, Integer> paramMap, zzc paramzzc, Api.zza<? extends zzwz, zzxa> paramzza, Lock paramLock, Context paramContext)
  {
    this.xk = paramzzqr;
    this.xB = paramzzh;
    this.xC = paramMap;
    this.xn = paramzzc;
    this.vQ = paramzza;
    this.xf = paramLock;
    this.mContext = paramContext;
  }
  
  private void zza(SignInResponse paramSignInResponse)
  {
    if (!zzfr(0)) {
      return;
    }
    Object localObject = paramSignInResponse.zzave();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramSignInResponse.zzcdl();
      paramSignInResponse = ((ResolveAccountResponse)localObject).zzave();
      if (!paramSignInResponse.isSuccess())
      {
        localObject = String.valueOf(paramSignInResponse);
        Log.wtf("GoogleApiClientConnecting", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        zzg(paramSignInResponse);
        return;
      }
      this.xx = true;
      this.xy = ((ResolveAccountResponse)localObject).zzavd();
      this.xz = ((ResolveAccountResponse)localObject).zzavf();
      this.xA = ((ResolveAccountResponse)localObject).zzavg();
      zzark();
      return;
    }
    if (zzf((ConnectionResult)localObject))
    {
      zzarn();
      zzark();
      return;
    }
    zzg((ConnectionResult)localObject);
  }
  
  private boolean zza(int paramInt1, int paramInt2, ConnectionResult paramConnectionResult)
  {
    if ((paramInt2 == 1) && (!zze(paramConnectionResult))) {}
    while ((this.xo != null) && (paramInt1 >= this.xp)) {
      return false;
    }
    return true;
  }
  
  private boolean zzarj()
  {
    this.xr -= 1;
    if (this.xr > 0) {
      return false;
    }
    if (this.xr < 0)
    {
      Log.w("GoogleApiClientConnecting", this.xk.wV.zzarv());
      Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
      zzg(new ConnectionResult(8, null));
      return false;
    }
    if (this.xo != null)
    {
      this.xk.yo = this.xp;
      zzg(this.xo);
      return false;
    }
    return true;
  }
  
  private void zzark()
  {
    if (this.xr != 0) {}
    while ((this.xw) && (!this.xx)) {
      return;
    }
    zzarl();
  }
  
  private void zzarl()
  {
    ArrayList localArrayList = new ArrayList();
    this.xq = 1;
    this.xr = this.xk.xW.size();
    Iterator localIterator = this.xk.xW.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api.zzc localzzc = (Api.zzc)localIterator.next();
      if (this.xk.yl.containsKey(localzzc))
      {
        if (zzarj()) {
          zzarm();
        }
      }
      else {
        localArrayList.add((Api.zze)this.xk.xW.get(localzzc));
      }
    }
    if (!localArrayList.isEmpty()) {
      this.xD.add(zzqs.zzarz().submit(new zzc(localArrayList)));
    }
  }
  
  private void zzarm()
  {
    this.xk.zzarx();
    zzqs.zzarz().execute(new Runnable()
    {
      public void run()
      {
        zzqn.zzb(zzqn.this).zzbq(zzqn.zza(zzqn.this));
      }
    });
    if (this.xu != null)
    {
      if (this.xz) {
        this.xu.zza(this.xy, this.xA);
      }
      zzbq(false);
    }
    Object localObject = this.xk.yl.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Api.zzc localzzc = (Api.zzc)((Iterator)localObject).next();
      ((Api.zze)this.xk.xW.get(localzzc)).disconnect();
    }
    if (this.xs.isEmpty()) {}
    for (localObject = null;; localObject = this.xs)
    {
      this.xk.yp.zzn((Bundle)localObject);
      return;
    }
  }
  
  private void zzarn()
  {
    this.xw = false;
    this.xk.wV.xX = Collections.emptySet();
    Iterator localIterator = this.xt.iterator();
    while (localIterator.hasNext())
    {
      Api.zzc localzzc = (Api.zzc)localIterator.next();
      if (!this.xk.yl.containsKey(localzzc)) {
        this.xk.yl.put(localzzc, new ConnectionResult(17, null));
      }
    }
  }
  
  private void zzaro()
  {
    Iterator localIterator = this.xD.iterator();
    while (localIterator.hasNext()) {
      ((Future)localIterator.next()).cancel(true);
    }
    this.xD.clear();
  }
  
  private Set<Scope> zzarp()
  {
    if (this.xB == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet(this.xB.zzaug());
    Map localMap = this.xB.zzaui();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      if (!this.xk.yl.containsKey(localApi.zzapp())) {
        localHashSet.addAll(((zzh.zza)localMap.get(localApi)).hm);
      }
    }
    return localHashSet;
  }
  
  private void zzb(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt)
  {
    if (paramInt != 2)
    {
      int i = paramApi.zzapm().getPriority();
      if (zza(i, paramInt, paramConnectionResult))
      {
        this.xo = paramConnectionResult;
        this.xp = i;
      }
    }
    this.xk.yl.put(paramApi.zzapp(), paramConnectionResult);
  }
  
  private void zzbq(boolean paramBoolean)
  {
    if (this.xu != null)
    {
      if ((this.xu.isConnected()) && (paramBoolean)) {
        this.xu.zzcda();
      }
      this.xu.disconnect();
      this.xy = null;
    }
  }
  
  private boolean zze(ConnectionResult paramConnectionResult)
  {
    if (paramConnectionResult.hasResolution()) {}
    while (this.xn.zzfl(paramConnectionResult.getErrorCode()) != null) {
      return true;
    }
    return false;
  }
  
  private boolean zzf(ConnectionResult paramConnectionResult)
  {
    return (this.xv == 2) || ((this.xv == 1) && (!paramConnectionResult.hasResolution()));
  }
  
  private boolean zzfr(int paramInt)
  {
    if (this.xq != paramInt)
    {
      Log.w("GoogleApiClientConnecting", this.xk.wV.zzarv());
      String str1 = String.valueOf(this);
      Log.w("GoogleApiClientConnecting", String.valueOf(str1).length() + 23 + "Unexpected callback in " + str1);
      int i = this.xr;
      Log.w("GoogleApiClientConnecting", 33 + "mRemainingConnections=" + i);
      str1 = String.valueOf(zzfs(this.xq));
      String str2 = String.valueOf(zzfs(paramInt));
      Log.wtf("GoogleApiClientConnecting", String.valueOf(str1).length() + 70 + String.valueOf(str2).length() + "GoogleApiClient connecting is in step " + str1 + " but received callback for step " + str2, new Exception());
      zzg(new ConnectionResult(8, null));
      return false;
    }
    return true;
  }
  
  private String zzfs(int paramInt)
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
  
  private void zzg(ConnectionResult paramConnectionResult)
  {
    zzaro();
    if (!paramConnectionResult.hasResolution()) {}
    for (boolean bool = true;; bool = false)
    {
      zzbq(bool);
      this.xk.zzi(paramConnectionResult);
      this.xk.yp.zzd(paramConnectionResult);
      return;
    }
  }
  
  public void begin()
  {
    this.xk.yl.clear();
    this.xw = false;
    this.xo = null;
    this.xq = 0;
    this.xv = 2;
    this.xx = false;
    this.xz = false;
    HashMap localHashMap = new HashMap();
    Object localObject = this.xC.keySet().iterator();
    int i = 0;
    if (((Iterator)localObject).hasNext())
    {
      Api localApi = (Api)((Iterator)localObject).next();
      Api.zze localzze = (Api.zze)this.xk.xW.get(localApi.zzapp());
      int k = ((Integer)this.xC.get(localApi)).intValue();
      if (localApi.zzapm().getPriority() == 1) {}
      for (int j = 1;; j = 0)
      {
        if (localzze.zzahd())
        {
          this.xw = true;
          if (k < this.xv) {
            this.xv = k;
          }
          if (k != 0) {
            this.xt.add(localApi.zzapp());
          }
        }
        localHashMap.put(localzze, new zza(this, localApi, k));
        i = j | i;
        break;
      }
    }
    if (i != 0) {
      this.xw = false;
    }
    if (this.xw)
    {
      this.xB.zzc(Integer.valueOf(this.xk.wV.getSessionId()));
      localObject = new zze(null);
      this.xu = ((zzwz)this.vQ.zza(this.mContext, this.xk.wV.getLooper(), this.xB, this.xB.zzaum(), (GoogleApiClient.ConnectionCallbacks)localObject, (GoogleApiClient.OnConnectionFailedListener)localObject));
    }
    this.xr = this.xk.xW.size();
    this.xD.add(zzqs.zzarz().submit(new zzb(localHashMap)));
  }
  
  public void connect() {}
  
  public boolean disconnect()
  {
    zzaro();
    zzbq(true);
    this.xk.zzi(null);
    return true;
  }
  
  public void onConnected(Bundle paramBundle)
  {
    if (!zzfr(1)) {}
    do
    {
      return;
      if (paramBundle != null) {
        this.xs.putAll(paramBundle);
      }
    } while (!zzarj());
    zzarm();
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    zzg(new ConnectionResult(8, null));
  }
  
  public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt)
  {
    if (!zzfr(1)) {}
    do
    {
      return;
      zzb(paramConnectionResult, paramApi, paramInt);
    } while (!zzarj());
    zzarm();
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(T paramT)
  {
    this.xk.wV.xQ.add(paramT);
    return paramT;
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
  
  private static class zza
    implements zze.zzf
  {
    private final Api<?> tv;
    private final int wT;
    private final WeakReference<zzqn> xF;
    
    public zza(zzqn paramzzqn, Api<?> paramApi, int paramInt)
    {
      this.xF = new WeakReference(paramzzqn);
      this.tv = paramApi;
      this.wT = paramInt;
    }
    
    public void zzh(@NonNull ConnectionResult paramConnectionResult)
    {
      boolean bool = false;
      zzqn localzzqn = (zzqn)this.xF.get();
      if (localzzqn == null) {
        return;
      }
      if (Looper.myLooper() == zzqn.zzd(localzzqn).wV.getLooper()) {
        bool = true;
      }
      zzac.zza(bool, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
      zzqn.zzc(localzzqn).lock();
      try
      {
        bool = zzqn.zza(localzzqn, 0);
        if (!bool) {
          return;
        }
        if (!paramConnectionResult.isSuccess()) {
          zzqn.zza(localzzqn, paramConnectionResult, this.tv, this.wT);
        }
        if (zzqn.zzk(localzzqn)) {
          zzqn.zzj(localzzqn);
        }
        return;
      }
      finally
      {
        zzqn.zzc(localzzqn).unlock();
      }
    }
  }
  
  private class zzb
    extends zzqn.zzf
  {
    private final Map<Api.zze, zzqn.zza> xG;
    
    public zzb()
    {
      super(null);
      Map localMap;
      this.xG = localMap;
    }
    
    @WorkerThread
    public void zzari()
    {
      int n = 1;
      int m = 0;
      final Object localObject = this.xG.keySet().iterator();
      int j = 1;
      int i = 0;
      Api.zze localzze;
      int k;
      if (((Iterator)localObject).hasNext())
      {
        localzze = (Api.zze)((Iterator)localObject).next();
        if (localzze.zzapr())
        {
          if (zzqn.zza.zza((zzqn.zza)this.xG.get(localzze)) != 0) {
            break label301;
          }
          i = 1;
          k = n;
        }
      }
      for (;;)
      {
        if (k != 0) {
          m = zzqn.zzb(zzqn.this).isGooglePlayServicesAvailable(zzqn.zza(zzqn.this));
        }
        if ((m != 0) && ((i != 0) || (j != 0)))
        {
          localObject = new ConnectionResult(m, null);
          zzqn.zzd(zzqn.this).zza(new zzqr.zza(zzqn.this)
          {
            public void zzari()
            {
              zzqn.zza(zzqn.this, localObject);
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
          if (zzqn.zze(zzqn.this)) {
            zzqn.zzf(zzqn.this).connect();
          }
          localObject = this.xG.keySet().iterator();
          while (((Iterator)localObject).hasNext())
          {
            localzze = (Api.zze)((Iterator)localObject).next();
            final zze.zzf localzzf = (zze.zzf)this.xG.get(localzze);
            if ((localzze.zzapr()) && (m != 0)) {
              zzqn.zzd(zzqn.this).zza(new zzqr.zza(zzqn.this)
              {
                public void zzari()
                {
                  localzzf.zzh(new ConnectionResult(16, null));
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
    extends zzqn.zzf
  {
    private final ArrayList<Api.zze> xK;
    
    public zzc()
    {
      super(null);
      ArrayList localArrayList;
      this.xK = localArrayList;
    }
    
    @WorkerThread
    public void zzari()
    {
      zzqn.zzd(zzqn.this).wV.xX = zzqn.zzg(zzqn.this);
      Iterator localIterator = this.xK.iterator();
      while (localIterator.hasNext()) {
        ((Api.zze)localIterator.next()).zza(zzqn.zzh(zzqn.this), zzqn.zzd(zzqn.this).wV.xX);
      }
    }
  }
  
  private static class zzd
    extends zzb
  {
    private final WeakReference<zzqn> xF;
    
    zzd(zzqn paramzzqn)
    {
      this.xF = new WeakReference(paramzzqn);
    }
    
    @BinderThread
    public void zzb(final SignInResponse paramSignInResponse)
    {
      final zzqn localzzqn = (zzqn)this.xF.get();
      if (localzzqn == null) {
        return;
      }
      zzqn.zzd(localzzqn).zza(new zzqr.zza(localzzqn)
      {
        public void zzari()
        {
          zzqn.zza(localzzqn, paramSignInResponse);
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
      zzqn.zzf(zzqn.this).zza(new zzqn.zzd(zzqn.this));
    }
    
    /* Error */
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   4: invokestatic 46	com/google/android/gms/internal/zzqn:zzc	(Lcom/google/android/gms/internal/zzqn;)Ljava/util/concurrent/locks/Lock;
      //   7: invokeinterface 51 1 0
      //   12: aload_0
      //   13: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   16: aload_1
      //   17: invokestatic 55	com/google/android/gms/internal/zzqn:zzb	(Lcom/google/android/gms/internal/zzqn;Lcom/google/android/gms/common/ConnectionResult;)Z
      //   20: ifeq +30 -> 50
      //   23: aload_0
      //   24: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   27: invokestatic 58	com/google/android/gms/internal/zzqn:zzi	(Lcom/google/android/gms/internal/zzqn;)V
      //   30: aload_0
      //   31: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   34: invokestatic 61	com/google/android/gms/internal/zzqn:zzj	(Lcom/google/android/gms/internal/zzqn;)V
      //   37: aload_0
      //   38: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   41: invokestatic 46	com/google/android/gms/internal/zzqn:zzc	(Lcom/google/android/gms/internal/zzqn;)Ljava/util/concurrent/locks/Lock;
      //   44: invokeinterface 64 1 0
      //   49: return
      //   50: aload_0
      //   51: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   54: aload_1
      //   55: invokestatic 67	com/google/android/gms/internal/zzqn:zza	(Lcom/google/android/gms/internal/zzqn;Lcom/google/android/gms/common/ConnectionResult;)V
      //   58: goto -21 -> 37
      //   61: astore_1
      //   62: aload_0
      //   63: getfield 17	com/google/android/gms/internal/zzqn$zze:xE	Lcom/google/android/gms/internal/zzqn;
      //   66: invokestatic 46	com/google/android/gms/internal/zzqn:zzc	(Lcom/google/android/gms/internal/zzqn;)Ljava/util/concurrent/locks/Lock;
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
      zzqn.zzc(zzqn.this).lock();
      try
      {
        boolean bool = Thread.interrupted();
        if (bool) {
          return;
        }
        zzari();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        zzqn.zzd(zzqn.this).zza(localRuntimeException);
        return;
      }
      finally
      {
        zzqn.zzc(zzqn.this).unlock();
      }
    }
    
    @WorkerThread
    protected abstract void zzari();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */