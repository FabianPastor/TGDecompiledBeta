package com.google.android.gms.internal.config;

import java.util.HashMap;
import java.util.Map;

public final class zzj
{
  private long zzf = 43200L;
  private Map<String, String> zzg;
  private int zzh;
  private int zzi = -1;
  private int zzj = -1;
  private String zzk;
  
  public final zzj zza(int paramInt)
  {
    this.zzh = 10300;
    return this;
  }
  
  public final zzj zza(long paramLong)
  {
    this.zzf = paramLong;
    return this;
  }
  
  public final zzj zza(String paramString)
  {
    this.zzk = paramString;
    return this;
  }
  
  public final zzj zza(String paramString1, String paramString2)
  {
    if (this.zzg == null) {
      this.zzg = new HashMap();
    }
    this.zzg.put(paramString1, paramString2);
    return this;
  }
  
  public final zzj zzb(int paramInt)
  {
    this.zzi = paramInt;
    return this;
  }
  
  public final zzj zzc(int paramInt)
  {
    this.zzj = paramInt;
    return this;
  }
  
  public final zzi zzf()
  {
    return new zzi(this, null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */