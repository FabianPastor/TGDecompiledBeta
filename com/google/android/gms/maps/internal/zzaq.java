package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.maps.zzb;

public abstract class zzaq
  extends zzb
  implements zzap
{
  public zzaq()
  {
    super("com.google.android.gms.maps.internal.IOnMapReadyCallback");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (paramInt1 == 1)
    {
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 == null)
      {
        paramParcel1 = null;
        zza(paramParcel1);
        paramParcel2.writeNoException();
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.internal.IGoogleMapDelegate");
      if ((localIInterface instanceof IGoogleMapDelegate))
      {
        paramParcel1 = (IGoogleMapDelegate)localIInterface;
        break;
      }
      paramParcel1 = new zzg(paramParcel1);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzaq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */