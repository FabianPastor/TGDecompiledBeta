package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public final class zzcff
  extends zzed
  implements zzcfd
{
  zzcff(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
  }
  
  public final List<zzcji> zza(zzceh paramzzceh, boolean paramBoolean)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramzzceh);
    zzef.zza((Parcel)localObject, paramBoolean);
    paramzzceh = zza(7, (Parcel)localObject);
    localObject = paramzzceh.createTypedArrayList(zzcji.CREATOR);
    paramzzceh.recycle();
    return (List<zzcji>)localObject;
  }
  
  public final List<zzcek> zza(String paramString1, String paramString2, zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzef.zza(localParcel, paramzzceh);
    paramString1 = zza(16, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcek.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final List<zzcji> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    zzef.zza(localParcel, paramBoolean);
    paramString1 = zza(15, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcji.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final List<zzcji> zza(String paramString1, String paramString2, boolean paramBoolean, zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzef.zza(localParcel, paramBoolean);
    zzef.zza(localParcel, paramzzceh);
    paramString1 = zza(14, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcji.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final void zza(long paramLong, String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeLong(paramLong);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    zzb(10, localParcel);
  }
  
  public final void zza(zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzceh);
    zzb(4, localParcel);
  }
  
  public final void zza(zzcek paramzzcek, zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcek);
    zzef.zza(localParcel, paramzzceh);
    zzb(12, localParcel);
  }
  
  public final void zza(zzcez paramzzcez, zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcez);
    zzef.zza(localParcel, paramzzceh);
    zzb(1, localParcel);
  }
  
  public final void zza(zzcez paramzzcez, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcez);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzb(5, localParcel);
  }
  
  public final void zza(zzcji paramzzcji, zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcji);
    zzef.zza(localParcel, paramzzceh);
    zzb(2, localParcel);
  }
  
  public final byte[] zza(zzcez paramzzcez, String paramString)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcez);
    localParcel.writeString(paramString);
    paramzzcez = zza(9, localParcel);
    paramString = paramzzcez.createByteArray();
    paramzzcez.recycle();
    return paramString;
  }
  
  public final void zzb(zzceh paramzzceh)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzceh);
    zzb(6, localParcel);
  }
  
  public final void zzb(zzcek paramzzcek)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzcek);
    zzb(13, localParcel);
  }
  
  public final String zzc(zzceh paramzzceh)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramzzceh);
    paramzzceh = zza(11, (Parcel)localObject);
    localObject = paramzzceh.readString();
    paramzzceh.recycle();
    return (String)localObject;
  }
  
  public final List<zzcek> zzk(String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    paramString1 = zza(17, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcek.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */