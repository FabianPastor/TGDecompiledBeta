package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcko
  implements Runnable
{
  zzcko(zzckg paramzzckg, boolean paramBoolean1, boolean paramBoolean2, zzcha paramzzcha, zzcgi paramzzcgi, String paramString) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Discarding data. Failed to send event to service");
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
      localObject = this.zzjgs;
      break;
      try
      {
        if (!TextUtils.isEmpty(this.zzimf)) {
          break label122;
        }
        localzzche.zza(this.zzjgs, this.zzjgn);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzjij.zzawy().zzazd().zzj("Failed to send event to the service", localRemoteException);
      }
      continue;
      label122:
      localzzche.zza(this.zzjgs, this.zzimf, this.zzjij.zzawy().zzazk());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcko.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */