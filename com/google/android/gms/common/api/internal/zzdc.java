package com.google.android.gms.common.api.internal;

import android.os.Bundle;

final class zzdc
  implements Runnable
{
  zzdc(zzdb paramzzdb, LifecycleCallback paramLifecycleCallback, String paramString) {}
  
  public final void run()
  {
    LifecycleCallback localLifecycleCallback;
    if (zzdb.zza(this.zzfuw) > 0)
    {
      localLifecycleCallback = this.zzfuh;
      if (zzdb.zzb(this.zzfuw) == null) {
        break label118;
      }
    }
    label118:
    for (Bundle localBundle = zzdb.zzb(this.zzfuw).getBundle(this.zzat);; localBundle = null)
    {
      localLifecycleCallback.onCreate(localBundle);
      if (zzdb.zza(this.zzfuw) >= 2) {
        this.zzfuh.onStart();
      }
      if (zzdb.zza(this.zzfuw) >= 3) {
        this.zzfuh.onResume();
      }
      if (zzdb.zza(this.zzfuw) >= 4) {
        this.zzfuh.onStop();
      }
      if (zzdb.zza(this.zzfuw) >= 5) {
        this.zzfuh.onDestroy();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */