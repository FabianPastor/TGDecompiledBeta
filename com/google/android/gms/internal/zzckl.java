package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzckl
  implements Runnable
{
  zzckl(zzckg paramzzckg, AppMeasurement.zzb paramzzb) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Failed to send current screen to service");
      return;
    }
    for (;;)
    {
      try
      {
        if (this.zzjil == null)
        {
          localzzche.zza(0L, null, null, this.zzjij.getContext().getPackageName());
          zzckg.zze(this.zzjij);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        this.zzjij.zzawy().zzazd().zzj("Failed to send current screen to the service", localRemoteException);
        return;
      }
      localRemoteException.zza(this.zzjil.zziwm, this.zzjil.zziwk, this.zzjil.zziwl, this.zzjij.getContext().getPackageName());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */