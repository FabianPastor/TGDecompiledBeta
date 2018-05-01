package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzbe;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;

final class zzp
  extends zzbe
{
  zzp(GoogleMap paramGoogleMap, GoogleMap.OnPolylineClickListener paramOnPolylineClickListener) {}
  
  public final void zza(IPolylineDelegate paramIPolylineDelegate)
  {
    this.zzblO.onPolylineClick(new Polyline(paramIPolylineDelegate));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */