package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzau;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzc
  extends zzau
{
  zzc(GoogleMap paramGoogleMap, GoogleMap.OnMarkerDragListener paramOnMarkerDragListener) {}
  
  public final void zzb(zzp paramzzp)
  {
    this.zzblB.onMarkerDragStart(new Marker(paramzzp));
  }
  
  public final void zzc(zzp paramzzp)
  {
    this.zzblB.onMarkerDragEnd(new Marker(paramzzp));
  }
  
  public final void zzd(zzp paramzzp)
  {
    this.zzblB.onMarkerDrag(new Marker(paramzzp));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */