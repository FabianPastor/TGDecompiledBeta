package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.zze;
import com.google.android.gms.maps.model.zzk;
import com.google.android.gms.maps.model.zzn;

public abstract interface IStreetViewPanoramaDelegate
  extends IInterface
{
  public abstract void animateTo(StreetViewPanoramaCamera paramStreetViewPanoramaCamera, long paramLong)
    throws RemoteException;
  
  public abstract void enablePanning(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableStreetNames(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableUserNavigation(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableZoom(boolean paramBoolean)
    throws RemoteException;
  
  public abstract StreetViewPanoramaCamera getPanoramaCamera()
    throws RemoteException;
  
  public abstract StreetViewPanoramaLocation getStreetViewPanoramaLocation()
    throws RemoteException;
  
  public abstract boolean isPanningGesturesEnabled()
    throws RemoteException;
  
  public abstract boolean isStreetNamesEnabled()
    throws RemoteException;
  
  public abstract boolean isUserNavigationEnabled()
    throws RemoteException;
  
  public abstract boolean isZoomGesturesEnabled()
    throws RemoteException;
  
  public abstract zzd orientationToPoint(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
    throws RemoteException;
  
  public abstract StreetViewPanoramaOrientation pointToOrientation(zzd paramzzd)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaCameraChangeListener(zzab paramzzab)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaChangeListener(zzac paramzzac)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaClickListener(zzad paramzzad)
    throws RemoteException;
  
  public abstract void setOnStreetViewPanoramaLongClickListener(zzae paramzzae)
    throws RemoteException;
  
  public abstract void setPosition(LatLng paramLatLng)
    throws RemoteException;
  
  public abstract void setPositionWithID(String paramString)
    throws RemoteException;
  
  public abstract void setPositionWithRadius(LatLng paramLatLng, int paramInt)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements IStreetViewPanoramaDelegate
  {
    public static IStreetViewPanoramaDelegate zzix(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
      if ((localIInterface != null) && ((localIInterface instanceof IStreetViewPanoramaDelegate))) {
        return (IStreetViewPanoramaDelegate)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject = null;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      boolean bool1 = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        enableZoom(bool1);
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = bool2;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        enablePanning(bool1);
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = bool3;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        enableUserNavigation(bool1);
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = bool4;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        enableStreetNames(bool1);
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = isZoomGesturesEnabled();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = isPanningGesturesEnabled();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = isUserNavigationEnabled();
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        bool1 = isStreetNamesEnabled();
        paramParcel2.writeNoException();
        paramInt1 = m;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (StreetViewPanoramaCamera)StreetViewPanoramaCamera.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          animateTo((StreetViewPanoramaCamera)localObject, paramParcel1.readLong());
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        paramParcel1 = getPanoramaCamera();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        setPositionWithID(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LatLng)LatLng.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setPosition(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (LatLng)LatLng.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setPositionWithRadius((LatLng)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        paramParcel1 = getStreetViewPanoramaLocation();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 15: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        setOnStreetViewPanoramaChangeListener(zzac.zza.zzir(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        setOnStreetViewPanoramaCameraChangeListener(zzab.zza.zziq(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        setOnStreetViewPanoramaClickListener(zzad.zza.zzis(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        paramParcel1 = pointToOrientation(zzd.zza.zzfe(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 19: 
        paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (StreetViewPanoramaOrientation)StreetViewPanoramaOrientation.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzd localzzd = orientationToPoint(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel1 = (Parcel)localObject;
          if (localzzd != null) {
            paramParcel1 = localzzd.asBinder();
          }
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
      setOnStreetViewPanoramaLongClickListener(zzae.zza.zzit(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements IStreetViewPanoramaDelegate
    {
      private IBinder zzajf;
      
      zza(IBinder paramIBinder)
      {
        this.zzajf = paramIBinder;
      }
      
      /* Error */
      public void animateTo(StreetViewPanoramaCamera paramStreetViewPanoramaCamera, long paramLong)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 31
        //   14: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +55 -> 73
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 45	com/google/android/gms/maps/model/StreetViewPanoramaCamera:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: lload_2
        //   37: invokevirtual 49	android/os/Parcel:writeLong	(J)V
        //   40: aload_0
        //   41: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   44: bipush 9
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokeinterface 55 5 0
        //   56: pop
        //   57: aload 5
        //   59: invokevirtual 58	android/os/Parcel:readException	()V
        //   62: aload 5
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: return
        //   73: aload 4
        //   75: iconst_0
        //   76: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   79: goto -45 -> 34
        //   82: astore_1
        //   83: aload 5
        //   85: invokevirtual 61	android/os/Parcel:recycle	()V
        //   88: aload 4
        //   90: invokevirtual 61	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	zza
        //   0	95	1	paramStreetViewPanoramaCamera	StreetViewPanoramaCamera
        //   0	95	2	paramLong	long
        //   3	86	4	localParcel1	Parcel
        //   8	76	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	82	finally
        //   21	34	82	finally
        //   34	62	82	finally
        //   73	79	82	finally
      }
      
      public IBinder asBinder()
      {
        return this.zzajf;
      }
      
      public void enablePanning(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.zzajf.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void enableStreetNames(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
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
      
      public void enableUserNavigation(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      /* Error */
      public void enableZoom(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 31
        //   14: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +38 -> 56
        //   21: aload_3
        //   22: iload_2
        //   23: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   26: aload_0
        //   27: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   30: iconst_1
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 55 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 58	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 61	android/os/Parcel:recycle	()V
        //   51: aload_3
        //   52: invokevirtual 61	android/os/Parcel:recycle	()V
        //   55: return
        //   56: iconst_0
        //   57: istore_2
        //   58: goto -37 -> 21
        //   61: astore 5
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload 5
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	zza
        //   0	75	1	paramBoolean	boolean
        //   1	57	2	i	int
        //   5	64	3	localParcel1	Parcel
        //   9	55	4	localParcel2	Parcel
        //   61	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	61	finally
        //   21	46	61	finally
      }
      
      /* Error */
      public StreetViewPanoramaCamera getPanoramaCamera()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   18: bipush 10
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 55 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 58	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 75	android/os/Parcel:readInt	()I
        //   37: ifeq +24 -> 61
        //   40: getstatic 79	com/google/android/gms/maps/model/StreetViewPanoramaCamera:CREATOR	Lcom/google/android/gms/maps/model/zzk;
        //   43: aload_3
        //   44: invokevirtual 85	com/google/android/gms/maps/model/zzk:createFromParcel	(Landroid/os/Parcel;)Ljava/lang/Object;
        //   47: checkcast 41	com/google/android/gms/maps/model/StreetViewPanoramaCamera
        //   50: astore_1
        //   51: aload_3
        //   52: invokevirtual 61	android/os/Parcel:recycle	()V
        //   55: aload_2
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_1
        //   60: areturn
        //   61: aconst_null
        //   62: astore_1
        //   63: goto -12 -> 51
        //   66: astore_1
        //   67: aload_3
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload_1
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	zza
        //   50	13	1	localStreetViewPanoramaCamera	StreetViewPanoramaCamera
        //   66	10	1	localObject	Object
        //   3	69	2	localParcel1	Parcel
        //   7	61	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	51	66	finally
      }
      
      /* Error */
      public StreetViewPanoramaLocation getStreetViewPanoramaLocation()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   18: bipush 14
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 55 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 58	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 75	android/os/Parcel:readInt	()I
        //   37: ifeq +24 -> 61
        //   40: getstatic 92	com/google/android/gms/maps/model/StreetViewPanoramaLocation:CREATOR	Lcom/google/android/gms/maps/model/zzm;
        //   43: aload_3
        //   44: invokevirtual 95	com/google/android/gms/maps/model/zzm:createFromParcel	(Landroid/os/Parcel;)Ljava/lang/Object;
        //   47: checkcast 89	com/google/android/gms/maps/model/StreetViewPanoramaLocation
        //   50: astore_1
        //   51: aload_3
        //   52: invokevirtual 61	android/os/Parcel:recycle	()V
        //   55: aload_2
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_1
        //   60: areturn
        //   61: aconst_null
        //   62: astore_1
        //   63: goto -12 -> 51
        //   66: astore_1
        //   67: aload_3
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload_1
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	zza
        //   50	13	1	localStreetViewPanoramaLocation	StreetViewPanoramaLocation
        //   66	10	1	localObject	Object
        //   3	69	2	localParcel1	Parcel
        //   7	61	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	51	66	finally
      }
      
      public boolean isPanningGesturesEnabled()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          this.zzajf.transact(6, localParcel1, localParcel2, 0);
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
      
      public boolean isStreetNamesEnabled()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          this.zzajf.transact(8, localParcel1, localParcel2, 0);
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
      
      public boolean isUserNavigationEnabled()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          this.zzajf.transact(7, localParcel1, localParcel2, 0);
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
      
      public boolean isZoomGesturesEnabled()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          this.zzajf.transact(5, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public zzd orientationToPoint(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +51 -> 66
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 105	com/google/android/gms/maps/model/StreetViewPanoramaOrientation:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   33: bipush 19
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 108	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   52: invokestatic 114	com/google/android/gms/dynamic/zzd$zza:zzfe	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamic/zzd;
        //   55: astore_1
        //   56: aload_3
        //   57: invokevirtual 61	android/os/Parcel:recycle	()V
        //   60: aload_2
        //   61: invokevirtual 61	android/os/Parcel:recycle	()V
        //   64: aload_1
        //   65: areturn
        //   66: aload_2
        //   67: iconst_0
        //   68: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   71: goto -42 -> 29
        //   74: astore_1
        //   75: aload_3
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: aload_2
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	zza
        //   0	85	1	paramStreetViewPanoramaOrientation	StreetViewPanoramaOrientation
        //   3	77	2	localParcel1	Parcel
        //   7	69	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	74	finally
        //   18	29	74	finally
        //   29	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public StreetViewPanoramaOrientation pointToOrientation(zzd paramzzd)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 31
        //   14: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +69 -> 87
        //   21: aload_1
        //   22: invokeinterface 120 1 0
        //   27: astore_1
        //   28: aload_3
        //   29: aload_1
        //   30: invokevirtual 123	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   33: aload_0
        //   34: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   37: bipush 18
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokeinterface 55 5 0
        //   48: pop
        //   49: aload 4
        //   51: invokevirtual 58	android/os/Parcel:readException	()V
        //   54: aload_2
        //   55: astore_1
        //   56: aload 4
        //   58: invokevirtual 75	android/os/Parcel:readInt	()I
        //   61: ifeq +15 -> 76
        //   64: getstatic 126	com/google/android/gms/maps/model/StreetViewPanoramaOrientation:CREATOR	Lcom/google/android/gms/maps/model/zzn;
        //   67: aload 4
        //   69: invokevirtual 129	com/google/android/gms/maps/model/zzn:createFromParcel	(Landroid/os/Parcel;)Ljava/lang/Object;
        //   72: checkcast 104	com/google/android/gms/maps/model/StreetViewPanoramaOrientation
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: areturn
        //   87: aconst_null
        //   88: astore_1
        //   89: goto -61 -> 28
        //   92: astore_1
        //   93: aload 4
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload_3
        //   99: invokevirtual 61	android/os/Parcel:recycle	()V
        //   102: aload_1
        //   103: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	104	0	this	zza
        //   0	104	1	paramzzd	zzd
        //   1	54	2	localObject	Object
        //   5	94	3	localParcel1	Parcel
        //   9	85	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	92	finally
        //   21	28	92	finally
        //   28	54	92	finally
        //   56	76	92	finally
      }
      
      /* Error */
      public void setOnStreetViewPanoramaCameraChangeListener(zzab paramzzab)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 134 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 123	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: bipush 16
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	zza
        //   0	74	1	paramzzab	zzab
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      /* Error */
      public void setOnStreetViewPanoramaChangeListener(zzac paramzzac)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 139 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 123	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: bipush 15
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	zza
        //   0	74	1	paramzzac	zzac
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      /* Error */
      public void setOnStreetViewPanoramaClickListener(zzad paramzzad)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 144 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 123	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: bipush 17
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	zza
        //   0	74	1	paramzzad	zzad
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      /* Error */
      public void setOnStreetViewPanoramaLongClickListener(zzae paramzzae)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 149 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 123	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   34: bipush 20
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	zza
        //   0	74	1	paramzzae	zzae
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      /* Error */
      public void setPosition(LatLng paramLatLng)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 154	com/google/android/gms/maps/model/LatLng:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   33: bipush 12
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
        //   59: invokevirtual 39	android/os/Parcel:writeInt	(I)V
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
        //   0	76	1	paramLatLng	LatLng
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setPositionWithID(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
          localParcel1.writeString(paramString);
          this.zzajf.transact(11, localParcel1, localParcel2, 0);
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
      public void setPositionWithRadius(LatLng paramLatLng, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 31
        //   12: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 154	com/google/android/gms/maps/model/LatLng:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 18	com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate$zza$zza:zzajf	Landroid/os/IBinder;
        //   39: bipush 13
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	zza
        //   0	86	1	paramLatLng	LatLng
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/IStreetViewPanoramaDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */