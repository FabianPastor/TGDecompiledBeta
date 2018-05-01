package com.google.android.gms.vision.face.internal.client;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.vision.zzk;

public abstract interface zze
  extends IInterface
{
  public abstract FaceParcel[] zzc(IObjectWrapper paramIObjectWrapper, zzk paramzzk)
    throws RemoteException;
  
  public abstract void zzf()
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */