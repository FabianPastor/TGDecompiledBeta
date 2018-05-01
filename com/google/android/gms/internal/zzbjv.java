package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;

public abstract interface zzbjv
  extends IInterface
{
  public abstract void zzTS()
    throws RemoteException;
  
  public abstract zzbjr[] zzc(IObjectWrapper paramIObjectWrapper, zzbka paramzzbka)
    throws RemoteException;
  
  public abstract boolean zzoh(int paramInt)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzbjv
  {
    public static zzbjv zzfp(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
      if ((localIInterface != null) && ((localIInterface instanceof zzbjv))) {
        return (zzbjv)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
        IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzcd(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzbka)zzbka.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zzc(localIObjectWrapper, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
        boolean bool = zzoh(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
      zzTS();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzbjv
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
      
      public void zzTS()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
          this.zzrk.transact(3, localParcel1, localParcel2, 0);
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
      public zzbjr[] zzc(IObjectWrapper paramIObjectWrapper, zzbka paramzzbka)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +73 -> 89
        //   19: aload_1
        //   20: invokeinterface 55 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 58	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +62 -> 94
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 68	com/google/android/gms/internal/zzbka:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/internal/zzbjv$zza$zza:zzrk	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 42 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 45	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: getstatic 74	com/google/android/gms/internal/zzbjr:CREATOR	Landroid/os/Parcelable$Creator;
        //   71: invokevirtual 78	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   74: checkcast 80	[Lcom/google/android/gms/internal/zzbjr;
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 48	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 48	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: areturn
        //   89: aconst_null
        //   90: astore_1
        //   91: goto -65 -> 26
        //   94: aload_3
        //   95: iconst_0
        //   96: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 4
        //   105: invokevirtual 48	android/os/Parcel:recycle	()V
        //   108: aload_3
        //   109: invokevirtual 48	android/os/Parcel:recycle	()V
        //   112: aload_1
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	zza
        //   0	114	1	paramIObjectWrapper	IObjectWrapper
        //   0	114	2	paramzzbka	zzbka
        //   3	106	3	localParcel1	Parcel
        //   7	97	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	102	finally
        //   19	26	102	finally
        //   26	31	102	finally
        //   35	46	102	finally
        //   46	78	102	finally
        //   94	99	102	finally
      }
      
      public boolean zzoh(int paramInt)
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
          localParcel1.writeInt(paramInt);
          this.zzrk.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */