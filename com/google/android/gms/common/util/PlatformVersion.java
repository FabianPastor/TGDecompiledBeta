package com.google.android.gms.common.util;

import android.os.Build.VERSION;

public final class PlatformVersion
{
  public static boolean isAtLeastIceCreamSandwich()
  {
    return true;
  }
  
  public static boolean isAtLeastIceCreamSandwichMR1()
  {
    if (Build.VERSION.SDK_INT >= 15) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastJellyBean()
  {
    if (Build.VERSION.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastJellyBeanMR2()
  {
    if (Build.VERSION.SDK_INT >= 18) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastKitKat()
  {
    if (Build.VERSION.SDK_INT >= 19) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastKitKatWatch()
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastLollipop()
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastN()
  {
    if (Build.VERSION.SDK_INT >= 24) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isAtLeastO()
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/PlatformVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */