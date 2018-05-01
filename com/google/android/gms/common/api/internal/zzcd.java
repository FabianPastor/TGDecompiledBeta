package com.google.android.gms.common.api.internal;

import android.os.Bundle;

final class zzcd
  implements Runnable
{
  zzcd(zzcc paramzzcc, LifecycleCallback paramLifecycleCallback, String paramString) {}
  
  public final void run()
  {
    LifecycleCallback localLifecycleCallback;
    if (zzcc.zza(this.zzly) > 0)
    {
      localLifecycleCallback = this.zzle;
      if (zzcc.zzb(this.zzly) == null) {
        break label118;
      }
    }
    label118:
    for (Bundle localBundle = zzcc.zzb(this.zzly).getBundle(this.zzlf);; localBundle = null)
    {
      localLifecycleCallback.onCreate(localBundle);
      if (zzcc.zza(this.zzly) >= 2) {
        this.zzle.onStart();
      }
      if (zzcc.zza(this.zzly) >= 3) {
        this.zzle.onResume();
      }
      if (zzcc.zza(this.zzly) >= 4) {
        this.zzle.onStop();
      }
      if (zzcc.zza(this.zzly) >= 5) {
        this.zzle.onDestroy();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */