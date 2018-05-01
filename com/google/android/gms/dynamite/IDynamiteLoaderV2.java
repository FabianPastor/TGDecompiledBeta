package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IDynamiteLoaderV2
  extends IInterface
{
  public abstract IObjectWrapper loadModule(IObjectWrapper paramIObjectWrapper, String paramString, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract IObjectWrapper loadModule2(IObjectWrapper paramIObjectWrapper1, String paramString, int paramInt, IObjectWrapper paramIObjectWrapper2)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IDynamiteLoaderV2
  {
    public static IDynamiteLoaderV2 asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoaderV2");
        if ((localIInterface instanceof IDynamiteLoaderV2)) {
          paramIBinder = (IDynamiteLoaderV2)localIInterface;
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
        paramParcel1 = loadModule(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
      }
      for (;;)
      {
        bool = true;
        break;
        paramParcel1 = loadModule2(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt(), IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
      }
    }
    
    public static class Proxy
      extends zza
      implements IDynamiteLoaderV2
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.dynamite.IDynamiteLoaderV2");
      }
      
      public IObjectWrapper loadModule(IObjectWrapper paramIObjectWrapper, String paramString, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper);
        localParcel.writeString(paramString);
        localParcel.writeByteArray(paramArrayOfByte);
        paramIObjectWrapper = transactAndReadException(1, localParcel);
        paramString = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
        paramIObjectWrapper.recycle();
        return paramString;
      }
      
      public IObjectWrapper loadModule2(IObjectWrapper paramIObjectWrapper1, String paramString, int paramInt, IObjectWrapper paramIObjectWrapper2)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper1);
        localParcel.writeString(paramString);
        localParcel.writeInt(paramInt);
        zzc.zza(localParcel, paramIObjectWrapper2);
        paramString = transactAndReadException(2, localParcel);
        paramIObjectWrapper1 = IObjectWrapper.Stub.asInterface(paramString.readStrongBinder());
        paramString.recycle();
        return paramIObjectWrapper1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/IDynamiteLoaderV2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */