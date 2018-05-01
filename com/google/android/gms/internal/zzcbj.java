package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.identity.intents.UserAddressRequest;

public final class zzcbj
  extends zzed
  implements zzcbi
{
  zzcbj(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.identity.intents.internal.IAddressService");
  }
  
  public final void zza(zzcbg paramzzcbg, UserAddressRequest paramUserAddressRequest, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcbg);
    zzef.zza(localParcel, paramUserAddressRequest);
    zzef.zza(localParcel, paramBundle);
    zzb(2, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcbj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */