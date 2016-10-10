package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

public final class zzi
{
  private static Boolean EL;
  private static Boolean EM;
  private static Boolean EN;
  private static Boolean EO;
  
  public static boolean zzb(Resources paramResources)
  {
    boolean bool = false;
    if (paramResources == null) {
      return false;
    }
    if (EL == null) {
      if ((paramResources.getConfiguration().screenLayout & 0xF) <= 3) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      if (((zzs.zzaxk()) && (i != 0)) || (zzc(paramResources))) {
        bool = true;
      }
      EL = Boolean.valueOf(bool);
      return EL.booleanValue();
    }
  }
  
  @TargetApi(13)
  private static boolean zzc(Resources paramResources)
  {
    if (EM == null)
    {
      paramResources = paramResources.getConfiguration();
      if ((!zzs.zzaxm()) || ((paramResources.screenLayout & 0xF) > 3) || (paramResources.smallestScreenWidthDp < 600)) {
        break label54;
      }
    }
    label54:
    for (boolean bool = true;; bool = false)
    {
      EM = Boolean.valueOf(bool);
      return EM.booleanValue();
    }
  }
  
  @TargetApi(20)
  public static boolean zzcl(Context paramContext)
  {
    if (EN == null) {
      if ((!zzs.zzaxs()) || (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.watch"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      EN = Boolean.valueOf(bool);
      return EN.booleanValue();
    }
  }
  
  @TargetApi(21)
  public static boolean zzcm(Context paramContext)
  {
    if (EO == null) {
      if ((!zzs.zzaxu()) || (!paramContext.getPackageManager().hasSystemFeature("cn.google"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      EO = Boolean.valueOf(bool);
      return EO.booleanValue();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */