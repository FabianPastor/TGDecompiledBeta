package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzcfe
  extends zzee
  implements zzcfd
{
  public zzcfe()
  {
    attachInterface(this, "com.google.android.gms.measurement.internal.IMeasurementService");
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    case 3: 
    case 8: 
    default: 
      return false;
    case 1: 
      zza((zzcez)zzef.zza(paramParcel1, zzcez.CREATOR), (zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
    }
    for (;;)
    {
      return true;
      zza((zzcji)zzef.zza(paramParcel1, zzcji.CREATOR), (zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zza((zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zza((zzcez)zzef.zza(paramParcel1, zzcez.CREATOR), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      continue;
      zzb((zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zza((zzceh)zzef.zza(paramParcel1, zzceh.CREATOR), zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza((zzcez)zzef.zza(paramParcel1, zzcez.CREATOR), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeByteArray(paramParcel1);
      continue;
      zza(paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zzc((zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      continue;
      zza((zzcek)zzef.zza(paramParcel1, zzcek.CREATOR), (zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zzb((zzcek)zzef.zza(paramParcel1, zzcek.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), zzef.zza(paramParcel1), (zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), (zzceh)zzef.zza(paramParcel1, zzceh.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zzk(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */