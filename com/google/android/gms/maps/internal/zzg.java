package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface zzg
  extends IInterface
{
  public abstract void onCameraMoveCanceled()
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzg
  {
    public zza()
    {
      attachInterface(this, "com.google.android.gms.maps.internal.IOnCameraMoveCanceledListener");
    }
    
    public static zzg zzhv(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.internal.IOnCameraMoveCanceledListener");
      if ((localIInterface != null) && ((localIInterface instanceof zzg))) {
        return (zzg)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.maps.internal.IOnCameraMoveCanceledListener");
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IOnCameraMoveCanceledListener");
      onCameraMoveCanceled();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzg
    {
      private IBinder zzajf;
      
      zza(IBinder paramIBinder)
      {
        this.zzajf = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzajf;
      }
      
      public void onCameraMoveCanceled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IOnCameraMoveCanceledListener");
          this.zzajf.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */