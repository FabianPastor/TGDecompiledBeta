package com.google.android.gms.maps.internal;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.GoogleMapOptions;

public abstract interface IMapFragmentDelegate
  extends IInterface
{
  public abstract IGoogleMapDelegate getMap()
    throws RemoteException;
  
  public abstract void getMapAsync(zzap paramzzap)
    throws RemoteException;
  
  public abstract boolean isReady()
    throws RemoteException;
  
  public abstract void onCreate(Bundle paramBundle)
    throws RemoteException;
  
  public abstract IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onDestroy()
    throws RemoteException;
  
  public abstract void onDestroyView()
    throws RemoteException;
  
  public abstract void onEnterAmbient(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onExitAmbient()
    throws RemoteException;
  
  public abstract void onInflate(IObjectWrapper paramIObjectWrapper, GoogleMapOptions paramGoogleMapOptions, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onLowMemory()
    throws RemoteException;
  
  public abstract void onPause()
    throws RemoteException;
  
  public abstract void onResume()
    throws RemoteException;
  
  public abstract void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onStart()
    throws RemoteException;
  
  public abstract void onStop()
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IMapFragmentDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */