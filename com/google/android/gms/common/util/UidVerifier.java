package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.GoogleSignatureVerifier;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;

public final class UidVerifier
{
  public static boolean isGooglePlayServicesUid(Context paramContext, int paramInt)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!uidHasPackageName(paramContext, paramInt, "com.google.android.gms")) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      Object localObject = paramContext.getPackageManager();
      try
      {
        localObject = ((PackageManager)localObject).getPackageInfo("com.google.android.gms", 64);
        bool2 = GoogleSignatureVerifier.getInstance(paramContext).isGooglePublicSignedPackage((PackageInfo)localObject);
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        bool2 = bool1;
      }
      if (Log.isLoggable("UidVerifier", 3))
      {
        Log.d("UidVerifier", "Package manager can't find google play services package, defaulting to false");
        bool2 = bool1;
      }
    }
  }
  
  @TargetApi(19)
  public static boolean uidHasPackageName(Context paramContext, int paramInt, String paramString)
  {
    return Wrappers.packageManager(paramContext).uidHasPackageName(paramInt, paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/UidVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */