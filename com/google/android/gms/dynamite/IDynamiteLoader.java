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

public abstract interface IDynamiteLoader
  extends IInterface
{
  public abstract IObjectWrapper createModuleContext(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getModuleVersion(IObjectWrapper paramIObjectWrapper, String paramString)
    throws RemoteException;
  
  public abstract int getModuleVersion2(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IDynamiteLoader
  {
    public static IDynamiteLoader asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoader");
        if ((localIInterface instanceof IDynamiteLoader)) {
          paramIBinder = (IDynamiteLoader)localIInterface;
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
        paramInt1 = getModuleVersion(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
      }
      for (;;)
      {
        bool = true;
        break;
        paramParcel1 = createModuleContext(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
        continue;
        paramInt1 = getModuleVersion2(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), zzc.zza(paramParcel1));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
      }
    }
    
    public static class Proxy
      extends zza
      implements IDynamiteLoader
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.dynamite.IDynamiteLoader");
      }
      
      public IObjectWrapper createModuleContext(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper);
        localParcel.writeString(paramString);
        localParcel.writeInt(paramInt);
        paramIObjectWrapper = transactAndReadException(2, localParcel);
        paramString = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
        paramIObjectWrapper.recycle();
        return paramString;
      }
      
      public int getModuleVersion(IObjectWrapper paramIObjectWrapper, String paramString)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper);
        localParcel.writeString(paramString);
        paramIObjectWrapper = transactAndReadException(1, localParcel);
        int i = paramIObjectWrapper.readInt();
        paramIObjectWrapper.recycle();
        return i;
      }
      
      public int getModuleVersion2(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper);
        localParcel.writeString(paramString);
        zzc.zza(localParcel, paramBoolean);
        paramIObjectWrapper = transactAndReadException(3, localParcel);
        int i = paramIObjectWrapper.readInt();
        paramIObjectWrapper.recycle();
        return i;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/IDynamiteLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */