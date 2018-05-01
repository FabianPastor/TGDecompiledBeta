package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;

public abstract class zze
  extends zzee
  implements zzd
{
  public static zzd zzab(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ICircleDelegate");
    if ((localIInterface instanceof zzd)) {
      return (zzd)localIInterface;
    }
    return new zzf(paramIBinder);
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
      setCenter((LatLng)zzef.zza(paramParcel1, LatLng.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getCenter();
      paramParcel2.writeNoException();
      zzef.zzb(paramParcel2, paramParcel1);
      continue;
      setRadius(paramParcel1.readDouble());
      paramParcel2.writeNoException();
      continue;
      double d = getRadius();
      paramParcel2.writeNoException();
      paramParcel2.writeDouble(d);
      continue;
      setStrokeWidth(paramParcel1.readFloat());
      paramParcel2.writeNoException();
      continue;
      float f = getStrokeWidth();
      paramParcel2.writeNoException();
      paramParcel2.writeFloat(f);
      continue;
      setStrokeColor(paramParcel1.readInt());
      paramParcel2.writeNoException();
      continue;
      paramInt1 = getStrokeColor();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      setFillColor(paramParcel1.readInt());
      paramParcel2.writeNoException();
      continue;
      paramInt1 = getFillColor();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
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
        IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.ICircleDelegate");
        if ((localIInterface instanceof zzd)) {
          paramParcel1 = (zzd)localIInterface;
        } else {
          paramParcel1 = new zzf(paramParcel1);
        }
      }
      paramInt1 = hashCodeRemote();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      setClickable(zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      continue;
      bool = isClickable();
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, bool);
      continue;
      setStrokePattern(paramParcel1.createTypedArrayList(PatternItem.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = getStrokePattern();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */