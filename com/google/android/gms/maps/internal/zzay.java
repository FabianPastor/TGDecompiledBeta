package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.maps.zzb;

public abstract class zzay
  extends zzb
  implements zzax
{
  public zzay()
  {
    super("com.google.android.gms.maps.internal.IOnMyLocationChangeListener");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    boolean bool = true;
    if (paramInt1 == 1)
    {
      zza(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
    }
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */