package com.google.android.gms.vision.face.internal.client;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zzb;

public abstract interface zzd
  extends IInterface
{
  public abstract boolean zzabr(int paramInt)
    throws RemoteException;
  
  public abstract FaceParcel[] zzc(com.google.android.gms.dynamic.zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
    throws RemoteException;
  
  public abstract void zzclr()
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzd
  {
    public static zzd zzll(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
      if ((localIInterface != null) && ((localIInterface instanceof zzd))) {
        return (zzd)localIInterface;
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
        com.google.android.gms.dynamic.zzd localzzd = com.google.android.gms.dynamic.zzd.zza.zzfe(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (FrameMetadataParcel)FrameMetadataParcel.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zzc(localzzd, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
        boolean bool = zzabr(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
      zzclr();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzd
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
      
      public boolean zzabr(int paramInt)
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
          localParcel1.writeInt(paramInt);
          this.zzajf.transact(2, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public FaceParcel[] zzc(com.google.android.gms.dynamic.zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
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
        //   16: ifnull +73 -> 89
        //   19: aload_1
        //   20: invokeinterface 64 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +62 -> 94
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 73	com/google/android/gms/vision/internal/client/FrameMetadataParcel:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/vision/face/internal/client/zzd$zza$zza:zzajf	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 47 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 50	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: getstatic 79	com/google/android/gms/vision/face/internal/client/FaceParcel:CREATOR	Lcom/google/android/gms/vision/face/internal/client/zzb;
        //   71: invokevirtual 83	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   74: checkcast 85	[Lcom/google/android/gms/vision/face/internal/client/FaceParcel;
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 57	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 57	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: areturn
        //   89: aconst_null
        //   90: astore_1
        //   91: goto -65 -> 26
        //   94: aload_3
        //   95: iconst_0
        //   96: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 4
        //   105: invokevirtual 57	android/os/Parcel:recycle	()V
        //   108: aload_3
        //   109: invokevirtual 57	android/os/Parcel:recycle	()V
        //   112: aload_1
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	zza
        //   0	114	1	paramzzd	com.google.android.gms.dynamic.zzd
        //   0	114	2	paramFrameMetadataParcel	FrameMetadataParcel
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
      
      public void zzclr()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
          this.zzajf.transact(3, localParcel1, localParcel2, 0);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */