package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzal;

public final class zzctt
  extends zzed
  implements zzcts
{
  zzctt(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.signin.internal.ISignInService");
  }
  
  public final void zza(zzal paramzzal, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzal);
    localParcel.writeInt(paramInt);
    zzef.zza(localParcel, paramBoolean);
    zzb(9, localParcel);
  }
  
  public final void zza(zzctv paramzzctv, zzctq paramzzctq)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzctv);
    zzef.zza(localParcel, paramzzctq);
    zzb(12, localParcel);
  }
  
  public final void zzbv(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt);
    zzb(7, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */