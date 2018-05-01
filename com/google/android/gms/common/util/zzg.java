package com.google.android.gms.common.util;

import android.content.Context;
import android.os.DropBoxManager;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;

public final class zzg
{
  private static final String[] zzaJD = { "android.", "com.android.", "dalvik.", "java.", "javax." };
  private static DropBoxManager zzaJE = null;
  private static boolean zzaJF = false;
  private static int zzaJG = -1;
  private static int zzaJH = 0;
  
  public static boolean zza(Context paramContext, Throwable paramThrowable)
  {
    return zza(paramContext, paramThrowable, 0);
  }
  
  private static boolean zza(Context paramContext, Throwable paramThrowable, int paramInt)
  {
    try
    {
      zzbo.zzu(paramContext);
      zzbo.zzu(paramThrowable);
      return false;
    }
    catch (Exception paramContext)
    {
      Log.e("CrashUtils", "Error adding exception to DropBox!", paramContext);
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */