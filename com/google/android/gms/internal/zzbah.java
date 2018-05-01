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

public final class zzbah
{
  public static final Api<zzbaj> API = new Api("SignIn.API", zzaie, zzaid);
  public static final Api<zza> zzaKN = new Api("SignIn.INTERNAL_API", zzbEj, zzbEi);
  public static final Api.zzf<zzbat> zzaid = new Api.zzf();
  public static final Api.zza<zzbat, zzbaj> zzaie;
  public static final Scope zzakh;
  public static final Scope zzaki;
  public static final Api.zzf<zzbat> zzbEi = new Api.zzf();
  static final Api.zza<zzbat, zza> zzbEj;
  
  static
  {
    zzaie = new Api.zza()
    {
      public zzbat zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, zzbaj paramAnonymouszzbaj, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        if (paramAnonymouszzbaj == null) {
          paramAnonymouszzbaj = zzbaj.zzbEl;
        }
        for (;;)
        {
          return new zzbat(paramAnonymousContext, paramAnonymousLooper, true, paramAnonymouszzg, paramAnonymouszzbaj, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
        }
      }
    };
    zzbEj = new Api.zza()
    {
      public zzbat zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, zzbah.zza paramAnonymouszza, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        return new zzbat(paramAnonymousContext, paramAnonymousLooper, false, paramAnonymouszzg, paramAnonymouszza.zzPK(), paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
      }
    };
    zzakh = new Scope("profile");
    zzaki = new Scope("email");
  }
  
  public static class zza
    implements Api.ApiOptions.HasOptions
  {
    private final Bundle zzbEk;
    
    public Bundle zzPK()
    {
      return this.zzbEk;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */