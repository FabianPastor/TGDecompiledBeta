package com.google.android.gms.internal;

import android.os.Bundle;

final class zzbep
  implements Runnable
{
  zzbep(zzbeo paramzzbeo, zzbds paramzzbds, String paramString) {}
  
  public final void run()
  {
    zzbds localzzbds;
    if (zzbeo.zza(this.zzaEZ) > 0)
    {
      localzzbds = this.zzaEK;
      if (zzbeo.zzb(this.zzaEZ) == null) {
        break label118;
      }
    }
    label118:
    for (Bundle localBundle = zzbeo.zzb(this.zzaEZ).getBundle(this.zzO);; localBundle = null)
    {
      localzzbds.onCreate(localBundle);
      if (zzbeo.zza(this.zzaEZ) >= 2) {
        this.zzaEK.onStart();
      }
      if (zzbeo.zza(this.zzaEZ) >= 3) {
        this.zzaEK.onResume();
      }
      if (zzbeo.zza(this.zzaEZ) >= 4) {
        this.zzaEK.onStop();
      }
      if (zzbeo.zza(this.zzaEZ) >= 5) {
        this.zzaEK.onDestroy();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbep.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */