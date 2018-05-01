package com.google.android.gms.internal.measurement;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzio
  implements Runnable
{
  zzio(zzil paramzzil, AtomicReference paramAtomicReference, zzec paramzzec) {}
  
  public final void run()
  {
    localAtomicReference = this.zzapz;
    for (;;)
    {
      try
      {
        localObject1 = zzil.zzd(this.zzapy);
        if (localObject1 == null) {
          this.zzapy.zzgg().zzil().log("Failed to get app instance id");
        }
      }
      catch (RemoteException localRemoteException)
      {
        Object localObject1;
        this.zzapy.zzgg().zzil().zzg("Failed to get app instance id", localRemoteException);
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
      this.zzapz.set(((zzey)localObject1).zzc(this.zzanq));
      localObject1 = (String)this.zzapz.get();
      if (localObject1 != null)
      {
        this.zzapy.zzfu().zzbm((String)localObject1);
        this.zzapy.zzgh().zzaka.zzbn((String)localObject1);
      }
      zzil.zze(this.zzapy);
      this.zzapz.notify();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzio.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */