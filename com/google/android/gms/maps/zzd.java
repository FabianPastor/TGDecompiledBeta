package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzac;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzd
  extends zzac
{
  zzd(GoogleMap paramGoogleMap, GoogleMap.OnInfoWindowClickListener paramOnInfoWindowClickListener) {}
  
  public final void zze(zzp paramzzp)
  {
    this.zzblC.onInfoWindowClick(new Marker(paramzzp));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */