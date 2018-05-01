package com.google.android.gms.maps.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;

public final class zzbu
  extends zzed
  implements IStreetViewPanoramaViewDelegate
{
  zzbu(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate");
  }
  
  public final IStreetViewPanoramaDelegate getStreetViewPanorama()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzZ());
    Object localObject = localParcel.readStrongBinder();
    if (localObject == null) {
      localObject = null;
    }
    for (;;)
    {
      localParcel.recycle();
      return (IStreetViewPanoramaDelegate)localObject;
      IInterface localIInterface = ((IBinder)localObject).queryLocalInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
      if ((localIInterface instanceof IStreetViewPanoramaDelegate)) {
        localObject = (IStreetViewPanoramaDelegate)localIInterface;
      } else {
        localObject = new zzbs((IBinder)localObject);
      }
    }
  }
  
  public final void getStreetViewPanoramaAsync(zzbn paramzzbn)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzbn);
    zzb(9, localParcel);
  }
  
  public final IObjectWrapper getView()
    throws RemoteException
  {
    Parcel localParcel = zza(8, zzZ());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzM(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final void onCreate(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    zzb(2, localParcel);
  }
  
  public final void onDestroy()
    throws RemoteException
  {
    zzb(5, zzZ());
  }
  
  public final void onLowMemory()
    throws RemoteException
  {
    zzb(6, zzZ());
  }
  
  public final void onPause()
    throws RemoteException
  {
    zzb(4, zzZ());
  }
  
  public final void onResume()
    throws RemoteException
  {
    zzb(3, zzZ());
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    localParcel = zza(7, localParcel);
    if (localParcel.readInt() != 0) {
      paramBundle.readFromParcel(localParcel);
    }
    localParcel.recycle();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */