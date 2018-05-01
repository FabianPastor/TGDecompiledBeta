package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import java.util.Set;

public abstract class zzab<T extends IInterface>
  extends zzd<T>
  implements Api.zze, zzaf
{
  private final Account zzebz;
  private final zzr zzfpx;
  
  protected zzab(Context paramContext, Looper paramLooper, int paramInt, zzr paramzzr, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, zzag.zzco(paramContext), GoogleApiAvailability.getInstance(), paramInt, paramzzr, (GoogleApiClient.ConnectionCallbacks)zzbq.checkNotNull(paramConnectionCallbacks), (GoogleApiClient.OnConnectionFailedListener)zzbq.checkNotNull(paramOnConnectionFailedListener));
  }
  
  private zzab(Context paramContext, Looper paramLooper, zzag paramzzag, GoogleApiAvailability paramGoogleApiAvailability, int paramInt, zzr paramzzr, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener) {}
  
  public final Account getAccount()
  {
    return this.zzebz;
  }
  
  public zzc[] zzakl()
  {
    return new zzc[0];
  }
  
  protected final Set<Scope> zzakp()
  {
    return this.zzehs;
  }
  
  protected Set<Scope> zzb(Set<Scope> paramSet)
  {
    return paramSet;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */