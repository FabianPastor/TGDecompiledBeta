package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzuz.zza;

@DynamiteApi
public class FlagProviderImpl
  extends zzuz.zza
{
  private boolean zzaom = false;
  private SharedPreferences zzbak;
  
  public boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
  {
    if (!this.zzaom) {
      return paramBoolean;
    }
    return zza.zza.zza(this.zzbak, paramString, Boolean.valueOf(paramBoolean)).booleanValue();
  }
  
  public int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
  {
    if (!this.zzaom) {
      return paramInt1;
    }
    return zza.zzb.zza(this.zzbak, paramString, Integer.valueOf(paramInt1)).intValue();
  }
  
  public long getLongFlagValue(String paramString, long paramLong, int paramInt)
  {
    if (!this.zzaom) {
      return paramLong;
    }
    return zza.zzc.zza(this.zzbak, paramString, Long.valueOf(paramLong)).longValue();
  }
  
  public String getStringFlagValue(String paramString1, String paramString2, int paramInt)
  {
    if (!this.zzaom) {
      return paramString2;
    }
    return zza.zzd.zza(this.zzbak, paramString1, paramString2);
  }
  
  public void init(zzd paramzzd)
  {
    paramzzd = (Context)zze.zzae(paramzzd);
    if (this.zzaom) {
      return;
    }
    try
    {
      this.zzbak = zzb.zzn(paramzzd.createPackageContext("com.google.android.gms", 0));
      this.zzaom = true;
      return;
    }
    catch (PackageManager.NameNotFoundException paramzzd) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/FlagProviderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */