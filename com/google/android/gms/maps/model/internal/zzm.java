package com.google.android.gms.maps.model.internal;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface zzm
  extends IInterface
{
  public abstract void activate()
    throws RemoteException;
  
  public abstract String getName()
    throws RemoteException;
  
  public abstract String getShortName()
    throws RemoteException;
  
  public abstract int hashCodeRemote()
    throws RemoteException;
  
  public abstract boolean zza(zzm paramzzm)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */