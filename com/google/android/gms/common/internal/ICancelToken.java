package com.google.android.gms.common.internal;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface ICancelToken
  extends IInterface
{
  public abstract void cancel()
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ICancelToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */