package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzcfc<V>
{
  private final String zzBN;
  private final V zzahV;
  private final zzbez<V> zzahW;
  
  private zzcfc(String paramString, zzbez<V> paramzzbez, V paramV)
  {
    zzbo.zzu(paramzzbez);
    this.zzahW = paramzzbez;
    this.zzahV = paramV;
    this.zzBN = paramString;
  }
  
  static zzcfc<Long> zzb(String paramString, long paramLong1, long paramLong2)
  {
    return new zzcfc(paramString, zzbez.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
  }
  
  static zzcfc<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new zzcfc(paramString, zzbez.zzg(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
  }
  
  static zzcfc<String> zzj(String paramString1, String paramString2, String paramString3)
  {
    return new zzcfc(paramString1, zzbez.zzv(paramString1, paramString3), paramString2);
  }
  
  static zzcfc<Integer> zzm(String paramString, int paramInt1, int paramInt2)
  {
    return new zzcfc(paramString, zzbez.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
  }
  
  public final V get()
  {
    return (V)this.zzahV;
  }
  
  public final V get(V paramV)
  {
    if (paramV != null) {
      return paramV;
    }
    return (V)this.zzahV;
  }
  
  public final String getKey()
  {
    return this.zzBN;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */