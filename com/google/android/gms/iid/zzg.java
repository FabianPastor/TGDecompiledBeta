package com.google.android.gms.iid;

import android.util.Log;

final class zzg
  implements Runnable
{
  zzg(zzf paramzzf, zzd paramzzd) {}
  
  public final void run()
  {
    if (Log.isLoggable("EnhancedIntentService", 3)) {
      Log.d("EnhancedIntentService", "bg processing of the intent starting now");
    }
    zzf.zza(this.zzifa).handleIntent(this.zziez.intent);
    this.zziez.finish();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */