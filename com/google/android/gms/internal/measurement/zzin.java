package com.google.android.gms.internal.measurement;

import android.os.RemoteException;

final class zzin
  implements Runnable
{
  zzin(zzil paramzzil, zzec paramzzec) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null) {
      this.zzapy.zzgg().zzil().log("Failed to reset data on the service; null service");
    }
    for (;;)
    {
      return;
      try
      {
        localzzey.zzd(this.zzanq);
        zzil.zze(this.zzapy);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          this.zzapy.zzgg().zzil().zzg("Failed to reset data on the service", localRemoteException);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */