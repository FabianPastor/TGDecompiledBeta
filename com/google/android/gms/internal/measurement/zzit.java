package com.google.android.gms.internal.measurement;

import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

final class zzit
  implements Runnable
{
  zzit(zzil paramzzil, boolean paramBoolean1, boolean paramBoolean2, zzeu paramzzeu, zzec paramzzec, String paramString) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null)
    {
      this.zzapy.zzgg().zzil().log("Discarding data. Failed to send event to service");
      return;
    }
    Object localObject;
    if (this.zzaqb)
    {
      zzil localzzil = this.zzapy;
      if (this.zzaqc)
      {
        localObject = null;
        label49:
        localzzil.zza(localzzey, (AbstractSafeParcelable)localObject, this.zzanq);
      }
    }
    for (;;)
    {
      zzil.zze(this.zzapy);
      break;
      localObject = this.zzaod;
      break label49;
      try
      {
        if (!TextUtils.isEmpty(this.zzaoc)) {
          break label124;
        }
        localzzey.zza(this.zzaod, this.zzanq);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzapy.zzgg().zzil().zzg("Failed to send event to the service", localRemoteException);
      }
      continue;
      label124:
      localzzey.zza(this.zzaod, this.zzaoc, this.zzapy.zzgg().zzit());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */