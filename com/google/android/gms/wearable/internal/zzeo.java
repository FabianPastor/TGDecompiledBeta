package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.wearable.zza;
import com.google.android.gms.internal.wearable.zzc;
import java.util.List;

public final class zzeo
  extends zza
  implements zzem
{
  zzeo(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wearable.internal.IWearableListener");
  }
  
  public final void onConnectedNodes(List<zzfo> paramList)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeTypedList(paramList);
    transactOneway(5, localParcel);
  }
  
  public final void zza(DataHolder paramDataHolder)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramDataHolder);
    transactOneway(1, localParcel);
  }
  
  public final void zza(zzah paramzzah)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzah);
    transactOneway(8, localParcel);
  }
  
  public final void zza(zzaw paramzzaw)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzaw);
    transactOneway(7, localParcel);
  }
  
  public final void zza(zzfe paramzzfe)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzfe);
    transactOneway(2, localParcel);
  }
  
  public final void zza(zzfo paramzzfo)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzfo);
    transactOneway(3, localParcel);
  }
  
  public final void zza(zzi paramzzi)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzi);
    transactOneway(9, localParcel);
  }
  
  public final void zza(zzl paramzzl)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzl);
    transactOneway(6, localParcel);
  }
  
  public final void zzb(zzfo paramzzfo)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzfo);
    transactOneway(4, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzeo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */