package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzbc;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.internal.zzs;

final class zzo
  extends zzbc
{
  zzo(GoogleMap paramGoogleMap, GoogleMap.OnPolygonClickListener paramOnPolygonClickListener) {}
  
  public final void zza(zzs paramzzs)
  {
    this.zzblN.onPolygonClick(new Polygon(paramzzs));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */