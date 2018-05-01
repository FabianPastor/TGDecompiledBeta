package com.google.android.gms.internal.config;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class zzb
  extends Binder
  implements IInterface
{
  private static zzd zzc = null;
  
  protected zzb(String paramString)
  {
    attachInterface(this, paramString);
  }
  
  public IBinder asBinder()
  {
    return this;
  }
  
  protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    return false;
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (paramInt1 > 16777215)
    {
      bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      if (!bool) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = dispatchTransaction(paramInt1, paramParcel1, paramParcel2, paramInt2))
    {
      return bool;
      paramParcel1.enforceInterface(getInterfaceDescriptor());
      bool = false;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */