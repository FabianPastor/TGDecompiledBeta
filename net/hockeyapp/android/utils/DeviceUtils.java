package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import net.hockeyapp.android.Constants;

public class DeviceUtils
{
  public static DeviceUtils getInstance()
  {
    return DeviceUtilsHolder.INSTANCE;
  }
  
  public String getAppName(Context paramContext)
  {
    if (paramContext == null) {
      return "";
    }
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return "";
      }
      paramContext = localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(paramContext.getPackageName(), 0)).toString();
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  public int getCurrentVersionCode(Context paramContext)
  {
    return Integer.parseInt(Constants.APP_VERSION);
  }
  
  private static class DeviceUtilsHolder
  {
    public static final DeviceUtils INSTANCE = new DeviceUtils(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/DeviceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */