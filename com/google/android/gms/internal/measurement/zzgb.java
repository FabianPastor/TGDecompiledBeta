package com.google.android.gms.internal.measurement;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.internal.Preconditions;

public final class zzgb
{
  public static boolean zza(Context paramContext)
  {
    bool1 = false;
    Preconditions.checkNotNull(paramContext);
    for (;;)
    {
      try
      {
        localPackageManager = paramContext.getPackageManager();
        if (localPackageManager != null) {
          continue;
        }
        bool2 = bool1;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        PackageManager localPackageManager;
        ComponentName localComponentName;
        boolean bool3;
        boolean bool2 = bool1;
        continue;
      }
      return bool2;
      localComponentName = new android/content/ComponentName;
      localComponentName.<init>(paramContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
      paramContext = localPackageManager.getReceiverInfo(localComponentName, 0);
      bool2 = bool1;
      if (paramContext != null)
      {
        bool3 = paramContext.enabled;
        bool2 = bool1;
        if (bool3) {
          bool2 = true;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzgb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */