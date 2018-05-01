package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.signin.internal.zzg;

public final class zzxo
{
  public static final Api<zzxq> API = new Api("SignIn.API", hh, hg);
  public static final Api<zza> Jb = new Api("SignIn.INTERNAL_API", aDj, aDi);
  public static final Api.zzf<zzg> aDi;
  static final Api.zza<zzg, zza> aDj;
  public static final Api.zzf<zzg> hg = new Api.zzf();
  public static final Api.zza<zzg, zzxq> hh;
  public static final Scope jn;
  public static final Scope jo;
  
  static
  {
    aDi = new Api.zzf();
    hh = new Api.zza()
    {
      public zzg zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzf paramAnonymouszzf, zzxq paramAnonymouszzxq, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        if (paramAnonymouszzxq == null) {
          paramAnonymouszzxq = zzxq.aDl;
        }
        for (;;)
        {
          return new zzg(paramAnonymousContext, paramAnonymousLooper, true, paramAnonymouszzf, paramAnonymouszzxq, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
        }
      }
    };
    aDj = new Api.zza()
    {
      public zzg zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzf paramAnonymouszzf, zzxo.zza paramAnonymouszza, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        return new zzg(paramAnonymousContext, paramAnonymousLooper, false, paramAnonymouszzf, paramAnonymouszza.zzcdb(), paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
      }
    };
    jn = new Scope("profile");
    jo = new Scope("email");
  }
  
  public static class zza
    implements Api.ApiOptions.HasOptions
  {
    private final Bundle aDk;
    
    public Bundle zzcdb()
    {
      return this.aDk;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzxo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */