package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzaq;

final class zzaj
  extends zzaq
{
  zzaj(SupportMapFragment.zza paramzza, OnMapReadyCallback paramOnMapReadyCallback) {}
  
  public final void zza(IGoogleMapDelegate paramIGoogleMapDelegate)
    throws RemoteException
  {
    this.zzbmr.onMapReady(new GoogleMap(paramIGoogleMapDelegate));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */