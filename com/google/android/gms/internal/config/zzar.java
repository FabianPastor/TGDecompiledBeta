package com.google.android.gms.internal.config;

import java.util.HashMap;
import java.util.Map;

public final class zzar
{
  private boolean zzap;
  private int zzaz = 0;
  private long zzbd;
  private Map<String, zzal> zzbe;
  private long zzbf;
  
  public zzar()
  {
    this(-1L);
  }
  
  private zzar(int paramInt, long paramLong, Map<String, zzal> paramMap, boolean paramBoolean)
  {
    this(0, -1L, null, false, -1L);
  }
  
  private zzar(int paramInt, long paramLong1, Map<String, zzal> paramMap, boolean paramBoolean, long paramLong2)
  {
    this.zzbd = paramLong1;
    this.zzbe = new HashMap();
    this.zzap = false;
    this.zzbf = -1L;
  }
  
  private zzar(long paramLong)
  {
    this(0, -1L, null, false);
  }
  
  public final int getLastFetchStatus()
  {
    return this.zzaz;
  }
  
  public final boolean isDeveloperModeEnabled()
  {
    return this.zzap;
  }
  
  public final void zza(Map<String, zzal> paramMap)
  {
    this.zzbe = paramMap;
  }
  
  public final void zza(boolean paramBoolean)
  {
    this.zzap = paramBoolean;
  }
  
  public final void zzc(long paramLong)
  {
    this.zzbd = paramLong;
  }
  
  public final void zzd(long paramLong)
  {
    this.zzbf = paramLong;
  }
  
  public final void zzf(int paramInt)
  {
    this.zzaz = paramInt;
  }
  
  public final Map<String, zzal> zzr()
  {
    return this.zzbe;
  }
  
  public final long zzt()
  {
    return this.zzbf;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */