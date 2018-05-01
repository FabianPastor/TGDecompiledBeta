package com.google.android.gms.internal.measurement;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class zzn
  implements IInterface
{
  private final IBinder zzqd;
  private final String zzqe;
  
  protected zzn(IBinder paramIBinder, String paramString)
  {
    this.zzqd = paramIBinder;
    this.zzqe = paramString;
  }
  
  public IBinder asBinder()
  {
    return this.zzqd;
  }
  
  protected final Parcel obtainAndWriteInterfaceToken()
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken(this.zzqe);
    return localParcel;
  }
  
  protected final Parcel transactAndReadException(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      this.zzqd.transact(paramInt, paramParcel, localParcel, 0);
      localParcel.readException();
      return localParcel;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    finally
    {
      paramParcel.recycle();
    }
  }
  
  protected final void transactAndReadExceptionReturnVoid(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      this.zzqd.transact(paramInt, paramParcel, localParcel, 0);
      localParcel.readException();
      return;
    }
    finally
    {
      paramParcel.recycle();
      localParcel.recycle();
    }
  }
  
  protected final void transactOneway(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    try
    {
      this.zzqd.transact(1, paramParcel, null, 1);
      return;
    }
    finally
    {
      paramParcel.recycle();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */