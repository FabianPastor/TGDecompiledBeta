package com.google.android.gms.maps.internal;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzbr
  extends zzee
  implements zzbq
{
  public zzbr()
  {
    attachInterface(this, "com.google.android.gms.maps.internal.ISnapshotReadyCallback");
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
      onSnapshotReady((Bitmap)zzef.zza(paramParcel1, Bitmap.CREATOR));
    }
    for (;;)
    {
      paramParcel2.writeNoException();
      return true;
      zzG(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */