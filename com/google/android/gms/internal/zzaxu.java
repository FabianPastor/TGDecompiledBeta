package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

public abstract interface zzaxu
  extends IInterface
{
  public abstract void zza(ConnectionResult paramConnectionResult, zzaxp paramzzaxp)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, GoogleSignInAccount paramGoogleSignInAccount)
    throws RemoteException;
  
  public abstract void zzb(zzayb paramzzayb)
    throws RemoteException;
  
  public abstract void zzbI(Status paramStatus)
    throws RemoteException;
  
  public abstract void zzbJ(Status paramStatus)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzaxu
  {
    public zza()
    {
      attachInterface(this, "com.google.android.gms.signin.internal.ISignInCallbacks");
    }
    
    public static zzaxu zzeX(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof zzaxu))) {
        return (zzaxu)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.signin.internal.ISignInCallbacks");
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ConnectionResult)ConnectionResult.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label144;
          }
        }
        for (paramParcel1 = (zzaxp)zzaxp.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza((ConnectionResult)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Status)Status.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzbI(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Status)Status.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzbJ(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        label144:
        paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (Status)Status.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label299;
          }
        }
        label299:
        for (paramParcel1 = (GoogleSignInAccount)GoogleSignInAccount.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza((Status)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (zzayb)zzayb.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        zzb(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class zza
      implements zzaxu
    {
      private IBinder zzrp;
      
      zza(IBinder paramIBinder)
      {
        this.zzrp = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzrp;
      }
      
      public void zza(ConnectionResult paramConnectionResult, zzaxp paramzzaxp)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInCallbacks");
            if (paramConnectionResult != null)
            {
              localParcel1.writeInt(1);
              paramConnectionResult.writeToParcel(localParcel1, 0);
              if (paramzzaxp != null)
              {
                localParcel1.writeInt(1);
                paramzzaxp.writeToParcel(localParcel1, 0);
                this.zzrp.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zza(Status paramStatus, GoogleSignInAccount paramGoogleSignInAccount)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.signin.internal.ISignInCallbacks");
            if (paramStatus != null)
            {
              localParcel1.writeInt(1);
              paramStatus.writeToParcel(localParcel1, 0);
              if (paramGoogleSignInAccount != null)
              {
                localParcel1.writeInt(1);
                paramGoogleSignInAccount.writeToParcel(localParcel1, 0);
                this.zzrp.transact(7, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void zzb(zzayb paramzzayb)
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
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 74	com/google/android/gms/internal/zzayb:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzaxu$zza$zza:zzrp	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramzzayb	zzayb
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
      public void zzbI(Status paramStatus)
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
        //   26: invokevirtual 66	com/google/android/gms/common/api/Status:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzaxu$zza$zza:zzrp	Landroid/os/IBinder;
        //   33: iconst_4
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 55 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 58	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 61	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 61	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	zza
        //   0	75	1	paramStatus	Status
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
      public void zzbJ(Status paramStatus)
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
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 66	com/google/android/gms/common/api/Status:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzaxu$zza$zza:zzrp	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramStatus	Status
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */