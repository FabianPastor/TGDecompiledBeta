package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzl
  extends zzeu
  implements zzk
{
  zzl(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.dynamite.IDynamiteLoader");
  }
  
  public final int zza(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    zzew.zza(localParcel, paramBoolean);
    paramIObjectWrapper = zza(3, localParcel);
    int i = paramIObjectWrapper.readInt();
    paramIObjectWrapper.recycle();
    return i;
  }
  
  public final IObjectWrapper zza(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt);
    paramIObjectWrapper = zza(2, localParcel);
    paramString = IObjectWrapper.zza.zzaq(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */