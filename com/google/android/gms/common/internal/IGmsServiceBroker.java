package com.google.android.gms.common.internal;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGmsServiceBroker
  extends IInterface
{
  public abstract void getService(IGmsCallbacks paramIGmsCallbacks, GetServiceRequest paramGetServiceRequest)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGmsServiceBroker
  {
    public static IGmsServiceBroker asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
        if ((localIInterface != null) && ((localIInterface instanceof IGmsServiceBroker))) {
          paramIBinder = (IGmsServiceBroker)localIInterface;
        } else {
          paramIBinder = new zza(paramIBinder);
        }
      }
    }
    
    protected void getLegacyService(int paramInt1, IGmsCallbacks paramIGmsCallbacks, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle, IBinder paramIBinder, String paramString3, String paramString4)
      throws RemoteException
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool;
      if (paramInt1 > 16777215)
      {
        bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        return bool;
      }
      paramParcel1.enforceInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
      IGmsCallbacks localIGmsCallbacks = IGmsCallbacks.Stub.asInterface(paramParcel1.readStrongBinder());
      if (paramInt1 == 46) {
        if (paramParcel1.readInt() == 0) {
          break label692;
        }
      }
      label320:
      label687:
      label692:
      for (paramParcel1 = (GetServiceRequest)GetServiceRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        getService(localIGmsCallbacks, paramParcel1);
        paramParcel2.writeNoException();
        bool = true;
        break;
        if (paramInt1 == 47) {
          if (paramParcel1.readInt() == 0) {
            break label687;
          }
        }
        for (paramParcel1 = (ValidateAccountRequest)ValidateAccountRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          validateAccount(localIGmsCallbacks, paramParcel1);
          break;
          paramInt2 = paramParcel1.readInt();
          if (paramInt1 != 4) {}
          for (String str1 = paramParcel1.readString();; str1 = null)
          {
            String str2;
            String str3;
            IBinder localIBinder;
            String[] arrayOfString;
            String str4;
            switch (paramInt1)
            {
            case 3: 
            case 4: 
            case 21: 
            case 22: 
            case 24: 
            case 26: 
            case 28: 
            case 29: 
            case 31: 
            case 32: 
            case 33: 
            case 35: 
            case 36: 
            case 39: 
            case 40: 
            case 42: 
            default: 
              str2 = null;
              str3 = null;
              localIBinder = null;
              paramParcel1 = null;
              arrayOfString = null;
              str4 = null;
            }
            for (;;)
            {
              getLegacyService(paramInt1, localIGmsCallbacks, paramInt2, str1, str4, arrayOfString, paramParcel1, localIBinder, str3, str2);
              break;
              localIBinder = paramParcel1.readStrongBinder();
              if (paramParcel1.readInt() != 0)
              {
                paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                str2 = null;
                str3 = null;
                arrayOfString = null;
                str4 = null;
                continue;
                str3 = paramParcel1.readString();
                arrayOfString = paramParcel1.createStringArray();
                str4 = paramParcel1.readString();
                if (paramParcel1.readInt() != 0)
                {
                  paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                  str2 = null;
                  localIBinder = null;
                  continue;
                  str4 = paramParcel1.readString();
                  arrayOfString = paramParcel1.createStringArray();
                  str3 = paramParcel1.readString();
                  localIBinder = paramParcel1.readStrongBinder();
                  str2 = paramParcel1.readString();
                  if (paramParcel1.readInt() != 0)
                  {
                    paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                    continue;
                    arrayOfString = paramParcel1.createStringArray();
                    str4 = paramParcel1.readString();
                    if (paramParcel1.readInt() != 0)
                    {
                      paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                      str2 = null;
                      str3 = null;
                      localIBinder = null;
                      continue;
                      str4 = paramParcel1.readString();
                      arrayOfString = paramParcel1.createStringArray();
                      str2 = null;
                      str3 = null;
                      localIBinder = null;
                      paramParcel1 = null;
                      continue;
                      str4 = paramParcel1.readString();
                      str2 = null;
                      str3 = null;
                      localIBinder = null;
                      paramParcel1 = null;
                      arrayOfString = null;
                      continue;
                      if (paramParcel1.readInt() == 0) {
                        break label320;
                      }
                      paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                      str2 = null;
                      str3 = null;
                      localIBinder = null;
                      arrayOfString = null;
                      str4 = null;
                      continue;
                    }
                    str2 = null;
                    str3 = null;
                    localIBinder = null;
                    paramParcel1 = null;
                    continue;
                  }
                  paramParcel1 = null;
                  continue;
                }
                str2 = null;
                localIBinder = null;
                paramParcel1 = null;
                continue;
              }
              str2 = null;
              str3 = null;
              paramParcel1 = null;
              arrayOfString = null;
              str4 = null;
            }
          }
        }
      }
    }
    
    protected void validateAccount(IGmsCallbacks paramIGmsCallbacks, ValidateAccountRequest paramValidateAccountRequest)
      throws RemoteException
    {
      throw new UnsupportedOperationException();
    }
    
    private static final class zza
      implements IGmsServiceBroker
    {
      private final IBinder zza;
      
      zza(IBinder paramIBinder)
      {
        this.zza = paramIBinder;
      }
      
      public final IBinder asBinder()
      {
        return this.zza;
      }
      
      /* Error */
      public final void getService(IGmsCallbacks paramIGmsCallbacks, GetServiceRequest paramGetServiceRequest)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +61 -> 77
        //   19: aload_1
        //   20: invokeinterface 41 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 44	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +50 -> 82
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 54	com/google/android/gms/common/internal/GetServiceRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/common/internal/IGmsServiceBroker$Stub$zza:zza	Landroid/os/IBinder;
        //   50: bipush 46
        //   52: aload_3
        //   53: aload 4
        //   55: iconst_0
        //   56: invokeinterface 60 5 0
        //   61: pop
        //   62: aload 4
        //   64: invokevirtual 63	android/os/Parcel:readException	()V
        //   67: aload 4
        //   69: invokevirtual 66	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 66	android/os/Parcel:recycle	()V
        //   76: return
        //   77: aconst_null
        //   78: astore_1
        //   79: goto -53 -> 26
        //   82: aload_3
        //   83: iconst_0
        //   84: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   87: goto -41 -> 46
        //   90: astore_1
        //   91: aload 4
        //   93: invokevirtual 66	android/os/Parcel:recycle	()V
        //   96: aload_3
        //   97: invokevirtual 66	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	zza
        //   0	102	1	paramIGmsCallbacks	IGmsCallbacks
        //   0	102	2	paramGetServiceRequest	GetServiceRequest
        //   3	94	3	localParcel1	Parcel
        //   7	85	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	90	finally
        //   19	26	90	finally
        //   26	31	90	finally
        //   35	46	90	finally
        //   46	67	90	finally
        //   82	87	90	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/IGmsServiceBroker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */