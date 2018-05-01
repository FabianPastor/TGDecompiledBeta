package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzeu;

public final class zzav
  extends zzeu
  implements zzat
{
  zzav(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.common.internal.ICertData");
  }
  
  public final IObjectWrapper zzaga()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzbe());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzaq(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final int zzagb()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzbe());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzav.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */