package com.google.android.gms.maps;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.maps.internal.zzbz;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import javax.annotation.concurrent.GuardedBy;

public final class MapsInitializer
{
  @GuardedBy("MapsInitializer.class")
  private static boolean zzbl = false;
  
  public static int initialize(Context paramContext)
  {
    int i = 0;
    for (;;)
    {
      try
      {
        Preconditions.checkNotNull(paramContext, "Context is null");
        boolean bool = zzbl;
        if (!bool) {
          continue;
        }
      }
      finally
      {
        try
        {
          paramContext = zzbz.zza(paramContext);
        }
        catch (GooglePlayServicesNotAvailableException paramContext)
        {
          i = paramContext.errorCode;
        }
        try
        {
          CameraUpdateFactory.zza(paramContext.zzd());
          BitmapDescriptorFactory.zza(paramContext.zze());
          zzbl = true;
        }
        catch (RemoteException localRemoteException)
        {
          paramContext = new com/google/android/gms/maps/model/RuntimeRemoteException;
          paramContext.<init>(localRemoteException);
          throw paramContext;
        }
        paramContext = finally;
      }
      return i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/MapsInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */