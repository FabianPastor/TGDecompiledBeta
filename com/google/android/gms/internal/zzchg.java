package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public final class zzchg
  extends zzeu
  implements zzche
{
  zzchg(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
  }
  
  public final List<zzcln> zza(zzcgi paramzzcgi, boolean paramBoolean)
    throws RemoteException
  {
    Object localObject = zzbe();
    zzew.zza((Parcel)localObject, paramzzcgi);
    zzew.zza((Parcel)localObject, paramBoolean);
    paramzzcgi = zza(7, (Parcel)localObject);
    localObject = paramzzcgi.createTypedArrayList(zzcln.CREATOR);
    paramzzcgi.recycle();
    return (List<zzcln>)localObject;
  }
  
  public final List<zzcgl> zza(String paramString1, String paramString2, zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzew.zza(localParcel, paramzzcgi);
    paramString1 = zza(16, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcgl.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final List<zzcln> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    zzew.zza(localParcel, paramBoolean);
    paramString1 = zza(15, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcln.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final List<zzcln> zza(String paramString1, String paramString2, boolean paramBoolean, zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzew.zza(localParcel, paramBoolean);
    zzew.zza(localParcel, paramzzcgi);
    paramString1 = zza(14, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcln.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final void zza(long paramLong, String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeLong(paramLong);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    zzb(10, localParcel);
  }
  
  public final void zza(zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcgi);
    zzb(4, localParcel);
  }
  
  public final void zza(zzcgl paramzzcgl, zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcgl);
    zzew.zza(localParcel, paramzzcgi);
    zzb(12, localParcel);
  }
  
  public final void zza(zzcha paramzzcha, zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcha);
    zzew.zza(localParcel, paramzzcgi);
    zzb(1, localParcel);
  }
  
  public final void zza(zzcha paramzzcha, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcha);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzb(5, localParcel);
  }
  
  public final void zza(zzcln paramzzcln, zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcln);
    zzew.zza(localParcel, paramzzcgi);
    zzb(2, localParcel);
  }
  
  public final byte[] zza(zzcha paramzzcha, String paramString)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcha);
    localParcel.writeString(paramString);
    paramzzcha = zza(9, localParcel);
    paramString = paramzzcha.createByteArray();
    paramzzcha.recycle();
    return paramString;
  }
  
  public final void zzb(zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcgi);
    zzb(6, localParcel);
  }
  
  public final void zzb(zzcgl paramzzcgl)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcgl);
    zzb(13, localParcel);
  }
  
  public final String zzc(zzcgi paramzzcgi)
    throws RemoteException
  {
    Object localObject = zzbe();
    zzew.zza((Parcel)localObject, paramzzcgi);
    paramzzcgi = zza(11, (Parcel)localObject);
    localObject = paramzzcgi.readString();
    paramzzcgi.recycle();
    return (String)localObject;
  }
  
  public final void zzd(zzcgi paramzzcgi)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramzzcgi);
    zzb(18, localParcel);
  }
  
  public final List<zzcgl> zzj(String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    paramString1 = zza(17, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzcgl.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */