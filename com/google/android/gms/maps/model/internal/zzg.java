package com.google.android.gms.maps.model.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public abstract interface zzg
  extends IInterface
{
  public abstract float getBearing()
    throws RemoteException;
  
  public abstract LatLngBounds getBounds()
    throws RemoteException;
  
  public abstract float getHeight()
    throws RemoteException;
  
  public abstract String getId()
    throws RemoteException;
  
  public abstract LatLng getPosition()
    throws RemoteException;
  
  public abstract IObjectWrapper getTag()
    throws RemoteException;
  
  public abstract float getTransparency()
    throws RemoteException;
  
  public abstract float getWidth()
    throws RemoteException;
  
  public abstract float getZIndex()
    throws RemoteException;
  
  public abstract int hashCodeRemote()
    throws RemoteException;
  
  public abstract boolean isClickable()
    throws RemoteException;
  
  public abstract boolean isVisible()
    throws RemoteException;
  
  public abstract void remove()
    throws RemoteException;
  
  public abstract void setBearing(float paramFloat)
    throws RemoteException;
  
  public abstract void setClickable(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setDimensions(float paramFloat)
    throws RemoteException;
  
  public abstract void setPosition(LatLng paramLatLng)
    throws RemoteException;
  
  public abstract void setPositionFromBounds(LatLngBounds paramLatLngBounds)
    throws RemoteException;
  
  public abstract void setTag(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract void setTransparency(float paramFloat)
    throws RemoteException;
  
  public abstract void setVisible(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setZIndex(float paramFloat)
    throws RemoteException;
  
  public abstract void zzJ(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract boolean zzb(zzg paramzzg)
    throws RemoteException;
  
  public abstract void zzf(float paramFloat1, float paramFloat2)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */