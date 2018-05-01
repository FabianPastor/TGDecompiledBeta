package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import java.util.Iterator;
import java.util.Set;

public abstract class GmsClient<T extends IInterface>
  extends BaseGmsClient<T>
  implements Api.Client, GmsClientEventManager.GmsClientEventState
{
  private final Set<Scope> mScopes;
  private final ClientSettings zzgf;
  private final Account zzs;
  
  protected GmsClient(Context paramContext, Looper paramLooper, int paramInt, ClientSettings paramClientSettings, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, GmsClientSupervisor.getInstance(paramContext), GoogleApiAvailability.getInstance(), paramInt, paramClientSettings, (GoogleApiClient.ConnectionCallbacks)Preconditions.checkNotNull(paramConnectionCallbacks), (GoogleApiClient.OnConnectionFailedListener)Preconditions.checkNotNull(paramOnConnectionFailedListener));
  }
  
  protected GmsClient(Context paramContext, Looper paramLooper, GmsClientSupervisor paramGmsClientSupervisor, GoogleApiAvailability paramGoogleApiAvailability, int paramInt, ClientSettings paramClientSettings, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, paramGmsClientSupervisor, paramGoogleApiAvailability, paramInt, zza(paramConnectionCallbacks), zza(paramOnConnectionFailedListener), paramClientSettings.getRealClientClassName());
    this.zzgf = paramClientSettings;
    this.zzs = paramClientSettings.getAccount();
    this.mScopes = zza(paramClientSettings.getAllRequestedScopes());
  }
  
  private static BaseGmsClient.BaseConnectionCallbacks zza(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    if (paramConnectionCallbacks == null) {}
    for (paramConnectionCallbacks = null;; paramConnectionCallbacks = new zzf(paramConnectionCallbacks)) {
      return paramConnectionCallbacks;
    }
  }
  
  private static BaseGmsClient.BaseOnConnectionFailedListener zza(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    if (paramOnConnectionFailedListener == null) {}
    for (paramOnConnectionFailedListener = null;; paramOnConnectionFailedListener = new zzg(paramOnConnectionFailedListener)) {
      return paramOnConnectionFailedListener;
    }
  }
  
  private final Set<Scope> zza(Set<Scope> paramSet)
  {
    Set localSet = validateScopes(paramSet);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext()) {
      if (!paramSet.contains((Scope)localIterator.next())) {
        throw new IllegalStateException("Expanding scopes is not permitted, use implied scopes instead");
      }
    }
    return localSet;
  }
  
  public final Account getAccount()
  {
    return this.zzs;
  }
  
  public int getMinApkVersion()
  {
    return super.getMinApkVersion();
  }
  
  public Feature[] getRequiredFeatures()
  {
    return new Feature[0];
  }
  
  protected final Set<Scope> getScopes()
  {
    return this.mScopes;
  }
  
  protected Set<Scope> validateScopes(Set<Scope> paramSet)
  {
    return paramSet;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GmsClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */