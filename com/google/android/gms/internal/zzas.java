package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import java.io.File;

public final class zzas
{
  public static zzs zza(Context paramContext, zzan paramzzan)
  {
    File localFile = new File(paramContext.getCacheDir(), "volley");
    paramzzan = "volley/0";
    try
    {
      String str = paramContext.getPackageName();
      int i = paramContext.getPackageManager().getPackageInfo(str, 0).versionCode;
      paramContext = String.valueOf(str).length() + 12 + str + "/" + i;
      if (Build.VERSION.SDK_INT >= 9) {}
      for (paramContext = new zzao();; paramContext = new zzak(AndroidHttpClient.newInstance(paramContext)))
      {
        paramContext = new zzad(paramContext);
        paramContext = new zzs(new zzag(localFile), paramContext);
        paramContext.start();
        return paramContext;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = paramzzan;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzas.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */