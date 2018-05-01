package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzn
  extends zzeu
  implements zzm
{
  zzn(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.dynamite.IDynamiteLoaderV2");
  }
  
  public final IObjectWrapper zza(IObjectWrapper paramIObjectWrapper1, String paramString, int paramInt, IObjectWrapper paramIObjectWrapper2)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIObjectWrapper1);
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt);
    zzew.zza(localParcel, paramIObjectWrapper2);
    paramIObjectWrapper1 = zza(2, localParcel);
    paramString = IObjectWrapper.zza.zzaq(paramIObjectWrapper1.readStrongBinder());
    paramIObjectWrapper1.recycle();
    return paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */