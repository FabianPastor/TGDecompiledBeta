package org.telegram.messenger.support.customtabs;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPostMessageService
  extends IInterface
{
  public abstract void onMessageChannelReady(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPostMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPostMessageService
  {
    private static final String DESCRIPTOR = "android.support.customtabs.IPostMessageService";
    static final int TRANSACTION_onMessageChannelReady = 2;
    static final int TRANSACTION_onPostMessage = 3;
    
    public Stub()
    {
      attachInterface(this, "android.support.customtabs.IPostMessageService");
    }
    
    public static IPostMessageService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.IPostMessageService");
        if ((localIInterface != null) && ((localIInterface instanceof IPostMessageService))) {
          paramIBinder = (IPostMessageService)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool = true;
      switch (paramInt1)
      {
      default: 
        bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      }
      for (;;)
      {
        return bool;
        paramParcel1.enforceInterface("android.support.customtabs.IPostMessageService");
        ICustomTabsCallback localICustomTabsCallback = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onMessageChannelReady(localICustomTabsCallback, paramParcel1);
          paramParcel2.writeNoException();
          break;
        }
        paramParcel1.enforceInterface("android.support.customtabs.IPostMessageService");
        localICustomTabsCallback = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPostMessage(localICustomTabsCallback, str, paramParcel1);
          paramParcel2.writeNoException();
          break;
        }
        paramParcel2.writeString("android.support.customtabs.IPostMessageService");
      }
    }
    
    private static class Proxy
      implements IPostMessageService
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
      
      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.IPostMessageService";
      }
      
      /* Error */
      public void onMessageChannelReady(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +60 -> 76
        //   19: aload_1
        //   20: invokeinterface 44 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +49 -> 81
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 57	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 19	org/telegram/messenger/support/customtabs/IPostMessageService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_2
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 63 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 66	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: invokevirtual 69	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 69	android/os/Parcel:recycle	()V
        //   75: return
        //   76: aconst_null
        //   77: astore_1
        //   78: goto -52 -> 26
        //   81: aload_3
        //   82: iconst_0
        //   83: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   86: goto -40 -> 46
        //   89: astore_1
        //   90: aload 4
        //   92: invokevirtual 69	android/os/Parcel:recycle	()V
        //   95: aload_3
        //   96: invokevirtual 69	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramICustomTabsCallback	ICustomTabsCallback
        //   0	101	2	paramBundle	Bundle
        //   3	93	3	localParcel1	Parcel
        //   7	84	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	89	finally
        //   19	26	89	finally
        //   26	31	89	finally
        //   35	46	89	finally
        //   46	66	89	finally
        //   81	86	89	finally
      }
      
      /* Error */
      public void onPostMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +71 -> 89
        //   21: aload_1
        //   22: invokeinterface 44 1 0
        //   27: astore_1
        //   28: aload 4
        //   30: aload_1
        //   31: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload_3
        //   41: ifnull +53 -> 94
        //   44: aload 4
        //   46: iconst_1
        //   47: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   50: aload_3
        //   51: aload 4
        //   53: iconst_0
        //   54: invokevirtual 57	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   57: aload_0
        //   58: getfield 19	org/telegram/messenger/support/customtabs/IPostMessageService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: iconst_3
        //   62: aload 4
        //   64: aload 5
        //   66: iconst_0
        //   67: invokeinterface 63 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 66	android/os/Parcel:readException	()V
        //   78: aload 5
        //   80: invokevirtual 69	android/os/Parcel:recycle	()V
        //   83: aload 4
        //   85: invokevirtual 69	android/os/Parcel:recycle	()V
        //   88: return
        //   89: aconst_null
        //   90: astore_1
        //   91: goto -63 -> 28
        //   94: aload 4
        //   96: iconst_0
        //   97: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   100: goto -43 -> 57
        //   103: astore_1
        //   104: aload 5
        //   106: invokevirtual 69	android/os/Parcel:recycle	()V
        //   109: aload 4
        //   111: invokevirtual 69	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	116	0	this	Proxy
        //   0	116	1	paramICustomTabsCallback	ICustomTabsCallback
        //   0	116	2	paramString	String
        //   0	116	3	paramBundle	Bundle
        //   3	107	4	localParcel1	Parcel
        //   8	97	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	103	finally
        //   21	28	103	finally
        //   28	40	103	finally
        //   44	57	103	finally
        //   57	78	103	finally
        //   94	100	103	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/IPostMessageService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */