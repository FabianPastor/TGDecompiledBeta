package com.google.android.gms.internal.config;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzai
  extends zza
  implements zzah
{
  zzai(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.config.internal.IConfigService");
  }
  
  public final void zza(zzaf paramzzaf, zzab paramzzab)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzaf);
    zzc.zza(localParcel, paramzzab);
    transactAndReadExceptionReturnVoid(8, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */