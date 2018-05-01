package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

public final class zzj
{
  private static Boolean zzaJJ;
  private static Boolean zzaJK;
  private static Boolean zzaJL;
  private static Boolean zzaJM;
  private static Boolean zzaJN;
  
  public static boolean zza(Resources paramResources)
  {
    boolean bool2 = false;
    if (paramResources == null) {
      return false;
    }
    int i;
    if (zzaJJ == null)
    {
      if ((paramResources.getConfiguration().screenLayout & 0xF) <= 3) {
        break label102;
      }
      i = 1;
      if (i == 0) {
        if (zzaJK == null)
        {
          paramResources = paramResources.getConfiguration();
          if (((paramResources.screenLayout & 0xF) > 3) || (paramResources.smallestScreenWidthDp < 600)) {
            break label107;
          }
        }
      }
    }
    label102:
    label107:
    for (boolean bool1 = true;; bool1 = false)
    {
      zzaJK = Boolean.valueOf(bool1);
      bool1 = bool2;
      if (zzaJK.booleanValue()) {
        bool1 = true;
      }
      zzaJJ = Boolean.valueOf(bool1);
      return zzaJJ.booleanValue();
      i = 0;
      break;
    }
  }
  
  @TargetApi(20)
  public static boolean zzaG(Context paramContext)
  {
    if (zzaJL == null) {
      if ((!zzq.zzsd()) || (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.watch"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzaJL = Boolean.valueOf(bool);
      return zzaJL.booleanValue();
    }
  }
  
  @TargetApi(24)
  public static boolean zzaH(Context paramContext)
  {
    return ((!zzq.isAtLeastN()) || (zzaI(paramContext))) && (zzaG(paramContext));
  }
  
  @TargetApi(21)
  public static boolean zzaI(Context paramContext)
  {
    if (zzaJM == null) {
      if ((!zzq.zzse()) || (!paramContext.getPackageManager().hasSystemFeature("cn.google"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzaJM = Boolean.valueOf(bool);
      return zzaJM.booleanValue();
    }
  }
  
  public static boolean zzaJ(Context paramContext)
  {
    if (zzaJN == null) {
      if ((!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.iot")) && (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded"))) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      zzaJN = Boolean.valueOf(bool);
      return zzaJN.booleanValue();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */