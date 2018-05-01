package com.google.android.gms.internal.measurement;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zziy
  implements Runnable
{
  zziy(zzil paramzzil, AtomicReference paramAtomicReference, zzec paramzzec, boolean paramBoolean) {}
  
  public final void run()
  {
    localAtomicReference = this.zzapz;
    for (;;)
    {
      try
      {
        localzzey = zzil.zzd(this.zzapy);
        if (localzzey == null) {
          this.zzapy.zzgg().zzil().log("Failed to get user properties");
        }
      }
      catch (RemoteException localRemoteException)
      {
        zzey localzzey;
        this.zzapy.zzgg().zzil().zzg("Failed to get user properties", localRemoteException);
        this.zzapz.notify();
        continue;
      }
      finally
      {
        this.zzapz.notify();
      }
      try
      {
        this.zzapz.notify();
        return;
      }
      finally {}
      this.zzapz.set(localzzey.zza(this.zzanq, this.zzaos));
      zzil.zze(this.zzapy);
      this.zzapz.notify();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zziy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */