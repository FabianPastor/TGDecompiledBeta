package com.google.android.gms.internal;

public class zzbey<T>
{
  private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
  private static final Object sLock = new Object();
  private static zzbfe zzfvo = null;
  private static int zzfvp = 0;
  private String zzbhb;
  private T zzbhc;
  private T zzfvq = null;
  
  protected zzbey(String paramString, T paramT)
  {
    this.zzbhb = paramString;
    this.zzbhc = paramT;
  }
  
  public static zzbey<Integer> zza(String paramString, Integer paramInteger)
  {
    return new zzbfb(paramString, paramInteger);
  }
  
  public static zzbey<Long> zza(String paramString, Long paramLong)
  {
    return new zzbfa(paramString, paramLong);
  }
  
  public static zzbey<Boolean> zze(String paramString, boolean paramBoolean)
  {
    return new zzbez(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public static zzbey<String> zzs(String paramString1, String paramString2)
  {
    return new zzbfd(paramString1, paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */