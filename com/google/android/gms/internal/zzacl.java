package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface zzacl
  extends IInterface
{
  public abstract void zza(zzack paramzzack)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzacl
  {
    public static zzacl zzbB(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.service.ICommonService");
      if ((localIInterface != null) && ((localIInterface instanceof zzacl))) {
        return (zzacl)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.common.internal.service.ICommonService");
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.common.internal.service.ICommonService");
      zza(zzack.zza.zzbA(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class zza
      implements zzacl
    {
      private IBinder zzrk;
      
      zza(IBinder paramIBinder)
      {
        this.zzrk = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzrk;
      }
      
      public void zza(zzack paramzzack)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gms.common.internal.service.ICommonService");
          if (paramzzack != null) {
            localIBinder = paramzzack.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.zzrk.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */