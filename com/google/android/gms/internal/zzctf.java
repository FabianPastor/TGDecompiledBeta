package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.Scope;

public final class zzctf
{
  public static final Api<zzctk> API = new Api("SignIn.API", zzajS, zzajR);
  private static Api<zzcti> zzaMc = new Api("SignIn.INTERNAL_API", zzbCK, zzbCJ);
  private static Api.zzf<zzctt> zzajR = new Api.zzf();
  public static final Api.zza<zzctt, zzctk> zzajS;
  private static Scope zzalV;
  private static Scope zzalW;
  private static Api.zzf<zzctt> zzbCJ = new Api.zzf();
  private static Api.zza<zzctt, zzcti> zzbCK;
  
  static
  {
    zzajS = new zzctg();
    zzbCK = new zzcth();
    zzalV = new Scope("profile");
    zzalW = new Scope("email");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */