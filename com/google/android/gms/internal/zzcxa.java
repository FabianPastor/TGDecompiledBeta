package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.Scope;

public final class zzcxa
{
  public static final Api<zzcxe> API = new Api("SignIn.API", zzebg, zzebf);
  private static Api.zzf<zzcxn> zzebf = new Api.zzf();
  public static final Api.zza<zzcxn, zzcxe> zzebg;
  private static Scope zzehi;
  private static Scope zzehj;
  private static Api<Object> zzgjb = new Api("SignIn.INTERNAL_API", zzkbr, zzkbq);
  private static Api.zzf<zzcxn> zzkbq = new Api.zzf();
  private static Api.zza<zzcxn, Object> zzkbr;
  
  static
  {
    zzebg = new zzcxb();
    zzkbr = new zzcxc();
    zzehi = new Scope("profile");
    zzehj = new Scope("email");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */