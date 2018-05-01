package com.google.android.gms.flags;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IFlagProvider
  extends IInterface
{
  public abstract boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract long getLongFlagValue(String paramString, long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract String getStringFlagValue(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void init(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IFlagProvider
  {
    public Stub()
    {
      super();
    }
    
    public static IFlagProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.flags.IFlagProvider");
        if ((localIInterface instanceof IFlagProvider)) {
          paramIBinder = (IFlagProvider)localIInterface;
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
        init(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
      }
      for (;;)
      {
        bool = true;
        break;
        bool = getBooleanFlagValue(paramParcel1.readString(), zzc.zza(paramParcel1), paramParcel1.readInt());
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        paramInt1 = getIntFlagValue(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        long l = getLongFlagValue(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        continue;
        paramParcel1 = getStringFlagValue(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
      }
    }
    
    public static class Proxy
      extends zza
      implements IFlagProvider
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.flags.IFlagProvider");
      }
      
      public boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeString(paramString);
        zzc.zza(localParcel, paramBoolean);
        localParcel.writeInt(paramInt);
        paramString = transactAndReadException(2, localParcel);
        paramBoolean = zzc.zza(paramString);
        paramString.recycle();
        return paramBoolean;
      }
      
      public int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeString(paramString);
        localParcel.writeInt(paramInt1);
        localParcel.writeInt(paramInt2);
        paramString = transactAndReadException(3, localParcel);
        paramInt1 = paramString.readInt();
        paramString.recycle();
        return paramInt1;
      }
      
      public long getLongFlagValue(String paramString, long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeString(paramString);
        localParcel.writeLong(paramLong);
        localParcel.writeInt(paramInt);
        paramString = transactAndReadException(4, localParcel);
        paramLong = paramString.readLong();
        paramString.recycle();
        return paramLong;
      }
      
      public String getStringFlagValue(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeString(paramString1);
        localParcel.writeString(paramString2);
        localParcel.writeInt(paramInt);
        paramString1 = transactAndReadException(5, localParcel);
        paramString2 = paramString1.readString();
        paramString1.recycle();
        return paramString2;
      }
      
      public void init(IObjectWrapper paramIObjectWrapper)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIObjectWrapper);
        transactAndReadExceptionReturnVoid(1, localParcel);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/IFlagProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */