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
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.signin.internal.zzg;

public final class zzwy
{
  public static final Api<zzxa> API = new Api("SignIn.API", fb, fa);
  public static final Api<zza> Hp = new Api("SignIn.INTERNAL_API", azZ, azY);
  public static final Api.zzf<zzg> azY;
  static final Api.zza<zzg, zza> azZ;
  public static final Api.zzf<zzg> fa = new Api.zzf();
  public static final Api.zza<zzg, zzxa> fb;
  public static final Scope hd;
  public static final Scope he;
  
  static
  {
    azY = new Api.zzf();
    fb = new Api.zza()
    {
      public zzg zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzh paramAnonymouszzh, zzxa paramAnonymouszzxa, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        if (paramAnonymouszzxa == null) {
          paramAnonymouszzxa = zzxa.aAa;
        }
        for (;;)
        {
          return new zzg(paramAnonymousContext, paramAnonymousLooper, true, paramAnonymouszzh, paramAnonymouszzxa, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
        }
      }
    };
    azZ = new Api.zza()
    {
      public zzg zza(Context paramAnonymousContext, Looper paramAnonymousLooper, zzh paramAnonymouszzh, zzwy.zza paramAnonymouszza, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
      {
        return new zzg(paramAnonymousContext, paramAnonymousLooper, false, paramAnonymouszzh, paramAnonymouszza.zzccz(), paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
      }
    };
    hd = new Scope("profile");
    he = new Scope("email");
  }
  
  public static class zza
    implements Api.ApiOptions.HasOptions
  {
    public Bundle zzccz()
    {
      return null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzwy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */