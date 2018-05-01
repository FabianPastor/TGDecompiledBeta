package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

public final class zzbs
  extends zzed
  implements IStreetViewPanoramaDelegate
{
  zzbs(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
  }
  
  public final void animateTo(StreetViewPanoramaCamera paramStreetViewPanoramaCamera, long paramLong)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramStreetViewPanoramaCamera);
    localParcel.writeLong(paramLong);
    zzb(9, localParcel);
  }
  
  public final void enablePanning(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(2, localParcel);
  }
  
  public final void enableStreetNames(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(4, localParcel);
  }
  
  public final void enableUserNavigation(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(3, localParcel);
  }
  
  public final void enableZoom(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(1, localParcel);
  }
  
  public final StreetViewPanoramaCamera getPanoramaCamera()
    throws RemoteException
  {
    Parcel localParcel = zza(10, zzZ());
    StreetViewPanoramaCamera localStreetViewPanoramaCamera = (StreetViewPanoramaCamera)zzef.zza(localParcel, StreetViewPanoramaCamera.CREATOR);
    localParcel.recycle();
    return localStreetViewPanoramaCamera;
  }
  
  public final StreetViewPanoramaLocation getStreetViewPanoramaLocation()
    throws RemoteException
  {
    Parcel localParcel = zza(14, zzZ());
    StreetViewPanoramaLocation localStreetViewPanoramaLocation = (StreetViewPanoramaLocation)zzef.zza(localParcel, StreetViewPanoramaLocation.CREATOR);
    localParcel.recycle();
    return localStreetViewPanoramaLocation;
  }
  
  public final boolean isPanningGesturesEnabled()
    throws RemoteException
  {
    Parcel localParcel = zza(6, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isStreetNamesEnabled()
    throws RemoteException
  {
    Parcel localParcel = zza(8, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isUserNavigationEnabled()
    throws RemoteException
  {
    Parcel localParcel = zza(7, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isZoomGesturesEnabled()
    throws RemoteException
  {
    Parcel localParcel = zza(5, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final IObjectWrapper orientationToPoint(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramStreetViewPanoramaOrientation);
    paramStreetViewPanoramaOrientation = zza(19, (Parcel)localObject);
    localObject = IObjectWrapper.zza.zzM(paramStreetViewPanoramaOrientation.readStrongBinder());
    paramStreetViewPanoramaOrientation.recycle();
    return (IObjectWrapper)localObject;
  }
  
  public final StreetViewPanoramaOrientation pointToOrientation(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramIObjectWrapper);
    paramIObjectWrapper = zza(18, (Parcel)localObject);
    localObject = (StreetViewPanoramaOrientation)zzef.zza(paramIObjectWrapper, StreetViewPanoramaOrientation.CREATOR);
    paramIObjectWrapper.recycle();
    return (StreetViewPanoramaOrientation)localObject;
  }
  
  public final void setOnStreetViewPanoramaCameraChangeListener(zzbf paramzzbf)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzbf);
    zzb(16, localParcel);
  }
  
  public final void setOnStreetViewPanoramaChangeListener(zzbh paramzzbh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzbh);
    zzb(15, localParcel);
  }
  
  public final void setOnStreetViewPanoramaClickListener(zzbj paramzzbj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzbj);
    zzb(17, localParcel);
  }
  
  public final void setOnStreetViewPanoramaLongClickListener(zzbl paramzzbl)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzbl);
    zzb(20, localParcel);
  }
  
  public final void setPosition(LatLng paramLatLng)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramLatLng);
    zzb(12, localParcel);
  }
  
  public final void setPositionWithID(String paramString)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString);
    zzb(11, localParcel);
  }
  
  public final void setPositionWithRadius(LatLng paramLatLng, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramLatLng);
    localParcel.writeInt(paramInt);
    zzb(13, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */