package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public abstract interface IProjectionDelegate
  extends IInterface
{
  public abstract LatLng fromScreenLocation(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract VisibleRegion getVisibleRegion()
    throws RemoteException;
  
  public abstract IObjectWrapper toScreenLocation(LatLng paramLatLng)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IProjectionDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */