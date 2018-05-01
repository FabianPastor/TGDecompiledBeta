package com.google.android.search.verification.api;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ISearchActionVerificationService
  extends IInterface
{
  public abstract int getVersion()
    throws RemoteException;
  
  public abstract boolean isSearchAction(Intent paramIntent, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISearchActionVerificationService
  {
    public static ISearchActionVerificationService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
        if ((localIInterface != null) && ((localIInterface instanceof ISearchActionVerificationService))) {
          paramIBinder = (ISearchActionVerificationService)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool1 = true;
      switch (paramInt1)
      {
      default: 
        bool1 = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      }
      for (;;)
      {
        return bool1;
        paramParcel2.writeString("com.google.android.search.verification.api.ISearchActionVerificationService");
        continue;
        paramParcel1.enforceInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
        Intent localIntent;
        if (paramParcel1.readInt() != 0)
        {
          localIntent = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          label90:
          if (paramParcel1.readInt() == 0) {
            break label144;
          }
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          label110:
          boolean bool2 = isSearchAction(localIntent, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool2) {
            break label149;
          }
        }
        label144:
        label149:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          break;
          localIntent = null;
          break label90;
          paramParcel1 = null;
          break label110;
        }
        paramParcel1.enforceInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
        paramInt1 = getVersion();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
      }
    }
    
    private static class Proxy
      implements ISearchActionVerificationService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public int getVersion()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.search.verification.api.ISearchActionVerificationService");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isSearchAction(Intent paramIntent, Bundle paramBundle)
        throws RemoteException
      {
        boolean bool = true;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.search.verification.api.ISearchActionVerificationService");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label129;
                }
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label129:
          bool = false;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/search/verification/api/ISearchActionVerificationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */