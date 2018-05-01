package com.google.android.gms.maps.model;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.maps.zze;

public final class BitmapDescriptorFactory
{
  private static zze zzcl;
  
  public static BitmapDescriptor fromBitmap(Bitmap paramBitmap)
  {
    try
    {
      paramBitmap = new BitmapDescriptor(zzf().zza(paramBitmap));
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      throw new RuntimeRemoteException(paramBitmap);
    }
  }
  
  public static BitmapDescriptor fromResource(int paramInt)
  {
    try
    {
      BitmapDescriptor localBitmapDescriptor = new BitmapDescriptor(zzf().zza(paramInt));
      return localBitmapDescriptor;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public static void zza(zze paramzze)
  {
    if (zzcl != null) {}
    for (;;)
    {
      return;
      zzcl = (zze)Preconditions.checkNotNull(paramzze);
    }
  }
  
  private static zze zzf()
  {
    return (zze)Preconditions.checkNotNull(zzcl, "IBitmapDescriptorFactory is not initialized");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/BitmapDescriptorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */