package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.GoogleMapOptions;

public abstract interface zze
  extends IInterface
{
  public abstract IMapViewDelegate zza(IObjectWrapper paramIObjectWrapper, GoogleMapOptions paramGoogleMapOptions)
    throws RemoteException;
  
  public abstract void zza(IObjectWrapper paramIObjectWrapper, int paramInt)
    throws RemoteException;
  
  public abstract ICameraUpdateFactoryDelegate zzd()
    throws RemoteException;
  
  public abstract com.google.android.gms.internal.maps.zze zze()
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */