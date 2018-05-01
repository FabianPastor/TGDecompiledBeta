package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.maps.zza;
import com.google.android.gms.internal.maps.zzc;
import com.google.android.gms.maps.GoogleMapOptions;

public final class zzf
  extends zza
  implements zze
{
  zzf(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.ICreator");
  }
  
  public final IMapViewDelegate zza(IObjectWrapper paramIObjectWrapper, GoogleMapOptions paramGoogleMapOptions)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzc.zza((Parcel)localObject, paramIObjectWrapper);
    zzc.zza((Parcel)localObject, paramGoogleMapOptions);
    paramGoogleMapOptions = transactAndReadException(3, (Parcel)localObject);
    paramIObjectWrapper = paramGoogleMapOptions.readStrongBinder();
    if (paramIObjectWrapper == null) {
      paramIObjectWrapper = null;
    }
    for (;;)
    {
      paramGoogleMapOptions.recycle();
      return paramIObjectWrapper;
      localObject = paramIObjectWrapper.queryLocalInterface("com.google.android.gms.maps.internal.IMapViewDelegate");
      if ((localObject instanceof IMapViewDelegate)) {
        paramIObjectWrapper = (IMapViewDelegate)localObject;
      } else {
        paramIObjectWrapper = new zzk(paramIObjectWrapper);
      }
    }
  }
  
  public final void zza(IObjectWrapper paramIObjectWrapper, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper);
    localParcel.writeInt(paramInt);
    transactAndReadExceptionReturnVoid(6, localParcel);
  }
  
  public final ICameraUpdateFactoryDelegate zzd()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(4, obtainAndWriteInterfaceToken());
    Object localObject = localParcel.readStrongBinder();
    if (localObject == null) {
      localObject = null;
    }
    for (;;)
    {
      localParcel.recycle();
      return (ICameraUpdateFactoryDelegate)localObject;
      IInterface localIInterface = ((IBinder)localObject).queryLocalInterface("com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate");
      if ((localIInterface instanceof ICameraUpdateFactoryDelegate)) {
        localObject = (ICameraUpdateFactoryDelegate)localIInterface;
      } else {
        localObject = new zzb((IBinder)localObject);
      }
    }
  }
  
  public final com.google.android.gms.internal.maps.zze zze()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(5, obtainAndWriteInterfaceToken());
    com.google.android.gms.internal.maps.zze localzze = com.google.android.gms.internal.maps.zzf.zzb(localParcel.readStrongBinder());
    localParcel.recycle();
    return localzze;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */