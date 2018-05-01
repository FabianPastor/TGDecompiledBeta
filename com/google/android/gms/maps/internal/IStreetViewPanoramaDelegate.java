package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

public abstract interface IStreetViewPanoramaDelegate
  extends IInterface
{
  public abstract void animateTo(StreetViewPanoramaCamera paramStreetViewPanoramaCamera, long paramLong)
    throws RemoteException;
  
  public abstract void enablePanning(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableStreetNames(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableUserNavigation(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableZoom(boolean paramBoolean)
    throws RemoteException;
  
  public abstract StreetViewPanoramaCamera getPanoramaCamera()
    throws RemoteException;
  
  public abstract StreetViewPanoramaLocation getStreetViewPanoramaLocation()
    throws RemoteException;
  
  public abstract boolean isPanningGesturesEnabled()
    throws RemoteException;
  
  public abstract boolean isStreetNamesEnabled()
    throws RemoteException;
  
  public abstract boolean isUserNavigationEnabled()
    throws RemoteException;
  
  public abstract boolean isZoomGesturesEnabled()
    throws RemoteException;
  
  public abstract IObjectWrapper orientationToPoint(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
    throws RemoteException;
  
  public abstract StreetViewPanoramaOrientation pointToOrientation(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaCameraChangeListener(zzbf paramzzbf)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaChangeListener(zzbh paramzzbh)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaClickListener(zzbj paramzzbj)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaLongClickListener(zzbl paramzzbl)
    throws RemoteException;
  
  public abstract void setPosition(LatLng paramLatLng)
    throws RemoteException;
  
  public abstract void setPositionWithID(String paramString)
    throws RemoteException;
  
  public abstract void setPositionWithRadius(LatLng paramLatLng, int paramInt)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */