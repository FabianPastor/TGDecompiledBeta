package com.google.android.gms.maps;

import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.internal.zzi;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzg
  extends zzi
{
  zzg(GoogleMap paramGoogleMap, GoogleMap.InfoWindowAdapter paramInfoWindowAdapter) {}
  
  public final IObjectWrapper zzh(zzp paramzzp)
  {
    return zzn.zzw(this.zzblF.getInfoWindow(new Marker(paramzzp)));
  }
  
  public final IObjectWrapper zzi(zzp paramzzp)
  {
    return zzn.zzw(this.zzblF.getInfoContents(new Marker(paramzzp)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */