package com.google.android.gms.internal.measurement;

import android.os.Bundle;

final class zzii
  implements Runnable
{
  zzii(zzih paramzzih, boolean paramBoolean, zzig paramzzig, zzik paramzzik) {}
  
  public final void run()
  {
    if ((this.zzapl) && (this.zzapo.zzapc != null)) {
      zzih.zza(this.zzapo, this.zzapo.zzapc);
    }
    if ((this.zzapm == null) || (this.zzapm.zzapb != this.zzapn.zzapb) || (!zzjv.zzs(this.zzapm.zzapa, this.zzapn.zzapa)) || (!zzjv.zzs(this.zzapm.zzug, this.zzapn.zzug))) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        Bundle localBundle = new Bundle();
        zzih.zza(this.zzapn, localBundle, true);
        if (this.zzapm != null)
        {
          if (this.zzapm.zzug != null) {
            localBundle.putString("_pn", this.zzapm.zzug);
          }
          localBundle.putString("_pc", this.zzapm.zzapa);
          localBundle.putLong("_pi", this.zzapm.zzapb);
        }
        this.zzapo.zzfu().zza("auto", "_vs", localBundle);
      }
      this.zzapo.zzapc = this.zzapn;
      this.zzapo.zzfx().zza(this.zzapn);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzii.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */