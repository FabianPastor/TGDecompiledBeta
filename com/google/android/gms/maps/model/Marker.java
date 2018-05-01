package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.maps.zzt;

public final class Marker
{
  private final zzt zzdl;
  
  public Marker(zzt paramzzt)
  {
    this.zzdl = ((zzt)Preconditions.checkNotNull(paramzzt));
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool;
    if (!(paramObject instanceof Marker)) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      try
      {
        bool = this.zzdl.zzj(((Marker)paramObject).zzdl);
      }
      catch (RemoteException paramObject)
      {
        throw new RuntimeRemoteException((RemoteException)paramObject);
      }
    }
  }
  
  public final LatLng getPosition()
  {
    try
    {
      LatLng localLatLng = this.zzdl.getPosition();
      return localLatLng;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final int hashCode()
  {
    try
    {
      int i = this.zzdl.zzi();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setPosition(LatLng paramLatLng)
  {
    if (paramLatLng == null) {
      throw new IllegalArgumentException("latlng cannot be null - a position is required.");
    }
    try
    {
      this.zzdl.setPosition(paramLatLng);
      return;
    }
    catch (RemoteException paramLatLng)
    {
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/Marker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */