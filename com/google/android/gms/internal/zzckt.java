package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzckt
  implements Runnable
{
  zzckt(zzckg paramzzckg, AtomicReference paramAtomicReference, zzcgi paramzzcgi, boolean paramBoolean) {}
  
  public final void run()
  {
    localAtomicReference = this.zzjik;
    for (;;)
    {
      try
      {
        localzzche = zzckg.zzd(this.zzjij);
        if (localzzche == null) {
          this.zzjij.zzawy().zzazd().log("Failed to get user properties");
        }
      }
      catch (RemoteException localRemoteException)
      {
        zzche localzzche;
        this.zzjij.zzawy().zzazd().zzj("Failed to get user properties", localRemoteException);
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
    this.zzjik.set(localzzche.zza(this.zzjgn, this.zzjhf));
    zzckg.zze(this.zzjij);
    this.zzjik.notify();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */