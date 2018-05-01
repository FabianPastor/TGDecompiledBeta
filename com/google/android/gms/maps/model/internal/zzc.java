package com.google.android.gms.maps.model.internal;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzc
  extends zzeu
  implements zza
{
  zzc(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
  }
  
  public final IObjectWrapper zzd(Bitmap paramBitmap)
    throws RemoteException
  {
    Object localObject = zzbe();
    zzew.zza((Parcel)localObject, paramBitmap);
    paramBitmap = zza(6, (Parcel)localObject);
    localObject = IObjectWrapper.zza.zzaq(paramBitmap.readStrongBinder());
    paramBitmap.recycle();
    return (IObjectWrapper)localObject;
  }
  
  public final IObjectWrapper zzea(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeInt(paramInt);
    localParcel = zza(1, localParcel);
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzaq(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */