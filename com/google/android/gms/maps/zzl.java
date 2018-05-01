package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.ILocationSourceDelegate.zza;
import com.google.android.gms.maps.internal.zzah;

final class zzl
  extends ILocationSourceDelegate.zza
{
  zzl(GoogleMap paramGoogleMap, LocationSource paramLocationSource) {}
  
  public final void activate(zzah paramzzah)
  {
    this.zzblK.activate(new zzm(this, paramzzah));
  }
  
  public final void deactivate()
  {
    this.zzblK.deactivate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */