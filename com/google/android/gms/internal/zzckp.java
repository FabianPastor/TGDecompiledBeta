package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzckp
  implements Runnable
{
  zzckp(zzckg paramzzckg, boolean paramBoolean1, boolean paramBoolean2, zzcgl paramzzcgl1, zzcgi paramzzcgi, zzcgl paramzzcgl2) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Discarding data. Failed to send conditional user property to service");
      return;
    }
    Object localObject;
    if (this.zzjim)
    {
      zzckg localzzckg = this.zzjij;
      if (this.zzjin)
      {
        localObject = null;
        localzzckg.zza(localzzche, (zzbfm)localObject, this.zzjgn);
      }
    }
    for (;;)
    {
      zzckg.zze(this.zzjij);
      return;
      localObject = this.zzjio;
      break;
      try
      {
        if (!TextUtils.isEmpty(this.zzjip.packageName)) {
          break label125;
        }
        localzzche.zza(this.zzjio, this.zzjgn);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzjij.zzawy().zzazd().zzj("Failed to send conditional user property to the service", localRemoteException);
      }
      continue;
      label125:
      localzzche.zzb(this.zzjio);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */