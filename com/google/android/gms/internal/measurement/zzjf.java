package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.os.Build.VERSION;
import com.google.android.gms.common.internal.Preconditions;

public final class zzjf<T extends Context, >
{
  public static boolean zza(Context paramContext, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramContext);
    if (Build.VERSION.SDK_INT >= 24) {}
    for (paramBoolean = zzjv.zzc(paramContext, "com.google.android.gms.measurement.AppMeasurementJobService");; paramBoolean = zzjv.zzc(paramContext, "com.google.android.gms.measurement.AppMeasurementService")) {
      return paramBoolean;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */