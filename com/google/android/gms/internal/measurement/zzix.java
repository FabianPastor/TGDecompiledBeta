package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

final class zzix
  implements Runnable
{
  zzix(zzil paramzzil, boolean paramBoolean, zzjs paramzzjs, zzec paramzzec) {}
  
  public final void run()
  {
    zzey localzzey = zzil.zzd(this.zzapy);
    if (localzzey == null)
    {
      this.zzapy.zzgg().zzil().log("Discarding data. Failed to set user attribute");
      return;
    }
    zzil localzzil = this.zzapy;
    if (this.zzaqc) {}
    for (Object localObject = null;; localObject = this.zzaoe)
    {
      localzzil.zza(localzzey, (AbstractSafeParcelable)localObject, this.zzanq);
      zzil.zze(this.zzapy);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */