package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Process;
import com.google.android.gms.common.util.zzq;

public final class zzbhe
{
  private Context mContext;
  
  public zzbhe(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public final int checkCallingOrSelfPermission(String paramString)
  {
    return this.mContext.checkCallingOrSelfPermission(paramString);
  }
  
  public final ApplicationInfo getApplicationInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return this.mContext.getPackageManager().getApplicationInfo(paramString, paramInt);
  }
  
  public final PackageInfo getPackageInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return this.mContext.getPackageManager().getPackageInfo(paramString, paramInt);
  }
  
  public final String[] getPackagesForUid(int paramInt)
  {
    return this.mContext.getPackageManager().getPackagesForUid(paramInt);
  }
  
  public final boolean zzamu()
  {
    if (Binder.getCallingUid() == Process.myUid()) {
      return zzbhd.zzcz(this.mContext);
    }
    if (zzq.isAtLeastO())
    {
      String str = this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
      if (str != null) {
        return this.mContext.getPackageManager().isInstantApp(str);
      }
    }
    return false;
  }
  
  @TargetApi(19)
  public final boolean zzf(int paramInt, String paramString)
  {
    boolean bool2 = false;
    if (zzq.zzaml()) {}
    for (;;)
    {
      try
      {
        ((AppOpsManager)this.mContext.getSystemService("appops")).checkPackage(paramInt, paramString);
        bool1 = true;
        return bool1;
      }
      catch (SecurityException paramString) {}
      String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(paramInt);
      boolean bool1 = bool2;
      if (paramString != null)
      {
        bool1 = bool2;
        if (arrayOfString != null)
        {
          paramInt = 0;
          for (;;)
          {
            bool1 = bool2;
            if (paramInt >= arrayOfString.length) {
              break;
            }
            if (paramString.equals(arrayOfString[paramInt])) {
              return true;
            }
            paramInt += 1;
          }
        }
      }
    }
    return false;
  }
  
  public final CharSequence zzgt(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return this.mContext.getPackageManager().getApplicationLabel(this.mContext.getPackageManager().getApplicationInfo(paramString, 0));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */