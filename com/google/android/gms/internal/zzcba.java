package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class zzcba
  extends zzeu
  implements zzcay
{
  zzcba(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.flags.IFlagProvider");
  }
  
  public final boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString);
    zzew.zza(localParcel, paramBoolean);
    localParcel.writeInt(paramInt);
    paramString = zza(2, localParcel);
    paramBoolean = zzew.zza(paramString);
    paramString.recycle();
    return paramBoolean;
  }
  
  public final int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    paramString = zza(3, localParcel);
    paramInt1 = paramString.readInt();
    paramString.recycle();
    return paramInt1;
  }
  
  public final long getLongFlagValue(String paramString, long paramLong, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString);
    localParcel.writeLong(paramLong);
    localParcel.writeInt(paramInt);
    paramString = zza(4, localParcel);
    paramLong = paramString.readLong();
    paramString.recycle();
    return paramLong;
  }
  
  public final String getStringFlagValue(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeInt(paramInt);
    paramString1 = zza(5, localParcel);
    paramString2 = paramString1.readString();
    paramString1.recycle();
    return paramString2;
  }
  
  public final void init(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIObjectWrapper);
    zzb(1, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcba.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */