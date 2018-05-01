package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface IUiSettingsDelegate
  extends IInterface
{
  public abstract void setCompassEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMyLocationButtonEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setZoomControlsEnabled(boolean paramBoolean)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IUiSettingsDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */