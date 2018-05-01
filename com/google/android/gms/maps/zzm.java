package com.google.android.gms.maps;

import android.location.Location;
import android.os.RemoteException;
import com.google.android.gms.maps.internal.zzah;
import com.google.android.gms.maps.model.RuntimeRemoteException;

final class zzm
  implements LocationSource.OnLocationChangedListener
{
  zzm(zzl paramzzl, zzah paramzzah) {}
  
  public final void onLocationChanged(Location paramLocation)
  {
    try
    {
      this.zzblL.zzd(paramLocation);
      return;
    }
    catch (RemoteException paramLocation)
    {
      throw new RuntimeRemoteException(paramLocation);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */