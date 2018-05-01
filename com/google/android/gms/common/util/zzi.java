package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;

public final class zzi
{
  private static Boolean zzgei;
  private static Boolean zzgej;
  private static Boolean zzgek;
  
  @TargetApi(20)
  public static boolean zzcs(Context paramContext)
  {
    if (zzgei == null) {
      if ((!zzq.zzamm()) || (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.watch"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzgei = Boolean.valueOf(bool);
      return zzgei.booleanValue();
    }
  }
  
  @TargetApi(24)
  public static boolean zzct(Context paramContext)
  {
    return ((!zzq.isAtLeastN()) || (zzcu(paramContext))) && (zzcs(paramContext));
  }
  
  @TargetApi(21)
  public static boolean zzcu(Context paramContext)
  {
    if (zzgej == null) {
      if ((!zzq.zzamn()) || (!paramContext.getPackageManager().hasSystemFeature("cn.google"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzgej = Boolean.valueOf(bool);
      return zzgej.booleanValue();
    }
  }
  
  public static boolean zzcv(Context paramContext)
  {
    if (zzgek == null) {
      if ((!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.iot")) && (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded"))) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      zzgek = Boolean.valueOf(bool);
      return zzgek.booleanValue();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */