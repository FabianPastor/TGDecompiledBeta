package com.google.android.gms.internal.config;

import java.util.Collections;
import java.util.Map;

public final class zzi
{
  private final long zzf;
  private final Map<String, String> zzg;
  private final int zzh;
  private final int zzi;
  private final int zzj;
  private final String zzk;
  
  private zzi(zzj paramzzj)
  {
    this.zzf = zzj.zza(paramzzj);
    this.zzg = zzj.zzb(paramzzj);
    this.zzh = zzj.zzc(paramzzj);
    this.zzi = zzj.zzd(paramzzj);
    this.zzj = zzj.zze(paramzzj);
    this.zzk = zzj.zzf(paramzzj);
  }
  
  public final String getGmpAppId()
  {
    return this.zzk;
  }
  
  public final long zza()
  {
    return this.zzf;
  }
  
  public final Map<String, String> zzb()
  {
    if (this.zzg == null) {}
    for (Map localMap = Collections.emptyMap();; localMap = this.zzg) {
      return localMap;
    }
  }
  
  public final int zzc()
  {
    return this.zzh;
  }
  
  public final int zzd()
  {
    return this.zzj;
  }
  
  public final int zze()
  {
    return this.zzi;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */