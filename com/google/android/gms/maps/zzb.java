package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzas;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzb
  extends zzas
{
  zzb(GoogleMap paramGoogleMap, GoogleMap.OnMarkerClickListener paramOnMarkerClickListener) {}
  
  public final boolean zza(zzp paramzzp)
  {
    return this.zzblA.onMarkerClick(new Marker(paramzzp));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */