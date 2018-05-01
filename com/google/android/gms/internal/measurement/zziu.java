package com.google.android.gms.internal.measurement;

import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

final class zziu
  implements Runnable
{
  zziu(zzil paramzzil, boolean paramBoolean1, boolean paramBoolean2, zzef paramzzef1, zzec paramzzec, zzef paramzzef2) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null)
    {
      this.zzapy.zzgg().zzil().log("Discarding data. Failed to send conditional user property to service");
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
      localObject = this.zzaqd;
      break label49;
      try
      {
        if (!TextUtils.isEmpty(this.zzaqe.packageName)) {
          break label127;
        }
        localzzey.zza(this.zzaqd, this.zzanq);
      }
      catch (RemoteException localRemoteException)
      {
        this.zzapy.zzgg().zzil().zzg("Failed to send conditional user property to the service", localRemoteException);
      }
      continue;
      label127:
      localzzey.zzb(this.zzaqd);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zziu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */