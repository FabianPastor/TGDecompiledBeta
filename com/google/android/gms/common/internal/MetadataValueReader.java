package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import javax.annotation.concurrent.GuardedBy;

public class MetadataValueReader
{
  private static Object sLock = new Object();
  @GuardedBy("sLock")
  private static boolean zzui;
  private static String zzuj;
  private static int zzuk;
  
  public static String getGoogleAppId(Context paramContext)
  {
    zze(paramContext);
    return zzuj;
  }
  
  public static int getGooglePlayServicesVersion(Context paramContext)
  {
    zze(paramContext);
    return zzuk;
  }
  
  private static void zze(Context paramContext)
  {
    for (;;)
    {
      String str;
      synchronized (sLock)
      {
        if (zzui) {
          return;
        }
        zzui = true;
        str = paramContext.getPackageName();
        paramContext = Wrappers.packageManager(paramContext);
      }
      try
      {
        paramContext = paramContext.getApplicationInfo(str, 128).metaData;
        if (paramContext == null)
        {
          continue;
          paramContext = finally;
          throw paramContext;
        }
        zzuj = paramContext.getString("com.google.app.id");
        zzuk = paramContext.getInt("com.google.android.gms.version");
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        for (;;)
        {
          Log.wtf("MetadataValueReader", "This should never happen.", paramContext);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/MetadataValueReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */