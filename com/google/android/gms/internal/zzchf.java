package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzchf
  extends zzev
  implements zzche
{
  public zzchf()
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
      zza((zzcha)zzew.zza(paramParcel1, zzcha.CREATOR), (zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
    }
    for (;;)
    {
      return true;
      zza((zzcln)zzew.zza(paramParcel1, zzcln.CREATOR), (zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zza((zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zza((zzcha)zzew.zza(paramParcel1, zzcha.CREATOR), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      continue;
      zzb((zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zza((zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR), zzew.zza(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza((zzcha)zzew.zza(paramParcel1, zzcha.CREATOR), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeByteArray(paramParcel1);
      continue;
      zza(paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zzc((zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      continue;
      zza((zzcgl)zzew.zza(paramParcel1, zzcgl.CREATOR), (zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      continue;
      zzb((zzcgl)zzew.zza(paramParcel1, zzcgl.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), zzew.zza(paramParcel1), (zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), zzew.zza(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zza(paramParcel1.readString(), paramParcel1.readString(), (zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      paramParcel1 = zzj(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      continue;
      zzd((zzcgi)zzew.zza(paramParcel1, zzcgi.CREATOR));
      paramParcel2.writeNoException();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */