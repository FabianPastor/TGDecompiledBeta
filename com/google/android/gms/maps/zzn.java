package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.zzw;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.internal.zzd;

final class zzn
  extends zzw
{
  zzn(GoogleMap paramGoogleMap, GoogleMap.OnCircleClickListener paramOnCircleClickListener) {}
  
  public final void zza(zzd paramzzd)
  {
    this.zzblM.onCircleClick(new Circle(paramzzd));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */