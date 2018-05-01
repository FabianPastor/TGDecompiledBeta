package com.google.android.gms.iid;

import android.content.Intent;
import android.util.Log;

final class zze
  implements Runnable
{
  zze(zzd paramzzd, Intent paramIntent) {}
  
  public final void run()
  {
    String str = this.val$intent.getAction();
    Log.w("EnhancedIntentService", String.valueOf(str).length() + 61 + "Service took too long to process intent: " + str + " App may get closed.");
    this.zziex.finish();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */