package com.google.android.gms.common.wrappers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Process;
import com.google.android.gms.common.util.PlatformVersion;

public class PackageManagerWrapper
{
  private final Context zzjp;
  
  public PackageManagerWrapper(Context paramContext)
  {
    this.zzjp = paramContext;
  }
  
  public int checkCallingOrSelfPermission(String paramString)
  {
    return this.zzjp.checkCallingOrSelfPermission(paramString);
  }
  
  public ApplicationInfo getApplicationInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return this.zzjp.getPackageManager().getApplicationInfo(paramString, paramInt);
  }
  
  public CharSequence getApplicationLabel(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return this.zzjp.getPackageManager().getApplicationLabel(this.zzjp.getPackageManager().getApplicationInfo(paramString, 0));
  }
  
  public PackageInfo getPackageInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return this.zzjp.getPackageManager().getPackageInfo(paramString, paramInt);
  }
  
  public boolean isCallerInstantApp()
  {
    boolean bool;
    if (Binder.getCallingUid() == Process.myUid()) {
      bool = InstantApps.isInstantApp(this.zzjp);
    }
    for (;;)
    {
      return bool;
      if (PlatformVersion.isAtLeastO())
      {
        String str = this.zzjp.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (str != null)
        {
          bool = this.zzjp.getPackageManager().isInstantApp(str);
          continue;
        }
      }
      bool = false;
    }
  }
  
  @TargetApi(19)
  public boolean uidHasPackageName(int paramInt, String paramString)
  {
    bool1 = false;
    if (PlatformVersion.isAtLeastKitKat()) {}
    do
    {
      do
      {
        try
        {
          ((AppOpsManager)this.zzjp.getSystemService("appops")).checkPackage(paramInt, paramString);
          bool2 = true;
        }
        catch (SecurityException paramString)
        {
          for (;;)
          {
            String[] arrayOfString;
            boolean bool2 = bool1;
          }
        }
        return bool2;
        arrayOfString = this.zzjp.getPackageManager().getPackagesForUid(paramInt);
        bool2 = bool1;
      } while (paramString == null);
      bool2 = bool1;
    } while (arrayOfString == null);
    for (paramInt = 0;; paramInt++)
    {
      bool2 = bool1;
      if (paramInt >= arrayOfString.length) {
        break;
      }
      if (paramString.equals(arrayOfString[paramInt]))
      {
        bool2 = true;
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/wrappers/PackageManagerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */