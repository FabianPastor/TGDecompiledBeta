package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.maps.model.zze;

public abstract interface IProjectionDelegate
  extends IInterface
{
  public abstract LatLng fromScreenLocation(zzd paramzzd)
    throws RemoteException;
  
  public abstract VisibleRegion getVisibleRegion()
    throws RemoteException;
  
  public abstract zzd toScreenLocation(LatLng paramLatLng)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements IProjectionDelegate
  {
    public static IProjectionDelegate zziv(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.internal.IProjectionDelegate");
      if ((localIInterface != null) && ((localIInterface instanceof IProjectionDelegate))) {
        return (IProjectionDelegate)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.maps.internal.IProjectionDelegate");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IProjectionDelegate");
        paramParcel1 = fromScreenLocation(zzd.zza.zzfe(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IProjectionDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LatLng)LatLng.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzd localzzd = toScreenLocation(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel1 = (Parcel)localObject;
          if (localzzd != null) {
            paramParcel1 = localzzd.asBinder();
          }
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IProjectionDelegate");
      paramParcel1 = getVisibleRegion();
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    }
    
    private static class zza
      implements IProjectionDelegate
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
      public LatLng fromScreenLocation(zzd paramzzd)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 33
        //   14: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +68 -> 86
        //   21: aload_1
        //   22: invokeinterface 41 1 0
        //   27: astore_1
        //   28: aload_3
        //   29: aload_1
        //   30: invokevirtual 44	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   33: aload_0
        //   34: getfield 18	com/google/android/gms/maps/internal/IProjectionDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   37: iconst_1
        //   38: aload_3
        //   39: aload 4
        //   41: iconst_0
        //   42: invokeinterface 50 5 0
        //   47: pop
        //   48: aload 4
        //   50: invokevirtual 53	android/os/Parcel:readException	()V
        //   53: aload_2
        //   54: astore_1
        //   55: aload 4
        //   57: invokevirtual 57	android/os/Parcel:readInt	()I
        //   60: ifeq +15 -> 75
        //   63: getstatic 63	com/google/android/gms/maps/model/LatLng:CREATOR	Lcom/google/android/gms/maps/model/zze;
        //   66: aload 4
        //   68: invokevirtual 69	com/google/android/gms/maps/model/zze:createFromParcel	(Landroid/os/Parcel;)Ljava/lang/Object;
        //   71: checkcast 59	com/google/android/gms/maps/model/LatLng
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 72	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 72	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: areturn
        //   86: aconst_null
        //   87: astore_1
        //   88: goto -60 -> 28
        //   91: astore_1
        //   92: aload 4
        //   94: invokevirtual 72	android/os/Parcel:recycle	()V
        //   97: aload_3
        //   98: invokevirtual 72	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	zza
        //   0	103	1	paramzzd	zzd
        //   1	53	2	localObject	Object
        //   5	93	3	localParcel1	Parcel
        //   9	84	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	91	finally
        //   21	28	91	finally
        //   28	53	91	finally
        //   55	75	91	finally
      }
      
      /* Error */
      public VisibleRegion getVisibleRegion()
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
        //   14: aload_0
        //   15: getfield 18	com/google/android/gms/maps/internal/IProjectionDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   18: iconst_3
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 50 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 53	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 57	android/os/Parcel:readInt	()I
        //   36: ifeq +24 -> 60
        //   39: getstatic 80	com/google/android/gms/maps/model/VisibleRegion:CREATOR	Lcom/google/android/gms/maps/model/zzq;
        //   42: aload_3
        //   43: invokevirtual 83	com/google/android/gms/maps/model/zzq:createFromParcel	(Landroid/os/Parcel;)Ljava/lang/Object;
        //   46: checkcast 77	com/google/android/gms/maps/model/VisibleRegion
        //   49: astore_1
        //   50: aload_3
        //   51: invokevirtual 72	android/os/Parcel:recycle	()V
        //   54: aload_2
        //   55: invokevirtual 72	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: areturn
        //   60: aconst_null
        //   61: astore_1
        //   62: goto -12 -> 50
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 72	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 72	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   49	13	1	localVisibleRegion	VisibleRegion
        //   65	10	1	localObject	Object
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	50	65	finally
      }
      
      /* Error */
      public zzd toScreenLocation(LatLng paramLatLng)
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
        //   15: ifnull +50 -> 65
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 89	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 93	com/google/android/gms/maps/model/LatLng:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/maps/internal/IProjectionDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   33: iconst_2
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 50 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 53	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 96	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   51: invokestatic 102	com/google/android/gms/dynamic/zzd$zza:zzfe	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamic/zzd;
        //   54: astore_1
        //   55: aload_3
        //   56: invokevirtual 72	android/os/Parcel:recycle	()V
        //   59: aload_2
        //   60: invokevirtual 72	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: areturn
        //   65: aload_2
        //   66: iconst_0
        //   67: invokevirtual 89	android/os/Parcel:writeInt	(I)V
        //   70: goto -41 -> 29
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 72	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 72	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	zza
        //   0	84	1	paramLatLng	LatLng
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	73	finally
        //   18	29	73	finally
        //   29	55	73	finally
        //   65	70	73	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IProjectionDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */