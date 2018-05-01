package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.maps.zza;
import com.google.android.gms.internal.maps.zzc;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public final class zzb
  extends zza
  implements ICameraUpdateFactoryDelegate
{
  zzb(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate");
  }
  
  public final IObjectWrapper newLatLng(LatLng paramLatLng)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzc.zza((Parcel)localObject, paramLatLng);
    paramLatLng = transactAndReadException(8, (Parcel)localObject);
    localObject = IObjectWrapper.Stub.asInterface(paramLatLng.readStrongBinder());
    paramLatLng.recycle();
    return (IObjectWrapper)localObject;
  }
  
  public final IObjectWrapper newLatLngBounds(LatLngBounds paramLatLngBounds, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramLatLngBounds);
    localParcel.writeInt(paramInt);
    localParcel = transactAndReadException(10, localParcel);
    paramLatLngBounds = IObjectWrapper.Stub.asInterface(localParcel.readStrongBinder());
    localParcel.recycle();
    return paramLatLngBounds;
  }
  
  public final IObjectWrapper newLatLngZoom(LatLng paramLatLng, float paramFloat)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzc.zza((Parcel)localObject, paramLatLng);
    ((Parcel)localObject).writeFloat(paramFloat);
    paramLatLng = transactAndReadException(9, (Parcel)localObject);
    localObject = IObjectWrapper.Stub.asInterface(paramLatLng.readStrongBinder());
    paramLatLng.recycle();
    return (IObjectWrapper)localObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */