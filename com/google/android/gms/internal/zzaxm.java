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
import com.google.android.gms.common.internal.zzg;

public final class zzaxm
{
  public static final Api<zzaxo> API = new Api("SignIn.API", zzahd, zzahc);
  public static final Api<zza> zzaJq = new Api("SignIn.INTERNAL_API", zzbCe, zzbCd);
  public static final Api.zzf<zzaxy> zzahc = new Api.zzf();
  public static final Api.zza<zzaxy, zzaxo> zzahd;
  public static final Scope zzajd;
  public static final Scope zzaje;
  public static final Api.zzf<zzaxy> zzbCd = new Api.zzf();
  static final Api.zza<zzaxy, zza> zzbCe;
  
  static
  {
    zzahd = new Api.zza()
    {
      public zzaxy zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, zzaxo paramAnonymouszzaxo, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        if (paramAnonymouszzaxo == null) {
          paramAnonymouszzaxo = zzaxo.zzbCg;
        }
        for (;;)
        {
          return new zzaxy(paramAnonymousContext, paramAnonymousLooper, true, paramAnonymouszzg, paramAnonymouszzaxo, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
        }
      }
    };
    zzbCe = new Api.zza()
    {
      public zzaxy zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, zzaxm.zza paramAnonymouszza, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        return new zzaxy(paramAnonymousContext, paramAnonymousLooper, false, paramAnonymouszzg, paramAnonymouszza.zzOd(), paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
      }
    };
    zzajd = new Scope("profile");
    zzaje = new Scope("email");
  }
  
  public static class zza
    implements Api.ApiOptions.HasOptions
  {
    private final Bundle zzbCf;
    
    public Bundle zzOd()
    {
      return this.zzbCf;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */