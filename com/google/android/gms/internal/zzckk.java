package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzckk
  implements Runnable
{
  zzckk(zzckg paramzzckg, zzcgi paramzzcgi) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Discarding data. Failed to send app launch");
      return;
    }
    try
    {
      localzzche.zza(this.zzjgn);
      this.zzjij.zza(localzzche, null, this.zzjgn);
      zzckg.zze(this.zzjij);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      this.zzjij.zzawy().zzazd().zzj("Failed to send app launch to the service", localRemoteException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */