package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

final class zzaz
  implements zzay
{
  private final IBinder zzalc;
  
  zzaz(IBinder paramIBinder)
  {
    this.zzalc = paramIBinder;
  }
  
  public final IBinder asBinder()
  {
    return this.zzalc;
  }
  
  public final void zza(zzaw paramzzaw, zzz paramzzz)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
      localParcel1.writeStrongBinder(paramzzaw.asBinder());
      localParcel1.writeInt(1);
      paramzzz.writeToParcel(localParcel1, 0);
      this.zzalc.transact(46, localParcel1, localParcel2, 0);
      localParcel2.readException();
      return;
    }
    finally
    {
      localParcel2.recycle();
      localParcel1.recycle();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzaz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */