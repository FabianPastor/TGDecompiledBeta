package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.maps.zza;
import com.google.android.gms.internal.maps.zzc;

public final class zzbx
  extends zza
  implements IUiSettingsDelegate
{
  zzbx(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IUiSettingsDelegate");
  }
  
  public final void setCompassEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBoolean);
    transactAndReadExceptionReturnVoid(2, localParcel);
  }
  
  public final void setMyLocationButtonEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBoolean);
    transactAndReadExceptionReturnVoid(3, localParcel);
  }
  
  public final void setZoomControlsEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBoolean);
    transactAndReadExceptionReturnVoid(1, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */