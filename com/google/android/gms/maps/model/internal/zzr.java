package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;
import com.google.android.gms.maps.model.LatLng;

public final class zzr
  extends zzeu
  implements zzp
{
  zzr(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IMarkerDelegate");
  }
  
  public final LatLng getPosition()
    throws RemoteException
  {
    Parcel localParcel = zza(4, zzbe());
    LatLng localLatLng = (LatLng)zzew.zza(localParcel, LatLng.CREATOR);
    localParcel.recycle();
    return localLatLng;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(17, zzbe());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final void setPosition(LatLng paramLatLng)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramLatLng);
    zzb(3, localParcel);
  }
  
  public final boolean zzj(zzp paramzzp)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzp);
    paramzzp = zza(16, localParcel);
    boolean bool = zzew.zza(paramzzp);
    paramzzp.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */