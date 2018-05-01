package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcki
  implements Runnable
{
  zzcki(zzckg paramzzckg, zzcgi paramzzcgi) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Failed to reset data on the service; null service");
      return;
    }
    try
    {
      localzzche.zzd(this.zzjgn);
      zzckg.zze(this.zzjij);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.zzjij.zzawy().zzazd().zzj("Failed to reset data on the service", localRemoteException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcki.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */