package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzbhe;
import com.google.android.gms.internal.zzbhf;

public final class zzbf
{
  private static Object sLock = new Object();
  private static boolean zzcls;
  private static String zzgbc;
  private static int zzgbd;
  
  public static String zzcp(Context paramContext)
  {
    zzcr(paramContext);
    return zzgbc;
  }
  
  public static int zzcq(Context paramContext)
  {
    zzcr(paramContext);
    return zzgbd;
  }
  
  private static void zzcr(Context paramContext)
  {
    String str;
    synchronized (sLock)
    {
      if (zzcls) {
        return;
      }
      zzcls = true;
      str = paramContext.getPackageName();
      paramContext = zzbhf.zzdb(paramContext);
    }
    try
    {
      paramContext = paramContext.getApplicationInfo(str, 128).metaData;
      if (paramContext == null)
      {
        return;
        paramContext = finally;
        throw paramContext;
      }
      zzgbc = paramContext.getString("com.google.app.id");
      zzgbd = paramContext.getInt("com.google.android.gms.version");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */