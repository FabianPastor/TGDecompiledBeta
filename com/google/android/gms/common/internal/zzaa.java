package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzsh;
import com.google.android.gms.internal.zzsi;

public class zzaa
{
  private static String CS;
  private static int CT;
  private static Object zzaok = new Object();
  private static boolean zzcdp;
  
  public static String zzcg(Context paramContext)
  {
    zzci(paramContext);
    return CS;
  }
  
  public static int zzch(Context paramContext)
  {
    zzci(paramContext);
    return CT;
  }
  
  private static void zzci(Context paramContext)
  {
    String str;
    synchronized (zzaok)
    {
      if (zzcdp) {
        return;
      }
      zzcdp = true;
      str = paramContext.getPackageName();
      paramContext = zzsi.zzcr(paramContext);
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
      CS = paramContext.getString("com.google.app.id");
      CT = paramContext.getInt("com.google.android.gms.version");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */