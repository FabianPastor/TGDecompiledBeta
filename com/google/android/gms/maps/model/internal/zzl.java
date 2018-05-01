package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import java.util.ArrayList;
import java.util.List;

public final class zzl
  extends zzed
  implements zzj
{
  zzl(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IIndoorBuildingDelegate");
  }
  
  public final int getActiveLevelIndex()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final int getDefaultLevelIndex()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final List<IBinder> getLevels()
    throws RemoteException
  {
    Parcel localParcel = zza(3, zzZ());
    ArrayList localArrayList = localParcel.createBinderArrayList();
    localParcel.recycle();
    return localArrayList;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(6, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean isUnderground()
    throws RemoteException
  {
    Parcel localParcel = zza(4, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean zzb(zzj paramzzj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzj);
    paramzzj = zza(5, localParcel);
    boolean bool = zzef.zza(paramzzj);
    paramzzj.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */