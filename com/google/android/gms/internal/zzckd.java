package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzckd
  implements Runnable
{
  zzckd(zzckc paramzzckc, boolean paramBoolean, AppMeasurement.zzb paramzzb, zzckf paramzzckf) {}
  
  public final void run()
  {
    if ((this.zzjhw) && (this.zzjhz.zzjhn != null)) {
      zzckc.zza(this.zzjhz, this.zzjhz.zzjhn);
    }
    if ((this.zzjhx == null) || (this.zzjhx.zziwm != this.zzjhy.zziwm) || (!zzclq.zzas(this.zzjhx.zziwl, this.zzjhy.zziwl)) || (!zzclq.zzas(this.zzjhx.zziwk, this.zzjhy.zziwk))) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        Bundle localBundle = new Bundle();
        zzckc.zza(this.zzjhy, localBundle);
        if (this.zzjhx != null)
        {
          if (this.zzjhx.zziwk != null) {
            localBundle.putString("_pn", this.zzjhx.zziwk);
          }
          localBundle.putString("_pc", this.zzjhx.zziwl);
          localBundle.putLong("_pi", this.zzjhx.zziwm);
        }
        this.zzjhz.zzawm().zzc("auto", "_vs", localBundle);
      }
      this.zzjhz.zzjhn = this.zzjhy;
      this.zzjhz.zzawp().zza(this.zzjhy);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */