package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzfq
  extends zzeu
  implements zzfo
{
  zzfq(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzbe());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final boolean zzb(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, true);
    localParcel = zza(2, localParcel);
    paramBoolean = zzew.zza(localParcel);
    localParcel.recycle();
    return paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */