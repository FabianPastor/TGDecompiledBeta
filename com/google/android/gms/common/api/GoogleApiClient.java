package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl;
import com.google.android.gms.common.api.internal.LifecycleActivity;
import com.google.android.gms.common.api.internal.zzav;
import com.google.android.gms.common.api.internal.zzch;
import com.google.android.gms.common.api.internal.zzi;
import com.google.android.gms.common.api.internal.zzp;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.OptionalApiSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;

public abstract class GoogleApiClient
{
  @GuardedBy("sAllClients")
  private static final Set<GoogleApiClient> zzcu = Collections.newSetFromMap(new WeakHashMap());
  
  public abstract ConnectionResult blockingConnect();
  
  public abstract void connect();
  
  public void connect(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract void disconnect();
  
  public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    throw new UnsupportedOperationException();
  }
  
  public <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    throw new UnsupportedOperationException();
  }
  
  public Looper getLooper()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract boolean isConnected();
  
  public abstract void registerConnectionFailedListener(OnConnectionFailedListener paramOnConnectionFailedListener);
  
  public abstract void unregisterConnectionFailedListener(OnConnectionFailedListener paramOnConnectionFailedListener);
  
  public void zza(zzch paramzzch)
  {
    throw new UnsupportedOperationException();
  }
  
  public void zzb(zzch paramzzch)
  {
    throw new UnsupportedOperationException();
  }
  
  public static final class Builder
  {
    private final Context mContext;
    private Looper zzcn;
    private final Set<Scope> zzcv = new HashSet();
    private final Set<Scope> zzcw = new HashSet();
    private int zzcx;
    private View zzcy;
    private String zzcz;
    private String zzda;
    private final Map<Api<?>, ClientSettings.OptionalApiSettings> zzdb = new ArrayMap();
    private final Map<Api<?>, Api.ApiOptions> zzdc = new ArrayMap();
    private LifecycleActivity zzdd;
    private int zzde = -1;
    private GoogleApiClient.OnConnectionFailedListener zzdf;
    private GoogleApiAvailability zzdg = GoogleApiAvailability.getInstance();
    private Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzdh = SignIn.CLIENT_BUILDER;
    private final ArrayList<GoogleApiClient.ConnectionCallbacks> zzdi = new ArrayList();
    private final ArrayList<GoogleApiClient.OnConnectionFailedListener> zzdj = new ArrayList();
    private boolean zzdk = false;
    private Account zzs;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
      this.zzcn = paramContext.getMainLooper();
      this.zzcz = paramContext.getPackageName();
      this.zzda = paramContext.getClass().getName();
    }
    
    public final Builder addApi(Api<? extends Api.ApiOptions.NotRequiredOptions> paramApi)
    {
      Preconditions.checkNotNull(paramApi, "Api must not be null");
      this.zzdc.put(paramApi, null);
      paramApi = paramApi.zzj().getImpliedScopes(null);
      this.zzcw.addAll(paramApi);
      this.zzcv.addAll(paramApi);
      return this;
    }
    
    public final <O extends Api.ApiOptions.HasOptions> Builder addApi(Api<O> paramApi, O paramO)
    {
      Preconditions.checkNotNull(paramApi, "Api must not be null");
      Preconditions.checkNotNull(paramO, "Null options are not permitted for this Api");
      this.zzdc.put(paramApi, paramO);
      paramApi = paramApi.zzj().getImpliedScopes(paramO);
      this.zzcw.addAll(paramApi);
      this.zzcv.addAll(paramApi);
      return this;
    }
    
    public final Builder addConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
    {
      Preconditions.checkNotNull(paramConnectionCallbacks, "Listener must not be null");
      this.zzdi.add(paramConnectionCallbacks);
      return this;
    }
    
    public final Builder addOnConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      Preconditions.checkNotNull(paramOnConnectionFailedListener, "Listener must not be null");
      this.zzdj.add(paramOnConnectionFailedListener);
      return this;
    }
    
    public final GoogleApiClient build()
    {
      boolean bool;
      ClientSettings localClientSettings;
      ArrayMap localArrayMap1;
      ArrayMap localArrayMap2;
      ArrayList localArrayList;
      int i;
      label79:
      Api localApi;
      label128:
      Object localObject3;
      Object localObject4;
      if (!this.zzdc.isEmpty())
      {
        bool = true;
        Preconditions.checkArgument(bool, "must call addApi() to add at least one API");
        localClientSettings = buildClientSettings();
        ??? = null;
        Map localMap = localClientSettings.getOptionalApiSettings();
        localArrayMap1 = new ArrayMap();
        localArrayMap2 = new ArrayMap();
        localArrayList = new ArrayList();
        Iterator localIterator = this.zzdc.keySet().iterator();
        i = 0;
        if (!localIterator.hasNext()) {
          break label330;
        }
        localApi = (Api)localIterator.next();
        Object localObject2 = this.zzdc.get(localApi);
        if (localMap.get(localApi) == null) {
          break label310;
        }
        bool = true;
        localArrayMap1.put(localApi, Boolean.valueOf(bool));
        localObject3 = new zzp(localApi, bool);
        localArrayList.add(localObject3);
        localObject4 = localApi.zzk();
        localObject3 = ((Api.AbstractClientBuilder)localObject4).buildClient(this.mContext, this.zzcn, localClientSettings, localObject2, (GoogleApiClient.ConnectionCallbacks)localObject3, (GoogleApiClient.OnConnectionFailedListener)localObject3);
        localArrayMap2.put(localApi.getClientKey(), localObject3);
        if (((Api.BaseClientBuilder)localObject4).getPriority() != 1) {
          break label573;
        }
        if (localObject2 == null) {
          break label315;
        }
        i = 1;
      }
      label310:
      label315:
      label324:
      label330:
      label561:
      label573:
      for (;;)
      {
        if (((Api.Client)localObject3).providesSignIn())
        {
          localObject4 = localApi;
          if (??? == null) {
            break label324;
          }
          localObject4 = localApi.getName();
          ??? = ((Api)???).getName();
          throw new IllegalStateException(String.valueOf(localObject4).length() + 21 + String.valueOf(???).length() + (String)localObject4 + " cannot be used with " + (String)???);
          bool = false;
          break;
          bool = false;
          break label128;
          i = 0;
          continue;
        }
        localObject4 = ???;
        ??? = localObject4;
        break label79;
        if (??? != null)
        {
          if (i != 0)
          {
            ??? = ((Api)???).getName();
            throw new IllegalStateException(String.valueOf(???).length() + 82 + "With using " + (String)??? + ", GamesOptions can only be specified within GoogleSignInOptions.Builder");
          }
          if (this.zzs != null) {
            break label561;
          }
          bool = true;
        }
        for (;;)
        {
          Preconditions.checkState(bool, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", new Object[] { ((Api)???).getName() });
          Preconditions.checkState(this.zzcv.equals(this.zzcw), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", new Object[] { ((Api)???).getName() });
          i = zzav.zza(localArrayMap2.values(), true);
          localObject4 = new zzav(this.mContext, new ReentrantLock(), this.zzcn, localClientSettings, this.zzdg, this.zzdh, localArrayMap1, this.zzdi, this.zzdj, localArrayMap2, this.zzde, i, localArrayList, false);
          synchronized (GoogleApiClient.zzn())
          {
            GoogleApiClient.zzn().add(localObject4);
            if (this.zzde >= 0) {
              zzi.zza(this.zzdd).zza(this.zzde, (GoogleApiClient)localObject4, this.zzdf);
            }
            return (GoogleApiClient)localObject4;
            bool = false;
          }
        }
      }
    }
    
    public final ClientSettings buildClientSettings()
    {
      SignInOptions localSignInOptions = SignInOptions.DEFAULT;
      if (this.zzdc.containsKey(SignIn.API)) {
        localSignInOptions = (SignInOptions)this.zzdc.get(SignIn.API);
      }
      return new ClientSettings(this.zzs, this.zzcv, this.zzdb, this.zzcx, this.zzcy, this.zzcz, this.zzda, localSignInOptions);
    }
  }
  
  public static abstract interface ConnectionCallbacks
  {
    public abstract void onConnected(Bundle paramBundle);
    
    public abstract void onConnectionSuspended(int paramInt);
  }
  
  public static abstract interface OnConnectionFailedListener
  {
    public abstract void onConnectionFailed(ConnectionResult paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/GoogleApiClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */