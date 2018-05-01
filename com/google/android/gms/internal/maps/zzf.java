package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.IInterface;

public abstract class zzf
  extends zzb
  implements zze
{
  public static zze zzb(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      paramIBinder = null;
    }
    for (;;)
    {
      return paramIBinder;
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
      if ((localIInterface instanceof zze)) {
        paramIBinder = (zze)localIInterface;
      } else {
        paramIBinder = new zzg(paramIBinder);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/maps/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */