package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public abstract class zzh
  extends zzee
  implements zzg
{
  public static zzg zzac(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
    if ((localIInterface instanceof zzg)) {
      return (zzg)localIInterface;
    }
    return new zzi(paramIBinder);
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
      paramParcel1 = getId();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      continue;
      setPosition((LatLng)zzef.zza(paramParcel1, LatLng.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getPosition();
      paramParcel2.writeNoException();
      zzef.zzb(paramParcel2, paramParcel1);
      continue;
      setDimensions(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      zzf(paramParcel1.readFloat(), paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      float f = getWidth();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
      continue;
      f = getHeight();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
      continue;
      setPositionFromBounds((LatLngBounds)zzef.zza(paramParcel1, LatLngBounds.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getBounds();
      paramParcel2.writeNoException();
      zzef.zzb(paramParcel2, paramParcel1);
      continue;
      setBearing(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      f = getBearing();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
      continue;
      setZIndex(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      f = getZIndex();
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
      setTransparency(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      f = getTransparency();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
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
        IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
        if ((localIInterface instanceof zzg)) {
          paramParcel1 = (zzg)localIInterface;
        } else {
          paramParcel1 = new zzi(paramParcel1);
        }
      }
      paramInt1 = hashCodeRemote();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      zzJ(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      continue;
      setClickable(zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      continue;
      bool = isClickable();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, bool);
      continue;
      setTag(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getTag();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, paramParcel1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */