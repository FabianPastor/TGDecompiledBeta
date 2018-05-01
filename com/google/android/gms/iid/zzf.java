package com.google.android.gms.iid;

import android.os.Binder;
import android.os.Process;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public final class zzf
  extends Binder
{
  private final zzb zziey;
  
  zzf(zzb paramzzb)
  {
    this.zziey = paramzzb;
  }
  
  public final void zza(zzd paramzzd)
  {
    if (Binder.getCallingUid() != Process.myUid()) {
      throw new SecurityException("Binding only allowed within app");
    }
    if (Log.isLoggable("EnhancedIntentService", 3)) {
      Log.d("EnhancedIntentService", "service received new intent via bind strategy");
    }
    if (Log.isLoggable("EnhancedIntentService", 3)) {
      Log.d("EnhancedIntentService", "intent being queued for bg execution");
    }
    this.zziey.zzieo.execute(new zzg(this, paramzzd));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */