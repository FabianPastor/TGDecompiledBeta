package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzx
  extends zzee
  implements zzw
{
  public static zzw zzai(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ITileOverlayDelegate");
    if ((localIInterface instanceof zzw)) {
      return (zzw)localIInterface;
    }
    return new zzy(paramIBinder);
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
      remove();
      paramParcel2.writeNoException();
    }
    for (;;)
    {
      return true;
      clearTileCache();
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getId();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      continue;
      setZIndex(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      float f = getZIndex();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
      continue;
      setVisible(zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      continue;
      boolean bool = isVisible();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, bool);
      continue;
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 == null) {
        paramParcel1 = null;
      }
      for (;;)
      {
        bool = zza(paramParcel1);
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        break;
        IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.ITileOverlayDelegate");
        if ((localIInterface instanceof zzw)) {
          paramParcel1 = (zzw)localIInterface;
        } else {
          paramParcel1 = new zzy(paramParcel1);
        }
      }
      paramInt1 = hashCodeRemote();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      setFadeIn(zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      continue;
      bool = getFadeIn();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, bool);
      continue;
      setTransparency(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      f = getTransparency();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */