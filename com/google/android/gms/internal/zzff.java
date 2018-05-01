package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzff
  extends zzed
  implements zzfd
{
  zzff(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(1, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final boolean zzb(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    localParcel = zza(2, localParcel);
    paramBoolean = zzef.zza(localParcel);
    localParcel.recycle();
    return paramBoolean;
  }
  
  public final void zzc(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString);
    zzef.zza(localParcel, paramBoolean);
    zzb(4, localParcel);
  }
  
  public final String zzq(String paramString)
    throws RemoteException
  {
    Object localObject = zzZ();
    ((Parcel)localObject).writeString(paramString);
    paramString = zza(3, (Parcel)localObject);
    localObject = paramString.readString();
    paramString.recycle();
    return (String)localObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */