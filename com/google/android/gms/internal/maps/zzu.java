package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.IInterface;

public abstract class zzu
  extends zzb
  implements zzt
{
  public static zzt zzg(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      paramIBinder = null;
    }
    for (;;)
    {
      return paramIBinder;
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IMarkerDelegate");
      if ((localIInterface instanceof zzt)) {
        paramIBinder = (zzt)localIInterface;
      } else {
        paramIBinder = new zzv(paramIBinder);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/maps/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */