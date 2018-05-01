package com.google.android.gms.internal.config;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ClientKey;

public final class zze
{
  public static final Api<Object> API = new Api("Config.API", CLIENT_BUILDER, CLIENT_KEY);
  private static final Api.AbstractClientBuilder<zzw, Object> CLIENT_BUILDER;
  private static final Api.ClientKey<zzw> CLIENT_KEY = new Api.ClientKey();
  public static final zzg zze = new zzo();
  
  static
  {
    CLIENT_BUILDER = new zzf();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */