package com.google.android.gms.internal;

public abstract class zzbzt<T>
{
  private final int zzBO;
  private final String zzBP;
  private final T zzBQ;
  
  private zzbzt(int paramInt, String paramString, T paramT)
  {
    this.zzBO = paramInt;
    this.zzBP = paramString;
    this.zzBQ = paramT;
    zzcae.zzub().zza(this);
  }
  
  public static zzbzv zzb(int paramInt, String paramString, Boolean paramBoolean)
  {
    return new zzbzv(0, paramString, paramBoolean);
  }
  
  public static zzbzw zzb(int paramInt1, String paramString, int paramInt2)
  {
    return new zzbzw(0, paramString, Integer.valueOf(paramInt2));
  }
  
  public static zzbzx zzb(int paramInt, String paramString, long paramLong)
  {
    return new zzbzx(0, paramString, Long.valueOf(paramLong));
  }
  
  public final String getKey()
  {
    return this.zzBP;
  }
  
  public final int getSource()
  {
    return this.zzBO;
  }
  
  protected abstract T zza(zzcab paramzzcab);
  
  public final T zzdI()
  {
    return (T)this.zzBQ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */