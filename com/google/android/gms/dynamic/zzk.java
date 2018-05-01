package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;

public abstract interface zzk
  extends IInterface
{
  public abstract Bundle getArguments()
    throws RemoteException;
  
  public abstract int getId()
    throws RemoteException;
  
  public abstract boolean getRetainInstance()
    throws RemoteException;
  
  public abstract String getTag()
    throws RemoteException;
  
  public abstract int getTargetRequestCode()
    throws RemoteException;
  
  public abstract boolean getUserVisibleHint()
    throws RemoteException;
  
  public abstract IObjectWrapper getView()
    throws RemoteException;
  
  public abstract boolean isAdded()
    throws RemoteException;
  
  public abstract boolean isDetached()
    throws RemoteException;
  
  public abstract boolean isHidden()
    throws RemoteException;
  
  public abstract boolean isInLayout()
    throws RemoteException;
  
  public abstract boolean isRemoving()
    throws RemoteException;
  
  public abstract boolean isResumed()
    throws RemoteException;
  
  public abstract boolean isVisible()
    throws RemoteException;
  
  public abstract void setHasOptionsMenu(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMenuVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setRetainInstance(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserVisibleHint(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void startActivity(Intent paramIntent)
    throws RemoteException;
  
  public abstract void startActivityForResult(Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract IObjectWrapper zzapx()
    throws RemoteException;
  
  public abstract zzk zzapy()
    throws RemoteException;
  
  public abstract IObjectWrapper zzapz()
    throws RemoteException;
  
  public abstract zzk zzaqa()
    throws RemoteException;
  
  public abstract void zzv(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract void zzw(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */