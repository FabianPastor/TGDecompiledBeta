package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IAccountAccessor
  extends IInterface
{
  public abstract Account getAccount()
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IAccountAccessor
  {
    public static IAccountAccessor asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
        if ((localIInterface instanceof IAccountAccessor)) {
          paramIBinder = (IAccountAccessor)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 == 2)
      {
        paramParcel1 = getAccount();
        paramParcel2.writeNoException();
        zzc.zzb(paramParcel2, paramParcel1);
      }
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public static class Proxy
      extends zza
      implements IAccountAccessor
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.common.internal.IAccountAccessor");
      }
      
      public Account getAccount()
        throws RemoteException
      {
        Parcel localParcel = transactAndReadException(2, obtainAndWriteInterfaceToken());
        Account localAccount = (Account)zzc.zza(localParcel, Account.CREATOR);
        localParcel.recycle();
        return localAccount;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/IAccountAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */