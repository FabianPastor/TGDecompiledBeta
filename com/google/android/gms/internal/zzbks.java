package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public abstract interface zzbks
  extends IInterface
{
  public abstract int getState()
    throws RemoteException;
  
  public abstract void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
    throws RemoteException;
  
  public abstract void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException;
  
  public abstract void onCreate(Bundle paramBundle)
    throws RemoteException;
  
  public abstract IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPause()
    throws RemoteException;
  
  public abstract void onResume()
    throws RemoteException;
  
  public abstract void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onStart()
    throws RemoteException;
  
  public abstract void onStop()
    throws RemoteException;
  
  public abstract void setEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void updateMaskedWallet(MaskedWallet paramMaskedWallet)
    throws RemoteException;
  
  public abstract void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
    throws RemoteException;
  
  public abstract void zza(IObjectWrapper paramIObjectWrapper, WalletFragmentOptions paramWalletFragmentOptions, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzbks
  {
    public static zzbks zzft(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
      if ((localIInterface != null) && ((localIInterface instanceof zzbks))) {
        return (zzbks)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      WalletFragmentOptions localWalletFragmentOptions = null;
      IObjectWrapper localIObjectWrapper1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        localIObjectWrapper1 = IObjectWrapper.zza.zzcd(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localWalletFragmentOptions = (WalletFragmentOptions)WalletFragmentOptions.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label231;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza(localIObjectWrapper1, localWalletFragmentOptions, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localWalletFragmentOptions = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onCreate(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        localIObjectWrapper1 = IObjectWrapper.zza.zzcd(paramParcel1.readStrongBinder());
        IObjectWrapper localIObjectWrapper2 = IObjectWrapper.zza.zzcd(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          localIObjectWrapper1 = onCreateView(localIObjectWrapper1, localIObjectWrapper2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel1 = localWalletFragmentOptions;
          if (localIObjectWrapper1 != null) {
            paramParcel1 = localIObjectWrapper1.asBinder();
          }
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        onStart();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        onResume();
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        onPause();
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        onStop();
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onSaveInstanceState(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onActivityResult(paramInt1, paramInt2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (WalletFragmentInitParams)WalletFragmentInitParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          initialize(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (MaskedWalletRequest)MaskedWalletRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateMaskedWalletRequest(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          setEnabled(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        label231:
        paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
        paramInt1 = getState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (MaskedWallet)MaskedWallet.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        updateMaskedWallet(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class zza
      implements zzbks
    {
      private IBinder zzrk;
      
      zza(IBinder paramIBinder)
      {
        this.zzrk = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzrk;
      }
      
      public int getState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          this.zzrk.transact(13, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 65	com/google/android/gms/wallet/fragment/WalletFragmentInitParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   33: bipush 10
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramWalletFragmentInitParams	WalletFragmentInitParams
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 33
        //   14: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   29: aload_3
        //   30: ifnull +49 -> 79
        //   33: aload 4
        //   35: iconst_1
        //   36: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokevirtual 70	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   50: bipush 9
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 43 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 46	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 52	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 52	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   85: goto -39 -> 46
        //   88: astore_3
        //   89: aload 5
        //   91: invokevirtual 52	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 52	android/os/Parcel:recycle	()V
        //   99: aload_3
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	zza
        //   0	101	1	paramInt1	int
        //   0	101	2	paramInt2	int
        //   0	101	3	paramIntent	Intent
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	88	finally
        //   33	46	88	finally
        //   46	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void onCreate(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 75	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   33: iconst_2
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 43 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 46	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 52	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 52	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 52	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 52	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	zza
        //   0	75	1	paramBundle	Bundle
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      /* Error */
      public IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +95 -> 116
        //   24: aload_1
        //   25: invokeinterface 81 1 0
        //   30: astore_1
        //   31: aload 5
        //   33: aload_1
        //   34: invokevirtual 84	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   37: aload 4
        //   39: astore_1
        //   40: aload_2
        //   41: ifnull +10 -> 51
        //   44: aload_2
        //   45: invokeinterface 81 1 0
        //   50: astore_1
        //   51: aload 5
        //   53: aload_1
        //   54: invokevirtual 84	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   57: aload_3
        //   58: ifnull +63 -> 121
        //   61: aload 5
        //   63: iconst_1
        //   64: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   67: aload_3
        //   68: aload 5
        //   70: iconst_0
        //   71: invokevirtual 75	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   74: aload_0
        //   75: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   78: iconst_3
        //   79: aload 5
        //   81: aload 6
        //   83: iconst_0
        //   84: invokeinterface 43 5 0
        //   89: pop
        //   90: aload 6
        //   92: invokevirtual 46	android/os/Parcel:readException	()V
        //   95: aload 6
        //   97: invokevirtual 87	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   100: invokestatic 93	com/google/android/gms/dynamic/IObjectWrapper$zza:zzcd	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamic/IObjectWrapper;
        //   103: astore_1
        //   104: aload 6
        //   106: invokevirtual 52	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 52	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: areturn
        //   116: aconst_null
        //   117: astore_1
        //   118: goto -87 -> 31
        //   121: aload 5
        //   123: iconst_0
        //   124: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   127: goto -53 -> 74
        //   130: astore_1
        //   131: aload 6
        //   133: invokevirtual 52	android/os/Parcel:recycle	()V
        //   136: aload 5
        //   138: invokevirtual 52	android/os/Parcel:recycle	()V
        //   141: aload_1
        //   142: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	143	0	this	zza
        //   0	143	1	paramIObjectWrapper1	IObjectWrapper
        //   0	143	2	paramIObjectWrapper2	IObjectWrapper
        //   0	143	3	paramBundle	Bundle
        //   1	37	4	localObject	Object
        //   6	131	5	localParcel1	Parcel
        //   11	121	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	130	finally
        //   24	31	130	finally
        //   31	37	130	finally
        //   44	51	130	finally
        //   51	57	130	finally
        //   61	74	130	finally
        //   74	104	130	finally
        //   121	127	130	finally
      }
      
      public void onPause()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          this.zzrk.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onResume()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          this.zzrk.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void onSaveInstanceState(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +54 -> 69
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 75	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 49	android/os/Parcel:readInt	()I
        //   52: ifeq +8 -> 60
        //   55: aload_1
        //   56: aload_3
        //   57: invokevirtual 100	android/os/Bundle:readFromParcel	(Landroid/os/Parcel;)V
        //   60: aload_3
        //   61: invokevirtual 52	android/os/Parcel:recycle	()V
        //   64: aload_2
        //   65: invokevirtual 52	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload_2
        //   70: iconst_0
        //   71: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   74: goto -45 -> 29
        //   77: astore_1
        //   78: aload_3
        //   79: invokevirtual 52	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: invokevirtual 52	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	zza
        //   0	88	1	paramBundle	Bundle
        //   3	80	2	localParcel1	Parcel
        //   7	72	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	77	finally
        //   18	29	77	finally
        //   29	60	77	finally
        //   69	74	77	finally
      }
      
      public void onStart()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          this.zzrk.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onStop()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          this.zzrk.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.zzrk.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void updateMaskedWallet(MaskedWallet paramMaskedWallet)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 109	com/google/android/gms/wallet/MaskedWallet:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   33: bipush 14
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramMaskedWallet	MaskedWallet
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 114	com/google/android/gms/wallet/MaskedWalletRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzbks$zza$zza:zzrk	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramMaskedWalletRequest	MaskedWalletRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void zza(IObjectWrapper paramIObjectWrapper, WalletFragmentOptions paramWalletFragmentOptions, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label127:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
            if (paramIObjectWrapper != null)
            {
              paramIObjectWrapper = paramIObjectWrapper.asBinder();
              localParcel1.writeStrongBinder(paramIObjectWrapper);
              if (paramWalletFragmentOptions != null)
              {
                localParcel1.writeInt(1);
                paramWalletFragmentOptions.writeToParcel(localParcel1, 0);
                if (paramBundle == null) {
                  break label127;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.zzrk.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              paramIObjectWrapper = null;
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
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */