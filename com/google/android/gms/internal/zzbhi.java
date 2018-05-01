package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;

public abstract interface zzbhi
  extends IInterface
{
  public abstract void zzSu()
    throws RemoteException;
  
  public abstract zzbhk[] zza(zzd paramzzd, zzbhd paramzzbhd, zzbhm paramzzbhm)
    throws RemoteException;
  
  public abstract zzbhk[] zzd(zzd paramzzd, zzbhd paramzzbhd)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzbhi
  {
    public static zzbhi zzfk(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
      if ((localIInterface != null) && ((localIInterface instanceof zzbhi))) {
        return (zzbhi)localIInterface;
      }
      return new zza(paramIBinder);
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
        paramParcel2.writeString("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
        localObject = zzd.zza.zzcd(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzbhd)zzbhd.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zzd((zzd)localObject, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
        zzd localzzd = zzd.zza.zzcd(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject = (zzbhd)zzbhd.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label206;
          }
        }
        label206:
        for (paramParcel1 = (zzbhm)zzbhm.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zza(localzzd, (zzbhd)localObject, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
          localObject = null;
          break;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
      zzSu();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzbhi
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
      
      public void zzSu()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
          this.zzrp.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public zzbhk[] zza(zzd paramzzd, zzbhd paramzzbhd, zzbhm paramzzbhm)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label140:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
            if (paramzzd != null)
            {
              paramzzd = paramzzd.asBinder();
              localParcel1.writeStrongBinder(paramzzd);
              if (paramzzbhd != null)
              {
                localParcel1.writeInt(1);
                paramzzbhd.writeToParcel(localParcel1, 0);
                if (paramzzbhm == null) {
                  break label140;
                }
                localParcel1.writeInt(1);
                paramzzbhm.writeToParcel(localParcel1, 0);
                this.zzrp.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramzzd = (zzbhk[])localParcel2.createTypedArray(zzbhk.CREATOR);
                return paramzzd;
              }
            }
            else
            {
              paramzzd = null;
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
      
      /* Error */
      public zzbhk[] zzd(zzd paramzzd, zzbhd paramzzbhd)
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
        //   20: invokeinterface 54 1 0
        //   25: astore_1
        //   26: aload_3
        //   27: aload_1
        //   28: invokevirtual 57	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +62 -> 94
        //   35: aload_3
        //   36: iconst_1
        //   37: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   40: aload_2
        //   41: aload_3
        //   42: iconst_0
        //   43: invokevirtual 67	com/google/android/gms/internal/zzbhd:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/internal/zzbhi$zza$zza:zzrp	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload_3
        //   52: aload 4
        //   54: iconst_0
        //   55: invokeinterface 42 5 0
        //   60: pop
        //   61: aload 4
        //   63: invokevirtual 45	android/os/Parcel:readException	()V
        //   66: aload 4
        //   68: getstatic 76	com/google/android/gms/internal/zzbhk:CREATOR	Landroid/os/Parcelable$Creator;
        //   71: invokevirtual 80	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   74: checkcast 82	[Lcom/google/android/gms/internal/zzbhk;
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
        //   96: invokevirtual 61	android/os/Parcel:writeInt	(I)V
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
        //   0	114	1	paramzzd	zzd
        //   0	114	2	paramzzbhd	zzbhd
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
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */