package com.google.android.gms.maps.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.maps.zza;
import com.google.android.gms.internal.maps.zzc;

public final class zzk
  extends zza
  implements IMapViewDelegate
{
  zzk(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.internal.IMapViewDelegate");
  }
  
  public final void getMapAsync(zzap paramzzap)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzap);
    transactAndReadExceptionReturnVoid(9, localParcel);
  }
  
  public final IObjectWrapper getView()
    throws RemoteException
  {
    Parcel localParcel = transactAndReadException(8, obtainAndWriteInterfaceToken());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.Stub.asInterface(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final void onCreate(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBundle);
    transactAndReadExceptionReturnVoid(2, localParcel);
  }
  
  public final void onDestroy()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(5, obtainAndWriteInterfaceToken());
  }
  
  public final void onEnterAmbient(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBundle);
    transactAndReadExceptionReturnVoid(10, localParcel);
  }
  
  public final void onExitAmbient()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(11, obtainAndWriteInterfaceToken());
  }
  
  public final void onLowMemory()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(6, obtainAndWriteInterfaceToken());
  }
  
  public final void onPause()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(4, obtainAndWriteInterfaceToken());
  }
  
  public final void onResume()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(3, obtainAndWriteInterfaceToken());
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBundle);
    localParcel = transactAndReadException(7, localParcel);
    if (localParcel.readInt() != 0) {
      paramBundle.readFromParcel(localParcel);
    }
    localParcel.recycle();
  }
  
  public final void onStart()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(12, obtainAndWriteInterfaceToken());
  }
  
  public final void onStop()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(13, obtainAndWriteInterfaceToken());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */