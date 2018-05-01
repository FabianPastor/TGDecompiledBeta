package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import java.util.List;

public abstract interface zzj
  extends IInterface
{
  public abstract int getActiveLevelIndex()
    throws RemoteException;
  
  public abstract int getDefaultLevelIndex()
    throws RemoteException;
  
  public abstract List<IBinder> getLevels()
    throws RemoteException;
  
  public abstract int hashCodeRemote()
    throws RemoteException;
  
  public abstract boolean isUnderground()
    throws RemoteException;
  
  public abstract boolean zzb(zzj paramzzj)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */