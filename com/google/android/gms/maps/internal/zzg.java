package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.maps.zza;
import com.google.android.gms.internal.maps.zzc;
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.internal.maps.zzu;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

public final class zzg
  extends zza
  implements IGoogleMapDelegate
{
  zzg(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IGoogleMapDelegate");
  }
  
  public final zzt addMarker(MarkerOptions paramMarkerOptions)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzc.zza((Parcel)localObject, paramMarkerOptions);
    paramMarkerOptions = transactAndReadException(11, (Parcel)localObject);
    localObject = zzu.zzg(paramMarkerOptions.readStrongBinder());
    paramMarkerOptions.recycle();
    return (zzt)localObject;
  }
  
  public final void animateCamera(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper);
    transactAndReadExceptionReturnVoid(5, localParcel);
  }
  
  public final CameraPosition getCameraPosition()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(1, obtainAndWriteInterfaceToken());
    CameraPosition localCameraPosition = (CameraPosition)zzc.zza(localParcel, CameraPosition.CREATOR);
    localParcel.recycle();
    return localCameraPosition;
  }
  
  public final float getMaxZoomLevel()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(2, obtainAndWriteInterfaceToken());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final IUiSettingsDelegate getUiSettings()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(25, obtainAndWriteInterfaceToken());
    IBinder localIBinder = localParcel.readStrongBinder();
    Object localObject;
    if (localIBinder == null) {
      localObject = null;
    }
    for (;;)
    {
      localParcel.recycle();
      return (IUiSettingsDelegate)localObject;
      localObject = localIBinder.queryLocalInterface("com.google.android.gms.maps.internal.IUiSettingsDelegate");
      if ((localObject instanceof IUiSettingsDelegate)) {
        localObject = (IUiSettingsDelegate)localObject;
      } else {
        localObject = new zzbx(localIBinder);
      }
    }
  }
  
  public final void moveCamera(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper);
    transactAndReadExceptionReturnVoid(4, localParcel);
  }
  
  public final void setMapType(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeInt(paramInt);
    transactAndReadExceptionReturnVoid(16, localParcel);
  }
  
  public final void setMyLocationEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBoolean);
    transactAndReadExceptionReturnVoid(22, localParcel);
  }
  
  public final void setOnMyLocationChangeListener(zzax paramzzax)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzax);
    transactAndReadExceptionReturnVoid(36, localParcel);
  }
  
  public final void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    localParcel.writeInt(paramInt3);
    localParcel.writeInt(paramInt4);
    transactAndReadExceptionReturnVoid(39, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */