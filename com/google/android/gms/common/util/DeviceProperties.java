package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;

public final class DeviceProperties
{
  private static Boolean zzzn;
  private static Boolean zzzo;
  private static Boolean zzzr;
  
  public static boolean isIoT(Context paramContext)
  {
    if (zzzr == null) {
      if ((!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.iot")) && (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded"))) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      zzzr = Boolean.valueOf(bool);
      return zzzr.booleanValue();
    }
  }
  
  @TargetApi(21)
  public static boolean isSidewinder(Context paramContext)
  {
    if (zzzo == null) {
      if ((!PlatformVersion.isAtLeastLollipop()) || (!paramContext.getPackageManager().hasSystemFeature("cn.google"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzzo = Boolean.valueOf(bool);
      return zzzo.booleanValue();
    }
  }
  
  @TargetApi(20)
  public static boolean isWearable(Context paramContext)
  {
    if (zzzn == null) {
      if ((!PlatformVersion.isAtLeastKitKatWatch()) || (!paramContext.getPackageManager().hasSystemFeature("android.hardware.type.watch"))) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      zzzn = Boolean.valueOf(bool);
      return zzzn.booleanValue();
    }
  }
  
  @TargetApi(24)
  public static boolean isWearableWithoutPlayStore(Context paramContext)
  {
    if (((!PlatformVersion.isAtLeastN()) || (isSidewinder(paramContext))) && (isWearable(paramContext))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/DeviceProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */