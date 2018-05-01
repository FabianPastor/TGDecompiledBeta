package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.BaseClientBuilder;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.OptionalApiSettings;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.internal.SignInResponse;
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
import javax.annotation.concurrent.GuardedBy;

public final class zzaj
  implements zzbc
{
  private final Context mContext;
  private final Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzdh;
  private final Lock zzga;
  private final ClientSettings zzgf;
  private final Map<Api<?>, Boolean> zzgi;
  private final GoogleApiAvailabilityLight zzgk;
  private ConnectionResult zzgt;
  private final zzbd zzhf;
  private int zzhi;
  private int zzhj = 0;
  private int zzhk;
  private final Bundle zzhl = new Bundle();
  private final Set<Api.AnyClientKey> zzhm = new HashSet();
  private SignInClient zzhn;
  private boolean zzho;
  private boolean zzhp;
  private boolean zzhq;
  private IAccountAccessor zzhr;
  private boolean zzhs;
  private boolean zzht;
  private ArrayList<Future<?>> zzhu = new ArrayList();
  
  public zzaj(zzbd paramzzbd, ClientSettings paramClientSettings, Map<Api<?>, Boolean> paramMap, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder, Lock paramLock, Context paramContext)
  {
    this.zzhf = paramzzbd;
    this.zzgf = paramClientSettings;
    this.zzgi = paramMap;
    this.zzgk = paramGoogleApiAvailabilityLight;
    this.zzdh = paramAbstractClientBuilder;
    this.zzga = paramLock;
    this.mContext = paramContext;
  }
  
  @GuardedBy("mLock")
  private final void zza(SignInResponse paramSignInResponse)
  {
    if (!zze(0)) {}
    for (;;)
    {
      return;
      Object localObject = paramSignInResponse.getConnectionResult();
      if (((ConnectionResult)localObject).isSuccess())
      {
        localObject = paramSignInResponse.getResolveAccountResponse();
        paramSignInResponse = ((ResolveAccountResponse)localObject).getConnectionResult();
        if (!paramSignInResponse.isSuccess())
        {
          localObject = String.valueOf(paramSignInResponse);
          Log.wtf("GoogleApiClientConnecting", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
          zze(paramSignInResponse);
        }
        else
        {
          this.zzhq = true;
          this.zzhr = ((ResolveAccountResponse)localObject).getAccountAccessor();
          this.zzhs = ((ResolveAccountResponse)localObject).getSaveDefaultAccount();
          this.zzht = ((ResolveAccountResponse)localObject).isFromCrossClientAuth();
          zzas();
        }
      }
      else if (zzd((ConnectionResult)localObject))
      {
        zzau();
        zzas();
      }
      else
      {
        zze((ConnectionResult)localObject);
      }
    }
  }
  
  private final void zza(boolean paramBoolean)
  {
    if (this.zzhn != null)
    {
      if ((this.zzhn.isConnected()) && (paramBoolean)) {
        this.zzhn.clearAccountFromSessionStore();
      }
      this.zzhn.disconnect();
      this.zzhr = null;
    }
  }
  
  @GuardedBy("mLock")
  private final boolean zzar()
  {
    boolean bool = false;
    this.zzhk -= 1;
    if (this.zzhk > 0) {}
    for (;;)
    {
      return bool;
      if (this.zzhk < 0)
      {
        Log.w("GoogleApiClientConnecting", this.zzhf.zzfq.zzbb());
        Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
        zze(new ConnectionResult(8, null));
      }
      else if (this.zzgt != null)
      {
        this.zzhf.zzje = this.zzhi;
        zze(this.zzgt);
      }
      else
      {
        bool = true;
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzas()
  {
    if (this.zzhk != 0) {}
    for (;;)
    {
      return;
      if ((!this.zzhp) || (this.zzhq))
      {
        ArrayList localArrayList = new ArrayList();
        this.zzhj = 1;
        this.zzhk = this.zzhf.zzil.size();
        Iterator localIterator = this.zzhf.zzil.keySet().iterator();
        while (localIterator.hasNext())
        {
          Api.AnyClientKey localAnyClientKey = (Api.AnyClientKey)localIterator.next();
          if (this.zzhf.zzjb.containsKey(localAnyClientKey))
          {
            if (zzar()) {
              zzat();
            }
          }
          else {
            localArrayList.add((Api.Client)this.zzhf.zzil.get(localAnyClientKey));
          }
        }
        if (!localArrayList.isEmpty()) {
          this.zzhu.add(zzbg.zzbe().submit(new zzap(this, localArrayList)));
        }
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzat()
  {
    this.zzhf.zzbd();
    zzbg.zzbe().execute(new zzak(this));
    if (this.zzhn != null)
    {
      if (this.zzhs) {
        this.zzhn.saveDefaultAccount(this.zzhr, this.zzht);
      }
      zza(false);
    }
    Object localObject = this.zzhf.zzjb.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Api.AnyClientKey localAnyClientKey = (Api.AnyClientKey)((Iterator)localObject).next();
      ((Api.Client)this.zzhf.zzil.get(localAnyClientKey)).disconnect();
    }
    if (this.zzhl.isEmpty()) {}
    for (localObject = null;; localObject = this.zzhl)
    {
      this.zzhf.zzjf.zzb((Bundle)localObject);
      return;
    }
  }
  
  @GuardedBy("mLock")
  private final void zzau()
  {
    this.zzhp = false;
    this.zzhf.zzfq.zzim = Collections.emptySet();
    Iterator localIterator = this.zzhm.iterator();
    while (localIterator.hasNext())
    {
      Api.AnyClientKey localAnyClientKey = (Api.AnyClientKey)localIterator.next();
      if (!this.zzhf.zzjb.containsKey(localAnyClientKey)) {
        this.zzhf.zzjb.put(localAnyClientKey, new ConnectionResult(17, null));
      }
    }
  }
  
  private final void zzav()
  {
    ArrayList localArrayList = (ArrayList)this.zzhu;
    int i = localArrayList.size();
    int j = 0;
    while (j < i)
    {
      Object localObject = localArrayList.get(j);
      j++;
      ((Future)localObject).cancel(true);
    }
    this.zzhu.clear();
  }
  
  private final Set<Scope> zzaw()
  {
    Object localObject;
    if (this.zzgf == null) {
      localObject = Collections.emptySet();
    }
    for (;;)
    {
      return (Set<Scope>)localObject;
      localObject = new HashSet(this.zzgf.getRequiredScopes());
      Map localMap = this.zzgf.getOptionalApiSettings();
      Iterator localIterator = localMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Api localApi = (Api)localIterator.next();
        if (!this.zzhf.zzjb.containsKey(localApi.getClientKey())) {
          ((Set)localObject).addAll(((ClientSettings.OptionalApiSettings)localMap.get(localApi)).mScopes);
        }
      }
    }
  }
  
  @GuardedBy("mLock")
  private final void zzb(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    int i = 1;
    int j = paramApi.zzj().getPriority();
    if (paramBoolean)
    {
      if (paramConnectionResult.hasResolution())
      {
        k = 1;
        if (k == 0) {
          break label116;
        }
      }
    }
    else
    {
      k = i;
      if (this.zzgt != null) {
        if (j >= this.zzhi) {
          break label116;
        }
      }
    }
    label116:
    for (int k = i;; k = 0)
    {
      if (k != 0)
      {
        this.zzgt = paramConnectionResult;
        this.zzhi = j;
      }
      this.zzhf.zzjb.put(paramApi.getClientKey(), paramConnectionResult);
      return;
      if (this.zzgk.getErrorResolutionIntent(paramConnectionResult.getErrorCode()) != null)
      {
        k = 1;
        break;
      }
      k = 0;
      break;
    }
  }
  
  @GuardedBy("mLock")
  private final boolean zzd(ConnectionResult paramConnectionResult)
  {
    if ((this.zzho) && (!paramConnectionResult.hasResolution())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @GuardedBy("mLock")
  private final void zze(ConnectionResult paramConnectionResult)
  {
    zzav();
    if (!paramConnectionResult.hasResolution()) {}
    for (boolean bool = true;; bool = false)
    {
      zza(bool);
      this.zzhf.zzf(paramConnectionResult);
      this.zzhf.zzjf.zzc(paramConnectionResult);
      return;
    }
  }
  
  @GuardedBy("mLock")
  private final boolean zze(int paramInt)
  {
    if (this.zzhj != paramInt)
    {
      Log.w("GoogleApiClientConnecting", this.zzhf.zzfq.zzbb());
      String str1 = String.valueOf(this);
      Log.w("GoogleApiClientConnecting", String.valueOf(str1).length() + 23 + "Unexpected callback in " + str1);
      int i = this.zzhk;
      Log.w("GoogleApiClientConnecting", 33 + "mRemainingConnections=" + i);
      str1 = zzf(this.zzhj);
      String str2 = zzf(paramInt);
      Log.wtf("GoogleApiClientConnecting", String.valueOf(str1).length() + 70 + String.valueOf(str2).length() + "GoogleApiClient connecting is in step " + str1 + " but received callback for step " + str2, new Exception());
      zze(new ConnectionResult(8, null));
    }
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  private static String zzf(int paramInt)
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
      str = "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
      continue;
      str = "STEP_GETTING_REMOTE_SERVICE";
    }
  }
  
  public final void begin()
  {
    this.zzhf.zzjb.clear();
    this.zzhp = false;
    this.zzgt = null;
    this.zzhj = 0;
    this.zzho = true;
    this.zzhq = false;
    this.zzhs = false;
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.zzgi.keySet().iterator();
    int i = 0;
    Object localObject;
    if (localIterator.hasNext())
    {
      localObject = (Api)localIterator.next();
      Api.Client localClient = (Api.Client)this.zzhf.zzil.get(((Api)localObject).getClientKey());
      int j;
      label124:
      boolean bool;
      if (((Api)localObject).zzj().getPriority() == 1)
      {
        j = 1;
        bool = ((Boolean)this.zzgi.get(localObject)).booleanValue();
        if (localClient.requiresSignIn())
        {
          this.zzhp = true;
          if (!bool) {
            break label213;
          }
          this.zzhm.add(((Api)localObject).getClientKey());
        }
      }
      for (;;)
      {
        localHashMap.put(localClient, new zzal(this, (Api)localObject, bool));
        i = j | i;
        break;
        j = 0;
        break label124;
        label213:
        this.zzho = false;
      }
    }
    if (i != 0) {
      this.zzhp = false;
    }
    if (this.zzhp)
    {
      this.zzgf.setClientSessionId(Integer.valueOf(System.identityHashCode(this.zzhf.zzfq)));
      localObject = new zzas(this, null);
      this.zzhn = ((SignInClient)this.zzdh.buildClient(this.mContext, this.zzhf.zzfq.getLooper(), this.zzgf, this.zzgf.getSignInOptions(), (GoogleApiClient.ConnectionCallbacks)localObject, (GoogleApiClient.OnConnectionFailedListener)localObject));
    }
    this.zzhk = this.zzhf.zzil.size();
    this.zzhu.add(zzbg.zzbe().submit(new zzam(this, localHashMap)));
  }
  
  public final void connect() {}
  
  public final boolean disconnect()
  {
    zzav();
    zza(true);
    this.zzhf.zzf(null);
    return true;
  }
  
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    this.zzhf.zzfq.zzgo.add(paramT);
    return paramT;
  }
  
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
  
  @GuardedBy("mLock")
  public final void onConnected(Bundle paramBundle)
  {
    if (!zze(1)) {}
    for (;;)
    {
      return;
      if (paramBundle != null) {
        this.zzhl.putAll(paramBundle);
      }
      if (zzar()) {
        zzat();
      }
    }
  }
  
  @GuardedBy("mLock")
  public final void onConnectionSuspended(int paramInt)
  {
    zze(new ConnectionResult(8, null));
  }
  
  @GuardedBy("mLock")
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean)
  {
    if (!zze(1)) {}
    for (;;)
    {
      return;
      zzb(paramConnectionResult, paramApi, paramBoolean);
      if (zzar()) {
        zzat();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */