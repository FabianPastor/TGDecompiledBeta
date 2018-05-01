package org.telegram.messenger.support.customtabs;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface ICustomTabsService
  extends IInterface
{
  public abstract Bundle extraCommand(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
    throws RemoteException;
  
  public abstract boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
    throws RemoteException;
  
  public abstract int postMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean requestPostMessageChannel(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri)
    throws RemoteException;
  
  public abstract boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean warmup(long paramLong)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICustomTabsService
  {
    private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsService";
    static final int TRANSACTION_extraCommand = 5;
    static final int TRANSACTION_mayLaunchUrl = 4;
    static final int TRANSACTION_newSession = 3;
    static final int TRANSACTION_postMessage = 8;
    static final int TRANSACTION_requestPostMessageChannel = 7;
    static final int TRANSACTION_updateVisuals = 6;
    static final int TRANSACTION_warmup = 2;
    
    public Stub()
    {
      attachInterface(this, "android.support.customtabs.ICustomTabsService");
    }
    
    public static ICustomTabsService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.ICustomTabsService");
        if ((localIInterface != null) && ((localIInterface instanceof ICustomTabsService))) {
          paramIBinder = (ICustomTabsService)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      }
      for (;;)
      {
        return bool;
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        bool = warmup(paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        bool = newSession(ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        ICustomTabsCallback localICustomTabsCallback = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          label215:
          if (paramParcel1.readInt() == 0) {
            break label283;
          }
          localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          label236:
          bool = mayLaunchUrl(localICustomTabsCallback, (Uri)localObject1, (Bundle)localObject2, paramParcel1.createTypedArrayList(Bundle.CREATOR));
          paramParcel2.writeNoException();
          if (!bool) {
            break label289;
          }
        }
        label283:
        label289:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
          localObject1 = null;
          break label215;
          localObject2 = null;
          break label236;
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        Object localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          label326:
          paramParcel1 = extraCommand((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label364;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          bool = true;
          break;
          paramParcel1 = null;
          break label326;
          label364:
          paramParcel2.writeInt(0);
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        localObject1 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          label407:
          bool = updateVisuals((ICustomTabsCallback)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label443;
          }
        }
        label443:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
          paramParcel1 = null;
          break label407;
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        localObject1 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          label483:
          bool = requestPostMessageChannel((ICustomTabsCallback)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label519;
          }
        }
        label519:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
          paramParcel1 = null;
          break label483;
        }
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        localObject1 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        Object localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = postMessage((ICustomTabsCallback)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          bool = true;
          break;
        }
        paramParcel2.writeString("android.support.customtabs.ICustomTabsService");
        bool = true;
      }
    }
    
    private static class Proxy
      implements ICustomTabsService
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
      
      public Bundle extraCommand(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            localParcel1.writeString(paramString);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.ICustomTabsService";
      }
      
      public boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
        throws RemoteException
      {
        boolean bool = true;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback != null)
            {
              paramICustomTabsCallback = paramICustomTabsCallback.asBinder();
              localParcel1.writeStrongBinder(paramICustomTabsCallback);
              if (paramUri != null)
              {
                localParcel1.writeInt(1);
                paramUri.writeToParcel(localParcel1, 0);
                if (paramBundle == null) {
                  break label151;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeTypedList(paramList);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label160;
                }
                return bool;
              }
            }
            else
            {
              paramICustomTabsCallback = null;
              continue;
            }
            localParcel1.writeInt(0);
            continue;
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label151:
          continue;
          label160:
          bool = false;
        }
      }
      
      /* Error */
      public boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload_1
        //   22: invokeinterface 86 1 0
        //   27: astore_1
        //   28: aload_3
        //   29: aload_1
        //   30: invokevirtual 89	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   33: aload_0
        //   34: getfield 19	org/telegram/messenger/support/customtabs/ICustomTabsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   37: iconst_3
        //   38: aload_3
        //   39: aload 4
        //   41: iconst_0
        //   42: invokeinterface 57 5 0
        //   47: pop
        //   48: aload 4
        //   50: invokevirtual 60	android/os/Parcel:readException	()V
        //   53: aload 4
        //   55: invokevirtual 64	android/os/Parcel:readInt	()I
        //   58: istore 5
        //   60: iload 5
        //   62: ifeq +5 -> 67
        //   65: iconst_1
        //   66: istore_2
        //   67: aload 4
        //   69: invokevirtual 77	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 77	android/os/Parcel:recycle	()V
        //   76: iload_2
        //   77: ireturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -52 -> 28
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 77	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 77	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramICustomTabsCallback	ICustomTabsCallback
        //   1	76	2	bool	boolean
        //   5	85	3	localParcel1	Parcel
        //   9	76	4	localParcel2	Parcel
        //   58	3	5	i	int
        // Exception table:
        //   from	to	target	type
        //   11	17	83	finally
        //   21	28	83	finally
        //   28	60	83	finally
      }
      
      /* Error */
      public int postMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +81 -> 99
        //   21: aload_1
        //   22: invokeinterface 86 1 0
        //   27: astore_1
        //   28: aload 4
        //   30: aload_1
        //   31: invokevirtual 89	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload_3
        //   41: ifnull +63 -> 104
        //   44: aload 4
        //   46: iconst_1
        //   47: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   50: aload_3
        //   51: aload 4
        //   53: iconst_0
        //   54: invokevirtual 51	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   57: aload_0
        //   58: getfield 19	org/telegram/messenger/support/customtabs/ICustomTabsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: bipush 8
        //   63: aload 4
        //   65: aload 5
        //   67: iconst_0
        //   68: invokeinterface 57 5 0
        //   73: pop
        //   74: aload 5
        //   76: invokevirtual 60	android/os/Parcel:readException	()V
        //   79: aload 5
        //   81: invokevirtual 64	android/os/Parcel:readInt	()I
        //   84: istore 6
        //   86: aload 5
        //   88: invokevirtual 77	android/os/Parcel:recycle	()V
        //   91: aload 4
        //   93: invokevirtual 77	android/os/Parcel:recycle	()V
        //   96: iload 6
        //   98: ireturn
        //   99: aconst_null
        //   100: astore_1
        //   101: goto -73 -> 28
        //   104: aload 4
        //   106: iconst_0
        //   107: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   110: goto -53 -> 57
        //   113: astore_1
        //   114: aload 5
        //   116: invokevirtual 77	android/os/Parcel:recycle	()V
        //   119: aload 4
        //   121: invokevirtual 77	android/os/Parcel:recycle	()V
        //   124: aload_1
        //   125: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	126	0	this	Proxy
        //   0	126	1	paramICustomTabsCallback	ICustomTabsCallback
        //   0	126	2	paramString	String
        //   0	126	3	paramBundle	Bundle
        //   3	117	4	localParcel1	Parcel
        //   8	107	5	localParcel2	Parcel
        //   84	13	6	i	int
        // Exception table:
        //   from	to	target	type
        //   10	17	113	finally
        //   21	28	113	finally
        //   28	40	113	finally
        //   44	57	113	finally
        //   57	86	113	finally
        //   104	110	113	finally
      }
      
      public boolean requestPostMessageChannel(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri)
        throws RemoteException
      {
        boolean bool = true;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label126:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback != null)
            {
              paramICustomTabsCallback = paramICustomTabsCallback.asBinder();
              localParcel1.writeStrongBinder(paramICustomTabsCallback);
              if (paramUri != null)
              {
                localParcel1.writeInt(1);
                paramUri.writeToParcel(localParcel1, 0);
                this.mRemote.transact(7, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label126;
                }
                return bool;
              }
            }
            else
            {
              paramICustomTabsCallback = null;
              continue;
            }
            localParcel1.writeInt(0);
            continue;
            bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
        throws RemoteException
      {
        boolean bool = true;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label126:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback != null)
            {
              paramICustomTabsCallback = paramICustomTabsCallback.asBinder();
              localParcel1.writeStrongBinder(paramICustomTabsCallback);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label126;
                }
                return bool;
              }
            }
            else
            {
              paramICustomTabsCallback = null;
              continue;
            }
            localParcel1.writeInt(0);
            continue;
            bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean warmup(long paramLong)
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/ICustomTabsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */