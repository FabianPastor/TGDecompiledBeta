package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class zzeu
  implements IInterface
{
  private final IBinder zzalc;
  private final String zzald;
  
  protected zzeu(IBinder paramIBinder, String paramString)
  {
    this.zzalc = paramIBinder;
    this.zzald = paramString;
  }
  
  public IBinder asBinder()
  {
    return this.zzalc;
  }
  
  protected final Parcel zza(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      this.zzalc.transact(paramInt, paramParcel, localParcel, 0);
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
  
  protected final void zzb(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      this.zzalc.transact(paramInt, paramParcel, localParcel, 0);
      localParcel.readException();
      return;
    }
    finally
    {
      paramParcel.recycle();
      localParcel.recycle();
    }
  }
  
  protected final Parcel zzbe()
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken(this.zzald);
    return localParcel;
  }
  
  protected final void zzc(int paramInt, Parcel paramParcel)
    throws RemoteException
  {
    try
    {
      this.zzalc.transact(paramInt, paramParcel, null, 1);
      return;
    }
    finally
    {
      paramParcel.recycle();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzeu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */