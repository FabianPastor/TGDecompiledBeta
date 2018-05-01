package com.google.android.gms.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.vision.barcode.Barcode;

public abstract interface ex
  extends IInterface
{
  public abstract void zzDP()
    throws RemoteException;
  
  public abstract Barcode[] zza(IObjectWrapper paramIObjectWrapper, fc paramfc)
    throws RemoteException;
  
  public abstract Barcode[] zzb(IObjectWrapper paramIObjectWrapper, fc paramfc)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */