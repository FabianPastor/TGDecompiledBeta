package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public final class zzbp
  extends zzed
  implements IProjectionDelegate
{
  zzbp(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IProjectionDelegate");
  }
  
  public final LatLng fromScreenLocation(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramIObjectWrapper);
    paramIObjectWrapper = zza(1, (Parcel)localObject);
    localObject = (LatLng)zzef.zza(paramIObjectWrapper, LatLng.CREATOR);
    paramIObjectWrapper.recycle();
    return (LatLng)localObject;
  }
  
  public final VisibleRegion getVisibleRegion()
    throws RemoteException
  {
    Parcel localParcel = zza(3, zzZ());
    VisibleRegion localVisibleRegion = (VisibleRegion)zzef.zza(localParcel, VisibleRegion.CREATOR);
    localParcel.recycle();
    return localVisibleRegion;
  }
  
  public final IObjectWrapper toScreenLocation(LatLng paramLatLng)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramLatLng);
    paramLatLng = zza(2, (Parcel)localObject);
    localObject = IObjectWrapper.zza.zzM(paramLatLng.readStrongBinder());
    paramLatLng.recycle();
    return (IObjectWrapper)localObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */