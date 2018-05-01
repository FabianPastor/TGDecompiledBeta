package com.google.android.gms.internal.measurement;

import java.util.List;

public final class zzex<V>
{
  private final V zzaid;
  private final String zznt;
  private final V zzzw;
  
  private zzex(String paramString, V paramV1, V paramV2)
  {
    this.zzzw = paramV1;
    this.zzaid = paramV2;
    this.zznt = paramString;
  }
  
  static zzex<Double> zza(String paramString, double paramDouble1, double paramDouble2)
  {
    paramString = new zzex(paramString, Double.valueOf(-3.0D), Double.valueOf(-3.0D));
    zzew.zzage.add(paramString);
    return paramString;
  }
  
  static zzex<Long> zzb(String paramString, long paramLong1, long paramLong2)
  {
    paramString = new zzex(paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2));
    zzew.zzagb.add(paramString);
    return paramString;
  }
  
  static zzex<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramString = new zzex(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2));
    zzew.zzagc.add(paramString);
    return paramString;
  }
  
  static zzex<Integer> zzc(String paramString, int paramInt1, int paramInt2)
  {
    paramString = new zzex(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    zzew.zzaga.add(paramString);
    return paramString;
  }
  
  static zzex<String> zzd(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = new zzex(paramString1, paramString2, paramString3);
    zzew.zzagd.add(paramString1);
    return paramString1;
  }
  
  public final V get()
  {
    return (V)this.zzzw;
  }
  
  public final V get(V paramV)
  {
    if (paramV != null) {}
    for (;;)
    {
      return paramV;
      paramV = this.zzzw;
    }
  }
  
  public final String getKey()
  {
    return this.zznt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */