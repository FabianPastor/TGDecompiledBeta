package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;

public final class zzacf
{
  public static final Api<Api.ApiOptions.NoOptions> API = new Api("Common.API", zzaie, zzaid);
  public static final zzacg zzaGM = new zzach();
  public static final Api.zzf<zzacj> zzaid = new Api.zzf();
  private static final Api.zza<zzacj, Api.ApiOptions.NoOptions> zzaie = new Api.zza()
  {
    public zzacj zzf(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, Api.ApiOptions.NoOptions paramAnonymousNoOptions, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
    {
      return new zzacj(paramAnonymousContext, paramAnonymousLooper, paramAnonymouszzg, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
    }
  };
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */