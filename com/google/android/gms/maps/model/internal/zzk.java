package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzk
  extends zzee
  implements zzj
{
  public static zzj zzad(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorBuildingDelegate");
    if ((localIInterface instanceof zzj)) {
      return (zzj)localIInterface;
    }
    return new zzl(paramIBinder);
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
      paramInt1 = getActiveLevelIndex();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
    }
    for (;;)
    {
      return true;
      paramInt1 = getDefaultLevelIndex();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      paramParcel1 = getLevels();
      paramParcel2.writeNoException();
      paramParcel2.writeBinderList(paramParcel1);
      continue;
      boolean bool = isUnderground();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, bool);
      continue;
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 == null) {
        paramParcel1 = null;
      }
      for (;;)
      {
        bool = zzb(paramParcel1);
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        break;
        IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorBuildingDelegate");
        if ((localIInterface instanceof zzj)) {
          paramParcel1 = (zzj)localIInterface;
        } else {
          paramParcel1 = new zzl(paramParcel1);
        }
      }
      paramInt1 = hashCodeRemote();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */