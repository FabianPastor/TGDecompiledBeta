package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.os.RemoteException;

final class zziq
  implements Runnable
{
  zziq(zzil paramzzil, zzig paramzzig) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null)
    {
      this.zzapy.zzgg().zzil().log("Failed to send current screen to service");
      return;
    }
    for (;;)
    {
      try
      {
        if (this.zzaqa != null) {
          break label84;
        }
        localzzey.zza(0L, null, null, this.zzapy.getContext().getPackageName());
        zzil.zze(this.zzapy);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzapy.zzgg().zzil().zzg("Failed to send current screen to the service", localRemoteException);
      }
      break;
      label84:
      localRemoteException.zza(this.zzaqa.zzapb, this.zzaqa.zzug, this.zzaqa.zzapa, this.zzapy.getContext().getPackageName());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zziq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */