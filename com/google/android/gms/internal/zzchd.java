package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

public final class zzchd<V>
{
  private final String zzbhb;
  private final V zzdxn;
  private final zzbey<V> zzdxo;
  
  private zzchd(String paramString, zzbey<V> paramzzbey, V paramV)
  {
    zzbq.checkNotNull(paramzzbey);
    this.zzdxo = paramzzbey;
    this.zzdxn = paramV;
    this.zzbhb = paramString;
  }
  
  static zzchd<Long> zzb(String paramString, long paramLong1, long paramLong2)
  {
    return new zzchd(paramString, zzbey.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
  }
  
  static zzchd<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new zzchd(paramString, zzbey.zze(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
  }
  
  static zzchd<String> zzi(String paramString1, String paramString2, String paramString3)
  {
    return new zzchd(paramString1, zzbey.zzs(paramString1, paramString3), paramString2);
  }
  
  static zzchd<Integer> zzm(String paramString, int paramInt1, int paramInt2)
  {
    return new zzchd(paramString, zzbey.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
  }
  
  public final V get()
  {
    return (V)this.zzdxn;
  }
  
  public final V get(V paramV)
  {
    if (paramV != null) {
      return paramV;
    }
    return (V)this.zzdxn;
  }
  
  public final String getKey()
  {
    return this.zzbhb;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */