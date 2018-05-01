package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzn
  extends zzee
  implements zzm
{
  public static zzm zzae(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorLevelDelegate");
    if ((localIInterface instanceof zzm)) {
      return (zzm)localIInterface;
    }
    return new zzo(paramIBinder);
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    default: 
      return false;
    case 1: 
      paramParcel1 = getName();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
    }
    for (;;)
    {
      return true;
      paramParcel1 = getShortName();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      continue;
      activate();
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 == null) {
        paramParcel1 = null;
      }
      for (;;)
      {
        boolean bool = zza(paramParcel1);
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        break;
        IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorLevelDelegate");
        if ((localIInterface instanceof zzm)) {
          paramParcel1 = (zzm)localIInterface;
        } else {
          paramParcel1 = new zzo(paramParcel1);
        }
      }
      paramInt1 = hashCodeRemote();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */