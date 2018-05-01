package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzae;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzf
  extends zzae
{
  zzf(GoogleMap paramGoogleMap, GoogleMap.OnInfoWindowCloseListener paramOnInfoWindowCloseListener) {}
  
  public final void zzg(zzp paramzzp)
  {
    this.zzblE.onInfoWindowClose(new Marker(paramzzp));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */