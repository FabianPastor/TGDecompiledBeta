package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.maps.model.internal.zzq;

public abstract class zzau
  extends zzee
  implements zzat
{
  public zzau()
  {
    attachInterface(this, "com.google.android.gms.maps.internal.IOnMarkerDragListener");
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
      zzb(zzq.zzaf(paramParcel1.readStrongBinder()));
    }
    for (;;)
    {
      paramParcel2.writeNoException();
      return true;
      zzd(zzq.zzaf(paramParcel1.readStrongBinder()));
      continue;
      zzc(zzq.zzaf(paramParcel1.readStrongBinder()));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzau.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */