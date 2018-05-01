package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzckj
  implements Runnable
{
  zzckj(zzckg paramzzckg, AtomicReference paramAtomicReference, zzcgi paramzzcgi) {}
  
  public final void run()
  {
    localAtomicReference = this.zzjik;
    for (;;)
    {
      try
      {
        localObject1 = zzckg.zzd(this.zzjij);
        if (localObject1 == null) {
          this.zzjij.zzawy().zzazd().log("Failed to get app instance id");
        }
      }
      catch (RemoteException localRemoteException)
      {
        Object localObject1;
        this.zzjij.zzawy().zzazd().zzj("Failed to get app instance id", localRemoteException);
        this.zzjik.notify();
        continue;
      }
      finally
      {
        this.zzjik.notify();
      }
      try
      {
        this.zzjik.notify();
        return;
      }
      finally {}
    }
    this.zzjik.set(((zzche)localObject1).zzc(this.zzjgn));
    localObject1 = (String)this.zzjik.get();
    if (localObject1 != null)
    {
      this.zzjij.zzawm().zzjp((String)localObject1);
      this.zzjij.zzawz().zzjcx.zzjq((String)localObject1);
    }
    zzckg.zze(this.zzjij);
    this.zzjik.notify();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */