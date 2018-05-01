package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
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
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zze;
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

public final class zzbcd
  implements zzbcw
{
  private final Context mContext;
  private final Api.zza<? extends zzctk, zzctl> zzaBe;
  private final zzq zzaCA;
  private final Map<Api<?>, Boolean> zzaCD;
  private final zze zzaCF;
  private ConnectionResult zzaCO;
  private final zzbcx zzaCZ;
  private final Lock zzaCv;
  private int zzaDc;
  private int zzaDd = 0;
  private int zzaDe;
  private final Bundle zzaDf = new Bundle();
  private final Set<Api.zzc> zzaDg = new HashSet();
  private zzctk zzaDh;
  private boolean zzaDi;
  private boolean zzaDj;
  private boolean zzaDk;
  private zzal zzaDl;
  private boolean zzaDm;
  private boolean zzaDn;
  private ArrayList<Future<?>> zzaDo = new ArrayList();
  
  public zzbcd(zzbcx paramzzbcx, zzq paramzzq, Map<Api<?>, Boolean> paramMap, zze paramzze, Api.zza<? extends zzctk, zzctl> paramzza, Lock paramLock, Context paramContext)
  {
    this.zzaCZ = paramzzbcx;
    this.zzaCA = paramzzq;
    this.zzaCD = paramMap;
    this.zzaCF = paramzze;
    this.zzaBe = paramzza;
    this.zzaCv = paramLock;
    this.mContext = paramContext;
  }
  
  private final void zza(zzctx paramzzctx)
  {
    if (!zzan(0)) {
      return;
    }
    Object localObject = paramzzctx.zzpz();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramzzctx.zzAx();
      paramzzctx = ((zzbr)localObject).zzpz();
      if (!paramzzctx.isSuccess())
      {
        localObject = String.valueOf(paramzzctx);
        Log.wtf("GoogleApiClientConnecting", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        zze(paramzzctx);
        return;
      }
      this.zzaDk = true;
      this.zzaDl = ((zzbr)localObject).zzrH();
      this.zzaDm = ((zzbr)localObject).zzrI();
      this.zzaDn = ((zzbr)localObject).zzrJ();
      zzpX();
      return;
    }
    if (zzd((ConnectionResult)localObject))
    {
      zzpZ();
      zzpX();
      return;
    }
    zze((ConnectionResult)localObject);
  }
  
  private final void zzad(boolean paramBoolean)
  {
    if (this.zzaDh != null)
    {
      if ((this.zzaDh.isConnected()) && (paramBoolean)) {
        this.zzaDh.zzAq();
      }
      this.zzaDh.disconnect();
      this.zzaDl = null;
    }
  }
  
  private final boolean zzan(int paramInt)
  {
    if (this.zzaDd != paramInt)
    {
      Log.w("GoogleApiClientConnecting", this.zzaCZ.zzaCl.zzqg());
      String str1 = String.valueOf(this);
      Log.w("GoogleApiClientConnecting", String.valueOf(str1).length() + 23 + "Unexpected callback in " + str1);
      int i = this.zzaDe;
      Log.w("GoogleApiClientConnecting", 33 + "mRemainingConnections=" + i);
      str1 = String.valueOf(zzao(this.zzaDd));
      String str2 = String.valueOf(zzao(paramInt));
      Log.wtf("GoogleApiClientConnecting", String.valueOf(str1).length() + 70 + String.valueOf(str2).length() + "GoogleApiClient connecting is in step " + str1 + " but received callback for step " + str2, new Exception());
      zze(new ConnectionResult(8, null));
      return false;
    }
    return true;
  }
  
  private static String zzao(int paramInt)
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
  
  private final void zzb(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    int j = 1;
    int k = paramApi.zzpb().getPriority();
    if (paramBoolean)
    {
      if (paramConnectionResult.hasResolution())
      {
        i = 1;
        if (i == 0) {
          break label116;
        }
      }
    }
    else
    {
      i = j;
      if (this.zzaCO != null) {
        if (k >= this.zzaDc) {
          break label116;
        }
      }
    }
    label116:
    for (int i = j;; i = 0)
    {
      if (i != 0)
      {
        this.zzaCO = paramConnectionResult;
        this.zzaDc = k;
      }
      this.zzaCZ.zzaDU.put(paramApi.zzpd(), paramConnectionResult);
      return;
      if (this.zzaCF.zzak(paramConnectionResult.getErrorCode()) != null)
      {
        i = 1;
        break;
      }
      i = 0;
      break;
    }
  }
  
  private final boolean zzd(ConnectionResult paramConnectionResult)
  {
    return (this.zzaDi) && (!paramConnectionResult.hasResolution());
  }
  
  private final void zze(ConnectionResult paramConnectionResult)
  {
    zzqa();
    if (!paramConnectionResult.hasResolution()) {}
    for (boolean bool = true;; bool = false)
    {
      zzad(bool);
      this.zzaCZ.zzg(paramConnectionResult);
      this.zzaCZ.zzaDY.zzc(paramConnectionResult);
      return;
    }
  }
  
  private final boolean zzpW()
  {
    this.zzaDe -= 1;
    if (this.zzaDe > 0) {
      return false;
    }
    if (this.zzaDe < 0)
    {
      Log.w("GoogleApiClientConnecting", this.zzaCZ.zzaCl.zzqg());
      Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
      zze(new ConnectionResult(8, null));
      return false;
    }
    if (this.zzaCO != null)
    {
      this.zzaCZ.zzaDX = this.zzaDc;
      zze(this.zzaCO);
      return false;
    }
    return true;
  }
  
  private final void zzpX()
  {
    if (this.zzaDe != 0) {}
    ArrayList localArrayList;
    do
    {
      do
      {
        return;
      } while ((this.zzaDj) && (!this.zzaDk));
      localArrayList = new ArrayList();
      this.zzaDd = 1;
      this.zzaDe = this.zzaCZ.zzaDF.size();
      Iterator localIterator = this.zzaCZ.zzaDF.keySet().iterator();
      while (localIterator.hasNext())
      {
        Api.zzc localzzc = (Api.zzc)localIterator.next();
        if (this.zzaCZ.zzaDU.containsKey(localzzc))
        {
          if (zzpW()) {
            zzpY();
          }
        }
        else {
          localArrayList.add((Api.zze)this.zzaCZ.zzaDF.get(localzzc));
        }
      }
    } while (localArrayList.isEmpty());
    this.zzaDo.add(zzbda.zzqj().submit(new zzbcj(this, localArrayList)));
  }
  
  private final void zzpY()
  {
    this.zzaCZ.zzqi();
    zzbda.zzqj().execute(new zzbce(this));
    if (this.zzaDh != null)
    {
      if (this.zzaDm) {
        this.zzaDh.zza(this.zzaDl, this.zzaDn);
      }
      zzad(false);
    }
    Object localObject = this.zzaCZ.zzaDU.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Api.zzc localzzc = (Api.zzc)((Iterator)localObject).next();
      ((Api.zze)this.zzaCZ.zzaDF.get(localzzc)).disconnect();
    }
    if (this.zzaDf.isEmpty()) {}
    for (localObject = null;; localObject = this.zzaDf)
    {
      this.zzaCZ.zzaDY.zzm((Bundle)localObject);
      return;
    }
  }
  
  private final void zzpZ()
  {
    this.zzaDj = false;
    this.zzaCZ.zzaCl.zzaDG = Collections.emptySet();
    Iterator localIterator = this.zzaDg.iterator();
    while (localIterator.hasNext())
    {
      Api.zzc localzzc = (Api.zzc)localIterator.next();
      if (!this.zzaCZ.zzaDU.containsKey(localzzc)) {
        this.zzaCZ.zzaDU.put(localzzc, new ConnectionResult(17, null));
      }
    }
  }
  
  private final void zzqa()
  {
    ArrayList localArrayList = (ArrayList)this.zzaDo;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = localArrayList.get(i);
      i += 1;
      ((Future)localObject).cancel(true);
    }
    this.zzaDo.clear();
  }
  
  private final Set<Scope> zzqb()
  {
    if (this.zzaCA == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet(this.zzaCA.zzrn());
    Map localMap = this.zzaCA.zzrp();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Api localApi = (Api)localIterator.next();
      if (!this.zzaCZ.zzaDU.containsKey(localApi.zzpd())) {
        localHashSet.addAll(((zzr)localMap.get(localApi)).zzame);
      }
    }
    return localHashSet;
  }
  
  public final void begin()
  {
    this.zzaCZ.zzaDU.clear();
    this.zzaDj = false;
    this.zzaCO = null;
    this.zzaDd = 0;
    this.zzaDi = true;
    this.zzaDk = false;
    this.zzaDm = false;
    HashMap localHashMap = new HashMap();
    Object localObject = this.zzaCD.keySet().iterator();
    int i = 0;
    if (((Iterator)localObject).hasNext())
    {
      Api localApi = (Api)((Iterator)localObject).next();
      Api.zze localzze = (Api.zze)this.zzaCZ.zzaDF.get(localApi.zzpd());
      int j;
      label127:
      boolean bool;
      if (localApi.zzpb().getPriority() == 1)
      {
        j = 1;
        bool = ((Boolean)this.zzaCD.get(localApi)).booleanValue();
        if (localzze.zzmv())
        {
          this.zzaDj = true;
          if (!bool) {
            break label212;
          }
          this.zzaDg.add(localApi.zzpd());
        }
      }
      for (;;)
      {
        localHashMap.put(localzze, new zzbcf(this, localApi, bool));
        i = j | i;
        break;
        j = 0;
        break label127;
        label212:
        this.zzaDi = false;
      }
    }
    if (i != 0) {
      this.zzaDj = false;
    }
    if (this.zzaDj)
    {
      this.zzaCA.zzc(Integer.valueOf(System.identityHashCode(this.zzaCZ.zzaCl)));
      localObject = new zzbcm(this, null);
      this.zzaDh = ((zzctk)this.zzaBe.zza(this.mContext, this.zzaCZ.zzaCl.getLooper(), this.zzaCA, this.zzaCA.zzrt(), (GoogleApiClient.ConnectionCallbacks)localObject, (GoogleApiClient.OnConnectionFailedListener)localObject));
    }
    this.zzaDe = this.zzaCZ.zzaDF.size();
    this.zzaDo.add(zzbda.zzqj().submit(new zzbcg(this, localHashMap)));
  }
  
  public final void connect() {}
  
  public final boolean disconnect()
  {
    zzqa();
    zzad(true);
    this.zzaCZ.zzg(null);
    return true;
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    if (!zzan(1)) {}
    do
    {
      return;
      if (paramBundle != null) {
        this.zzaDf.putAll(paramBundle);
      }
    } while (!zzpW());
    zzpY();
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zze(new ConnectionResult(8, null));
  }
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    if (!zzan(1)) {}
    do
    {
      return;
      zzb(paramConnectionResult, paramApi, paramBoolean);
    } while (!zzpW());
    zzpY();
  }
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(T paramT)
  {
    this.zzaCZ.zzaCl.zzaCJ.add(paramT);
    return paramT;
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */