package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.internal.zzsh;
import com.google.android.gms.internal.zzsi;

public class zzd
{
  public static int zza(PackageInfo paramPackageInfo)
  {
    if ((paramPackageInfo == null) || (paramPackageInfo.applicationInfo == null)) {}
    do
    {
      return -1;
      paramPackageInfo = paramPackageInfo.applicationInfo.metaData;
    } while (paramPackageInfo == null);
    return paramPackageInfo.getInt("com.google.android.gms.version", -1);
  }
  
  public static boolean zzact()
  {
    return false;
  }
  
  public static int zzv(Context paramContext, String paramString)
  {
    return zza(zzw(paramContext, paramString));
  }
  
  @Nullable
  public static PackageInfo zzw(Context paramContext, String paramString)
  {
    try
    {
      paramContext = zzsi.zzcr(paramContext).getPackageInfo(paramString, 128);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  @TargetApi(12)
  public static boolean zzx(Context paramContext, String paramString)
  {
    if (!zzs.zzaxl()) {}
    for (;;)
    {
      return false;
      if ((!"com.google.android.gms".equals(paramString)) || (!zzact())) {
        try
        {
          int i = zzsi.zzcr(paramContext).getApplicationInfo(paramString, 0).flags;
          if ((i & 0x200000) != 0) {
            return true;
          }
        }
        catch (PackageManager.NameNotFoundException paramContext) {}
      }
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */