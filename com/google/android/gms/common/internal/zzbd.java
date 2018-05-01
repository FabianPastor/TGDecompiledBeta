package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzbgz;
import com.google.android.gms.internal.zzbha;

public final class zzbd
{
  private static boolean zzRk;
  private static String zzaIf;
  private static int zzaIg;
  private static Object zzuF = new Object();
  
  public static String zzaD(Context paramContext)
  {
    zzaF(paramContext);
    return zzaIf;
  }
  
  public static int zzaE(Context paramContext)
  {
    zzaF(paramContext);
    return zzaIg;
  }
  
  private static void zzaF(Context paramContext)
  {
    String str;
    synchronized (zzuF)
    {
      if (zzRk) {
        return;
      }
      zzRk = true;
      str = paramContext.getPackageName();
      paramContext = zzbha.zzaP(paramContext);
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
      zzaIf = paramContext.getString("com.google.app.id");
      zzaIg = paramContext.getInt("com.google.android.gms.version");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */