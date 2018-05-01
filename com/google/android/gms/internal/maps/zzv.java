package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.LatLng;

public final class zzv
  extends zza
  implements zzt
{
  zzv(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IMarkerDelegate");
  }
  
  public final LatLng getPosition()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(4, obtainAndWriteInterfaceToken());
    LatLng localLatLng = (LatLng)zzc.zza(localParcel, LatLng.CREATOR);
    localParcel.recycle();
    return localLatLng;
  }
  
  public final void setPosition(LatLng paramLatLng)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramLatLng);
    transactAndReadExceptionReturnVoid(3, localParcel);
  }
  
  public final int zzi()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(17, obtainAndWriteInterfaceToken());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean zzj(zzt paramzzt)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzt);
    paramzzt = transactAndReadException(16, localParcel);
    boolean bool = zzc.zza(paramzzt);
    paramzzt.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/maps/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */