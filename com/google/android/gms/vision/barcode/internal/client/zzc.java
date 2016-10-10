package com.google.android.gms.vision.barcode.internal.client;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zzb;

public abstract interface zzc
  extends IInterface
{
  public abstract Barcode[] zza(zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
    throws RemoteException;
  
  public abstract Barcode[] zzb(zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
    throws RemoteException;
  
  public abstract void zzclr()
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzc
  {
    public static zzc zzlj(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
      if ((localIInterface != null) && ((localIInterface instanceof zzc))) {
        return (zzc)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      zzd localzzd1 = null;
      Object localObject = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
        localzzd1 = zzd.zza.zzfe(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {
          localObject = (FrameMetadataParcel)FrameMetadataParcel.CREATOR.createFromParcel(paramParcel1);
        }
        paramParcel1 = zza(localzzd1, (FrameMetadataParcel)localObject);
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
        zzd localzzd2 = zzd.zza.zzfe(paramParcel1.readStrongBinder());
        localObject = localzzd1;
        if (paramParcel1.readInt() != 0) {
          localObject = (FrameMetadataParcel)FrameMetadataParcel.CREATOR.createFromParcel(paramParcel1);
        }
        paramParcel1 = zzb(localzzd2, (FrameMetadataParcel)localObject);
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
      zzclr();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzc
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
      
      /* Error */
      public Barcode[] zza(zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
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
        //   20: invokeinterface 40 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +62 -> 94
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 53	com/google/android/gms/vision/internal/client/FrameMetadataParcel:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/vision/barcode/internal/client/zzc$zza$zza:zzajf	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 59 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 62	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: getstatic 68	com/google/android/gms/vision/barcode/Barcode:CREATOR	Lcom/google/android/gms/vision/barcode/zzb;
        //   71: invokevirtual 72	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   74: checkcast 74	[Lcom/google/android/gms/vision/barcode/Barcode;
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 77	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 77	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: areturn
        //   89: aconst_null
        //   90: astore_1
        //   91: goto -65 -> 26
        //   94: aload_3
        //   95: iconst_0
        //   96: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 4
        //   105: invokevirtual 77	android/os/Parcel:recycle	()V
        //   108: aload_3
        //   109: invokevirtual 77	android/os/Parcel:recycle	()V
        //   112: aload_1
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	zza
        //   0	114	1	paramzzd	zzd
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
      
      /* Error */
      public Barcode[] zzb(zzd paramzzd, FrameMetadataParcel paramFrameMetadataParcel)
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
        //   20: invokeinterface 40 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +62 -> 94
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 53	com/google/android/gms/vision/internal/client/FrameMetadataParcel:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/vision/barcode/internal/client/zzc$zza$zza:zzajf	Landroid/os/IBinder;
        //   50: iconst_2
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 59 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 62	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: getstatic 68	com/google/android/gms/vision/barcode/Barcode:CREATOR	Lcom/google/android/gms/vision/barcode/zzb;
        //   71: invokevirtual 72	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   74: checkcast 74	[Lcom/google/android/gms/vision/barcode/Barcode;
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 77	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 77	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: areturn
        //   89: aconst_null
        //   90: astore_1
        //   91: goto -65 -> 26
        //   94: aload_3
        //   95: iconst_0
        //   96: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 4
        //   105: invokevirtual 77	android/os/Parcel:recycle	()V
        //   108: aload_3
        //   109: invokevirtual 77	android/os/Parcel:recycle	()V
        //   112: aload_1
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	zza
        //   0	114	1	paramzzd	zzd
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
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/internal/client/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */