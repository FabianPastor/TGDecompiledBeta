package com.google.android.gms.maps;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.internal.zzc;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class MapsInitializer
{
  private static boolean zzagt = false;
  
  public static int initialize(Context paramContext)
  {
    int i = 0;
    for (;;)
    {
      try
      {
        zzac.zzb(paramContext, "Context is null");
        boolean bool = zzagt;
        if (!bool) {
          continue;
        }
      }
      finally
      {
        try
        {
          paramContext = zzai.zzdp(paramContext);
          zza(paramContext);
          zzagt = true;
        }
        catch (GooglePlayServicesNotAvailableException paramContext)
        {
          i = paramContext.errorCode;
        }
        paramContext = finally;
      }
      return i;
    }
  }
  
  public static void zza(zzc paramzzc)
  {
    try
    {
      CameraUpdateFactory.zza(paramzzc.zzbsc());
      BitmapDescriptorFactory.zza(paramzzc.zzbsd());
      return;
    }
    catch (RemoteException paramzzc)
    {
      throw new RuntimeRemoteException(paramzzc);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/MapsInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */