package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.internal.zzq;

public abstract class zzi
  extends zzee
  implements zzh
{
  public zzi()
  {
    attachInterface(this, "com.google.android.gms.maps.internal.IInfoWindowAdapter");
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
      paramParcel1 = zzh(zzq.zzaf(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, paramParcel1);
      return true;
    }
    paramParcel1 = zzi(zzq.zzaf(paramParcel1.readStrongBinder()));
    paramParcel2.writeNoException();
    zzef.zza(paramParcel2, paramParcel1);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */