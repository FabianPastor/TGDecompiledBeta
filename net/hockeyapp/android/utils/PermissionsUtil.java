package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;

public class PermissionsUtil
{
  public static boolean isUnknownSourcesEnabled(Context paramContext)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 26) {
      if ((paramContext.getApplicationInfo().targetSdkVersion < 26) || (paramContext.getPackageManager().canRequestPackageInstalls())) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if ((Build.VERSION.SDK_INT >= 17) && (Build.VERSION.SDK_INT < 21)) {
        bool = "1".equals(Settings.Global.getString(paramContext.getContentResolver(), "install_non_market_apps"));
      } else {
        bool = "1".equals(Settings.Secure.getString(paramContext.getContentResolver(), "install_non_market_apps"));
      }
    }
  }
  
  public static boolean permissionsAreGranted(int[] paramArrayOfInt)
  {
    boolean bool = false;
    int i = paramArrayOfInt.length;
    int j = 0;
    if (j < i) {
      if (paramArrayOfInt[j] == 0) {}
    }
    for (;;)
    {
      return bool;
      j++;
      break;
      bool = true;
    }
  }
  
  public static int[] permissionsState(Context paramContext, String... paramVarArgs)
  {
    Object localObject;
    if (paramVarArgs == null)
    {
      localObject = null;
      return (int[])localObject;
    }
    int[] arrayOfInt = new int[paramVarArgs.length];
    for (int i = 0;; i++)
    {
      localObject = arrayOfInt;
      if (i >= paramVarArgs.length) {
        break;
      }
      arrayOfInt[i] = paramContext.checkCallingOrSelfPermission(paramVarArgs[i]);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/PermissionsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */