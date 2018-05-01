package com.google.android.gms.internal.config;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;

public abstract class zzag
  extends zzb
  implements zzaf
{
  public zzag()
  {
    super("com.google.android.gms.config.internal.IConfigCallbacks");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    boolean bool;
    switch (paramInt1)
    {
    default: 
      bool = false;
      return bool;
    case 1: 
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), paramParcel1.createByteArray());
    }
    for (;;)
    {
      bool = true;
      break;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), zzc.zza(paramParcel1));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (zzad)zzc.zza(paramParcel1, zzad.CREATOR));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */