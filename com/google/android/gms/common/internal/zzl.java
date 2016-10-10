package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import java.util.Iterator;
import java.util.Set;

public abstract class zzl<T extends IInterface>
  extends zze<T>
  implements Api.zze, zzm.zza
{
  private final Account ec;
  private final Set<Scope> hm;
  private final zzh xB;
  
  protected zzl(Context paramContext, Looper paramLooper, int paramInt, zzh paramzzh, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, zzn.zzcf(paramContext), GoogleApiAvailability.getInstance(), paramInt, paramzzh, (GoogleApiClient.ConnectionCallbacks)zzac.zzy(paramConnectionCallbacks), (GoogleApiClient.OnConnectionFailedListener)zzac.zzy(paramOnConnectionFailedListener));
  }
  
  protected zzl(Context paramContext, Looper paramLooper, zzn paramzzn, GoogleApiAvailability paramGoogleApiAvailability, int paramInt, zzh paramzzh, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, paramzzn, paramGoogleApiAvailability, paramInt, zza(paramConnectionCallbacks), zza(paramOnConnectionFailedListener), paramzzh.zzauk());
    this.xB = paramzzh;
    this.ec = paramzzh.getAccount();
    this.hm = zzb(paramzzh.zzauh());
  }
  
  @Nullable
  private static zze.zzb zza(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    if (paramConnectionCallbacks == null) {
      return null;
    }
    new zze.zzb()
    {
      public void onConnected(@Nullable Bundle paramAnonymousBundle)
      {
        zzl.this.onConnected(paramAnonymousBundle);
      }
      
      public void onConnectionSuspended(int paramAnonymousInt)
      {
        zzl.this.onConnectionSuspended(paramAnonymousInt);
      }
    };
  }
  
  @Nullable
  private static zze.zzc zza(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    if (paramOnConnectionFailedListener == null) {
      return null;
    }
    new zze.zzc()
    {
      public void onConnectionFailed(@NonNull ConnectionResult paramAnonymousConnectionResult)
      {
        zzl.this.onConnectionFailed(paramAnonymousConnectionResult);
      }
    };
  }
  
  private Set<Scope> zzb(@NonNull Set<Scope> paramSet)
  {
    Set localSet = zzc(paramSet);
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
    return this.ec;
  }
  
  protected final Set<Scope> zzatz()
  {
    return this.hm;
  }
  
  protected final zzh zzaus()
  {
    return this.xB;
  }
  
  @NonNull
  protected Set<Scope> zzc(@NonNull Set<Scope> paramSet)
  {
    return paramSet;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */