package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.wearable.zza;
import com.google.android.gms.internal.wearable.zzc;

public final class zzeq
  extends zza
  implements zzep
{
  zzeq(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wearable.internal.IWearableService");
  }
  
  public final void zza(zzek paramzzek, zzd paramzzd)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    zzc.zza(localParcel, paramzzd);
    transactAndReadExceptionReturnVoid(16, localParcel);
  }
  
  public final void zza(zzek paramzzek, zzei paramzzei, String paramString)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    zzc.zza(localParcel, paramzzei);
    localParcel.writeString(paramString);
    transactAndReadExceptionReturnVoid(34, localParcel);
  }
  
  public final void zza(zzek paramzzek, String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt);
    transactAndReadExceptionReturnVoid(42, localParcel);
  }
  
  public final void zza(zzek paramzzek, String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeByteArray(paramArrayOfByte);
    transactAndReadExceptionReturnVoid(12, localParcel);
  }
  
  public final void zzb(zzek paramzzek, zzei paramzzei, String paramString)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    zzc.zza(localParcel, paramzzei);
    localParcel.writeString(paramString);
    transactAndReadExceptionReturnVoid(35, localParcel);
  }
  
  public final void zzc(zzek paramzzek, String paramString)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramzzek);
    localParcel.writeString(paramString);
    transactAndReadExceptionReturnVoid(32, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzeq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */