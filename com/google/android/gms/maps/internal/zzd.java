package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzf.zza;

public abstract interface zzd
  extends IInterface
{
  public abstract com.google.android.gms.dynamic.zzd zzh(zzf paramzzf)
    throws RemoteException;
  
  public abstract com.google.android.gms.dynamic.zzd zzi(zzf paramzzf)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzd
  {
    public zza()
    {
      attachInterface(this, "com.google.android.gms.maps.internal.IInfoWindowAdapter");
    }
    
    public static zzd zzhp(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.internal.IInfoWindowAdapter");
      if ((localIInterface != null) && ((localIInterface instanceof zzd))) {
        return (zzd)localIInterface;
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
      com.google.android.gms.dynamic.zzd localzzd2 = null;
      com.google.android.gms.dynamic.zzd localzzd1 = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.maps.internal.IInfoWindowAdapter");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IInfoWindowAdapter");
        localzzd2 = zzh(zzf.zza.zzjg(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel1 = localzzd1;
        if (localzzd2 != null) {
          paramParcel1 = localzzd2.asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IInfoWindowAdapter");
      localzzd1 = zzi(zzf.zza.zzjg(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      paramParcel1 = localzzd2;
      if (localzzd1 != null) {
        paramParcel1 = localzzd1.asBinder();
      }
      paramParcel2.writeStrongBinder(paramParcel1);
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
      
      /* Error */
      public com.google.android.gms.dynamic.zzd zzh(zzf paramzzf)
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
        //   15: ifnull +51 -> 66
        //   18: aload_1
        //   19: invokeinterface 41 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 44	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/zzd$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 50 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 53	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 56	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   52: invokestatic 62	com/google/android/gms/dynamic/zzd$zza:zzfe	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamic/zzd;
        //   55: astore_1
        //   56: aload_3
        //   57: invokevirtual 65	android/os/Parcel:recycle	()V
        //   60: aload_2
        //   61: invokevirtual 65	android/os/Parcel:recycle	()V
        //   64: aload_1
        //   65: areturn
        //   66: aconst_null
        //   67: astore_1
        //   68: goto -43 -> 25
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 65	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 65	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	zza
        //   0	82	1	paramzzf	zzf
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	25	71	finally
        //   25	56	71	finally
      }
      
      /* Error */
      public com.google.android.gms.dynamic.zzd zzi(zzf paramzzf)
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
        //   15: ifnull +51 -> 66
        //   18: aload_1
        //   19: invokeinterface 41 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 44	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/zzd$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 50 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 53	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 56	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   52: invokestatic 62	com/google/android/gms/dynamic/zzd$zza:zzfe	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamic/zzd;
        //   55: astore_1
        //   56: aload_3
        //   57: invokevirtual 65	android/os/Parcel:recycle	()V
        //   60: aload_2
        //   61: invokevirtual 65	android/os/Parcel:recycle	()V
        //   64: aload_1
        //   65: areturn
        //   66: aconst_null
        //   67: astore_1
        //   68: goto -43 -> 25
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 65	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 65	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	zza
        //   0	82	1	paramzzf	zzf
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	25	71	finally
        //   25	56	71	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */