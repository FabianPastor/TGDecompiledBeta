package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzy;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.internal.zzg;

final class zzk
  extends zzy
{
  zzk(GoogleMap paramGoogleMap, GoogleMap.OnGroundOverlayClickListener paramOnGroundOverlayClickListener) {}
  
  public final void zza(zzg paramzzg)
  {
    this.zzblJ.onGroundOverlayClick(new GroundOverlay(paramzzg));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */