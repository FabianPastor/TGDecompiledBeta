package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import com.google.android.gms.common.util.zzq;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class zzbgy
{
  private static Context zzaKh;
  private static Boolean zzaKi;
  
  public static boolean zzaN(Context paramContext)
  {
    for (;;)
    {
      Context localContext;
      boolean bool;
      try
      {
        localContext = paramContext.getApplicationContext();
        if ((zzaKh != null) && (zzaKi != null) && (zzaKh == localContext))
        {
          bool = zzaKi.booleanValue();
          return bool;
        }
        zzaKi = null;
        bool = zzq.isAtLeastO();
        if (!bool) {}
      }
      finally {}
      try
      {
        zzaKi = (Boolean)PackageManager.class.getDeclaredMethod("isInstantApp", new Class[0]).invoke(localContext.getPackageManager(), new Object[0]);
        zzaKh = localContext;
        bool = zzaKi.booleanValue();
      }
      catch (NoSuchMethodException paramContext)
      {
        continue;
      }
      catch (InvocationTargetException paramContext)
      {
        continue;
      }
      catch (IllegalAccessException paramContext)
      {
        continue;
      }
      zzaKi = Boolean.valueOf(false);
      continue;
      try
      {
        paramContext.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
        zzaKi = Boolean.valueOf(true);
      }
      catch (ClassNotFoundException paramContext)
      {
        zzaKi = Boolean.valueOf(false);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */