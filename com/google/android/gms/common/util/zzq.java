package com.google.android.gms.common.util;

import android.os.Build.VERSION;

public final class zzq
{
  public static boolean isAtLeastN()
  {
    return Build.VERSION.SDK_INT >= 24;
  }
  
  public static boolean isAtLeastO()
  {
    return (Build.VERSION.SDK_INT >= 26) || ("O".equals(Build.VERSION.CODENAME)) || (Build.VERSION.CODENAME.startsWith("OMR")) || (Build.VERSION.CODENAME.startsWith("ODR"));
  }
  
  public static boolean zzamh()
  {
    return Build.VERSION.SDK_INT >= 15;
  }
  
  public static boolean zzami()
  {
    return Build.VERSION.SDK_INT >= 16;
  }
  
  public static boolean zzamk()
  {
    return Build.VERSION.SDK_INT >= 18;
  }
  
  public static boolean zzaml()
  {
    return Build.VERSION.SDK_INT >= 19;
  }
  
  public static boolean zzamm()
  {
    return Build.VERSION.SDK_INT >= 20;
  }
  
  public static boolean zzamn()
  {
    return Build.VERSION.SDK_INT >= 21;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */