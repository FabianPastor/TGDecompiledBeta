package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzaa;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.internal.zzj;

final class zza
  extends zzaa
{
  zza(GoogleMap paramGoogleMap, GoogleMap.OnIndoorStateChangeListener paramOnIndoorStateChangeListener) {}
  
  public final void onIndoorBuildingFocused()
  {
    this.zzblz.onIndoorBuildingFocused();
  }
  
  public final void zza(zzj paramzzj)
  {
    this.zzblz.onIndoorLevelActivated(new IndoorBuilding(paramzzj));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */