package com.google.android.gms.internal;

public final class zzt<T>
{
  public final T result;
  public final zzc zzae;
  public final zzaa zzaf;
  public boolean zzag = false;
  
  private zzt(zzaa paramzzaa)
  {
    this.result = null;
    this.zzae = null;
    this.zzaf = paramzzaa;
  }
  
  private zzt(T paramT, zzc paramzzc)
  {
    this.result = paramT;
    this.zzae = paramzzc;
    this.zzaf = null;
  }
  
  public static <T> zzt<T> zza(T paramT, zzc paramzzc)
  {
    return new zzt(paramT, paramzzc);
  }
  
  public static <T> zzt<T> zzc(zzaa paramzzaa)
  {
    return new zzt(paramzzaa);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */