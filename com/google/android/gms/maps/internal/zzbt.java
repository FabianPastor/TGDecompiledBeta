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
import com.google.android.gms.maps.StreetViewPanoramaOptions;

public final class zzbt
  extends zzed
  implements IStreetViewPanoramaFragmentDelegate
{
  zzbt(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IStreetViewPanoramaFragmentDelegate");
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
    zzb(12, localParcel);
  }
  
  public final boolean isReady()
    throws RemoteException
  {
    Parcel localParcel = zza(11, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final void onCreate(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    zzb(3, localParcel);
  }
  
  public final IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper1);
    zzef.zza(localParcel, paramIObjectWrapper2);
    zzef.zza(localParcel, paramBundle);
    paramIObjectWrapper1 = zza(4, localParcel);
    paramIObjectWrapper2 = IObjectWrapper.zza.zzM(paramIObjectWrapper1.readStrongBinder());
    paramIObjectWrapper1.recycle();
    return paramIObjectWrapper2;
  }
  
  public final void onDestroy()
    throws RemoteException
  {
    zzb(8, zzZ());
  }
  
  public final void onDestroyView()
    throws RemoteException
  {
    zzb(7, zzZ());
  }
  
  public final void onInflate(IObjectWrapper paramIObjectWrapper, StreetViewPanoramaOptions paramStreetViewPanoramaOptions, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramStreetViewPanoramaOptions);
    zzef.zza(localParcel, paramBundle);
    zzb(2, localParcel);
  }
  
  public final void onLowMemory()
    throws RemoteException
  {
    zzb(9, zzZ());
  }
  
  public final void onPause()
    throws RemoteException
  {
    zzb(6, zzZ());
  }
  
  public final void onResume()
    throws RemoteException
  {
    zzb(5, zzZ());
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    localParcel = zza(10, localParcel);
    if (localParcel.readInt() != 0) {
      paramBundle.readFromParcel(localParcel);
    }
    localParcel.recycle();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */