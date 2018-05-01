package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IGmsCallbacks
  extends IInterface
{
  public abstract void onAccountValidationComplete(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPostInitComplete(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPostInitCompleteWithConnectionInfo(int paramInt, IBinder paramIBinder, ConnectionInfo paramConnectionInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IGmsCallbacks
  {
    public Stub()
    {
      super();
    }
    
    public static IGmsCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsCallbacks");
        if ((localIInterface instanceof IGmsCallbacks)) {
          paramIBinder = (IGmsCallbacks)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool;
      switch (paramInt1)
      {
      default: 
        bool = false;
        return bool;
      case 1: 
        onPostInitComplete(paramParcel1.readInt(), paramParcel1.readStrongBinder(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      }
      for (;;)
      {
        paramParcel2.writeNoException();
        bool = true;
        break;
        onAccountValidationComplete(paramParcel1.readInt(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
        continue;
        onPostInitCompleteWithConnectionInfo(paramParcel1.readInt(), paramParcel1.readStrongBinder(), (ConnectionInfo)zzc.zza(paramParcel1, ConnectionInfo.CREATOR));
      }
    }
    
    public static class Proxy
      extends zza
      implements IGmsCallbacks
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.common.internal.IGmsCallbacks");
      }
      
      public void onAccountValidationComplete(int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeInt(paramInt);
        zzc.zza(localParcel, paramBundle);
        transactAndReadExceptionReturnVoid(2, localParcel);
      }
      
      public void onPostInitComplete(int paramInt, IBinder paramIBinder, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeInt(paramInt);
        localParcel.writeStrongBinder(paramIBinder);
        zzc.zza(localParcel, paramBundle);
        transactAndReadExceptionReturnVoid(1, localParcel);
      }
      
      public void onPostInitCompleteWithConnectionInfo(int paramInt, IBinder paramIBinder, ConnectionInfo paramConnectionInfo)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeInt(paramInt);
        localParcel.writeStrongBinder(paramIBinder);
        zzc.zza(localParcel, paramConnectionInfo);
        transactAndReadExceptionReturnVoid(3, localParcel);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/IGmsCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */