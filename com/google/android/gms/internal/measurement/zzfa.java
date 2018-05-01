package com.google.android.gms.internal.measurement;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public final class zzfa
  extends zzn
  implements zzey
{
  zzfa(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
  }
  
  public final List<zzjs> zza(zzec paramzzec, boolean paramBoolean)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzp.zza((Parcel)localObject, paramzzec);
    zzp.zza((Parcel)localObject, paramBoolean);
    paramzzec = transactAndReadException(7, (Parcel)localObject);
    localObject = paramzzec.createTypedArrayList(zzjs.CREATOR);
    paramzzec.recycle();
    return (List<zzjs>)localObject;
  }
  
  public final List<zzef> zza(String paramString1, String paramString2, zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzp.zza(localParcel, paramzzec);
    paramString1 = transactAndReadException(16, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzef.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final List<zzjs> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    zzp.zza(localParcel, paramBoolean);
    paramString2 = transactAndReadException(15, localParcel);
    paramString1 = paramString2.createTypedArrayList(zzjs.CREATOR);
    paramString2.recycle();
    return paramString1;
  }
  
  public final List<zzjs> zza(String paramString1, String paramString2, boolean paramBoolean, zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzp.zza(localParcel, paramBoolean);
    zzp.zza(localParcel, paramzzec);
    paramString1 = transactAndReadException(14, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzjs.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
  
  public final void zza(long paramLong, String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeLong(paramLong);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    transactAndReadExceptionReturnVoid(10, localParcel);
  }
  
  public final void zza(zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(4, localParcel);
  }
  
  public final void zza(zzef paramzzef, zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzef);
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(12, localParcel);
  }
  
  public final void zza(zzeu paramzzeu, zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzeu);
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(1, localParcel);
  }
  
  public final void zza(zzeu paramzzeu, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzeu);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    transactAndReadExceptionReturnVoid(5, localParcel);
  }
  
  public final void zza(zzjs paramzzjs, zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzjs);
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(2, localParcel);
  }
  
  public final void zzb(zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(6, localParcel);
  }
  
  public final void zzb(zzef paramzzef)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzef);
    transactAndReadExceptionReturnVoid(13, localParcel);
  }
  
  public final String zzc(zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzec);
    localParcel = transactAndReadException(11, localParcel);
    paramzzec = localParcel.readString();
    localParcel.recycle();
    return paramzzec;
  }
  
  public final void zzd(zzec paramzzec)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzp.zza(localParcel, paramzzec);
    transactAndReadExceptionReturnVoid(18, localParcel);
  }
  
  public final List<zzef> zze(String paramString1, String paramString2, String paramString3)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    paramString1 = transactAndReadException(17, localParcel);
    paramString2 = paramString1.createTypedArrayList(zzef.CREATOR);
    paramString1.recycle();
    return paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */