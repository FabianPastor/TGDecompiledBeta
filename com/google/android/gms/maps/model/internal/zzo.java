package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;

public final class zzo
  extends zzed
  implements zzm
{
  zzo(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IIndoorLevelDelegate");
  }
  
  public final void activate()
    throws RemoteException
  {
    zzb(3, zzZ());
  }
  
  public final String getName()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final String getShortName()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(5, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean zza(zzm paramzzm)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzm);
    paramzzm = zza(4, localParcel);
    boolean bool = zzef.zza(paramzzm);
    paramzzm.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */