package com.google.android.gms.internal.measurement;

import android.os.RemoteException;

final class zzip
  implements Runnable
{
  zzip(zzil paramzzil, zzec paramzzec) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null) {
      this.zzapy.zzgg().zzil().log("Discarding data. Failed to send app launch");
    }
    for (;;)
    {
      return;
      try
      {
        localzzey.zza(this.zzanq);
        this.zzapy.zza(localzzey, null, this.zzanq);
        zzil.zze(this.zzapy);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzapy.zzgg().zzil().zzg("Failed to send app launch to the service", localRemoteException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzip.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */