package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.AuthAccountRequest;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.internal.zzr.zza;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.common.internal.zzx.zza;

public abstract interface zze
  extends IInterface
{
  public abstract void zza(int paramInt, Account paramAccount, zzd paramzzd)
    throws RemoteException;
  
  public abstract void zza(AuthAccountRequest paramAuthAccountRequest, zzd paramzzd)
    throws RemoteException;
  
  public abstract void zza(ResolveAccountRequest paramResolveAccountRequest, zzx paramzzx)
    throws RemoteException;
  
  public abstract void zza(zzr paramzzr, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void zza(CheckServerAuthResult paramCheckServerAuthResult)
    throws RemoteException;
  
  public abstract void zza(RecordConsentRequest paramRecordConsentRequest, zzd paramzzd)
    throws RemoteException;
  
  public abstract void zza(SignInRequest paramSignInRequest, zzd paramzzd)
    throws RemoteException;
  
  public abstract void zzaaf(int paramInt)
    throws RemoteException;
  
  public abstract void zzb(zzd paramzzd)
    throws RemoteException;
  
  public abstract void zzcj(boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zze
  {
    public static zze zzlb(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInService");
      if ((localIInterface != null) && ((localIInterface instanceof zze))) {
        return (zze)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool = false;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = null;
      Object localObject6 = null;
      Object localObject1 = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.signin.internal.ISignInService");
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        if (paramParcel1.readInt() != 0) {
          localObject1 = (AuthAccountRequest)AuthAccountRequest.CREATOR.createFromParcel(paramParcel1);
        }
        zza((AuthAccountRequest)localObject1, zzd.zza.zzla(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        localObject1 = localObject2;
        if (paramParcel1.readInt() != 0) {
          localObject1 = (CheckServerAuthResult)CheckServerAuthResult.CREATOR.createFromParcel(paramParcel1);
        }
        zza((CheckServerAuthResult)localObject1);
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          zzcj(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        localObject1 = localObject3;
        if (paramParcel1.readInt() != 0) {
          localObject1 = (ResolveAccountRequest)ResolveAccountRequest.CREATOR.createFromParcel(paramParcel1);
        }
        zza((ResolveAccountRequest)localObject1, zzx.zza.zzdx(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        zzaaf(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        paramInt1 = paramParcel1.readInt();
        localObject1 = localObject4;
        if (paramParcel1.readInt() != 0) {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
        }
        zza(paramInt1, (Account)localObject1, zzd.zza.zzla(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        localObject1 = zzr.zza.zzdr(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        zza((zzr)localObject1, paramInt1, bool);
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        localObject1 = localObject5;
        if (paramParcel1.readInt() != 0) {
          localObject1 = (RecordConsentRequest)RecordConsentRequest.CREATOR.createFromParcel(paramParcel1);
        }
        zza((RecordConsentRequest)localObject1, zzd.zza.zzla(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
        zzb(zzd.zza.zzla(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInService");
      localObject1 = localObject6;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (SignInRequest)SignInRequest.CREATOR.createFromParcel(paramParcel1);
      }
      zza((SignInRequest)localObject1, zzd.zza.zzla(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zze
    {
      private IBinder zzajf;
      
      zza(IBinder paramIBinder)
      {
        this.zzajf = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzajf;
      }
      
      public void zza(int paramInt, Account paramAccount, zzd paramzzd)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
            localParcel1.writeInt(paramInt);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              if (paramzzd != null)
              {
                paramAccount = paramzzd.asBinder();
                localParcel1.writeStrongBinder(paramAccount);
                this.zzajf.transact(8, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramAccount = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zza(AuthAccountRequest paramAuthAccountRequest, zzd paramzzd)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
            if (paramAuthAccountRequest != null)
            {
              localParcel1.writeInt(1);
              paramAuthAccountRequest.writeToParcel(localParcel1, 0);
              if (paramzzd != null)
              {
                paramAuthAccountRequest = paramzzd.asBinder();
                localParcel1.writeStrongBinder(paramAuthAccountRequest);
                this.zzajf.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramAuthAccountRequest = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zza(ResolveAccountRequest paramResolveAccountRequest, zzx paramzzx)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
            if (paramResolveAccountRequest != null)
            {
              localParcel1.writeInt(1);
              paramResolveAccountRequest.writeToParcel(localParcel1, 0);
              if (paramzzx != null)
              {
                paramResolveAccountRequest = paramzzx.asBinder();
                localParcel1.writeStrongBinder(paramResolveAccountRequest);
                this.zzajf.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramResolveAccountRequest = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void zza(zzr paramzzr, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +70 -> 91
        //   24: aload_1
        //   25: invokeinterface 81 1 0
        //   30: astore_1
        //   31: aload 5
        //   33: aload_1
        //   34: invokevirtual 53	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   37: aload 5
        //   39: iload_2
        //   40: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   43: iload 4
        //   45: istore_2
        //   46: iload_3
        //   47: ifeq +5 -> 52
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 5
        //   54: iload_2
        //   55: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   58: aload_0
        //   59: getfield 18	com/google/android/gms/signin/internal/zze$zza$zza:zzajf	Landroid/os/IBinder;
        //   62: bipush 9
        //   64: aload 5
        //   66: aload 6
        //   68: iconst_0
        //   69: invokeinterface 59 5 0
        //   74: pop
        //   75: aload 6
        //   77: invokevirtual 62	android/os/Parcel:readException	()V
        //   80: aload 6
        //   82: invokevirtual 65	android/os/Parcel:recycle	()V
        //   85: aload 5
        //   87: invokevirtual 65	android/os/Parcel:recycle	()V
        //   90: return
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -62 -> 31
        //   96: astore_1
        //   97: aload 6
        //   99: invokevirtual 65	android/os/Parcel:recycle	()V
        //   102: aload 5
        //   104: invokevirtual 65	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	zza
        //   0	109	1	paramzzr	zzr
        //   0	109	2	paramInt	int
        //   0	109	3	paramBoolean	boolean
        //   1	43	4	i	int
        //   6	97	5	localParcel1	Parcel
        //   11	87	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	96	finally
        //   24	31	96	finally
        //   31	43	96	finally
        //   52	80	96	finally
      }
      
      /* Error */
      public void zza(CheckServerAuthResult paramCheckServerAuthResult)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 85	com/google/android/gms/signin/internal/CheckServerAuthResult:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/signin/internal/zze$zza$zza:zzajf	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 59 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 62	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 65	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 65	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 65	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 65	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	zza
        //   0	75	1	paramCheckServerAuthResult	CheckServerAuthResult
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      public void zza(RecordConsentRequest paramRecordConsentRequest, zzd paramzzd)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
            if (paramRecordConsentRequest != null)
            {
              localParcel1.writeInt(1);
              paramRecordConsentRequest.writeToParcel(localParcel1, 0);
              if (paramzzd != null)
              {
                paramRecordConsentRequest = paramzzd.asBinder();
                localParcel1.writeStrongBinder(paramRecordConsentRequest);
                this.zzajf.transact(10, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramRecordConsentRequest = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zza(SignInRequest paramSignInRequest, zzd paramzzd)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
            if (paramSignInRequest != null)
            {
              localParcel1.writeInt(1);
              paramSignInRequest.writeToParcel(localParcel1, 0);
              if (paramzzd != null)
              {
                paramSignInRequest = paramzzd.asBinder();
                localParcel1.writeStrongBinder(paramSignInRequest);
                this.zzajf.transact(12, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramSignInRequest = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zzaaf(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
          localParcel1.writeInt(paramInt);
          this.zzajf.transact(7, localParcel1, localParcel2, 0);
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
      public void zzb(zzd paramzzd)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 50 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 53	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/signin/internal/zze$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: bipush 11
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 59 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 62	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 65	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 65	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 65	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	zza
        //   0	74	1	paramzzd	zzd
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      public void zzcj(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.zzajf.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */