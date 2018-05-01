package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.maps.internal.zzba;
import com.google.android.gms.maps.model.PointOfInterest;

final class zzr
  extends zzba
{
  zzr(GoogleMap paramGoogleMap, GoogleMap.OnPoiClickListener paramOnPoiClickListener) {}
  
  public final void zza(PointOfInterest paramPointOfInterest)
    throws RemoteException
  {
    this.zzblQ.onPoiClick(paramPointOfInterest);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */