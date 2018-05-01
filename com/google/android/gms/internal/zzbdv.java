package com.google.android.gms.internal;

import android.os.Bundle;

final class zzbdv
  implements Runnable
{
  zzbdv(zzbdu paramzzbdu, zzbds paramzzbds, String paramString) {}
  
  public final void run()
  {
    zzbds localzzbds;
    if (zzbdu.zza(this.zzaEL) > 0)
    {
      localzzbds = this.zzaEK;
      if (zzbdu.zzb(this.zzaEL) == null) {
        break label118;
      }
    }
    label118:
    for (Bundle localBundle = zzbdu.zzb(this.zzaEL).getBundle(this.zzO);; localBundle = null)
    {
      localzzbds.onCreate(localBundle);
      if (zzbdu.zza(this.zzaEL) >= 2) {
        this.zzaEK.onStart();
      }
      if (zzbdu.zza(this.zzaEL) >= 3) {
        this.zzaEK.onResume();
      }
      if (zzbdu.zza(this.zzaEL) >= 4) {
        this.zzaEK.onStop();
      }
      if (zzbdu.zza(this.zzaEL) >= 5) {
        this.zzaEK.onDestroy();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */