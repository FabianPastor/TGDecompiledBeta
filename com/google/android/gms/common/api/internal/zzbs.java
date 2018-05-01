package com.google.android.gms.common.api.internal;

import android.os.Bundle;

final class zzbs
  implements Runnable
{
  zzbs(zzbr paramzzbr, LifecycleCallback paramLifecycleCallback, String paramString) {}
  
  public final void run()
  {
    LifecycleCallback localLifecycleCallback;
    if (zzbr.zza(this.zzlg) > 0)
    {
      localLifecycleCallback = this.zzle;
      if (zzbr.zzb(this.zzlg) == null) {
        break label118;
      }
    }
    label118:
    for (Bundle localBundle = zzbr.zzb(this.zzlg).getBundle(this.zzlf);; localBundle = null)
    {
      localLifecycleCallback.onCreate(localBundle);
      if (zzbr.zza(this.zzlg) >= 2) {
        this.zzle.onStart();
      }
      if (zzbr.zza(this.zzlg) >= 3) {
        this.zzle.onResume();
      }
      if (zzbr.zza(this.zzlg) >= 4) {
        this.zzle.onStop();
      }
      if (zzbr.zza(this.zzlg) >= 5) {
        this.zzle.onDestroy();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */