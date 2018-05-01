package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzan;

public final class zzcxm
  extends zzeu
  implements zzcxl
{
  zzcxm(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.signin.internal.ISignInService");
  }
  
  public final void zza(zzan paramzzan, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzan);
    localParcel.writeInt(paramInt);
    zzew.zza(localParcel, paramBoolean);
    zzb(9, localParcel);
  }
  
  public final void zza(zzcxo paramzzcxo, zzcxj paramzzcxj)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcxo);
    zzew.zza(localParcel, paramzzcxj);
    zzb(12, localParcel);
  }
  
  public final void zzeh(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeInt(paramInt);
    zzb(7, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */