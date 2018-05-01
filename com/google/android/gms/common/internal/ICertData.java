package com.google.android.gms.common.internal;

import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface ICertData
  extends IInterface
{
  public abstract IObjectWrapper getBytesWrapped()
    throws RemoteException;
  
  public abstract int getHashCode()
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements ICertData
  {
    public Stub()
    {
      super();
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
        paramParcel1 = getBytesWrapped();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
      }
      for (;;)
      {
        bool = true;
        break;
        paramInt1 = getHashCode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ICertData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */