package com.google.android.gms.internal;

public abstract class zzbzu<T>
{
  private final int zzBM;
  private final String zzBN;
  private final T zzBO;
  
  private zzbzu(int paramInt, String paramString, T paramT)
  {
    this.zzBM = paramInt;
    this.zzBN = paramString;
    this.zzBO = paramT;
    zzcaf.zzub().zza(this);
  }
  
  public static zzbzw zzb(int paramInt, String paramString, Boolean paramBoolean)
  {
    return new zzbzw(0, paramString, paramBoolean);
  }
  
  public static zzbzx zzb(int paramInt1, String paramString, int paramInt2)
  {
    return new zzbzx(0, paramString, Integer.valueOf(paramInt2));
  }
  
  public static zzbzy zzb(int paramInt, String paramString, long paramLong)
  {
    return new zzbzy(0, paramString, Long.valueOf(paramLong));
  }
  
  public final String getKey()
  {
    return this.zzBN;
  }
  
  public final int getSource()
  {
    return this.zzBM;
  }
  
  protected abstract T zza(zzcac paramzzcac);
  
  public final T zzdI()
  {
    return (T)this.zzBO;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */