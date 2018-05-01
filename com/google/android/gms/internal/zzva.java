package com.google.android.gms.internal;

public final class zzva
{
  private static zzva Uw;
  private final zzux Ux = new zzux();
  private final zzuy Uy = new zzuy();
  
  static
  {
    zza(new zzva());
  }
  
  protected static void zza(zzva paramzzva)
  {
    try
    {
      Uw = paramzzva;
      return;
    }
    finally {}
  }
  
  private static zzva zzbhl()
  {
    try
    {
      zzva localzzva = Uw;
      return localzzva;
    }
    finally {}
  }
  
  public static zzux zzbhm()
  {
    return zzbhl().Ux;
  }
  
  public static zzuy zzbhn()
  {
    return zzbhl().Uy;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzva.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */