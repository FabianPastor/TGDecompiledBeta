package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzag;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zze
  extends zzag
{
  zze(GoogleMap paramGoogleMap, GoogleMap.OnInfoWindowLongClickListener paramOnInfoWindowLongClickListener) {}
  
  public final void zzf(zzp paramzzp)
  {
    this.zzblD.onInfoWindowLongClick(new Marker(paramzzp));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */