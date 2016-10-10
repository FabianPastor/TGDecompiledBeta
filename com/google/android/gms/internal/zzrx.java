package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzh;

public final class zzrx
{
  public static final Api<Api.ApiOptions.NoOptions> API = new Api("Common.API", fb, fa);
  public static final zzry Dh = new zzrz();
  public static final Api.zzf<zzsb> fa = new Api.zzf();
  private static final Api.zza<zzsb, Api.ApiOptions.NoOptions> fb = new Api.zza()
  {
    public zzsb zzf(Context paramAnonymousContext, Looper paramAnonymousLooper, zzh paramAnonymouszzh, Api.ApiOptions.NoOptions paramAnonymousNoOptions, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
    {
      return new zzsb(paramAnonymousContext, paramAnonymousLooper, paramAnonymouszzh, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
    }
  };
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */